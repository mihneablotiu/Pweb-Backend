-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Create todo_lists table
CREATE TABLE IF NOT EXISTS todo_lists (
    todo_list_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_user_id_todo_lists FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Create todos table
CREATE TABLE IF NOT EXISTS todos (
    todo_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    is_completed BOOLEAN NOT NULL,
    todo_list_id BIGINT NOT NULL,
    CONSTRAINT fk_todo_list_id_todos FOREIGN KEY (todo_list_id) REFERENCES todo_lists (todo_list_id) ON DELETE CASCADE
);

-- Create tags table
CREATE TABLE IF NOT EXISTS tags (
    tag_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Add table feedback
CREATE TABLE IF NOT EXISTS feedbacks (
    feedback_id BIGSERIAL PRIMARY KEY,
    feedback_type VARCHAR(50) NOT NULL,
    satisfaction_level INTEGER NOT NULL,
    accept_terms BOOLEAN NOT NULL DEFAULT false,
    comment TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_user_id_feedback FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


-- Create junction table for many-to-many relationship between todos and tags
-- Create table for many-to-many relationship between todos and tags
CREATE TABLE IF NOT EXISTS todo_tag (
    todo_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT pk_todo_tag PRIMARY KEY (todo_id, tag_id),
    CONSTRAINT fk_todo_id_todo_tag FOREIGN KEY (todo_id) REFERENCES todos (todo_id) ON DELETE CASCADE,
    CONSTRAINT fk_tag_id_todo_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
);

