# ClinicFlow – Clinic Queue Management System

A full-stack clinic management application designed to simplify patient registration, doctor management, token generation, queue handling and patient consultations.

ClinicFlow provides a centralized platform for clinic owners, receptionists and doctors to manage daily outpatient department (OPD) operations.

## Overview

Managing patient queues and consultations manually can lead to long waiting times, inefficient coordination and difficulty maintaining patient records.

ClinicFlow aims to streamline these operations through a role-based web application built with React, Spring Boot and PostgreSQL.

The system connects a responsive frontend with RESTful backend APIs and a relational database to manage clinic operations.

## Key Features

### Authentication and Authorization
- User login using clinic ID and mobile number.
- JWT-based authentication.
- Role-based access control.
- Clinic-level access validation.
- Protected frontend routes.

### Patient Management
- Register new patients.
- Search patients using mobile numbers.
- View registered patient details.
- Maintain patient information.

### Doctor Management
- Register doctors.
- View doctor listings.
- Update doctor information.
- Activate or deactivate doctor accounts.

### Queue and Token Management
- Generate patient tokens.
- Manage doctor-wise queues.
- Call the next waiting patient.
- Update token statuses.
- View clinic-wide queue information.
- Track queue statistics and currently called patients.

### Consultation Management
- Start patient consultations.
- Record symptoms and diagnosis.
- Maintain prescriptions and medical advice.
- Complete consultations.
- Retrieve patient visit history.

### Clinic Settings
- Configure online booking availability.
- Set daily token limits.
- Configure OPD opening and closing times.

### Waiting Room
- Display queue information and currently called patients.
- Automatically refresh queue information.

## Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React 19, TypeScript, Vite |
| Styling | CSS, Tailwind CSS |
| Backend | Java 21, Spring Boot |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| API Communication | REST APIs, Axios |
| Build Tools | Maven, npm |
| Development Tools | VS Code, Postman, Git, GitHub |

## System Architecture

```text
              React Frontend
              (TypeScript)
                    |
                    | Axios / REST API
                    | JWT Authentication
                    |
              Spring Boot Backend
                    |
          -------------------------
          |           |           |
      Controllers  Services   Security
          |           |           |
          -------------------------
                    |
              Repositories
                    |
                PostgreSQL
```

## Project Structure

```text
ClinicFlow/
│
├── clinic-backend/
│   ├── src/main/java/com/clinic/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   │
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.tsx
│   │   └── types.ts
│   │
│   └── package.json
│
├── database/
│   └── schema.sql
│
└── README.md
```

## Database

ClinicFlow uses PostgreSQL for persistent data storage.

The database schema includes the following primary tables:

- `clinics`
- `users`
- `doctors`
- `patients`
- `clinic_settings`
- `tokens`
- `visits`
- `visit_notes`

The database schema is available in:

`database/schema.sql`

## REST API Modules

The backend exposes RESTful endpoints under `/api`.

| Module | Base Endpoint | Purpose |
|---|---|---|
| Authentication | `/api/auth` | User login |
| Clinics | `/api/clinics` | Clinic management |
| Patients | `/api/patients` | Patient registration and search |
| Doctors | `/api/doctors` | Doctor management |
| Tokens | `/api/tokens` | Token and queue management |
| Visits | `/api/visits` | Consultation management |
| Visit Notes | `/api/visit-notes` | Consultation notes |
| Clinic Settings | `/api/clinic-settings` | Clinic configuration |
| Health | `/api/health` | Backend health check |

## Installation and Setup

### Prerequisites

Install the following:

- Java 21
- Maven (or use the included Maven wrapper)
- Node.js and npm
- PostgreSQL
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/ManasBhat3166/ClinicFlow.git
cd ClinicFlow
```

### 2. Set Up PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE clinic_db;
```

Execute the database schema:

```bash
psql -U postgres -d clinic_db -f database/schema.sql
```

Alternatively, execute `database/schema.sql` using your preferred PostgreSQL client.

### 3. Configure the Backend

Navigate to:

`clinic-backend/src/main/resources/`

Create your local `application.properties` using `application-example.properties` as a reference.

Configure your own database credentials and JWT settings using environment variables:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=none

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

Set the required environment variables on your machine. Use a strong JWT secret and never commit actual credentials.

### 4. Run the Backend

From the project root:

```bash
cd clinic-backend
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

Backend runs at:

`http://localhost:8080`

Health check:

`http://localhost:8080/api/health`

### 5. Configure the Frontend

Open another terminal:

```bash
cd frontend
```

Create a `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Frontend runs at:

`http://localhost:5173`

## User Roles

The application currently implements three primary roles:

| Role | Main Responsibilities |
|---|---|
| OWNER | Clinic operations, doctor management and settings |
| RECEPTION | Patient registration and queue operations |
| DOCTOR | Patient queue and consultation workflows |

Access to backend resources is protected through authentication, role checks and clinic-level authorization.

## Security

Security mechanisms implemented include:

- JWT authentication.
- Spring Security integration.
- Role-based endpoint authorization.
- Clinic-level resource access checks.
- Protected frontend routes.
- Environment-based configuration for sensitive credentials.

**Security note:** Never commit `.env`, database passwords, JWT secrets, private keys or production credentials.

## Current Development Status

Core frontend and backend modules have been implemented and integrated.

The following areas are undergoing continued integration testing and refinement:

- Complete doctor consultation workflow.
- Queue operation edge cases.
- Role-based authorization testing.
- Waiting-room display refinements.
- Additional validation and error handling.
- Deployment and production configuration.

## Future Enhancements

- Online patient appointment booking.
- Live queue updates.
- SMS and WhatsApp notifications.
- Advanced clinic analytics and reports.
- Multi-clinic management.
- Production deployment and monitoring.

## Author

**Manas Bhat**

GitHub: [@ManasBhat3166](https://github.com/ManasBhat3166)

Project: [ClinicFlow](https://github.com/ManasBhat3166/ClinicFlow)

---

*ClinicFlow – Simplifying Clinic Operations, One Queue at a Time.*