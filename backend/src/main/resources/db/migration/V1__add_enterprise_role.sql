-- Allow ENTERPRISE role in users.role column
-- Run this once if you get "Data truncated for column 'role'" when registering as Enterprise.
-- MySQL:
ALTER TABLE users MODIFY COLUMN role VARCHAR(20) NOT NULL;
