# ContactSphere

A secure contact management application built with Java and Spring Boot, providing authenticated users with features to manage contacts, user profiles, email communication, profile images, and contact data exports.

## Features

- User Registration and Login
- Authentication and Authorization using Spring Security
- OAuth 2.0 Authentication with Google and GitHub
- Email Verification
- Password Management
- Add, Update, Delete, and View Contacts
- Contact Search
- Favorite Contacts
- Pagination
- User Profile Management
- Contact Profile Image Upload using Cloudinary
- Email Sending with File Attachments
- Contact Data Export to Excel and PDF
- Feedback System

## Technologies Used

### Backend

- Java
- Spring Boot
- Spring Security
- OAuth 2.0
- Spring Data JPA
- Hibernate

### Database

- MySQL

### Integrations & Tools

- Cloudinary
- JavaMail
- Apache POI
- Maven
- Git
- GitHub

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

## Getting Started

### Prerequisites

- Java
- Maven
- MySQL
- Git

### Setup

1. Clone the repository.
2. Create a MySQL database for the application.
3. Configure the required application properties in:

```text
application.properties
```

4. Configure the required values for:

   - MySQL
   - Google OAuth 2.0
   - GitHub OAuth 2.0
   - Email service
   - Cloudinary

5. Build and run the Spring Boot application.
6. Open the application in your browser.

## Configuration

Use the provided configuration example as a reference for setting up the application locally.

Do not commit passwords, API keys, OAuth credentials, or other sensitive information to the repository.

## Author

**Arushi Yadav**
