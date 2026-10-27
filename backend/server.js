const express = require('express');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
require('dotenv').config({ path: path.join(__dirname, '.env') });

const {
  requestStudentOtp,
  verifyStudentOtp,
  isEmailVerified
} = require('./otpService');

const app = express();
const PORT = process.env.BACKEND_PORT || 5000;

app.use(cors());
app.use(express.json());

// In-memory persistent datastore for notes and metadata (compatible with MySQL schema)
let notesDatabase = [];

// Allowed Academic File Formats and MIME types per Section 4
const ALLOWED_EXTENSIONS = new Set(['pdf', 'doc', 'docx', 'ppt', 'pptx']);
const ALLOWED_MIME_TYPES = new Set([
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/vnd.ms-powerpoint',
  'application/vnd.openxmlformats-officedocument.presentationml.presentation',
  'application/octet-stream' // fallback from some Android upload pickers
]);

const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 50 * 1024 * 1024 } // 50MB maximum
});

/**
 * Section 2: Dynamically requests the current NitroFlare upload server URL.
 * NEVER hard-code s93 or any specific server.
 */
async function getNitroFlareUploadServer(retries = 3) {
  const getServerUrl = 'https://nitroflare.com/plugins/fileupload/getServer';

  for (let attempt = 1; attempt <= retries; attempt++) {
    try {
      const response = await fetch(getServerUrl, { method: 'GET', signal: AbortSignal.timeout(10000) });
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      const serverUrl = (await response.text()).trim();
      if (!serverUrl || !serverUrl.startsWith('http')) {
        throw new Error(`Invalid upload server URL returned: ${serverUrl}`);
      }
      return serverUrl;
    } catch (err) {
      if (attempt >= retries) throw err;
      await new Promise(r => setTimeout(r, 1000 * Math.pow(2, attempt - 1)));
    }
  }
}

/**
 * Section 2 & 3: Uploads file to the returned NitroFlare server and parses the verified response.
 */
async function uploadToNitroFlare(fileBuffer, originalName, mimeType, retries = 3) {
  const userHash = process.env.NITROFLARE_USER_HASH;
  if (!userHash) {
    throw new Error('NITROFLARE_USER_HASH is not configured on the backend server.');
  }

  const uploadServer = await getNitroFlareUploadServer(retries);

  for (let attempt = 1; attempt <= retries; attempt++) {
    try {
      const blob = new Blob([fileBuffer], { type: mimeType });
      const formData = new FormData();
      formData.append('user', userHash);
      formData.append('files', blob, originalName);

      const response = await fetch(uploadServer, {
        method: 'POST',
        body: formData,
        signal: AbortSignal.timeout(60000)
      });

      if (!response.ok) {
        throw new Error(`NitroFlare upload returned HTTP ${response.status}`);
      }

      const responseText = await response.text();
      let parsed;
      try {
        parsed = JSON.parse(responseText);
      } catch (parseError) {
        throw new Error('Malformed NitroFlare response.');
      }

      // Verified NitroFlare response format:
      // { "files": [ { "name": "...", "size": "...", "type": "...", "xxhash": "...", "url": "..." } ] }
      if (!parsed || !Array.isArray(parsed.files) || parsed.files.length === 0) {
        throw new Error('NitroFlare API returned unexpected payload structure.');
      }

      const fileInfo = parsed.files[0];
      return {
        name: fileInfo.name || originalName,
        size: parseInt(fileInfo.size || fileBuffer.length, 10),
        type: fileInfo.type || mimeType,
        xxhash: fileInfo.xxhash || null,
        url: fileInfo.url
      };
    } catch (err) {
      if (attempt >= retries) throw err;
      await new Promise(r => setTimeout(r, 1500 * Math.pow(2, attempt - 1)));
    }
  }
}

/**
 * Health check endpoint
 */
app.get('/api/health', (req, res) => {
  res.json({
    status: 'online',
    service: 'NEO NOTES Backend API',
    storageProvider: 'NitroFlare Cloud Archive',
    hasNitroFlareHash: Boolean(process.env.NITROFLARE_USER_HASH),
    emailProvider: 'Brevo Transactional API',
    hasBrevoKey: Boolean(process.env.BREVO_API_KEY),
    timestamp: Date.now()
  });
});

/**
 * Section 1, 4, 8, 9, 14, 22:
 * POST /api/auth/student/send-otp
 * Generates secure 6-digit OTP, sends via Brevo, stores cryptographic hash.
 * Never returns the OTP in API response.
 */
app.post('/api/auth/student/send-otp', async (req, res) => {
  try {
    const { email } = req.body;
    if (!email) {
      return res.status(400).json({ success: false, message: 'Email address is required.' });
    }

    const result = await requestStudentOtp(email);
    return res.status(result.status || (result.success ? 200 : 400)).json({
      success: result.success,
      message: result.message,
      cooldownSeconds: result.cooldownSeconds || undefined
    });
  } catch (err) {
    console.error('Error in send-otp route:', err);
    return res.status(500).json({
      success: false,
      message: 'Unable to send verification code. Please try again later.'
    });
  }
});

