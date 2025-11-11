-- Initialize database for stream tests
-- This script creates the necessary tables and indexes for testing external system interactions

-- Create messages table for storing data
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    processed BOOLEAN DEFAULT FALSE
);

-- Create index for faster lookups
CREATE INDEX idx_messages_content ON messages(content);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- Create metrics table for system health monitoring
CREATE TABLE metrics (
    id SERIAL PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value NUMERIC NOT NULL,
    recorded_at TIMESTAMP NOT NULL
);

-- Add some initial metrics data
INSERT INTO metrics (metric_name, metric_value, recorded_at) 
VALUES 
    ('cpu_usage', 0.05, NOW()),
    ('memory_usage', 0.15, NOW()),
    ('disk_usage', 0.25, NOW()),
    ('network_traffic', 0.10, NOW());