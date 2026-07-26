# EasyBuy Marketplace API

## 📖 Overview
EasyBuy is a robust, multi-vendor e-commerce marketplace backend designed to handle complex business logic, from user authentication and shopping cart management to store onboarding and analytics. The RESTful API is built to scale, providing secure, documented, and efficient endpoints for all marketplace operations.

## 🛠 Tech Stack
*   **Core**: Java, Spring Boot
*   **Database & ORM**: PostgreSQL, Hibernate / Spring Data JPA
*   **API Documentation**: Swagger / OpenAPI 3.1.0
*   **Security**: JWT-based Authentication
*   **Integrations**: Stripe (for shop billing and onboarding)

## 🏗 Project Structure
The application architecture follows a modular and scalable design. The package structure is logically grouped by domain and technical responsibility (e.g., controllers, services, repositories, entities, DTOs, and security configurations). This separation of concerns ensures high maintainability and adherence to clean architecture principles.

## 🚀 Core Modules & Features

### 👤 User & Authentication
*   **Authentication**: Secure login, registration, JWT token generation, refresh tokens, and email confirmation workflows.
*   **User Management**: Profile creation, avatar uploads, and complete password reset flows.
*   **Delivery Addresses**: CRUD operations for managing user delivery address profiles, including setting a primary/default address.

### 🏪 Shop Management
*   **Store Profiles**: Comprehensive endpoints for managing shops, including nested contact info, tax and legal information, and SEO settings.
*   **Analytics & Optimization**: Features for tracking shop performance and dedicated endpoints for dead-shop optimization.
*   **Moderation**: API for maintaining and reversing shop moderation history records.
*   **Team Memberships**: Delegation of store-scoped roles (such as `MANAGER` or `CONTENT_MANAGER`) with the ability to suspend, reactivate, or revoke access.
*   **Billing**: Integration for managing shop Stripe onboarding and payouts.

### 📦 Product Catalog
*   **Categories**: Hierarchical category management.
*   **Attributes**: Creation of category-specific attributes (`STRING`, `NUMBER`, `BOOLEAN`, `ENUM`) and mapping values to individual goods.
*   **Goods Management**: API for managing products, supporting extensive filtering by price, stock, ratings, and status (`ACTIVE`, `INACTIVE`, `ARCHIVED`).
*   **Media**: Endpoints for uploading, updating, and deleting goods images.

### 🛒 Shopping Experience
*   **Shopping Cart**: Adding new items, updating product quantities, and removing items from the cart.
*   **Orders**: Order creation and lifecycle tracking (statuses include `CREATED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`, `RETURNED`).
*   **Favorites**: Operations for users to manage their favorite products wishlist.

## 📚 API Documentation
The API endpoints and schemas are fully documented using OpenAPI 3.1.0.
*   **Primary Server URL**: `http://89.168.115.138:8080`
*   **Interactive Swagger UI**: Explore and test the API directly via our deployed documentation:
    👉 [EasyBuy Swagger UI](http://89.168.115.138:8080/swagger-ui/index.html#/)

## ⚙️ Getting Started

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/AlexanderMagichi/EasyBuy.git](https://github.com/AlexanderMagichi/EasyBuy.git)
    cd EasyBuy
    ```
2.  **Configure the Database:**
    Ensure your PostgreSQL instance is running. Update the `application.yml` or `.env` file with your specific database credentials and environment variables.
3.  **Run the Application:**
    Build and run the Spring Boot application using your IDE or terminal wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```

## 👨‍💻 Author
**Alexander Mogilnitsky**
*   [LinkedIn Profile](https://www.linkedin.com/in/%D0%BE%D0%BB%D0%B5%D0%BA%D1%81%D0%B0%D0%BD%D0%B4%D1%80-%D0%BC%D0%BE%D0%B3%D0%B8%D0%BB%D1%8C%D0%BD%D0%B8%D1%86%D1%8C%D0%BA%D0%B8%D0%B9-7808a3272/)
*   [GitHub](https://github.com/AlexanderMagichi)