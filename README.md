# University Vehicle Tracking System

A real-time vehicle tracking system for universities to manage and monitor their vehicle fleets.

## Overview

The University Vehicle Tracking System is a web application that provides real-time tracking and management of university vehicle fleets. The system supports multiple user roles with specific permissions and functionalities.

## Features

- Real-time vehicle location tracking with GPS coordinates
- Multi-role user authentication (Administrator, Driver, Student, Teacher, Office Admin)
- Vehicle fleet management
- Driver assignment and management
- Live dashboard with auto-refresh capabilities
- Route monitoring and fuel level tracking

## Technology Stack

**Backend**
- Spring Boot 3.2
- Java 17
- Spring Security with JWT
- H2 Database
- Spring Data JPA
- Maven

**Frontend**
- React 18
- Material-UI (MUI)
- React Router
- Axios

## System Requirements

- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6 or higher
- npm 8 or higher

## Installation

### Clone Repository
```bash
git clone https://github.com/auntor101/Multiple-University-Vehicles-Tracking-in-Real-Time-for-a-Smoother-Transportation.git
cd Multiple-University-Vehicles-Tracking-in-Real-Time-for-a-Smoother-Transportation
```

### Install Maven

**Windows:**
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add `C:\Program Files\Apache\maven\bin` to PATH environment variable
4. Verify: `mvn -version`

**macOS:**
```bash
brew install maven
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install maven
```

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Install dependencies:
```bash
mvn clean install
```

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

## Running the Application

### Start Backend Server

**Using scripts:**
```bash
# Windows
cd backend
run.bat

# Linux/macOS
cd backend
chmod +x run.sh
./run.sh
```

**Using Maven:**
```bash
cd backend
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

### Start Frontend Application

**Using scripts:**
```bash
# Windows
cd frontend
run.bat

# Linux/macOS
cd frontend
chmod +x run.sh
./run.sh
```

**Using npm:**
```bash
cd frontend
npm start
```

Frontend runs on: `http://localhost:3000`

## User Accounts

| Role | Email | Password |
|------|-------|----------|
| Administrator | admin@university.edu | SecureAdmin@123 |
| Driver 1 | driver1@university.edu | SecureDriver@123 |
| Driver 2 | driver2@university.edu | SecureDriver@124 |
| Student | student1@university.edu | SecureStudent@123 |
| Teacher | teacher1@university.edu | SecureTeacher@123 |
| Office Admin | office_admin@university.edu | SecureOffice@123 |

## API Endpoints

**Base URL:** `http://localhost:8080/api`

### Authentication
- `POST /auth/signin` - User login
- `POST /auth/signup` - User registration
- `GET /auth/me` - Get current user info

### Vehicle Management
- `GET /vehicles` - List all vehicles
- `POST /vehicles` - Create vehicle
- `PUT /vehicles/{id}` - Update vehicle
- `DELETE /vehicles/{id}` - Delete vehicle

### Location Tracking
- `GET /tracking/vehicles` - Get vehicles with location
- `POST /tracking/location/{vehicleId}` - Update vehicle location
- `POST /tracking/my-vehicle/location` - Update driver's vehicle location

### User Management
- `GET /users` - List users
- `GET /users/stats` - User statistics

## Project Structure

```
├── backend/                    # Spring Boot application
│   ├── src/main/java/         # Application source code
│   ├── src/main/resources/    # Configuration files
│   ├── pom.xml               # Maven configuration
│   ├── run.bat               # Windows startup script
│   └── run.sh                # Unix startup script
├── frontend/                  # React application
│   ├── src/                  # React source code
│   ├── public/               # Static assets
│   ├── package.json          # npm dependencies
│   ├── run.bat               # Windows startup script
│   └── run.sh                # Unix startup script
└── README.md                 # This file
```

## Configuration

The application uses default configurations suitable for local development:

- **Database**: H2 in-memory database
- **Backend Port**: 8080
- **Frontend Port**: 3000
- **Authentication**: JWT tokens
- **CORS**: Configured for localhost

## Access Points

- **Application**: http://localhost:3000
- **API**: http://localhost:8080/api
- **H2 Console**: http://localhost:8080/api/h2-console

## Common Issues

**Port already in use:**
```bash
# Windows - Kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <process-id> /F

# Linux/macOS - Kill process on port 8080
lsof -i :8080
kill -9 <process-id>
```

**Maven not found:**
- Ensure Maven is installed and added to PATH
- Restart terminal after installation

**npm install fails:**
```bash
npm cache clean --force
rm -rf node_modules
npm install
```
