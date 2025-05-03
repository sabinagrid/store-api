CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255),
                          available INT,
                          price DECIMAL(19,2)
);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        created_at TIMESTAMP,
                        status VARCHAR(255),
                        total DECIMAL(19,2),
                        user_id BIGINT,
                        CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_item (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            product_id BIGINT,
                            quantity INT,
                            price_at_purchase DECIMAL(19,2),
                            order_id BIGINT,
                            CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES products(id),
                            CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

INSERT INTO users (email, password_hash) VALUES
    ('admin@store.com', '$2a$10$cMmKzuRFNdOz4RBm/i4pXOfJoEiHYa4YMEfd1vI7XdliFiEK4YA2q');

INSERT INTO products (id, title, available, price) VALUES
                                                       (2411, 'Nail gun', 8, 23.95),
                                                       (2412, 'Hammer', 12, 12.49),
                                                       (2413, 'Screwdriver Set', 15, 9.99),
                                                       (3000, 'Cordless Drill', 14, 40.55),
                                                       (3001, 'Electric Saw', 10, 57.79),
                                                       (3002, 'Measuring Tape', 15, 39.84),
                                                       (3003, 'Paint Brush', 6, 31.25),
                                                       (3004, 'Pliers', 6, 36.51),
                                                       (3005, 'Adjustable Wrench', 20, 95.96),
                                                       (3006, 'Sledgehammer', 10, 54.62),
                                                       (3007, 'Utility Knife', 19, 68.41),
                                                       (3008, 'Toolbox', 5, 37.02),
                                                       (3009, 'Workbench Lamp', 13, 17.46);
