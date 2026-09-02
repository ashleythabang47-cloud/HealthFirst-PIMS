# HealthFirst PIMS – Pharmacy Inventory Management System

## Project Overview

HealthFirst PIMS is a desktop-based Pharmacy Inventory Management System being developed to help a pharmacy manage medicine inventory, suppliers, users, sales, and business reports.

The application uses **Java Swing/AWT** for the graphical user interface, **JDBC** for database connectivity, and **MySQL** as the relational database management system.

> **Project Status:** Under Development  
> **Current Progress:** The MySQL database and relational schema have been created.

---

## Features

### Authentication
- Secure user login.
- Role-based access control.
- Administrator and Cashier roles.
- Role-based dashboard redirection.

### Administrator
- Manage medicines using CRUD operations.
- Manage suppliers using CRUD operations.
- Manage Cashier accounts.
- View sales reports.
- View item-wise sales reports.
- View low-stock reports.
- View expiry reports.

### Cashier
- Search for medicines.
- Check medicine prices and availability.
- Add medicines to a shopping cart.
- Remove items from the cart.
- Process sales.
- Generate customer bills.
- Clear the shopping cart.

---

## Technologies

| Technology | Purpose |
|---|---|
| Java | Application development |
| Java Swing/AWT | Desktop GUI |
| JDBC | Database connectivity |
| MySQL | Relational database |
| IntelliJ IDEA | Development environment |
| Git/GitHub | Version control |

---

## User Roles

### Administrator

Administrators will have access to:

- Medicine management.
- Supplier management.
- User management.
- Sales reports.
- Item-wise reports.
- Low-stock reports.
- Expiry reports.

### Cashier

Cashiers will be able to:

- Access the Point of Sale system.
- Search medicines.
- Check stock availability.
- View medicine prices.
- Add items to a cart.
- Process sales.
- Generate bills.

Cashiers cannot manage medicines, suppliers, users, or administrative reports.

---

## Database Design

The project uses a MySQL relational database consisting of five main tables:

1. `users`
2. `suppliers`
3. `medicines`
4. `sales`
5. `sale_items`

### Entity Relationships

```text
SUPPLIERS
    |
    +--------< MEDICINES

USERS
    |
    +--------< SALES
                  |
                  +--------< SALE_ITEMS >-------- MEDICINES
```

---

## Database Tables

### Users

Stores user credentials and roles.

| Column | Description |
|---|---|
| user_id | Primary key |
| username | Unique username |
| password | User password |
| role | Admin or Cashier |
| full_name | Full name |

### Suppliers

Stores supplier information.

| Column | Description |
|---|---|
| supplier_id | Primary key |
| name | Supplier name |
| contact_person | Contact person |
| phone | Phone number |
| email | Email address |
| address | Supplier address |

### Medicines

Stores pharmacy inventory information.

| Column | Description |
|---|---|
| medicine_id | Primary key |
| name | Medicine name |
| company | Manufacturer |
| medicine_type | Type of medicine |
| price | Medicine price |
| quantity_in_stock | Available quantity |
| reorder_level | Minimum stock level |
| expiry_date | Expiry date |
| supplier_id | Supplier foreign key |

### Sales

Stores each sales transaction.

| Column | Description |
|---|---|
| sale_id | Primary key |
| sale_date | Date and time of sale |
| total_amount | Total transaction amount |
| user_id | User who processed the sale |

### Sale Items

Stores individual medicines included in a sale.

| Column | Description |
|---|---|
| sale_item_id | Primary key |
| sale_id | Sale foreign key |
| medicine_id | Medicine foreign key |
| quantity_sold | Quantity sold |
| price_at_sale | Price at the time of sale |

---

## Reports

The completed application will provide the following reports:

### Sales Report
Displays sales transactions, dates, cashiers, and total amounts.

### Item-Wise Sales Report
Displays medicine sales quantities and generated revenue.

### Low Stock Report
Displays medicines where:

```text
quantity_in_stock <= reorder_level
```

### Expiry Report
Displays medicines expiring within the next month.

---

## Current Development Progress

### Completed

- [x] Project planning.
- [x] Database design.
- [x] MySQL database creation.
- [x] Database schema creation.
- [x] `users` table.
- [x] `suppliers` table.
- [x] `medicines` table.
- [x] `sales` table.
- [x] `sale_items` table.
- [x] Primary keys.
- [x] Foreign key relationships.

### In Progress

- [ ] Java project setup.
- [ ] JDBC database connectivity.
- [ ] Login module.
- [ ] Swing/AWT user interface.

### Planned

- [ ] Role-based authentication.
- [ ] Administrator dashboard.
- [ ] Cashier dashboard.
- [ ] Medicine CRUD.
- [ ] Supplier CRUD.
- [ ] User management.
- [ ] Point of Sale system.
- [ ] Shopping cart.
- [ ] Checkout functionality.
- [ ] Automatic stock updates.
- [ ] Bill generation.
- [ ] Sales report.
- [ ] Item-wise sales report.
- [ ] Low-stock report.
- [ ] Expiry report.
- [ ] Testing and documentation.
- [ ] Executable packaging.

---

## Project Structure

```text
HealthFirstPIMS/
|
├── src/
├── database.sql
├── screenshots/
└── README.md
```

The project structure will be expanded as development progresses.

---

## Prerequisites

The following software is required:

- Java Development Kit (JDK)
- MySQL Server
- MySQL Connector/J
- IntelliJ IDEA or another Java IDE

---

## Database Setup

1. Open MySQL Workbench.
2. Run the provided database script.
3. Verify that the required tables were created successfully.

The database script will be included as:

```text
database.sql
```

---

## Planned Default Credentials

| Role | Username | Password |
|---|---|---|
| Administrator | admin | admin123 |
| Cashier | cashier | cash123 |

> These credentials are intended for testing and demonstration purposes.

---

## Screenshots

Screenshots will be added as the application is developed.

The final project will include:

- Login screen.
- Administrator dashboard.
- Manage Medicines interface.
- Cashier POS interface.
- Generated bill.
- Sales report.
- Item-wise sales report.
- Low-stock report.
- Expiry report.

---

## Academic Information

This project is being developed for the **PRO732 Java Programming** module.

The project applies:

- Object-Oriented Programming.
- Java Swing and AWT.
- JDBC.
- MySQL databases.
- CRUD operations.
- Role-based access control.
- Event-driven programming.
- Transaction processing.
- Database relationships.
- Business reporting.

---

## Author

**Ashley Thabang Phahlamohlaka**

Final-Year BSc Information Technology Student  
Software Development

---

## License

This project is developed for academic purposes.
