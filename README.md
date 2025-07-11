# University Vehicle Tracking System

A modern, **Firebase-powered** real-time vehicle tracking platform for university fleets with live updates, push notifications, and comprehensive management capabilities.

## ✨ Features

- **🔥 Firebase Realtime Database** - Instant data synchronization across all devices
- **📱 Push Notifications** - Firebase Cloud Messaging for alerts and updates
- **🔐 Firebase Authentication** - Secure user management with multiple providers
- **📸 Firebase Storage** - Vehicle images and document management
- **⚡ Real-time Tracking** - Live GPS location updates with instant dashboard refresh
- **👥 Multi-role Access** - Admin, Driver, Student, Teacher, and Office Admin roles
- **🚗 Fleet Management** - Vehicle assignment, status monitoring, and maintenance tracking
- **📱 Responsive UI** - Modern Material-UI interface with mobile-first design

## 🛠 Tech Stack

**Backend**
- Spring Boot 3.2 + Java 17
- **Firebase Realtime Database** - Primary data storage
- **Firebase Authentication** - User management & security
- **Firebase Cloud Messaging** - Push notifications
- **Firebase Storage** - File and image storage
- Spring Security + JWT (for API endpoints)
- WebSocket + Firebase listeners for real-time updates

**Frontend**
- React 18 + Material-UI
- **Firebase SDK** - Direct database connection for real-time updates
- Firebase Authentication integration
- Real-time dashboard with instant updates
- Push notification support
- Responsive design

## 📋 Prerequisites

- **Java 17+**
- **Node.js 16+**
- **Maven 3.6+**
- **Firebase Project** - [Create at Firebase Console](https://console.firebase.google.com)

## 🚀 Quick Start

### 1. Firebase Setup
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable **Authentication**, **Realtime Database**, **Cloud Messaging**, and **Storage**
3. Download `firebase-service-account.json` for backend
4. Get Firebase config object for frontend

### 2. Clone Repository
```bash
git clone https://github.com/auntor101/Multiple-University-Vehicles-Tracking-in-Real-Time-for-a-Smoother-Transportation.git
cd Multiple-University-Vehicles-Tracking-in-Real-Time-for-a-Smoother-Transportation
```

### 3. Backend Setup
```bash
cd backend
# Place firebase-service-account.json in src/main/resources/
mvn clean install
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`

### 4. Frontend Setup
```bash
cd frontend
npm install
# Update src/firebase/config.js with your Firebase config
npm start
```
Frontend runs on `http://localhost:3000`

## 🔐 Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@university.edu | SecureAdmin@123 |
| **Driver** | driver1@university.edu | SecureDriver@123 |
| **Student** | student1@university.edu | SecureStudent@123 |

## 📡 API Endpoints

**Base URL:** `http://localhost:8080/api`

### Core Endpoints
- `POST /auth/signin` - User authentication
- `GET /vehicles` - List all vehicles (Firebase proxy)
- `GET /tracking/vehicles` - Get vehicles with live location
- `POST /tracking/my-vehicle/location` - Update driver location
- `POST /notifications/send` - Send push notifications

## 🔥 Firebase Features

### **Realtime Database Structure**
```json
{
  "users": {
    "userId": {
      "email": "user@university.edu",
      "role": "DRIVER",
      "profile": { ... },
      "isActive": true
    }
  },
  "vehicles": {
    "vehicleId": {
      "vehicleNumber": "UN-001",
      "location": {
        "latitude": 23.8103,
        "longitude": 90.4125,
        "timestamp": 1234567890
      },
      "status": "ACTIVE",
      "driver": "driverId"
    }
  },
  "notifications": {
    "notificationId": {
      "title": "Vehicle Update",
      "message": "Vehicle UN-001 arrived",
      "timestamp": 1234567890
    }
  }
}
```

### **Real-time Features**
- 🔄 **Live Location Updates** - Vehicles update every 5 seconds
- 📱 **Instant Notifications** - Push alerts for status changes
- 👥 **Multi-device Sync** - Changes appear immediately on all devices
- 🚨 **Emergency Alerts** - Real-time emergency broadcasting

## 🔧 Configuration

### Environment Variables
```bash
# Firebase Configuration
export FIREBASE_PROJECT_ID="your-project-id"
export FIREBASE_PRIVATE_KEY="your-private-key"
export FIREBASE_CLIENT_EMAIL="your-client-email"
export FIREBASE_DATABASE_URL="https://your-project.firebaseio.com"
export FIREBASE_STORAGE_BUCKET="your-project.appspot.com"

# JWT for API authentication
export JWT_SECRET="your-secure-jwt-secret"
```

### Firebase Security Rules
```javascript
// Realtime Database Rules
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid || root.child('users').child(auth.uid).child('role').val() === 'ADMIN'",
        ".write": "$uid === auth.uid || root.child('users').child(auth.uid).child('role').val() === 'ADMIN'"
      }
    },
    "vehicles": {
      ".read": "auth != null",
      ".write": "root.child('users').child(auth.uid).child('role').val() === 'ADMIN' || root.child('users').child(auth.uid).child('role').val() === 'DRIVER'"
    }
  }
}
```

## 🏗 Project Structure

```
├── backend/          # Spring Boot + Firebase
│   ├── src/main/java/
│   │   ├── config/FirebaseConfig.java
│   │   ├── service/FirebaseService.java
│   │   └── service/NotificationService.java
│   ├── src/main/resources/
│   │   └── firebase-service-account.json
│   └── pom.xml
├── frontend/         # React + Firebase SDK
│   ├── src/
│   │   ├── firebase/config.js
│   │   ├── hooks/useFirebase.js
│   │   └── components/
│   └── package.json
└── README.md
```

## 🌐 Access Points

- **Application**: http://localhost:3000
- **API Documentation**: http://localhost:8080/api
- **Firebase Console**: https://console.firebase.google.com

## 📈 Firebase-Powered Features

### For Administrators
- **Real-time Fleet Dashboard** - Live vehicle monitoring
- **Push Notifications** - Send alerts to all users
- **User Management** - Firebase Auth integration
- **Analytics Dashboard** - Real-time usage statistics

### For Drivers
- **Live Location Sharing** - Automatic GPS updates
- **Instant Notifications** - Receive dispatch alerts
- **Vehicle Status Updates** - Real-time status broadcasting
- **Emergency Button** - Instant emergency alerts

### For Students & Staff
- **Live Vehicle Tracking** - See exact locations instantly
- **Push Notifications** - Arrival/departure alerts
- **Real-time Updates** - No page refresh needed
- **Offline Support** - Firebase offline capabilities

---

**Built with 🔥 Firebase + Spring Boot for real-time university transportation**


