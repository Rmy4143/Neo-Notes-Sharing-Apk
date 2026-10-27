const crypto = require('crypto');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '.env') });
const { generateBrevoEmailHtml } = require('./emailTemplate');

/**
 * In-memory secure datastore for OTP verifications (matches email_verifications table schema)
 */
const verificationsStore = new Map();

// Configuration
const OTP_SECRET = process.env.OTP_SECRET || 'neonotes_secure_otp_salt_secret_2026';
const OTP_EXPIRY_MS = 10 * 60 * 1000; // 10 minutes
const RESEND_COOLDOWN_MS = 60 * 1000; // 60 seconds
const MAX_INCORRECT_ATTEMPTS = 5;

/**
 * Computes cryptographically secure HMAC/SHA-256 hash of OTP.
 * Plaintext OTP is NEVER stored in memory or database.
 */
function hashOtp(email, otp) {
  return crypto
    .createHash('sha256')
    .update(`${email.trim().toLowerCase()}:${String(otp).trim()}:${OTP_SECRET}`)
    .digest('hex');
}

/**
 * Generates cryptographically secure 6-digit integer string (100000 to 999999)
 */
function generateSecureSixDigitOtp() {
  return crypto.randomInt(100000, 1000000).toString();
}

/**
 * Sends transactional email via Brevo REST API v3
 * Endpoint: POST https://api.brevo.com/v3/smtp/email
 */
async function sendBrevoEmail(toEmail, otpCode) {
  const brevoApiKey = (process.env.BREVO_API_KEY || '').trim();
  if (!brevoApiKey) {
    throw new Error('BREVO_API_KEY is not configured on the backend server. Please configure BREVO_API_KEY in backend/.env.');
  }

  const senderEmail = (process.env.BREVO_SENDER_EMAIL || 'verify@neonotes.edu').trim();
  const senderName = (process.env.BREVO_SENDER_NAME || 'NEO NOTES Security').trim();
  const htmlContent = generateBrevoEmailHtml(toEmail, otpCode);

  const payload = {
    sender: {
      name: senderName,
      email: senderEmail
    },
    to: [
      {
        email: toEmail,
        name: 'Student'
      }
    ],
    subject: `NEO NOTES // Verification Code: ${otpCode}`,
    htmlContent: htmlContent
  };

  const response = await fetch('https://api.brevo.com/v3/smtp/email', {
    method: 'POST',
    headers: {
      'accept': 'application/json',
      'api-key': brevoApiKey,
      'content-type': 'application/json'
    },
    body: JSON.stringify(payload),
    signal: AbortSignal.timeout(15000)
  });

  if (!response.ok) {
    let errorDetail = `HTTP ${response.status}`;
    try {
      const errJson = await response.json();
      errorDetail = errJson.message || errJson.code || errorDetail;
    } catch (_) {
      try {
        errorDetail = await response.text();
      } catch (__) {}
    }
    throw new Error(`Brevo rejected email delivery: ${errorDetail}`);
  }

  const resJson = await response.json();
  return resJson; // Typically contains { messageId: "<...>" }
}

/**
 * Section 1, 4, 5, 6, 8, 9:
 * Initiates real OTP generation, Brevo dispatch, and hashed record storage.
 */
