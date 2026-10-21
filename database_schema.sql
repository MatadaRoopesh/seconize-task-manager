-- Reference schema and sample data for Seconize Task Manager.
-- IMPORTANT: Compare with your actual JPA entities/migrations before claiming this
-- is the exact schema. Optional collaboration tables should only be used if supported.

CREATE TABLE IF NOT EXISTS projects (
 id BIGSERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL, description TEXT,
 start_date DATE, deadline DATE, status VARCHAR(30) NOT NULL DEFAULT 'PLANNING',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS tasks (
 id BIGSERIAL PRIMARY KEY, project_id BIGINT REFERENCES projects(id) ON DELETE SET NULL,
 title VARCHAR(255) NOT NULL, description TEXT, assignee VARCHAR(150),
 priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', status VARCHAR(30) NOT NULL DEFAULT 'TODO',
 deadline DATE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- Optional tables: include only if these features exist in the backend.
CREATE TABLE IF NOT EXISTS subtasks (
 id BIGSERIAL PRIMARY KEY, task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
 title VARCHAR(255) NOT NULL, completed BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS task_comments (
 id BIGSERIAL PRIMARY KEY, task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
 author VARCHAR(150) NOT NULL, comment_text TEXT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS task_activity (
 id BIGSERIAL PRIMARY KEY, task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
 actor VARCHAR(150), action VARCHAR(100) NOT NULL, details TEXT,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_deadline ON tasks(deadline);

-- Sample rows for a test database. Remove/adjust IDs if they conflict with existing data.
INSERT INTO projects (id,name,description,start_date,deadline,status) VALUES
(1,'Seconize Task Manager','AI-powered project and task management application',DATE '2026-10-16',DATE '2026-10-21','IN_PROGRESS'),
(2,'Website Login Module','Design and implement secure login and validation',DATE '2026-10-17',DATE '2026-10-28','PLANNING')
ON CONFLICT (id) DO NOTHING;
INSERT INTO tasks (id,project_id,title,description,assignee,priority,status,deadline) VALUES
(1,1,'Create Task Management API','Implement REST endpoints for task operations','Roopesh','HIGH','TODO',DATE '2026-10-21'),
(2,1,'Connect PostgreSQL database','Verify persistence and database configuration','Roopesh','HIGH','IN_PROGRESS',DATE '2026-10-22'),
(3,2,'Implement login form validation','Validate required fields and show useful errors','Arun','MEDIUM','TODO',DATE '2026-10-28')
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('projects','id'), GREATEST(COALESCE((SELECT MAX(id) FROM projects),1),1), true);
SELECT setval(pg_get_serial_sequence('tasks','id'), GREATEST(COALESCE((SELECT MAX(id) FROM tasks),1),1), true);
