-- =======================================================
-- NEO NOTES - MySQL Database Schema
-- Document Storage Provider: NitroFlare Cloud Archive
-- =======================================================

CREATE DATABASE IF NOT EXISTS `neonotes` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `neonotes`;

-- Users table
CREATE TABLE IF NOT EXISTS `users` (
  `id` VARCHAR(64) PRIMARY KEY,
  `name` VARCHAR(128) NOT NULL,
  `email` VARCHAR(191) NOT NULL UNIQUE,
  `college` VARCHAR(191) NOT NULL,
  `branch` VARCHAR(128) NOT NULL,
  `semester` VARCHAR(64) NOT NULL,
  `role` ENUM('STUDENT', 'TEACHER', 'ADMIN') DEFAULT 'STUDENT',
  `avatar_url` VARCHAR(512) DEFAULT '',
  `email_verified` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Email Verifications table (Brevo OTP)
CREATE TABLE IF NOT EXISTS `email_verifications` (
  `id` VARCHAR(64) PRIMARY KEY,
  `email` VARCHAR(191) NOT NULL,
  `otp_hash` VARCHAR(128) NOT NULL,
  `purpose` VARCHAR(64) NOT NULL DEFAULT 'STUDENT_EMAIL_VERIFICATION',
  `expires_at` BIGINT NOT NULL,
  `attempt_count` INT NOT NULL DEFAULT 0,
  `verified` BOOLEAN NOT NULL DEFAULT FALSE,
  `last_sent_at` BIGINT NOT NULL,
  `created_at` BIGINT NOT NULL,
  INDEX `idx_email_verif` (`email`),
  INDEX `idx_verif_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notes table with NitroFlare Cloud File References
CREATE TABLE IF NOT EXISTS `notes` (
  `id` VARCHAR(64) PRIMARY KEY,
  `user_id` VARCHAR(64) NOT NULL,
  `uploader_name` VARCHAR(128) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `subject` VARCHAR(128) NOT NULL,
  `branch` VARCHAR(128) NOT NULL,
  `semester` VARCHAR(64) NOT NULL,
  `university` VARCHAR(191) NOT NULL,
  `academic_year` VARCHAR(32) NOT NULL,
  `tags` VARCHAR(512) DEFAULT '',
  `file_name` VARCHAR(255) NOT NULL,
  `file_type` VARCHAR(16) NOT NULL, -- PDF, DOC, DOCX, PPT, PPTX
  `file_size` VARCHAR(32) NOT NULL,
  `file_size_bytes` BIGINT NOT NULL DEFAULT 0,
  `nitroflare_url` VARCHAR(512) NOT NULL,
  `nitroflare_xxhash` VARCHAR(64) DEFAULT NULL,
  `status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
  `rejection_reason` TEXT DEFAULT NULL,
  `download_count` INT NOT NULL DEFAULT 0,
  `last_accessed_at` BIGINT DEFAULT NULL,
  `created_at` BIGINT NOT NULL,
  `updated_at` BIGINT NOT NULL,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_subject` (`subject`),
  INDEX `idx_last_accessed` (`last_accessed_at`),
  CONSTRAINT `fk_notes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Bookmarks table
CREATE TABLE IF NOT EXISTS `bookmarks` (
  `id` VARCHAR(64) PRIMARY KEY,
  `note_id` VARCHAR(64) NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `created_at` BIGINT NOT NULL,
  UNIQUE KEY `uk_user_note` (`user_id`, `note_id`),
  CONSTRAINT `fk_bm_note` FOREIGN KEY (`note_id`) REFERENCES `notes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_bm_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notifications table
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` VARCHAR(64) PRIMARY KEY,
  `user_id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(191) NOT NULL,
  `message` TEXT NOT NULL,
  `type` VARCHAR(32) NOT NULL,
  `is_read` BOOLEAN DEFAULT FALSE,
  `created_at` BIGINT NOT NULL,
  INDEX `idx_notif_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
