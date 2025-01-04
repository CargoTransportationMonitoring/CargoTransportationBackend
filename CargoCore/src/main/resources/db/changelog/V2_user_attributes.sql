create table user_admin (
    id SERIAL PRIMARY KEY,
    user_id varchar(255) UNIQUE ,
    admin_id varchar(255) NOT NULL
)