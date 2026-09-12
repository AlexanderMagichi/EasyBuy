# EasyBuy 🛒

A modern, feature-rich **Java 17 + Spring Boot 3.4.5** marketplace platform for connecting sellers and customers. Built with enterprise-grade security, scalability, and clean architecture principles.

> **Live Demo**: [http://89.168.115.138:8080](http://89.168.115.138:8080)  
> **Interactive API Docs**: [Swagger UI](http://89.168.115.138:8080/swagger-ui/index.html#/)

---

## 🌟 Features

### Core Marketplace Functionality
- **Dual-Role Authentication**: Seamless registration for customers and sellers with role-based access control
- **Shop Management**: Create and manage multiple shops with rich profiles and media
- **Product Catalog**: Add, organize, and manage products with detailed information, images, and inventory
- **Smart Search & Filtering**: Powerful product discovery with filters by category, price range, rating, and more
- **Shopping Experience**: Add-to-cart, order management, and complete checkout flow
- **Review & Rating System**: Customer reviews and rating system for products and sellers

### Security & Performance
- **JWT Authentication** with token blacklist for secure logout
- **Rate Limiting** to protect API endpoints from abuse
- **Account Lockout Policy** after failed login attempts
- **Method-Level Security** with role-based access control (CUSTOMER, SELLER, MANAGER, ADMIN)
- **Response Caching** with Redis for improved performance
- **Automatic Retry Logic** for transient failures

### Business Operations
- **Payment Integration**: Stripe integration for secure payment processing
- **Email Notifications**: Customer notifications for orders, confirmations, and account updates
- **Media Management**: Cloudinary integration for efficient image uploads and delivery
- **Event-Driven Architecture**: Asynchronous processing of business events (orders, shop updates, etc.)
- **Database Auditing**: Automatic tracking of created/modified timestamps on all entities

### Developer Experience
- **OpenAPI/Swagger Documentation**: Auto-generated, interactive API documentation
- **Comprehensive Testing**: Unit and integration tests with H2 in-memory database
- **Docker Support**: Containerized deployment with docker-compose for local development
- **Database Migrations**: Flyway-based schema versioning and management

---

## 🏗️ Architecture

### Technology Stack
| Component | Technology |
|-----------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4.5 |
| **Database** | PostgreSQL 15+ (production), H2 (testing) |
| **Cache** | Redis 7+ |
| **API Documentation** | OpenAPI 3.0 / Swagger UI |
| **ORM** | Spring Data JPA (Hibernate) |
| **Build Tool** | Maven 3.9+ |
| **Container** | Docker & Docker Compose |
| **Mapping** | MapStruct for DTOs |

### Project Structure
```
src/main/java/com/teamchallenge/easybuy/
├── auth/                    # Legacy auth (deprecated)
├── user/                    # User profiles & avatars
├── shop/                    # Shop management & search
├── product/                 # Product catalog
├── payment/                 # Payment processing
├── security/                # JWT auth, rate limiting, account lockout
├── infrastructure/          # Cross-cutting concerns
├── common/                  # Shared utilities & exceptions
└── EasyBuyApplication.java  # Main application class

src/main/resources/
├── application.properties           # Default config
├── application-docker.properties    # Docker environment config
├── openapi.yaml                     # API specification
└── db/migration/                    # Flyway migrations
```

### Key Patterns

**Feature-Based Packaging**: Each domain is organized as a feature with its own controllers, services, repositories, and DTOs.

```
shop/
├── controller/              # REST endpoints
├── dto/                     # Request/Response models
├── entity/                  # JPA entities
├── repository/              # Data access layer
├── service/                 # Business logic
└── mapper/                  # DTO ↔ Entity mapping
```

**Event-Driven Side Effects**: Shop and product changes publish events that are handled asynchronously:
```java
// Service publishes event
applicationEventPublisher.publishEvent(new ShopCreatedEvent(shop));

// Listener handles it asynchronously
@EventListener
@Async
public void onShopCreated(ShopCreatedEvent event) { ... }
```

**Centralized Exception Handling**: All domain exceptions are converted to standardized JSON responses:
```json
{
  "timestamp": "2025-01-13T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid role: SUPERUSER",
  "path": "/api/v1/auth/register"
}
```

**Role-Based Access Control**: Method security with JWT tokens:
```java
@PostMapping("/shop")
@PreAuthorize("hasRole('SELLER')")
public ResponseEntity<ShopResponse> createShop(...) { ... }
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** (Amazon Corretto, Eclipse Temurin, or Oracle JDK)
- **Maven 3.9+**
- **Docker & Docker Compose** (for local stack)
- **PostgreSQL 15+** (production)
- **Redis 7+** (caching)

### Local Development (Docker Compose)

1. **Clone the repository**
   ```bash
   git clone https://github.com/AlexanderMagichi/EasyBuy.git
   cd EasyBuy
   ```

2. **Start the full stack**
   ```bash
   docker compose up -d --build
   ```
   This starts:
   - PostgreSQL on `5432`
   - Redis on `6379`
   - EasyBuy API on `8081`
   - pgAdmin on `8080` (database UI)

3. **Access the application**
   - API: http://localhost:8081
   - Swagger UI: http://localhost:8081/swagger-ui/index.html
   - pgAdmin: http://localhost:8080 (admin@admin.com / admin)

4. **View logs**
   ```bash
   docker compose logs -f easybuy-app
   ```

5. **Stop the stack**
   ```bash
   docker compose down
   ```

### Local Development (Maven)

1. **Clone and navigate to repository**
   ```bash
   git clone https://github.com/AlexanderMagichi/EasyBuy.git
   cd EasyBuy
   ```

2. **Build the project**
   ```bash
   ./mvnw clean compile
   ```

3. **Run tests**
   ```bash
   ./mvnw clean test
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   - API will be available at http://localhost:8081
   - Swagger UI at http://localhost:8081/swagger-ui/index.html

---

## 📚 API Documentation

The API is fully documented with OpenAPI 3.0 specification and interactive Swagger UI.

### Key Endpoints

#### Authentication (`/api/v1/auth`)
- `POST /register` - Register new customer or seller (requires `role` field: CUSTOMER or SELLER)
- `POST /authenticate` - Login with email and password
- `POST /refresh` - Refresh JWT access token
- `POST /logout` - Logout and blacklist token
- `POST /confirm-email` - Confirm email address
- `POST /request-password-reset` - Request password reset
- `POST /reset-password` - Reset password with token

#### User Management (`/api/v1/users`)
- `GET /profile` - Get current user profile
- `PUT /profile` - Update user profile
- `PUT /avatar` - Upload/update user avatar
- `DELETE /avatar` - Remove avatar

#### Shop Management (`/api/v1/shops`)
- `GET` - List all shops with filters
- `GET /{id}` - Get shop details
- `POST` - Create new shop (seller only)
- `PUT /{id}` - Update shop (owner only)
- `DELETE /{id}` - Delete shop (owner only)

#### Products (`/api/v1/products`)
- `GET` - List products with advanced filtering
- `GET /{id}` - Get product details
- `POST` - Add product (seller only)
- `PUT /{id}` - Update product (seller only)
- `DELETE /{id}` - Delete product (seller only)

#### Orders & Payments (`/api/v1/orders`, `/api/v1/payments`)
- Full order lifecycle management
- Stripe payment processing
- Order history and tracking

**Interactive documentation**: Visit http://89.168.115.138:8080/swagger-ui/index.html# to explore all endpoints.

---

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the project root:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/easybuy
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
JWT_SECRET_KEY=your-secret-key-here-min-32-chars
CLOUDINARY_URL=cloudinary://key:secret@cloud_name
STRIPE_API_KEY=sk_test_...
MAIL_SMTP_HOST=smtp.gmail.com
MAIL_SMTP_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### Application Properties
- **Default**: `src/main/resources/application.properties`
- **Docker**: `src/main/resources/application-docker.properties`
- **Testing**: `src/test/resources/application-test.properties`

---

## 🧪 Testing

### Run All Tests
```bash
./mvnw clean test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=ShopControllerTest
```

### Run Tests with Coverage
```bash
./mvnw clean test jacoco:report
# Coverage report: target/site/jacoco/index.html
```

### Integration Tests
Integration tests use `@IntegrationTest` annotation with full Spring context and H2 database:
```java
@IntegrationTest
class ShopServiceTest {
    @Autowired
    private ShopService shopService;
    
    @Test
    void testCreateShop() { ... }
}
```

---

## 🏭 Building & Deployment

### Build for Production
```bash
./mvnw clean package -DskipTests -Pproduction
```

### Docker Build
```bash
docker build -t easybuy:latest .
```

### Run Docker Container
```bash
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/easybuy \
  -e SPRING_REDIS_HOST=redis \
  easybuy:latest
```

### Static Analysis
```bash
./mvnw spotbugs:check           # Find potential bugs
./mvnw pmd:check                # Code quality analysis
./mvnw checkstyle:check         # Code style verification
```

---

## 🔐 Security

### JWT Authentication Flow
1. User registers or logs in with email and password
2. Server validates credentials and issues JWT token
3. Client includes token in `Authorization: Bearer <token>` header
4. Server validates token and checks JWT blacklist before processing request
5. On logout, token is added to blacklist

### Rate Limiting
- Protected endpoints are rate-limited to prevent abuse
- Limits vary by endpoint (e.g., 5 login attempts per minute, 30 requests per minute for API)
- Rate limit headers are included in responses

### Account Lockout
- After 5 failed login attempts, account is locked for 15 minutes
- User receives email notification of lockout attempt
- Automatic unlock after timeout or manual unlock by admin

### Access Control
```
CUSTOMER  - Place orders, manage profile, leave reviews
SELLER    - Manage shops and products, process orders
MANAGER   - Manage multiple sellers (limited)
ADMIN     - Full platform access
```

---

## 📝 Development Guidelines

### Adding New Features

1. **Create feature package** under `src/main/java/com/teamchallenge/easybuy/`
2. **Implement layers** in order: Entity → Repository → Service → Controller → DTO
3. **Add OpenAPI annotations** to controller methods for Swagger documentation
4. **Write unit & integration tests** in `src/test/java/` with same package structure
5. **Update database schema** using Flyway migrations in `src/main/resources/db/migration/`
6. **Handle exceptions** in `GlobalExceptionHandler`

### Code Style
- Follow Google Java Style Guide
- Use MapStruct for DTO mapping (avoid manual getters/setters)
- Keep business logic in services, not controllers
- Use `@Transactional` for operations that modify data
- Add meaningful JavaDoc comments for public APIs

### Commit Messages
```
feat: Add user profile update endpoint
fix: Correct JWT token validation in security filter
docs: Update API documentation for shops
refactor: Extract common validation logic to utility
test: Add tests for shop search filter
```

---

## 📋 Recent Changes

### v2.0.0 - Auth Consolidation
- ✅ Removed duplicate authentication implementation
- ✅ Consolidated to unified security package with JWT blacklist
- ✅ Added role-based registration (CUSTOMER/SELLER)
- ✅ Enhanced rate limiting and account lockout
- ✅ Improved error handling and validation
- ⚠️ **Breaking Change**: Registration now requires `role` field

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/your-feature-name`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/your-feature-name`
5. Submit Pull Request

### Before Submitting PR
- [ ] Tests pass: `./mvnw clean test`
- [ ] No compilation warnings
- [ ] Code follows style guidelines
- [ ] Database migrations are added (if schema changed)
- [ ] OpenAPI documentation is updated
- [ ] Commit messages are descriptive

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Find process using port 8081
lsof -i :8081
# Kill process
kill -9 <PID>
```

### Docker Compose Issues
```bash
# Restart services
docker compose restart

# View logs for specific service
docker compose logs easybuy-app

# Clean up and rebuild
docker compose down -v
docker compose up -d --build
```

### Database Connection Error
- Ensure PostgreSQL is running: `docker compose ps`
- Check credentials in `application.properties`
- Verify network connectivity to database

### Maven Build Failures
```bash
# Clear Maven cache and rebuild
rm -rf ~/.m2/repository
./mvnw clean install
```

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📞 Support & Contact

- **Issues**: [GitHub Issues](https://github.com/AlexanderMagichi/EasyBuy/issues)
- **Discussions**: [GitHub Discussions](https://github.com/AlexanderMagichi/EasyBuy/discussions)
- **Email**: contact@easybuy.local

---

## 🙌 Acknowledgments

- Spring Boot & Spring Framework team
- PostgreSQL and Redis communities
- MapStruct for excellent DTO mapping
- OpenAPI/Swagger for API documentation

---

**Happy coding! 🚀**