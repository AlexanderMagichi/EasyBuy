# AGENTS.md

## Project map
- EasyBuy is a **Spring Boot 3.4.5 / Java 17** marketplace app with **feature-based packaging** under `src/main/java/com/teamchallenge/easybuy/`.
- **Repository Context**: [https://github.com/AlexanderMagichi/EasyBuy.git](https://github.com/AlexanderMagichi/EasyBuy.git)
- Core domains: `auth`, `user`, `shop`, `product`, `payment`, `security`, `infrastructure`, and `common`.
- Keep edits inside the owning feature; the main flow is `Controller -> Service -> Repository -> Mapper -> DTO`.

## Environments & Live Documentation
- **Remote Server Deployment**: `http://89.168.115.138:8080`
- **Interactive Swagger UI (Live)**: [http://89.168.115.138:8080/swagger-ui/index.html#/](http://89.168.115.138:8080/swagger-ui/index.html#/)
- Agents should reference the live Swagger documentation when ensuring synchronization with deployed schemas.

## Architecture you should preserve
- `EasyBuyApplication` enables **JPA auditing**, **caching**, **retry**, explicit JPA repositories, and sets the JVM timezone to `UTC`.
- `shop` is the best reference for patterns: controller/request DTOs in `shop/controller`, business rules in `shop/service`, query composition in `shop/repository/ShopSearchBuilder`, and mapping in `shop/mapper/ShopMapper`.
- Shop/product side effects are **event-driven**: services publish events, and `ShopEventListener` fan-outs async notification/analytics/audit work.
- Global API errors are centralized in `common/exception/GlobalExceptionHandler` and return a JSON map with `timestamp`, `status`, `error`, `message`, and `path`.

## Conventions that matter here
- MapStruct mappers are Spring beans and often use `unmappedTargetPolicy = ReportingPolicy.IGNORE`; partial updates use `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)`.
- Controllers often adapt request DTOs into domain DTOs with small private helper methods before calling services (`ShopController` is the template).
- Security is JWT-based with method security; public auth/Swagger paths are whitelisted in `security/config/SecurityConfig`.
- Ownership checks are explicit in service-layer guards (for example `ShopAccessGuard` + seller/admin branching in `ShopService`).

## Local workflows
- Standard build/test: `./mvnw clean test`.
- Run locally: `./mvnw spring-boot:run`.
- Dockerized local stack: `docker compose up -d --build` (PostgreSQL `5432`, Redis `6379`, app `8081`, pgAdmin `8080`).
- Swagger health check script: `./check_swagger.ps1` validates `/swagger-ui.html` and `/v3/api-docs`.

## Configuration notes
- Runtime config lives in `src/main/resources/application*.properties`.
- `application-docker.properties` points to `postgres`/`redis` service names and disables Vault for compose-based runs.
- Tests use `src/test/resources/application-test.properties` with H2, `spring.cache.type=simple`, Vault off, and stubbed Cloudinary/Mail/Stripe values.

## Testing guidance
- Reuse `@IntegrationTest` for full-context tests; it sets `@SpringBootTest` plus `@ActiveProfiles("test")`.
- Test fixtures and assertions usually assume the H2 test profile, not the Docker profile.
- When changing events, security, mapping, or exceptions, check the related feature tests under `src/test/java/com/teamchallenge/easybuy/`.

## Before you change code
- Check whether the change affects event listeners, permissions, or search predicates in another package.
- Update both request/response DTOs and the MapStruct mapper when adding or renaming shop/product fields.
- Add new domain exceptions to `GlobalExceptionHandler` so failures stay consistent.