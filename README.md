# Spring Security JWT Example

This module is a Spring Boot project that demonstrates how to secure a RESTful API using Spring Security with JWT (JSON Web Token) authentication. It is designed as part of a "book social network" backend, featuring secure endpoints, user authentication, authorization, and integration with PostgreSQL.

## Features

- **JWT Authentication:** Secure REST APIs using stateless JWT tokens.
- **Spring Security:** Customizable security configurations for endpoints.
- **Role-based Access:** Manage user access based on roles.
- **User registration and login:** Secure endpoints for user signup and authentication.
- **Database Integration:** Uses Spring Data JPA with PostgreSQL for persistence.
- **Email Support:** Java Mail Sender integration for notifications.
- **Validation:** Input validation using Spring Boot's validation mechanisms.
- **Thymeleaf Integration:** For rendering views if needed.
- **OpenAPI Integration:** API documentation with springdoc-openapi.

## Getting Started

### Prerequisites

- Java 23 or higher
- Maven
- PostgreSQL

### Build and Run

1. **Clone the repository:**
   ```sh
   git clone https://github.com/fanindra-k/spring-security-jwt.git
   cd spring-security-jwt/spring-security
   ```

2. **Configure Database:**
   Update your `application.properties` or `application.yml` file in `src/main/resources` with your PostgreSQL credentials.

3. **Build the project:**
   ```sh
   ./mvnw clean install
   ```

4. **Run the application:**
   ```sh
   ./mvnw spring-boot:run
   ```

### Reference Documentation

- [Spring Security Reference](https://docs.spring.io/spring-boot/3.4.5/reference/web/spring-security.html)
- [JWT API Documentation (JJWT)](https://github.com/jwtk/jjwt)
- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)

### Useful Maven Commands

- Build: `./mvnw clean install`
- Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`

## Dependencies

Key dependencies included in the `pom.xml`:
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-mail
- spring-boot-starter-thymeleaf
- springdoc-openapi
- jjwt (JWT implementation)
- PostgreSQL driver
- Lombok (for cleaner code)
- Test dependencies: spring-boot-starter-test, spring-security-test

## Guides

See these guides for help with key concepts:
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Validation](https://spring.io/guides/gs/validating-form-input/)

## License

This project inherits licensing from the parent Spring Boot project but can be customized as needed.

---

**For more details, see the [HELP.md](HELP.md) file.**
