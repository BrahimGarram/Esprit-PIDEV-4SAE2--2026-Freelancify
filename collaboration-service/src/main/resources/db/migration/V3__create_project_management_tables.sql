-- =====================================================
-- Project Management System - Database Migration
-- Version: 3.0
-- Description: Creates tables for Kanban, Sprints, Milestones, Time Tracking
-- =====================================================

-- =====================================================
-- 1. MILESTONES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS milestones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collaboration_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    order_index INT NOT NULL DEFAULT 0,
    due_date DATETIME,
    payment_amount DECIMAL(12, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME,
    
    INDEX idx_collaboration_id (collaboration_id),
    INDEX idx_status (status),
    INDEX idx_order_index (order_index),
    
    CONSTRAINT fk_milestone_collaboration 
        FOREIGN KEY (collaboration_id) 
        REFERENCES collaborations(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_milestone_status 
        CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. SPRINTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS sprints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collaboration_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    goal TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    duration_weeks INT NOT NULL DEFAULT 2,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_collaboration_id (collaboration_id),
    INDEX idx_status (status),
    INDEX idx_dates (start_date, end_date),
    
    CONSTRAINT fk_sprint_collaboration 
        FOREIGN KEY (collaboration_id) 
        REFERENCES collaborations(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_sprint_status 
        CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    
    CONSTRAINT chk_sprint_dates 
        CHECK (end_date > start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. TEAM MEMBERS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collaboration_id BIGINT NOT NULL,
    freelancer_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME,
    
    INDEX idx_collaboration_id (collaboration_id),
    INDEX idx_freelancer_id (freelancer_id),
    INDEX idx_active (is_active),
    
    CONSTRAINT fk_team_member_collaboration 
        FOREIGN KEY (collaboration_id) 
        REFERENCES collaborations(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_team_member_role 
        CHECK (role IN ('PROJECT_MANAGER', 'FRONTEND_DEVELOPER', 'BACKEND_DEVELOPER', 
                       'FULLSTACK_DEVELOPER', 'DESIGNER', 'QA_TESTER', 'DEVOPS_ENGINEER', 
                       'BUSINESS_ANALYST', 'TECHNICAL_WRITER', 'OTHER')),
    
    UNIQUE KEY uk_collaboration_freelancer (collaboration_id, freelancer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. TASKS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    collaboration_id BIGINT NOT NULL,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    assigned_freelancer_id BIGINT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    deadline DATETIME,
    estimated_hours INT NOT NULL DEFAULT 0,
    actual_hours INT NOT NULL DEFAULT 0,
    attachments TEXT,
    milestone_id BIGINT,
    parent_task_id BIGINT,
    sprint_id BIGINT,
    order_index INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME,
    
    INDEX idx_collaboration_id (collaboration_id),
    INDEX idx_assigned_freelancer (assigned_freelancer_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_milestone_id (milestone_id),
    INDEX idx_sprint_id (sprint_id),
    INDEX idx_parent_task_id (parent_task_id),
    INDEX idx_order_index (order_index),
    INDEX idx_deadline (deadline),
    
    CONSTRAINT fk_task_collaboration 
        FOREIGN KEY (collaboration_id) 
        REFERENCES collaborations(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_task_milestone 
        FOREIGN KEY (milestone_id) 
        REFERENCES milestones(id) 
        ON DELETE SET NULL,
    
    CONSTRAINT fk_task_sprint 
        FOREIGN KEY (sprint_id) 
        REFERENCES sprints(id) 
        ON DELETE SET NULL,
    
    CONSTRAINT fk_task_parent 
        FOREIGN KEY (parent_task_id) 
        REFERENCES tasks(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_task_priority 
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    
    CONSTRAINT chk_task_status 
        CHECK (status IN ('BACKLOG', 'TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. TASK DEPENDENCIES TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS task_dependencies (
    task_id BIGINT NOT NULL,
    depends_on_task_id BIGINT NOT NULL,
    
    PRIMARY KEY (task_id, depends_on_task_id),
    
    INDEX idx_depends_on (depends_on_task_id),
    
    CONSTRAINT fk_task_dependency_task 
        FOREIGN KEY (task_id) 
        REFERENCES tasks(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_task_dependency_depends 
        FOREIGN KEY (depends_on_task_id) 
        REFERENCES tasks(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_no_self_dependency 
        CHECK (task_id != depends_on_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. TASK COMMENTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS task_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    attachments TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_task_id (task_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    
    CONSTRAINT fk_comment_task 
        FOREIGN KEY (task_id) 
        REFERENCES tasks(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 7. COMMENT MENTIONS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS comment_mentions (
    comment_id BIGINT NOT NULL,
    mentioned_user_id BIGINT NOT NULL,
    
    PRIMARY KEY (comment_id, mentioned_user_id),
    
    INDEX idx_mentioned_user (mentioned_user_id),
    
    CONSTRAINT fk_mention_comment 
        FOREIGN KEY (comment_id) 
        REFERENCES task_comments(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 8. TIME LOGS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS time_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    freelancer_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration_minutes INT NOT NULL DEFAULT 0,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_task_id (task_id),
    INDEX idx_freelancer_id (freelancer_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    
    CONSTRAINT fk_timelog_task 
        FOREIGN KEY (task_id) 
        REFERENCES tasks(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT chk_timelog_status 
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    
    CONSTRAINT chk_timelog_times 
        CHECK (end_time IS NULL OR end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Composite indexes for common queries
CREATE INDEX idx_tasks_collaboration_status ON tasks(collaboration_id, status);
CREATE INDEX idx_tasks_collaboration_assignee ON tasks(collaboration_id, assigned_freelancer_id);
CREATE INDEX idx_tasks_milestone_status ON tasks(milestone_id, status);
CREATE INDEX idx_tasks_sprint_status ON tasks(sprint_id, status);
CREATE INDEX idx_timelogs_task_freelancer ON time_logs(task_id, freelancer_id);
CREATE INDEX idx_timelogs_freelancer_status ON time_logs(freelancer_id, status);

-- =====================================================
-- INITIAL DATA (Optional)
-- =====================================================

-- You can add sample data here if needed for testing
-- Example:
-- INSERT INTO milestones (collaboration_id, title, description, order_index, status)
-- VALUES (1, 'Phase 1: Planning', 'Initial planning and requirements gathering', 1, 'NOT_STARTED');
