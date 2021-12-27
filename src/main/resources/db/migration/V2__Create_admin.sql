INSERT INTO roles (id, created, updated, name, display_name) VALUES
(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ADMIN', 'Администратор'),
(2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'STAFF', 'Персонал'),
(3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'AGENT', 'Агент');

INSERT INTO users (id, created, updated, enabled, username, password) VALUES
(1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, 'admin', '$2a$10$NFZou0gRsIjqugJNIc/I9e5ohFIxuAWEMVpa9gdQ86HCxhA7mYeBa');

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 2),
(1, 1);