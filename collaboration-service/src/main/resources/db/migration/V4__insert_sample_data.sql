-- =====================================================
-- Sample Data for Project Management System
-- Version: 4.0
-- Description: Inserts realistic sample data for testing
-- =====================================================

-- IMPORTANT: User IDs reference users that should exist in Keycloak
-- Before running this migration, create Keycloak users using one of these methods:
-- 
-- Method 1 (Recommended): Automatic Seeding
--   1. Uncomment @Component in backend/src/main/java/com/freelance/userservice/util/KeycloakUserSeeder.java
--   2. Start the backend application (it will create users automatically)
--   3. Comment out @Component again after first run
--
-- Method 2: Manual Creation
--   See KEYCLOAK_USER_SETUP.md for detailed instructions
--
-- User IDs used in this migration: 1-10
-- All users have password: password123

-- =====================================================
-- 1. SAMPLE COLLABORATIONS
-- =====================================================
INSERT IGNORE INTO collaborations (id, company_id, title, description, collaboration_type, required_skills, 
                           budget_min, budget_max, estimated_duration, complexity_level, deadline, 
                           confidentiality_option, max_freelancers_needed, milestone_structure, 
                           industry, status, created_at, updated_at)
VALUES 
(1, 1, 'E-Commerce Platform Development', 
 'Build a modern e-commerce platform with React frontend and Spring Boot backend. Features include product catalog, shopping cart, payment integration, and admin dashboard.',
 'LONG_TERM', 'React, Spring Boot, MySQL, REST API, Payment Integration',
 15000.00, 25000.00, '6 months', 'ADVANCED', '2024-12-31 23:59:59',
 false, 5, 'Phase 1: Design, Phase 2: Backend, Phase 3: Frontend, Phase 4: Integration, Phase 5: Testing',
 'E-Commerce', 'IN_PROGRESS', NOW(), NOW()),

(2, 1, 'Mobile App for Fitness Tracking',
 'Develop a cross-platform mobile application for fitness tracking with features like workout logging, progress charts, and social sharing.',
 'LONG_TERM', 'React Native, Node.js, MongoDB, Firebase',
 10000.00, 18000.00, '4 months', 'INTERMEDIATE', '2024-11-30 23:59:59',
 false, 4, 'Phase 1: UI/UX Design, Phase 2: Core Features, Phase 3: Social Features, Phase 4: Testing',
 'Health & Fitness', 'IN_PROGRESS', NOW(), NOW()),

(3, 2, 'Corporate Website Redesign',
 'Redesign and modernize corporate website with improved UX, responsive design, and CMS integration.',
 'ONE_TIME', 'UI/UX Design, HTML/CSS, JavaScript, WordPress',
 5000.00, 8000.00, '2 months', 'BEGINNER', '2024-10-31 23:59:59',
 false, 2, 'Phase 1: Design Mockups, Phase 2: Development, Phase 3: Content Migration',
 'Corporate', 'OPEN', NOW(), NOW());

-- =====================================================
-- 2. SAMPLE TEAM MEMBERS
-- =====================================================
INSERT IGNORE INTO team_members (collaboration_id, freelancer_id, role, is_active, joined_at)
VALUES 
-- E-Commerce Platform Team
(1, 2, 'PROJECT_MANAGER', true, '2024-01-15 09:00:00'),
(1, 3, 'BACKEND_DEVELOPER', true, '2024-01-15 09:00:00'),
(1, 4, 'FRONTEND_DEVELOPER', true, '2024-01-16 10:00:00'),
(1, 5, 'DESIGNER', true, '2024-01-16 10:00:00'),
(1, 6, 'QA_TESTER', true, '2024-01-20 09:00:00'),

-- Fitness App Team
(2, 2, 'PROJECT_MANAGER', true, '2024-02-01 09:00:00'),
(2, 7, 'FULLSTACK_DEVELOPER', true, '2024-02-01 09:00:00'),
(2, 5, 'DESIGNER', true, '2024-02-02 10:00:00'),
(2, 8, 'QA_TESTER', true, '2024-02-05 09:00:00'),

-- Corporate Website Team
(3, 9, 'DESIGNER', true, '2024-03-01 09:00:00'),
(3, 10, 'FRONTEND_DEVELOPER', true, '2024-03-01 09:00:00');

