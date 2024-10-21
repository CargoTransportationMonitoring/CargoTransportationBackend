CREATE TABLE users
(
    user_id  SERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE roles
(
    role_id   SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles
(
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users (user_id),
    role_id INT REFERENCES roles (role_id),
    UNIQUE (user_id, role_id)
);

CREATE TABLE vehicle
(
    vehicle_id  SERIAL PRIMARY KEY,
    driver_id   INT REFERENCES users (user_id),
    description TEXT,
    name        VARCHAR(25)
);

CREATE TABLE cargo
(
    cargo_id    SERIAL PRIMARY KEY,
    name        varchar(50),
    description TEXT,
    weight      NUMERIC,
    status      VARCHAR(50),
    created_at  TIMESTAMPTZ DEFAULT now(),
    vehicle_id  INT REFERENCES vehicle (vehicle_id)
);

CREATE TABLE cargo_location
(
    location_id BIGINT,
    cargo_id    INT REFERENCES cargo (cargo_id),
    location    geography(Point, 4326) NOT NULL,
    timestamp   TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    PRIMARY KEY (location_id, timestamp)
);

SELECT create_hypertable('cargo_location', 'timestamp');
