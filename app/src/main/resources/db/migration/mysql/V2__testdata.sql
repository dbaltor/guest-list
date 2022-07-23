INSERT INTO table_db(table_number, table_capacity) VALUES (1, 10);
INSERT INTO table_db(table_number, table_capacity) VALUES (2, 10);
INSERT INTO table_db(table_number, table_capacity) VALUES (3, 10);
INSERT INTO table_db(table_number, table_capacity) VALUES (4, 10);

INSERT INTO reservation_db(name, table_number, accompanying_guests) VALUES ('guest1', 1, 8);
INSERT INTO reservation_db(name, table_number, accompanying_guests) VALUES ('guest2', 2, 9);
INSERT INTO reservation_db(name, table_number, accompanying_guests) VALUES ('guest3', 3, 10);

INSERT INTO arrived_guest_db(name, accompanying_guests, time_arrived) VALUES ('guest1', 5, '2022-05-05 20:22:00');
INSERT INTO arrived_guest_db(name, accompanying_guests, time_arrived) VALUES ('guest3', 3, '2022-05-05 21:00:00');