async function requestStudentOtp(rawEmail) {
  const email = (rawEmail || '').trim().toLowerCase();

  // Basic email syntax validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!email || !emailRegex.test(email)) {
    return {
      success: false,
      status: 400,
      message: 'Please provide a valid student email address.'
    };
  }

  const now = Date.now();
  const existingRecord = verificationsStore.get(email);

  // Section 8: Resend Cooldown (60 seconds)
  if (existingRecord && (now - existingRecord.last_sent_at) < RESEND_COOLDOWN_MS) {
    const secondsRemaining = Math.ceil((RESEND_COOLDOWN_MS - (now - existingRecord.last_sent_at)) / 1000);
    return {
      success: false,
      status: 429,
      cooldownSeconds: secondsRemaining,
      message: `Please wait ${secondsRemaining}s before requesting a new verification code.`
    };
  }

  // Generate real cryptographically secure 6-digit OTP
  const rawOtp = generateSecureSixDigitOtp();
  const otpHash = hashOtp(email, rawOtp);

  // Send real email via Brevo FIRST before confirming success
  try {
    const brevoResult = await sendBrevoEmail(email, rawOtp);
    console.log(`[BREVO DISPATCH] Verification email successfully delivered for ${email} (MessageId: ${brevoResult.messageId || 'OK'})`);
  } catch (brevoErr) {
    console.error(`[BREVO ERROR] Failed to send verification code to ${email}:`, brevoErr.message);
    return {
      success: false,
      status: 502,
      message: `Unable to send verification code: ${brevoErr.message}`
    };
  }

  // Brevo accepted and delivered -> store hashed OTP record
  const newRecord = {
    id: 'verif_' + crypto.randomBytes(8).toString('hex'),
    email: email,
    otp_hash: otpHash,
    purpose: 'STUDENT_EMAIL_VERIFICATION',
    expires_at: now + OTP_EXPIRY_MS,
    attempt_count: 0,
    verified: false,
    last_sent_at: now,
    created_at: now
  };

  verificationsStore.set(email, newRecord);

  // Return clean response without OTP per Section 4
  return {
    success: true,
    status: 200,
    message: 'Verification code sent to your email.'
  };
}

/**
 * Section 4, 6, 7, 14, 15:
 * Validates candidate OTP against secure hash with brute-force protection and expiration check.
 */
function verifyStudentOtp(rawEmail, candidateOtp) {
  const email = (rawEmail || '').trim().toLowerCase();
  const inputOtp = (candidateOtp || '').trim();

  if (!email || !inputOtp) {
    return {
      success: false,
      status: 400,
      verified: false,
      message: 'Both student email and 6-digit verification code are required.'
    };
  }

  if (!/^\d{6}$/.test(inputOtp)) {
    return {
      success: false,
      status: 400,
      verified: false,
      message: 'Verification code must be exactly 6 digits.'
    };
  }

  const record = verificationsStore.get(email);
  if (!record) {
    return {
      success: false,
      status: 404,
      verified: false,
      message: 'No verification request found for this email. Please request a code first.'
    };
  }

  const now = Date.now();

  // Section 7: Brute-force protection (Max 5 attempts)
  if (record.attempt_count >= MAX_INCORRECT_ATTEMPTS) {
    verificationsStore.delete(email); // Invalidate record
    return {
      success: false,
      status: 400,
      verified: false,
      isBlocked: true,
      message: 'Too many incorrect attempts. Please request a new verification code.'
    };
  }

  // Section 6: Expiration check (10 minutes)
  if (now > record.expires_at) {
    verificationsStore.delete(email); // Invalidate expired record
    return {
      success: false,
      status: 400,
      verified: false,
      isExpired: true,
      message: 'Verification code has expired. Please request a new code.'
    };
  }

  // Compare candidate hash with stored hash (Constant-time timing safe compare)
  const candidateHash = hashOtp(email, inputOtp);
  const isValid = crypto.timingSafeEqual(
    Buffer.from(candidateHash, 'hex'),
    Buffer.from(record.otp_hash, 'hex')
  );

  if (!isValid) {
    record.attempt_count += 1;
    const remaining = MAX_INCORRECT_ATTEMPTS - record.attempt_count;

    if (remaining <= 0) {
      verificationsStore.delete(email);
      return {
        success: false,
        status: 400,
        verified: false,
        isBlocked: true,
        message: 'Too many incorrect attempts. Please request a new verification code.'
      };
    }

    return {
      success: false,
      status: 400,
      verified: false,
      attemptsRemaining: remaining,
      message: `Invalid verification code. ${remaining} attempt${remaining === 1 ? '' : 's'} remaining.`
    };
  }

  // Success: mark verified
  record.verified = true;
  record.attempt_count = 0;

  return {
    success: true,
    status: 200,
    verified: true,
    message: 'Email verified successfully.'
  };
}

/**
 * Checks whether an email has an active verified session
 */
function isEmailVerified(rawEmail) {
  const email = (rawEmail || '').trim().toLowerCase();
  const record = verificationsStore.get(email);
  return Boolean(record && record.verified);
}

module.exports = {
  requestStudentOtp,
  verifyStudentOtp,
  isEmailVerified,
  // Exported for testing harness
  _verificationsStore: verificationsStore,
  _hashOtp: hashOtp
};
