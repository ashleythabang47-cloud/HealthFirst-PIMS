-- =====================================================================
-- HealthFirst Pharmacy Inventory Management System (PIMS)
-- database.sql
-- Run this entire script in MySQL Workbench to create and seed the DB.
-- =====================================================================

DROP DATABASE IF EXISTS pims_db;
CREATE DATABASE pims_db;
USE pims_db;

-- ---------------------------------------------------------------------
-- Table 1: users
-- Stores login credentials and roles.
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('Admin', 'Cashier') NOT NULL,
    full_name   VARCHAR(100) NOT NULL
);

-- ---------------------------------------------------------------------
-- Table 2: suppliers
-- Stores medicine supplier information.
-- ---------------------------------------------------------------------
CREATE TABLE suppliers (
    supplier_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT
);

-- ---------------------------------------------------------------------
-- Table 3: medicines
-- The core inventory table.
-- ---------------------------------------------------------------------
CREATE TABLE medicines (
    medicine_id         INT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    company             VARCHAR(100),
    medicine_type       VARCHAR(50),   -- 'Tablet', 'Capsule', 'Syrup', 'Injection', 'Cream'
    price               DECIMAL(10,2) NOT NULL,
    quantity_in_stock   INT NOT NULL DEFAULT 0,
    reorder_level       INT NOT NULL DEFAULT 0,
    expiry_date         DATE,
    supplier_id         INT,
    CONSTRAINT fk_medicine_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- ---------------------------------------------------------------------
-- Table 4: sales
-- Stores header information for each transaction.
-- ---------------------------------------------------------------------
CREATE TABLE sales (
    sale_id       INT AUTO_INCREMENT PRIMARY KEY,
    sale_date     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    user_id       INT NOT NULL,
    CONSTRAINT fk_sale_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- ---------------------------------------------------------------------
-- Table 5: sale_items
-- Stores the line items for each sale (normalized form).
-- ---------------------------------------------------------------------
CREATE TABLE sale_items (
    sale_item_id   INT AUTO_INCREMENT PRIMARY KEY,
    sale_id        INT NOT NULL,
    medicine_id    INT NOT NULL,
    quantity_sold  INT NOT NULL,
    price_at_sale  DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_saleitem_sale
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_saleitem_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- =====================================================================
-- Sample data
-- =====================================================================

-- Users (Admin: admin/admin123, Cashier: cashier/cash123 — plain text
-- here for simplicity; hash these in your Java code before storing,
-- e.g. with a simple SHA-256 utility, and update this script to match).
INSERT INTO users (username, password, role, full_name) VALUES
('admin',   'admin123', 'Admin',   'Thabo Mokoena'),
('cashier', 'cash123',  'Cashier', 'Naledi Dube');

-- Suppliers
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('MedSupply SA',        'Karabo Sithole', '011-555-0101', 'karabo@medsupplysa.co.za', '12 Industry Rd, Midrand'),
('PharmaCorp Wholesale', 'Lindiwe Nkosi',  '011-555-0202', 'lindiwe@pharmacorp.co.za', '45 Distribution Ave, Sandton'),
('National Meds Group',  'Sipho Zulu',     '011-555-0303', 'sipho@natmeds.co.za',      '8 Warehouse St, Kempton Park');

-- Medicines
-- Note: one item below its reorder_level (Panado) and one expiring soon
-- (Amoxil) so the Low Stock and Expiry reports have data to display.
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Panado',        'Adcock Ingram', 'Tablet',    45.99,  8,  20, '2027-06-30', 1),
('Amoxil 500mg',  'GSK',           'Capsule',   89.50, 40,  15, '2026-09-20', 2),
('Corenza C',      'Aspen',        'Tablet',    59.99, 60,  25, '2027-03-15', 1),
('Benylin Cough Syrup', 'Johnson & Johnson', 'Syrup', 74.00, 25, 10, '2026-12-01', 3),
('Insulin Actrapid', 'Novo Nordisk', 'Injection', 199.99, 12, 10, '2026-10-05', 2),
('Betnovate Cream', 'GSK',         'Cream',     65.50, 30,  10, '2027-08-01', 3);

-- Sample completed sale (processed by cashier, user_id 2)
INSERT INTO sales (total_amount, user_id) VALUES
(135.49, 2);

INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES
(1, 1, 1, 45.99),
(1, 3, 1, 59.99),
(1, 2, 1, 89.50);
-- Note: totals above are illustrative sample data only, not recalculated.
