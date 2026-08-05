# 🦷 Dental System Backend

A production-oriented backend application for managing the daily operations of a dental clinic. The system provides secure role-based access control, intelligent appointment scheduling, patient management, and cloud-based X-ray image storage.

Designed with scalability, maintainability, and clean architecture principles using Java and Spring Boot.

---

## 🚀 Features

### 🔐 Authentication & Authorization

- JWT-based Authentication (Access Token & Refresh Token)
- Role-Based Authorization using Spring Security
- Three user roles:
    - **ADMIN** – Full system access
    - **DENTIST** – Patient, appointment and X-ray management
    - **RECEPTIONIST** – Patient registration and appointment scheduling
- Secure endpoint protection
- Password encryption using BCrypt

---

### 📅 Smart Appointment Management

The appointment module includes business rules to prevent scheduling conflicts and ensure data consistency.

Features include:

- Create, update and cancel appointments
- Prevent overlapping appointments for the same dentist
- Validate appointment time ranges
- Track appointment status
- Patient appointment history

---

### 👤 Patient Management

- Patient registration
- Patient profile management
- Soft delete support
- Search and pagination
- Appointment history retrieval

---

### 🩻 X-Ray Image Management

The backend integrates with **Amazon S3** to provide reliable cloud storage for dental X-ray images.

Features:

- Upload X-ray images
- Secure cloud storage
- Image retrieval
- Metadata persistence
- Reduced server disk usage

> Images are uploaded by a separate desktop application (**X-Ray Agent**) that monitors Visiograph output folders and automatically sends newly generated images to this backend.

---

### 🗄 Database Management

- PostgreSQL
- Spring Data JPA
- Hibernate
- Liquibase database versioning
- Automatic schema migration
- Entity relationships
- Pagination & sorting

---

### ⚡ Performance

- Spring Cache support
- Optimized database queries
- Efficient entity mapping
- Reduced unnecessary database access

---

### 📖 API Documentation

- OpenAPI (Swagger UI)
- RESTful API design
- Standardized request/response models
- Validation with Jakarta Bean Validation

---

## 🛠 Tech Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Liquibase
- JWT
- Amazon S3
- Spring Cache
- Maven
- Docker
- Swagger / OpenAPI

---

## 📂 Project Architecture

```
src
├── config
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
├── service
├── specification
└── util
```

The project follows a layered architecture to keep responsibilities separated and improve maintainability.

---

## 🔒 Security

- Stateless authentication
- JWT Access & Refresh Tokens
- Role-based endpoint authorization
- Password hashing
- Request validation
- Exception handling

---

## 📦 Database Migration

Liquibase is used to manage all database schema changes.

Benefits:

- Version-controlled database
- Automated migrations
- Easy deployment
- Consistent environments

---

## ☁ Cloud Storage

Dental X-ray images are stored in **Amazon S3** instead of the application server.

Advantages:

- High availability
- Scalability
- Reduced server storage usage
- Reliable file management

---

## 🔗 Related Project

This backend works together with a standalone **X-Ray Agent** desktop application that:

- Monitors Visiograph output folders
- Detects newly generated X-ray images
- Uploads images automatically
- Writes detailed application logs
- Communicates securely with this backend

---

## Future Improvements

- Redis distributed caching
- Notification service
- Audit logging
- Email reminders
- Kubernetes support
- Multi-clinic (multi-tenant) architecture

---

## License

This project is intended for educational and portfolio purposes.