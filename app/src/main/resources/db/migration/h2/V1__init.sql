CREATE TABLE table_db (
    id BIGINT IDENTITY PRIMARY KEY,
    table_number INT NOT NULL UNIQUE,
    table_capacity INT NOT NULL
);

CREATE TABLE reservation_db (
    id BIGINT IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    table_number INT NOT NULL UNIQUE,
    accompanying_guests INT NOT NULL
);

CREATE TABLE arrived_guest_db (
    id BIGINT IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    accompanying_guests INT NOT NULL,
    time_arrived DATETIME
);
