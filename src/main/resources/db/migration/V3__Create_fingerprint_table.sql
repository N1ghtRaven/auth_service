CREATE TABLE fingerprint (
    id int8 not null,
    created timestamp,
    updated timestamp,
    expire timestamp,
    fingerprint varchar(255),
    primary key (id)
)