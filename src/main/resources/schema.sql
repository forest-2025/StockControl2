CREATE TABLE IF NOT EXISTS m_user
(
   id int NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
   email_address VARCHAR (254) NOT NULL UNIQUE,
   password VARCHAR (256) NOT NULL,
   family_name VARCHAR (100) NOT NULL,
   first_name VARCHAR (100) NOT NULL,
   employee_number char (6) NOT NULL UNIQUE,
   is_admin tinyint NOT NULL default '0',
   is_deleted tinyint NOT NULL default '0',
   register_date_time datetime NOT NULL default CURRENT_TIMESTAMP,
   update_date_time datetime NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS m_supplier
(
   id int NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
   name VARCHAR (100) NOT NULL,
   furigana VARCHAR (100) NOT NULL,
   is_deleted tinyint NOT NULL default '0',
   register_date_time datetime NOT NULL default CURRENT_TIMESTAMP,
   update_date_time datetime NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS m_customer
(
   id int NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
   name VARCHAR (100) NOT NULL,
   furigana VARCHAR (100) NOT NULL,
   is_deleted tinyint NOT NULL default '0',
   register_date_time datetime NOT NULL default CURRENT_TIMESTAMP,
   update_date_time datetime NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS m_product
(
   id int NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
   name VARCHAR (100) NOT NULL,
   number VARCHAR (50) UNIQUE NOT NULL,
   supplier_id int NOT NULL,
   image VARCHAR (512),
   is_deleted tinyint NOT NULL default '0',
   register_date_time datetime NOT NULL default CURRENT_TIMESTAMP,
   update_date_time datetime NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (supplier_id) REFERENCES m_supplier (id)
);
CREATE INDEX ix_m_product_name ON m_product (name);
CREATE INDEX ix_m_product_number ON m_product (number);
CREATE TABLE IF NOT EXISTS t_stock
(
   id int NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
   product_id int NOT NULL,
   stock_quantity int NOT NULL,
   register_date_time datetime NOT NULL default CURRENT_TIMESTAMP,
   update_date_time datetime NOT NULL default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (product_id) REFERENCES m_product (id)
);
CREATE TABLE IF NOT EXISTS t_transaction_history
(
   id int NOT NULL AUTO_INCREMENT UNIQUE PRIMARY KEY,
   product_id int NOT NULL,
   amount_of_change int NOT NULL,
   supplier_id int,
   customer_id int,
   user_id int NOT NULL,
   remarks varchar (100),
   register_date_time datetime NOT NULL default CURRENT_TIMESTAMP,
   FOREIGN KEY (product_id) REFERENCES m_product (id),
   FOREIGN KEY (supplier_id) REFERENCES m_supplier (id),
   FOREIGN KEY (customer_id) REFERENCES m_customer (id),
   FOREIGN KEY (user_id) REFERENCES m_user (id)
);