# University Vehicle Tracking System

> **Note**: This is a development and learning project to explore Firebase-first architecture with Spring Boot and React.

A real-time vehicle tracking platform built with Firebase, Spring Boot, and React. Features live GPS tracking, push notifications, and role-based access control for university fleet management.


## Features

- Firebase Realtime Database for instant data synchronization
- Push notifications via Firebase Cloud Messaging
- Firebase Authentication for secure user management
- Firebase Storage for vehicle images and documents
- Real-time GPS location tracking with live dashboard updates
- Multi-role access control (Admin, Driver, Student, Teacher, Office Admin)
- Fleet management with vehicle assignment and status monitoring  
- Responsive Material-UI interface

## Technology Stack

**Backend**
- Spring Boot 3.2 with Java 17
- Firebase Realtime Database for primary data storage
- Firebase Authentication for user management and security
- Firebase Cloud Messaging for push notifications
- Firebase Storage for file and image storage
- Spring Security with JWT tokens for API endpoints
- WebSocket and Firebase listeners for real-time updates

**Frontend**
- React 18 with Material-UI components
- Firebase SDK for direct database connection and real-time updates
- Firebase Authentication integration
- Real-time dashboard with instant updates
- Push notification support
- Mobile-responsive design

## Quick Start

### Prerequisites
- Java 17+
- Node.js 16+  
- Maven 3.6+
- Firebase account

### Setup & Run
```bash
# Clone and navigate
git clone https://github.com/auntor101/Multiple-University-Vehicles-Tracking-in-Real-Time-for-a-Smoother-Transportation.git
cd Multiple-University-Vehicles-Tracking-in-Real-Time-for-a-Smoother-Transportation

# Setup Firebase (see Firebase Configuration section below)

# Start backend
cd backend
mvn spring-boot:run

# Start frontend (new terminal)
cd frontend  
npm install && npm start
```

Access the app at `http://localhost:3000`

## Firebase Configuration

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable these services: Authentication, Realtime Database, Cloud Messaging, Storage
3. Download `firebase-service-account.json` and place in `backend/src/main/resources/`
4. Copy your Firebase config object to `frontend/src/firebase/config.js`

## Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@university.edu | SecureAdmin@123 |
| Driver | driver1@university.edu | SecureDriver@123 |
| Student | student1@university.edu | SecureStudent@123 |

## Development Commands

```bash
# Backend development
cd backend && mvn spring-boot:run    # Start server
mvn clean install                    # Clean build
mvn test                            # Run tests

# Frontend development  
cd frontend && npm start            # Dev server
npm install package-name            # Add packages
npm run build                       # Build for production

# Useful checks
curl http://localhost:8080/api/auth/signin  # Test backend
curl http://localhost:3000          # Test frontend
```

## Architecture & Learning Goals

This project demonstrates:
- **Firebase-first architecture**: Using Firebase as primary database with Spring Boot as API layer
- **Real-time synchronization**: WebSocket + Firebase listeners for live updates
- **Role-based security**: Spring Security JWT + Firebase Auth integration
- **Modern React patterns**: Hooks, Context API, Material-UI components
- **Multi-role system**: Admin, Driver, Student, Teacher, Office Admin access levels

## Project Structure

```
├── backend/                    # Spring Boot API server
│   ├── src/main/java/com/vehicletracking/
│   │   ├── config/            # Firebase & Security config
│   │   ├── controller/        # REST endpoints
│   │   ├── service/          # Business logic
│   │   ├── model/            # Data models
│   │   └── repository/       # Data access
│   └── src/main/resources/
│       ├── application.properties
│       └── firebase-service-account.json
├── frontend/                   # React application
│   ├── src/
│   │   ├── components/       # React components
│   │   ├── context/          # State management
│   │   ├── firebase/         # Firebase config
│   │   └── hooks/            # Custom hooks
│   └── package.json
└── README.md
```

## Key Features by Role

**Admin Dashboard**
- Real-time fleet monitoring with live GPS tracking
- User management and role assignment
- Push notification broadcasting
- Analytics and usage statistics

**Driver Interface**  
- Live location sharing with automatic updates
- Trip management and passenger tracking
- Emergency alert system
- Real-time dispatch notifications

**Student/Staff Features**
- Live vehicle tracking with arrival estimates
- Push notifications for vehicle updates
- Route information and schedules
- Offline support with data synchronization

---



