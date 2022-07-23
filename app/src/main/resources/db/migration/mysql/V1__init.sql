CREATE TABLE IF NOT EXISTS table_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    table_number INT NOT NULL UNIQUE,
    table_capacity INT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    table_number INT NOT NULL UNIQUE,
    accompanying_guests INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (table_number)
        REFERENCES table_db (table_number)
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS arrived_guest_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    accompanying_guests INT NOT NULL,
    time_arrived DATETIME,
    PRIMARY KEY (id)
);