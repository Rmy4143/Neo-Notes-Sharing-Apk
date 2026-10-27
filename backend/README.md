# NEO NOTES Backend API (NitroFlare Cloud Storage Integration)

This backend service implements the secure, server-side gateway between the NEO NOTES Android application and the NitroFlare file storage provider.

## Architecture

```
Android NEO NOTES
        |
        | HTTPS / REST
        v
NEO NOTES BACKEND / API (Node.js/Express)
        |
        | NitroFlare Dynamic Upload API
        v
NITROFLARE CLOUD STORAGE (nitroflare.com)
        |
        | file URL / metadata / xxhash
        v
MySQL DATABASE (Schema: schema.sql)
```

## Security & Credentials

The `NITROFLARE_USER_HASH` is kept **strictly** on this backend server inside environment variables (`.env`).
It is **never** embedded in the Android APK, returned over any API, or logged in client errors.

## Brevo Email OTP Verification Integration

This backend server implements real 6-digit email OTP verification for student authentication and registration using Brevo (formerly Sendinblue).

### Flow

```
Android Client -> Enter student email
      ↓
POST /api/auth/student/send-otp
      ↓
Backend generates cryptographically secure 6-digit OTP
      ↓
Backend hashes OTP with SHA-256 + salt (never stores plaintext)
      ↓
Brevo API dispatches tactical cybersecurity email (10-minute validity)
      ↓
Backend returns { success: true } (OTP is NEVER returned in response)
      ↓
Android displays smooth animated 6-digit input
      ↓
Student enters 6-digit OTP -> POST /api/auth/student/verify-otp
      ↓
Backend validates hash, 10-minute expiry, and 5-attempt limit
      ↓
Success: Email verified on server -> Account login/registration proceeds
```

### Environment Variables (.env)

- `BREVO_API_KEY`: Your Brevo account API key (e.g. `xkeysib-...`)
- `BREVO_SENDER_EMAIL`: Verified sender email address (default: `verify@neonotes.edu`)
- `BREVO_SENDER_NAME`: Sender display name (default: `NEO NOTES Security`)
- `OTP_SECRET`: Secret salt for SHA-256 hashing

### Endpoints

- `GET  /api/health` - Service, NitroFlare, and Brevo status
- `POST /api/auth/student/send-otp` - Generate & dispatch real Brevo OTP
- `POST /api/auth/student/verify-otp` - Verify 6-digit code with rate/attempt limits
- `GET  /api/auth/student/verification-status` - Check email verification status
- `POST /api/notes/upload` - Secure multi-part note document upload to NitroFlare
- `GET  /api/notes` - Filter and list notes
- `GET  /api/notes/:id` - Fetch note record
- `GET  /api/notes/:id/download` - Legitimate download counter and NitroFlare URL retrieval
- `DELETE /api/notes/:id` - Remove document metadata
- `GET  /api/storage/metrics` - Storage monitoring & inactivity tracking
