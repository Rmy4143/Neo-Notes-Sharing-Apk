/**
 * NEO NOTES - Tactical / Cybersecurity Transactional Email Template
 * Strictly adheres to Section 11, 12, 13:
 * - Premium cybersecurity HUD styling
 * - Neon cyan (#00E5FF), dark navy (#080A18, #0D1126), violet/magenta accents (#7C4DFF)
 * - No devil icons, no demon mode, no biohazard
 * - 100% email-client safe (tables, inline styles, web-safe fonts)
 */

function generateBrevoEmailHtml(recipientEmail, otpCode) {
  const formattedOtp = String(otpCode).trim();

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>NEO NOTES // Verification Code</title>
</head>
<body style="margin: 0; padding: 0; background-color: #050711; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; -webkit-font-smoothing: antialiased; color: #E0E7FF;">
  <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #050711; padding: 24px 12px;">
    <tr>
      <td align="center">
        <!-- Main Card Container (Max 580px) -->
        <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 580px; background-color: #0A0D1E; border: 1px solid #1E293B; border-radius: 16px; overflow: hidden; box-shadow: 0 12px 40px rgba(0, 229, 255, 0.08);">
          
          <!-- TOP HUD HEADER -->
          <tr>
            <td style="padding: 20px 24px; background-color: #0D1126; border-bottom: 1px solid #1E293B;">
              <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                  <td align="left" style="font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; font-size: 10px; color: #00E5FF; letter-spacing: 1.5px; font-weight: bold;">
                    SYSTEM // ONLINE
                  </td>
                  <td align="right" style="font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; font-size: 10px; color: #7C4DFF; letter-spacing: 1.5px; font-weight: bold;">
                    SECURE CHANNEL // ENCRYPTED
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- BRAND & CLASSIFICATION -->
          <tr>
            <td style="padding: 28px 28px 12px 28px; text-align: center;">
              <table role="presentation" border="0" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto 14px auto;">
                <tr>
                  <td style="width: 44px; height: 44px; background-color: rgba(0, 229, 255, 0.12); border: 1px solid #00E5FF; border-radius: 10px; text-align: center; vertical-align: middle;">
                    <span style="font-size: 22px; color: #00E5FF; line-height: 44px; display: inline-block;">✦</span>
                  </td>
                </tr>
              </table>
              <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-size: 22px; font-weight: 900; letter-spacing: 2px; color: #FFFFFF; text-transform: uppercase;">
                NEO NOTES
              </div>
              <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 11px; color: #00E5FF; letter-spacing: 2px; margin-top: 4px; text-transform: uppercase;">
                Campus Academic Repository
              </div>
              <div style="margin-top: 14px; display: inline-block; padding: 4px 12px; background-color: rgba(124, 77, 255, 0.15); border: 1px solid #7C4DFF; border-radius: 20px; font-family: 'SFMono-Regular', Consolas, monospace; font-size: 10px; color: #B388FF; letter-spacing: 1.2px; font-weight: bold;">
                // SECURE IDENTITY VERIFICATION //
              </div>
            </td>
          </tr>

          <!-- TITLE & INSTRUCTIONS -->
          <tr>
            <td style="padding: 12px 28px 20px 28px; text-align: center;">
              <h1 style="margin: 0 0 10px 0; font-size: 24px; font-weight: 800; color: #FFFFFF; letter-spacing: 0.5px;">
                VERIFY YOUR ACCESS
              </h1>
              <p style="margin: 0; font-size: 14px; line-height: 22px; color: #94A3B8;">
                Your NEO NOTES authentication sequence has been initiated.<br>
                Enter the six-digit verification code below to continue.
              </p>
            </td>
          </tr>

          <!-- TARGET PANEL -->
          <tr>
            <td style="padding: 0 28px 20px 28px;">
              <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #070914; border: 1px solid #1E293B; border-radius: 10px; padding: 12px 16px;">
                <tr>
                  <td align="left">
                    <span style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 10px; color: #64748B; letter-spacing: 1px; text-transform: uppercase;">Authentication Target:</span>
                    <div style="font-size: 13px; font-weight: 600; color: #E2E8F0; margin-top: 2px;">
                      ${recipientEmail}
                    </div>
                  </td>
                  <td align="right" style="vertical-align: middle;">
                    <span style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 10px; color: #00E676; letter-spacing: 1px; font-weight: bold;">
                      ● ACTIVE
                    </span>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- OTP PANEL (Section 12) -->
          <tr>
            <td style="padding: 0 28px 24px 28px;">
              <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #0D1126; border: 2px solid #00E5FF; border-radius: 14px; padding: 24px 16px; text-align: center; box-shadow: inset 0 0 20px rgba(0, 229, 255, 0.08);">
                <tr>
                  <td align="center">
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 11px; letter-spacing: 2px; color: #00E5FF; font-weight: bold; text-transform: uppercase;">
                      // AUTHORIZATION CODE //
                    </div>
                    <!-- Large Monospace 6-digit Code -->
                    <div style="margin: 16px 0; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; font-size: 40px; font-weight: 900; letter-spacing: 10px; color: #FFFFFF; text-shadow: 0 0 16px rgba(0, 229, 255, 0.6);">
                      ${formattedOtp}
                    </div>
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 11px; color: #94A3B8; letter-spacing: 1.2px; font-weight: bold;">
                      CODE VALID FOR <span style="color: #FFB300;">10:00 MINUTES</span>
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- SECURITY PROTOCOL GRID -->
          <tr>
            <td style="padding: 0 28px 20px 28px;">
              <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: #070914; border: 1px solid #1E293B; border-radius: 8px;">
                <tr>
                  <td width="33.3%" align="center" style="padding: 12px; border-right: 1px solid #1E293B;">
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 9px; color: #64748B; letter-spacing: 1px;">ENCRYPTION</div>
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 12px; font-weight: bold; color: #00E5FF; margin-top: 3px;">TLS 1.3</div>
                  </td>
                  <td width="33.3%" align="center" style="padding: 12px; border-right: 1px solid #1E293B;">
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 9px; color: #64748B; letter-spacing: 1px;">CHANNEL</div>
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 12px; font-weight: bold; color: #7C4DFF; margin-top: 3px;">BREVO API</div>
                  </td>
                  <td width="33.3%" align="center" style="padding: 12px;">
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 9px; color: #64748B; letter-spacing: 1px;">FORMAT</div>
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 12px; font-weight: bold; color: #E2E8F0; margin-top: 3px;">6 DIGITS</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- SECURITY NOTICE & WARNING -->
          <tr>
            <td style="padding: 0 28px 24px 28px;">
              <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="background-color: rgba(255, 82, 82, 0.08); border-left: 3px solid #FF5252; border-radius: 4px; padding: 12px 14px;">
                <tr>
                  <td>
                    <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 10px; font-weight: bold; color: #FF5252; letter-spacing: 1px;">
                      ⚠ WARNING: DO NOT SHARE THIS CODE
                    </div>
                    <div style="font-size: 12px; line-height: 18px; color: #CBD5E1; margin-top: 4px;">
                      NEO NOTES will never ask you to share your verification code through chat, email, or external messages. If you did not request this verification, please ignore this email.
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- FOOTER -->
          <tr>
            <td style="padding: 20px 28px; background-color: #070914; border-top: 1px solid #1E293B; text-align: center;">
              <p style="margin: 0 0 6px 0; font-size: 11px; color: #64748B;">
                This one-time verification code was generated specifically for this authentication session.
              </p>
              <div style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 10px; color: #475569; letter-spacing: 1px;">
                © 2026 NEO NOTES // CAMPUS AUTHENTICATION NETWORK
              </div>
            </td>
          </tr>

        </table>
      </td>
    </tr>
  </table>
</body>
</html>`;
}

module.exports = {
  generateBrevoEmailHtml
};
