-- init-db/init_mysql.sql
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100),
                          price NUMERIC(10, 2),
                          last_updated TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

INSERT INTO products (name, price) VALUES
                                       ('Laptop', 1200.00),
                                       ('Mouse', 25.00),
                                       ('Keyboard', 75.50);
