# EcommerceWebApp

An e-commerce web application built with **Java 17, Spring Boot, Spring Security, Spring Data JPA and MySQL**,
with server-rendered **Thymeleaf** pages and **DWR** for AJAX calls from the browser to Java services.

Users register and log in, browse and search products, and manage a cart. The admin side manages the whole
catalogue — products, brands, categories and sub-categories — including product images and bulk product upload
from an Excel file.

## Features

**Accounts & security**
- Registration with duplicate-username check; passwords hashed with BCrypt (strength 12).
- Form login (`/login`) and logout via Spring Security; every page except login, register and static assets
  requires an authenticated session.

**Catalogue**
- Products: add, edit, view, delete, with product images (multipart upload, up to 5 MB per file).
- Browse products by category, open a product inside its category, and keyword search.
- Categories, sub-categories and brands: create, edit, list and delete. A brand is linked to the sub-categories
  it sells in, and the forms load dependent dropdowns (brand → sub-categories → category) over DWR.

**Bulk product upload**
- Upload an Excel file (`.xlsx`, read with Apache POI). The header row is validated against the expected
  columns, every row is checked, and the page reports each error before anything is saved.
- A sample template is provided at `src/main/resources/static/uploads/ProductUpload.xlsx`.

**Cart**
- Add products with a quantity, view the cart, and remove items.

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 2.7, Spring MVC, Spring Security, Spring Data JPA (Hibernate), Bean Validation |
| Database | MySQL 8 |
| Views | Thymeleaf, HTML, CSS, JavaScript |
| AJAX | DWR 3 (Direct Web Remoting) |
| Excel | Apache POI 5 |
| Build | Maven (wrapper included) |

## Project structure

```
src/main/java/com/eCommerce/
├── Configuration/   Spring Security, DWR setup, custom physical naming strategy
├── Controller/      AuthController, AdminController (catalogue), CartController
├── Entity/          Users, Product, ProductCategory, ProductSubCategory, Brand, ImageDetails, CartItem
├── Repository/      Spring Data JPA repositories
└── Service/         UserService, ProductService (catalogue + Excel upload), CartService
src/main/resources/
├── templates/       Thymeleaf pages (login, register, products, categories, brands, cart, upload …)
├── static/css, js/  Page styles and scripts (DWR calls)
└── application.properties
```

## Main pages

| URL | Page |
|---|---|
| `/register`, `/login`, `/logout` | Account |
| `/admin/products` | Product list (home after login) |
| `/admin/products/add`, `/admin/products/edit/{id}` | Add / edit product |
| `/admin/products/{id}` | Product details |
| `/admin/products/category/{categoryId}` | Products in a category |
| `/admin/products/search?keyword=…` | Search |
| `/admin/products/uploadProduct` | Bulk upload from Excel |
| `/admin/products/showAllProductCategories` | Categories |
| `/admin/products/showAllProductSubCategories` | Sub-categories |
| `/admin/products/showAllBrands` | Brands |
| `/cart/view` | Cart |

## Getting started

**Prerequisites:** JDK 17 and MySQL 8 (Maven is not needed — the wrapper downloads it).

1. Create the database:
   ```sql
   CREATE DATABASE springBoot;
   ```
2. Check the connection in `src/main/resources/application.properties` (default: `localhost:3307`, user `root`)
   and supply the password through an environment variable:
   ```bash
   export DB_PASSWORD=your_mysql_password
   ```
3. Run the app:
   ```bash
   ./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
   ```
4. Open **http://localhost:8081/register**, create an account, then log in.

Tables are created automatically on first start (`spring.jpa.hibernate.ddl-auto=update`).

## Author

**Saurav Kumar** — Java Backend Engineer
[GitHub](https://github.com/sauravk1508) · [LinkedIn](https://www.linkedin.com/in/saurav-kumar-02164b247)