-- =====================================================
-- 3. SAMPLE MILESTONES
-- =====================================================
INSERT IGNORE INTO milestones (collaboration_id, title, description, order_index, due_date, payment_amount, status, created_at)
VALUES 
-- E-Commerce Platform Milestones
(1, 'Phase 1: Design & Planning', 'Complete UI/UX design, database schema, and API documentation', 1, '2024-02-28 23:59:59', 5000.00, 'COMPLETED', '2024-01-15 10:00:00'),
(1, 'Phase 2: Backend Development', 'Implement REST APIs, authentication, and database integration', 2, '2024-04-30 23:59:59', 7000.00, 'IN_PROGRESS', '2024-01-15 10:00:00'),
(1, 'Phase 3: Frontend Development', 'Build React components, integrate with APIs, implement responsive design', 3, '2024-06-30 23:59:59', 6000.00, 'NOT_STARTED', '2024-01-15 10:00:00'),
(1, 'Phase 4: Testing & Deployment', 'QA testing, bug fixes, deployment to production', 4, '2024-08-31 23:59:59', 4000.00, 'NOT_STARTED', '2024-01-15 10:00:00'),

-- Fitness App Milestones
(2, 'UI/UX Design Phase', 'Create wireframes, mockups, and design system', 1, '2024-03-15 23:59:59', 3000.00, 'COMPLETED', '2024-02-01 10:00:00'),
(2, 'Core Features Development', 'Implement workout logging, user profiles, and data sync', 2, '2024-05-15 23:59:59', 5000.00, 'IN_PROGRESS', '2024-02-01 10:00:00'),
(2, 'Social Features', 'Add friend system, activity feed, and sharing capabilities', 3, '2024-06-30 23:59:59', 4000.00, 'NOT_STARTED', '2024-02-01 10:00:00'),

-- Corporate Website Milestones
(3, 'Design Mockups', 'Create homepage, about, services, and contact page designs', 1, '2024-04-15 23:59:59', 2500.00, 'NOT_STARTED', '2024-03-01 10:00:00'),
(3, 'Website Development', 'Develop responsive website with CMS integration', 2, '2024-05-31 23:59:59', 3500.00, 'NOT_STARTED', '2024-03-01 10:00:00');

-- =====================================================
-- 4. SAMPLE SPRINTS
-- =====================================================
INSERT IGNORE INTO sprints (collaboration_id, name, goal, start_date, end_date, duration_weeks, status, created_at)
VALUES 
-- E-Commerce Platform Sprints
(1, 'Sprint 1: Authentication & User Management', 'Implement user registration, login, JWT authentication, and profile management', '2024-03-01 00:00:00', '2024-03-14 23:59:59', 2, 'COMPLETED', '2024-02-28 10:00:00'),
(1, 'Sprint 2: Product Catalog', 'Build product CRUD, categories, search, and filtering functionality', '2024-03-15 00:00:00', '2024-03-28 23:59:59', 2, 'COMPLETED', '2024-03-14 10:00:00'),
(1, 'Sprint 3: Shopping Cart & Checkout', 'Implement cart management, checkout flow, and order processing', '2024-03-29 00:00:00', '2024-04-11 23:59:59', 2, 'ACTIVE', '2024-03-28 10:00:00'),
(1, 'Sprint 4: Payment Integration', 'Integrate Stripe/PayPal, handle payment processing and webhooks', '2024-04-12 00:00:00', '2024-04-25 23:59:59', 2, 'PLANNED', '2024-03-28 10:00:00'),

-- Fitness App Sprints
(2, 'Sprint 1: User Onboarding', 'Create signup flow, profile setup, and goal setting', '2024-03-15 00:00:00', '2024-03-28 23:59:59', 2, 'COMPLETED', '2024-03-14 10:00:00'),
(2, 'Sprint 2: Workout Logging', 'Build workout creation, exercise library, and logging interface', '2024-03-29 00:00:00', '2024-04-11 23:59:59', 2, 'ACTIVE', '2024-03-28 10:00:00');

-- =====================================================
-- 5. SAMPLE TASKS
-- =====================================================
INSERT IGNORE INTO tasks (collaboration_id, title, description, assigned_freelancer_id, priority, status, 
                  deadline, estimated_hours, actual_hours, milestone_id, sprint_id, order_index, created_at)
