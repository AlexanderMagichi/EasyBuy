# EasyBuy

EasyBuy is a Spring Boot marketplace project built with a feature-based architecture. It includes JWT security, Redis/Caffeine caching, 
PostgreSQL persistence, Stripe payments, Cloudinary media handling, email notifications, and full Swagger documentation.

**Architecture migration status:** ✅ completed on 2026-04-14
**You can check Swagger: http://89.168.115.138:8080/swagger-ui.html

## Project overview

The repository currently contains a marketplace domain with the following features:

- `auth` — registration, login, JWT access/refresh tokens, password recovery
- `user` — user domain and profile-related operations
- `shop` — shops, shop profile management, billing/contact/tax/SEO settings, analytics, moderation history
- `product` — goods, categories, and product images
- `payment` — Stripe integration and onboarding flows
- `infrastructure` — mail, Cloudinary, and supporting services
- `security` — JWT filtering, access control, and security configuration
- `common` — shared DTOs, mappers, and configuration helpers

## Technology stack

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Data JPA / Hibernate**
- **Spring Security + JWT**
- **Spring Cache**, **Redis**, and **Caffeine**
- **MapStruct**
- **Lombok**
- **SpringDoc OpenAPI / Swagger UI**
- **PostgreSQL** for production data
- **H2** for tests
- **Spring Mail + FreeMarker**
- **Cloudinary** for file/media storage
- **Stripe** for billing
- **Spring Retry** and **Actuator**
- **Spring Cloud Vault** support
- **Docker / Docker Compose** for local infrastructure

## Architecture

EasyBuy uses **feature-based packaging** instead of a classic layered package structure. Each feature keeps its own controllers, services, repositories, DTOs, mappers, and domain objects together.

Main request flow:

`Controller -> Service -> Repository -> Mapper -> DTO`

### Main package structure

```text
src/main/java/com/teamchallenge/easybuy/
├── common/
├── security/
├── user/
├── auth/
├── shop/
├── product/
├── infrastructure/
└── payment/
```

### Architectural notes

- JPA repositories are enabled explicitly in `EasyBuyApplication`
- `@EnableJpaAuditing`, `@EnableCaching`, and `@EnableRetry` are active
- dynamic filtering uses JPA Specifications where needed
- shop and product domains use domain events and async side effects
- access control is centralized around JWT and role/ownership checks

## Implemented features

### Authentication
- sign up / sign in flows
- JWT access and refresh tokens
- password recovery
- public auth endpoints

### Users
- user entity and profile-related operations
- integration with auth and security flows

### Shops
- full CRUD for shops
- role-based access for `BUYER`, `SELLER`, `MANAGER`, and `ADMIN`
- ownership checks for own vs foreign shop access
- separate sub-resources for:
  - billing info
  - contact info
  - tax info
  - SEO settings
  - analytics
  - moderation history
- domain events and resilient async listeners

### Products
- goods management
- categories
- product image handling

### Payments
- Stripe onboarding integration
- webhook/service layer for billing scenarios

### Infrastructure
- email notifications with Spring Mail + FreeMarker
- Cloudinary integration for media uploads
- cache and retry support for external integrations

## Local run

### With Docker Compose

```bash
docker compose up -d --build
```

After startup:

- application: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- pgAdmin: `http://localhost:8080`

### With Maven

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## Configuration files

- `src/main/resources/application.properties`
- `src/main/resources/application-docker.properties`
- `src/main/resources/application-local.properties`

