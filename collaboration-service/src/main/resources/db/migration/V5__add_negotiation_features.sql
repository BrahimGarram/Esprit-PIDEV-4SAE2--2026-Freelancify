-- V5: Add Negotiation Features
-- This migration adds support for proposal negotiation, counter-offers, and discussion threads

-- Add new status for negotiation phase
ALTER TABLE collaboration_requests 
ADD COLUMN IF NOT EXISTS negotiation_status ENUM('INITIAL', 'NEGOTIATING', 'COUNTER_OFFERED', 'AGREED', 'DECLINED') DEFAULT 'INITIAL' AFTER status;

-- Add counter-offer fields to collaboration_requests
ALTER TABLE collaboration_requests
ADD COLUMN IF NOT EXISTS counter_offer_price DECIMAL(10,2) NULL AFTER proposed_price,
ADD COLUMN IF NOT EXISTS counter_offer_timeline VARCHAR(100) NULL AFTER counter_offer_price,
ADD COLUMN IF NOT EXISTS counter_offer_message TEXT NULL AFTER counter_offer_timeline,
ADD COLUMN IF NOT EXISTS counter_offered_by BIGINT NULL AFTER counter_offer_message,
ADD COLUMN IF NOT EXISTS counter_offered_at DATETIME(6) NULL AFTER counter_offered_by,
ADD COLUMN IF NOT EXISTS counter_offer_count INT DEFAULT 0 AFTER counter_offered_at;

-- Add milestone proposal (JSON format)
ALTER TABLE collaboration_requests
ADD COLUMN IF NOT EXISTS proposed_milestones JSON NULL AFTER counter_offer_count,
ADD COLUMN IF NOT EXISTS agreed_milestones JSON NULL AFTER proposed_milestones;

-- Add contract agreement fields
ALTER TABLE collaboration_requests
ADD COLUMN IF NOT EXISTS freelancer_agreed BOOLEAN DEFAULT FALSE AFTER agreed_milestones,
ADD COLUMN IF NOT EXISTS freelancer_agreed_at DATETIME(6) NULL AFTER freelancer_agreed,
ADD COLUMN IF NOT EXISTS company_agreed BOOLEAN DEFAULT FALSE AFTER freelancer_agreed_at,
ADD COLUMN IF NOT EXISTS company_agreed_at DATETIME(6) NULL AFTER company_agreed,
ADD COLUMN IF NOT EXISTS final_agreed_price DECIMAL(10,2) NULL AFTER company_agreed_at,
ADD COLUMN IF NOT EXISTS final_agreed_timeline VARCHAR(100) NULL AFTER final_agreed_price;

-- Create negotiation_messages table for discussion threads
CREATE TABLE IF NOT EXISTS negotiation_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collaboration_request_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_type ENUM('FREELANCER', 'COMPANY') NOT NULL,
    message_type ENUM('TEXT', 'COUNTER_OFFER', 'MILESTONE_PROPOSAL', 'QUESTION', 'ANSWER', 'SYSTEM') DEFAULT 'TEXT',
    message TEXT NOT NULL,
    metadata JSON NULL COMMENT 'Additional data like counter-offer details, milestone breakdown',
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    
    CONSTRAINT fk_negotiation_collab_request 
        FOREIGN KEY (collaboration_request_id) 
        REFERENCES collaboration_requests(id) 
        ON DELETE CASCADE,
    
    INDEX idx_collab_request (collaboration_request_id),
    INDEX idx_sender (sender_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create proposal_revisions table to track revision history
CREATE TABLE IF NOT EXISTS proposal_revisions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collaboration_request_id BIGINT NOT NULL,
    revision_number INT NOT NULL,
    revised_by BIGINT NOT NULL,
    revised_by_type ENUM('FREELANCER', 'COMPANY') NOT NULL,
    
    -- What changed
    previous_price DECIMAL(10,2) NULL,
    new_price DECIMAL(10,2) NULL,
    previous_timeline VARCHAR(100) NULL,
    new_timeline VARCHAR(100) NULL,
    previous_milestones JSON NULL,
    new_milestones JSON NULL,
    
    revision_message TEXT NULL,
    revision_reason VARCHAR(255) NULL,
    
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    
    CONSTRAINT fk_revision_collab_request 
        FOREIGN KEY (collaboration_request_id) 
        REFERENCES collaboration_requests(id) 
        ON DELETE CASCADE,
    
    INDEX idx_collab_request_revision (collaboration_request_id),
    INDEX idx_revision_number (revision_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_negotiation_status ON collaboration_requests(negotiation_status);
CREATE INDEX IF NOT EXISTS idx_counter_offered_by ON collaboration_requests(counter_offered_by);
CREATE INDEX IF NOT EXISTS idx_freelancer_agreed ON collaboration_requests(freelancer_agreed);
CREATE INDEX IF NOT EXISTS idx_company_agreed ON collaboration_requests(company_agreed);

-- Update existing PENDING requests to have INITIAL negotiation status
UPDATE collaboration_requests 
SET negotiation_status = 'INITIAL' 
WHERE status = 'PENDING' AND negotiation_status IS NULL;

-- Add comments for documentation
ALTER TABLE collaboration_requests 
MODIFY COLUMN negotiation_status ENUM('INITIAL', 'NEGOTIATING', 'COUNTER_OFFERED', 'AGREED', 'DECLINED') 
DEFAULT 'INITIAL' 
COMMENT 'Tracks the negotiation phase: INITIAL=just applied, NEGOTIATING=active discussion, COUNTER_OFFERED=waiting for response, AGREED=terms finalized, DECLINED=negotiation failed';

ALTER TABLE collaboration_requests 
MODIFY COLUMN proposed_milestones JSON NULL 
COMMENT 'Freelancer proposed milestone breakdown as JSON array: [{name, percentage, deliverables, duration}]';

ALTER TABLE collaboration_requests 
MODIFY COLUMN agreed_milestones JSON NULL 
COMMENT 'Final agreed milestone breakdown after negotiation';

ALTER TABLE negotiation_messages 
MODIFY COLUMN metadata JSON NULL 
COMMENT 'Stores structured data for special message types like counter-offers: {price, timeline, milestones}';