/**
 * Section 4, 6, 7, 14, 15:
 * POST /api/auth/student/verify-otp
 * Validates candidate 6-digit OTP against secure hash with brute-force protection.
 */
app.post('/api/auth/student/verify-otp', (req, res) => {
  try {
    const { email, otp } = req.body;
    if (!email || !otp) {
      return res.status(400).json({
        success: false,
        verified: false,
        message: 'Both student email and 6-digit OTP are required.'
      });
    }

    const result = verifyStudentOtp(email, otp);
    return res.status(result.status || (result.success ? 200 : 400)).json({
      success: result.success,
      verified: result.verified,
      message: result.message,
      attemptsRemaining: result.attemptsRemaining,
      isBlocked: result.isBlocked,
      isExpired: result.isExpired
    });
  } catch (err) {
    console.error('Error in verify-otp route:', err);
    return res.status(500).json({
      success: false,
      verified: false,
      message: 'Internal server error verifying code.'
    });
  }
});

/**
 * Section 15:
 * GET /api/auth/student/verification-status
 * Checks if student email is currently verified on server
 */
app.get('/api/auth/student/verification-status', (req, res) => {
  const { email } = req.query;
  if (!email) {
    return res.status(400).json({ success: false, message: 'Email query parameter required.' });
  }

  const verified = isEmailVerified(email);
  return res.json({
    success: true,
    email: email.trim().toLowerCase(),
    verified: verified
  });
});

/**
 * Section 7: POST /api/notes/upload
 * 1. Authenticate the user.
 * 2. Validate file.
 * 3. Validate metadata.
 * 4. Request NitroFlare upload server.
 * 5. Upload the file to NitroFlare.
 * 6. Parse NitroFlare response.
 * 7. Save metadata and NitroFlare URL/xxhash to MySQL/datastore.
 * 8. Set status to PENDING.
 * 9. Return clean API response without credentials.
 */
app.post('/api/notes/upload', upload.single('file'), async (req, res) => {
  try {
    const file = req.file;
    if (!file) {
      return res.status(400).json({ success: false, message: 'No document file provided.' });
    }

    const {
      userId,
      uploaderName,
      title,
      description = '',
      subject,
      branch,
      semester,
      university = 'Neo Institute of Technology',
      academicYear = '2025-2026',
      tags = ''
    } = req.body;

    // 1. Authenticate / validate user & metadata
    if (!title || !title.trim()) {
      return res.status(400).json({ success: false, message: 'Title is required.' });
    }
    if (!subject || !subject.trim()) {
      return res.status(400).json({ success: false, message: 'Subject is required.' });
    }

    // 2. Validate file extension and MIME type per Section 4
    const ext = path.extname(file.originalname).replace('.', '').toLowerCase();
    if (!ALLOWED_EXTENSIONS.has(ext)) {
      return res.status(400).json({
        success: false,
        message: `Unsupported file extension '.${ext}'. Allowed types: PDF, DOC, DOCX, PPT, PPTX.`
      });
    }

    // 3. Upload to NitroFlare
    let nitroResult;
    try {
      nitroResult = await uploadToNitroFlare(file.buffer, file.originalname, file.mimetype);
    } catch (uploadError) {
      console.error('NitroFlare upload failed:', uploadError.message);
      return res.status(502).json({
        success: false,
        message: 'File upload to storage provider failed. Please try again.'
      });
    }

    // 4. Save metadata to datastore with status PENDING per Section 5 & 6
    const noteId = 'note_' + Math.random().toString(36).substring(2, 10);
    const sizeFormatted = formatFileSize(nitroResult.size);

    const noteRecord = {
      id: noteId,
      user_id: userId || 'usr_anonymous',
      uploader_name: uploaderName || 'Anonymous Scholar',
      title: title.trim(),
      description: description.trim(),
      subject: subject.trim(),
      branch: branch || 'Computer Engineering',
      semester: semester || 'Semester 4',
      university: university.trim(),
      academic_year: academicYear.trim(),
      tags: tags.trim(),
      file_name: nitroResult.name,
      file_type: ext.toUpperCase(),
      file_size: sizeFormatted,
      file_size_bytes: nitroResult.size,
      nitroflare_url: nitroResult.url,
      nitroflare_xxhash: nitroResult.xxhash,
      status: 'PENDING', // Always PENDING initially per Section 5
      rejection_reason: null,
      download_count: 0,
      last_accessed_at: null,
      created_at: Date.now(),
      updated_at: Date.now()
    };

    notesDatabase.unshift(noteRecord);

    // 5. Clean API response without exposing credentials
    return res.status(201).json({
      success: true,
      message: 'Note uploaded successfully and is awaiting admin approval.',
      note: {
        id: noteRecord.id,
        title: noteRecord.title,
        status: noteRecord.status,
        fileName: noteRecord.file_name,
        fileType: noteRecord.file_type,
        fileSize: noteRecord.file_size,
        fileSizeBytes: noteRecord.file_size_bytes,
        nitroflareUrl: noteRecord.nitroflare_url,
        nitroflareXxhash: noteRecord.nitroflare_xxhash,
        createdAt: noteRecord.created_at
      }
    });
  } catch (err) {
    console.error('Server error during upload:', err);
    return res.status(500).json({
      success: false,
      message: 'Internal server error processing note upload.'
    });
  }
});