VALUES 
-- Sprint 3 Tasks (E-Commerce - Shopping Cart)
(1, 'Design Shopping Cart UI', 'Create responsive shopping cart interface with item list, quantity controls, and price summary', 4, 'HIGH', 'DONE', '2024-04-02 17:00:00', 8, 7, 2, 3, 1, '2024-03-29 09:00:00'),
(1, 'Implement Cart Backend API', 'Create REST endpoints for add/remove/update cart items with session management', 3, 'HIGH', 'DONE', '2024-04-03 17:00:00', 12, 11, 2, 3, 2, '2024-03-29 09:00:00'),
(1, 'Add Cart State Management', 'Implement Redux store for cart state with persistence', 4, 'MEDIUM', 'IN_PROGRESS', '2024-04-05 17:00:00', 6, 3, 2, 3, 3, '2024-03-29 09:00:00'),
(1, 'Create Checkout Flow UI', 'Design multi-step checkout with shipping, billing, and review', 4, 'HIGH', 'IN_PROGRESS', '2024-04-08 17:00:00', 10, 4, 2, 3, 4, '2024-03-29 09:00:00'),
(1, 'Implement Order Processing', 'Backend logic for order creation, validation, and confirmation', 3, 'CRITICAL', 'TODO', '2024-04-10 17:00:00', 16, 0, 2, 3, 5, '2024-03-29 09:00:00'),
(1, 'Add Email Notifications', 'Send order confirmation and shipping update emails', 3, 'MEDIUM', 'TODO', '2024-04-11 17:00:00', 8, 0, 2, 3, 6, '2024-03-29 09:00:00'),

-- Sprint 4 Tasks (E-Commerce - Payment)
(1, 'Integrate Stripe SDK', 'Add Stripe payment gateway integration', 3, 'CRITICAL', 'BACKLOG', '2024-04-15 17:00:00', 12, 0, 2, 4, 1, '2024-03-29 09:00:00'),
(1, 'Create Payment UI Components', 'Build credit card form and payment confirmation screens', 4, 'HIGH', 'BACKLOG', '2024-04-17 17:00:00', 8, 0, 2, 4, 2, '2024-03-29 09:00:00'),
(1, 'Handle Payment Webhooks', 'Process Stripe webhooks for payment status updates', 3, 'HIGH', 'BACKLOG', '2024-04-20 17:00:00', 10, 0, 2, 4, 3, '2024-03-29 09:00:00'),
(1, 'Add Refund Functionality', 'Implement refund processing for cancelled orders', 3, 'MEDIUM', 'BACKLOG', '2024-04-23 17:00:00', 8, 0, 2, 4, 4, '2024-03-29 09:00:00'),

-- Fitness App Tasks
(2, 'Design Workout Logging Screen', 'Create intuitive UI for logging exercises and sets', 5, 'HIGH', 'DONE', '2024-04-02 17:00:00', 6, 6, 2, 2, 1, '2024-03-29 09:00:00'),
(2, 'Build Exercise Library', 'Create database and UI for browsing exercises with images', 7, 'HIGH', 'IN_PROGRESS', '2024-04-05 17:00:00', 12, 5, 2, 2, 2, '2024-03-29 09:00:00'),
(2, 'Implement Workout Templates', 'Allow users to create and save workout templates', 7, 'MEDIUM', 'TODO', '2024-04-08 17:00:00', 8, 0, 2, 2, 3, '2024-03-29 09:00:00'),
(2, 'Add Progress Charts', 'Display workout history with charts and statistics', 7, 'MEDIUM', 'TODO', '2024-04-10 17:00:00', 10, 0, 2, 2, 4, '2024-03-29 09:00:00'),

-- Corporate Website Tasks
(3, 'Create Homepage Mockup', 'Design modern homepage with hero section and key features', 9, 'HIGH', 'REVIEW', '2024-04-05 17:00:00', 8, 8, 1, NULL, 1, '2024-03-01 09:00:00'),
(3, 'Design About Page', 'Create company story and team member showcase page', 9, 'MEDIUM', 'IN_PROGRESS', '2024-04-08 17:00:00', 6, 3, 1, NULL, 2, '2024-03-01 09:00:00'),
(3, 'Design Services Page', 'Layout service offerings with pricing and features', 9, 'MEDIUM', 'TODO', '2024-04-10 17:00:00', 6, 0, 1, NULL, 3, '2024-03-01 09:00:00');

