-- init-db/init_mysql.sql
CREATE TABLE IF NOT EXISTS products (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100),
    price DECIMAL(10, 2),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

INSERT INTO products (name, price) VALUES
                                       ('Laptop', 1200.00),
                                       ('Mouse', 25.00),
                                       ('Keyboard', 75.50),
                                       ('HeadPhone', 65.50),
                                       ('MousePad', 5.50),
                                       ('Camera', 55.50),
                                       ('HDMI Cable', 1.50),
                                       ('Monitor', 175.50);
