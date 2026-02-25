-- =====================================================
-- Fix Invalid Task Statuses
-- Version: 5.0
-- Description: Updates any tasks with invalid or empty status values
-- =====================================================

-- Update any tasks with empty or NULL status to TODO
UPDATE tasks 
SET status = 'TODO' 
WHERE status IS NULL OR status = '' OR status NOT IN ('BACKLOG', 'TODO', 'IN_PROGRESS', 'REVIEW', 'DONE');

-- Update any tasks with empty or NULL priority to MEDIUM
UPDATE tasks 
SET priority = 'MEDIUM' 
WHERE priority IS NULL OR priority = '' OR priority NOT IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');

-- Log the fix
SELECT CONCAT('Fixed ', ROW_COUNT(), ' tasks with invalid status/priority values') AS result;
