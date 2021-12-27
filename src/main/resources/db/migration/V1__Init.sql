CREATE SEQUENCE hibernate_sequence start 1 increment 1;

CREATE TABLE roles (
    id INT8 NOT NULL,
    created TIMESTAMP,
    updated TIMESTAMP,
    display_name VARCHAR(255),
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id INT8 NOT NULL,
    created TIMESTAMP,
    updated TIMESTAMP,
    enabled BOOLEAN,
    password VARCHAR(255),
    username VARCHAR(255),
    PRIMARY KEY (id)
);

create table user_roles (
    user_id INT8 NOT NULL,
    role_id INT8 NOT NULL
);

ALTER TABLE IF EXISTS user_roles
    ADD CONSTRAINT FKh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES roles;

ALTER TABLE IF EXISTS user_roles
    ADD CONSTRAINT FKhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES users;