-- =====================================================
-- 6. SAMPLE TASK COMMENTS
-- =====================================================
INSERT IGNORE INTO task_comments (task_id, user_id, content, created_at)
VALUES 
(1, 2, 'Great work on the cart UI! The design looks clean and intuitive.', '2024-04-02 14:30:00'),
(1, 4, 'Thanks! I added some animations for better UX. Ready for review.', '2024-04-02 15:00:00'),
(2, 2, 'API endpoints are working well. Good job on the session management!', '2024-04-03 16:00:00'),
(3, 4, 'Working on Redux integration. Should be done by tomorrow.', '2024-04-04 10:00:00'),
(3, 2, '@john Make sure to add error handling for network failures.', '2024-04-04 11:00:00'),
(4, 2, 'The checkout flow needs to handle guest checkout as well. Can you add that?', '2024-04-05 09:00:00'),
(4, 4, 'Sure, I''ll add guest checkout option in the first step.', '2024-04-05 09:30:00'),
(11, 5, 'Mockup approved by client! Moving to development phase.', '2024-04-05 16:00:00'),
(12, 9, 'Working on the team section. Need photos from the client.', '2024-04-06 10:00:00');

-- =====================================================
-- 7. SAMPLE COMMENT MENTIONS
-- =====================================================
INSERT IGNORE INTO comment_mentions (comment_id, mentioned_user_id)
VALUES 
(5, 4);  -- @john mention in comment

-- =====================================================
-- 8. SAMPLE TIME LOGS
-- =====================================================
INSERT IGNORE INTO time_logs (task_id, freelancer_id, start_time, end_time, duration_minutes, description, status, created_at)
VALUES 
-- Completed time logs
(1, 4, '2024-04-01 09:00:00', '2024-04-01 13:00:00', 240, 'Designed cart UI components and layout', 'APPROVED', '2024-04-01 09:00:00'),
(1, 4, '2024-04-02 09:00:00', '2024-04-02 13:30:00', 270, 'Added animations and responsive design', 'APPROVED', '2024-04-02 09:00:00'),
(2, 3, '2024-04-02 09:00:00', '2024-04-02 17:00:00', 480, 'Implemented cart CRUD endpoints', 'APPROVED', '2024-04-02 09:00:00'),
(2, 3, '2024-04-03 09:00:00', '2024-04-03 15:00:00', 360, 'Added session management and testing', 'APPROVED', '2024-04-03 09:00:00'),
(3, 4, '2024-04-04 09:00:00', '2024-04-04 12:00:00', 180, 'Redux store setup and cart actions', 'APPROVED', '2024-04-04 09:00:00'),

-- Pending time logs
(3, 4, '2024-04-05 09:00:00', '2024-04-05 11:00:00', 120, 'Working on cart persistence', 'PENDING', '2024-04-05 09:00:00'),
(4, 4, '2024-04-05 13:00:00', '2024-04-05 17:00:00', 240, 'Checkout flow UI development', 'PENDING', '2024-04-05 13:00:00'),

-- Active time log (no end time)
(4, 4, '2024-04-06 09:00:00', NULL, 0, 'Continuing checkout flow', 'PENDING', '2024-04-06 09:00:00'),

-- Fitness app time logs
(10, 5, '2024-04-01 09:00:00', '2024-04-01 15:00:00', 360, 'Designed workout logging interface', 'APPROVED', '2024-04-01 09:00:00'),
(11, 7, '2024-04-03 09:00:00', '2024-04-03 14:00:00', 300, 'Built exercise library backend', 'APPROVED', '2024-04-03 09:00:00'),
(11, 7, '2024-04-04 09:00:00', '2024-04-04 12:00:00', 180, 'Working on exercise search functionality', 'PENDING', '2024-04-04 09:00:00'),

-- Corporate website time logs
(15, 9, '2024-04-02 09:00:00', '2024-04-02 17:00:00', 480, 'Created homepage mockup', 'APPROVED', '2024-04-02 09:00:00'),
(16, 9, '2024-04-05 09:00:00', '2024-04-05 12:00:00', 180, 'Working on about page design', 'PENDING', '2024-04-05 09:00:00');

-- =====================================================
-- SUMMARY OF INSERTED DATA
-- =====================================================
-- Collaborations: 3
-- Team Members: 10 (across 3 projects)
-- Milestones: 9
-- Sprints: 6
-- Tasks: 17
-- Comments: 9
-- Mentions: 1
-- Time Logs: 13
-- =====================================================
