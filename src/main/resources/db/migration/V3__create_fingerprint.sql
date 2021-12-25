CREATE TABLE fingerprint (
    id int8 not null,
    created timestamp,
    updated timestamp,
    expired timestamp,
    fingerprint varchar(255),
    primary key (id)
)