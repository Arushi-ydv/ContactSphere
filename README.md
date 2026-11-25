# ContactSphere – Contact Management System

ContactSphere is a Java-based Contact Management Application developed using Spring Boot. It provides secure user authentication and allows users to manage their personal and professional contacts efficiently.

## Features

- **User Authentication:** User registration, login, and secure authentication.
- **Email Verification:** Email verification during user registration.
- **Contact Management:** Add, view, update, and delete contacts.
- **Email Integration:** Send emails with file attachments.
- **Pagination:** Display contacts in manageable pages.
- **Profile Management:** View and update user profile information.
- **Feedback System:** Submit feedback through the application.
- **Social Login:** Authentication using Google and GitHub.

## Technology Stack

| Category | Technologies |
|---|---|
| Programming Language | Java 21 |
| Backend Framework | Spring Boot |
| Security | Spring Security |
| ORM | Hibernate |
| Data Access | Spring Data JPA |
| Database | MySQL |
| Build Tool | Maven |
| Version Control | Git, GitHub |

## Architecture

ContactSphere follows a layered architecture that separates request handling, business logic, and data access.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL Database
```

### Main Components

- **Controllers** – Handle application requests and endpoints.
- **Services** – Implement business logic and application operations.
- **Repositories** – Handle database access using Spring Data JPA.
- **Entities** – Represent application data and database records.
- **Forms** – Handle request data and validation.
- **Configuration** – Contains application and security configuration.
- **Helpers** – Contains supporting utility components.

## Security

Spring Security is used to implement authentication and authorization.

OAuth 2.0 integration allows users to authenticate using:

- Google
- GitHub

Sensitive credentials and environment-specific configuration values are not committed to the repository.

## External Integrations

- **Cloudinary** – Used for contact profile image storage.
- **JavaMail** – Used for sending emails, including emails with file attachments.
- **Apache POI** – Used for exporting contact data to Excel.
- **PDF Export** – Used for generating contact data in PDF format.

## Project Structure

```text
src/main/java/com/contactsphere
│
├── config
├── controllers
├── entities
├── repositories
├── services
│   └── impl
├── forms
├── helpers
└── ContactsphereApplication.java
```

## Application Setup

### Prerequisites

- Java JDK 21
- MySQL

### Steps to Run

1. Clone the repository:

   ```bash
   git clone https://github.com/Arushi-ydv/ContactSphere
   ```

2. Navigate to the project directory:

   ```bash
   cd ContactSphere
   ```

3. Create a MySQL database named `contactsphere`.

4. Configure the required database, email, and OAuth credentials using environment variables or your local `application.properties` file.

5. Run the application:

   **On Windows PowerShell:**

   ```powershell
   .\mvnw spring-boot:run
   ```

   **On Linux/macOS:**

   ```bash
   ./mvnw spring-boot:run
   ```

6. Open the application in your browser:

   ```text
   http://localhost:8081
   ```

```text
application.properties
```

- Spring Security for application authentication and authorization.
- Sensitive credentials are managed through environment variables.
- OAuth2-based login integration with Google and GitHub.

**Note:** Do not commit passwords, API credentials, or other sensitive configuration values to the repository.

## Author

**Arushi Yadav**