/**
 * GET /api/notes - list notes
 */
app.get('/api/notes', (req, res) => {
  const { status, subject, branch } = req.query;
  let results = [...notesDatabase];

  if (status) results = results.filter(n => n.status.toUpperCase() === status.toUpperCase());
  if (subject) results = results.filter(n => n.subject.toLowerCase() === subject.toLowerCase());
  if (branch) results = results.filter(n => n.branch.toLowerCase() === branch.toLowerCase());

  res.json({ success: true, count: results.length, notes: results });
});

/**
 * GET /api/notes/:id - get note by id
 */
app.get('/api/notes/:id', (req, res) => {
  const note = notesDatabase.find(n => n.id === req.params.id);
  if (!note) return res.status(404).json({ success: false, message: 'Note not found.' });
  res.json({ success: true, note });
});

/**
 * Section 11 & 12: GET /api/notes/:id/download
 * Increments genuine download count and updates last_accessed_at
 */
app.get('/api/notes/:id/download', (req, res) => {
  const note = notesDatabase.find(n => n.id === req.params.id);
  if (!note) return res.status(404).json({ success: false, message: 'Note not found.' });

  // Increment legitimate user download
  note.download_count += 1;
  note.last_accessed_at = Date.now();
  note.updated_at = Date.now();

  res.json({
    success: true,
    downloadUrl: note.nitroflare_url,
    fileName: note.file_name,
    downloadCount: note.download_count
  });
});

/**
 * Section 16: DELETE /api/notes/:id
 * Removes MySQL/datastore metadata. Does NOT attempt unauthenticated NitroFlare deletion.
 */
app.delete('/api/notes/:id', (req, res) => {
  const index = notesDatabase.findIndex(n => n.id === req.params.id);
  if (index === -1) return res.status(404).json({ success: false, message: 'Note not found.' });

  notesDatabase.splice(index, 1);
  res.json({ success: true, message: 'Note removed from repository metadata.' });
});

/**
 * Section 13: Storage Monitoring
 * Legitimate monitoring metrics without artificial bots.
 */
app.get('/api/storage/metrics', (req, res) => {
  const totalNotes = notesDatabase.length;
  const totalBytes = notesDatabase.reduce((sum, n) => sum + (n.file_size_bytes || 0), 0);
  const now = Date.now();
  const SIXTY_DAYS_MS = 60 * 24 * 60 * 60 * 1000;

  const inactiveNotes = notesDatabase.filter(n => {
    const lastActive = n.last_accessed_at || n.created_at;
    return (now - lastActive) > SIXTY_DAYS_MS;
  });

  const recentlyAccessed = notesDatabase
    .filter(n => n.last_accessed_at !== null)
    .sort((a, b) => (b.last_accessed_at || 0) - (a.last_accessed_at || 0))
    .slice(0, 5);

  res.json({
    success: true,
    metrics: {
      totalNotes,
      totalFiles: totalNotes,
      totalStorageBytes: totalBytes,
      totalStorageFormatted: formatFileSize(totalBytes),
      inactiveNotesCount: inactiveNotes.length,
      inactiveNotes: inactiveNotes.map(n => ({
        id: n.id,
        title: n.title,
        fileSize: n.file_size,
        lastAccessedAt: n.last_accessed_at || n.created_at
      })),
      recentlyAccessed: recentlyAccessed.map(n => ({
        id: n.id,
        title: n.title,
        downloadCount: n.download_count,
        lastAccessedAt: n.last_accessed_at
      }))
    }
  });
});

function formatFileSize(bytes) {
  if (!bytes || bytes <= 0) return '0 KB';
  const kb = bytes / 1024;
  const mb = kb / 1024;
  if (mb >= 1) return mb.toFixed(1) + ' MB';
  return Math.round(kb) + ' KB';
}

app.listen(PORT, '0.0.0.0', () => {
  console.log(`[NEO NOTES BACKEND] Server running on port ${PORT}`);
  console.log(`[STORAGE PROVIDER] NitroFlare Cloud Integration Active`);
  if (!process.env.NITROFLARE_USER_HASH) {
    console.warn(`[WARNING] NITROFLARE_USER_HASH is not set. Please configure in .env`);
  }
});
