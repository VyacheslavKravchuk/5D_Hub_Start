CREATE TABLE users
(
    user_id         BIGSERIAL PRIMARY KEY,
    first_name varchar(60) NOT NULL,
    last_name  varchar(60) NOT NULL,
    phone_number      varchar(60) NOT NULL,
    height  INT NOT NULL,
    company_id BIGINT NOT NULL
);