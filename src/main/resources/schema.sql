CREATE TABLE IF NOT EXISTS types
(
    id INT NOT NULL PRIMARY KEY auto_increment,
    name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products
(
    id INT NOT NULL PRIMARY KEY auto_increment,
    types_id INT NOT NULL,
    name VARCHAR(200) NOT NULL UNIQUE,
    price INT,
    description TEXT,
    FOREIGN KEY (types_id) REFERENCES types (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    id INT NOT NULL PRIMARY KEY auto_increment,
    email TEXT,
    username TEXT,
    password TEXT,
    role TEXT
);

CREATE TABLE IF NOT EXISTS basket
(
    id INT NOT NULL PRIMARY KEY auto_increment,
    user_id INT,
    product_id INT,
    product_count INT,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);