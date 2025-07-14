# University Vehicle Tracking System - Frontend

A comprehensive real-time vehicle tracking system for university transportation with advanced features including live tracking, chat system, announcements, and role-based dashboards.

## 🚀 Features

### Core Functionality
- **Real-time Vehicle Tracking**: Live GPS tracking with map visualization
- **Role-based Authentication**: Support for Admin, Driver, Teacher, and Student roles
- **Real-time Chat System**: Multi-channel communication with role-based access
- **Announcements**: Create, manage, and view announcements with priority levels
- **Dashboard Analytics**: Real-time statistics and system overview
- **Responsive Design**: Mobile-first design with Material-UI components

### Role-Specific Features

#### Admin Dashboard
- Complete vehicle fleet management
- User management with role assignment
- System statistics and analytics
- Announcement management
- Maintenance tracking
- Interactive charts and visualizations

#### Driver Dashboard
- Vehicle status monitoring
- Real-time GPS tracking
- Trip management and route optimization
- Passenger management
- Maintenance alerts
- Duty status toggle
- Issue reporting system

#### Student/Teacher Access
- Vehicle location tracking
- Chat system access
- Announcement viewing
- Real-time notifications

## 🛠 Technology Stack

- **React 18** - Modern React with hooks and functional components
- **Material-UI (MUI) v5** - Comprehensive UI component library
- **Socket.io Client** - Real-time WebSocket communication
- **Axios** - HTTP client for API requests
- **React Router v6** - Navigation and routing
- **Firebase** - Push notifications and real-time database
- **Chart.js + React-Chartjs-2** - Data visualization
- **React Hook Form** - Form handling and validation
- **React Hot Toast** - User-friendly notifications
- **JWT Decode** - Token management
- **Date-fns** - Date manipulation utilities

## 📋 Prerequisites

- Node.js 16+ and npm/yarn
- Backend API server running
- Firebase project configured (optional for notifications)

## 🚀 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Environment Configuration**
   Copy `.env.example` to `.env` and configure:
   ```env
   REACT_APP_API_BASE_URL=http://localhost:8080/api
   REACT_APP_FIREBASE_API_KEY=your_firebase_api_key
   REACT_APP_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
   REACT_APP_FIREBASE_PROJECT_ID=your_project_id
   REACT_APP_FIREBASE_DATABASE_URL=https://your_project.firebaseio.com
   REACT_APP_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
   REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
   REACT_APP_FIREBASE_APP_ID=your_app_id
   REACT_APP_FIREBASE_VAPID_KEY=your_vapid_key
   ```

4. **Start Development Server**
   ```bash
   npm start
   ```
   The app will open at `http://localhost:3000`

## 🏗 Project Structure

```
src/
├── components/
│   ├── admin/              # Admin-specific components
│   │   └── AdminDashboard.js
│   ├── announcements/      # Announcement system
│   │   └── Announcements.js
│   ├── auth/              # Authentication components
│   │   ├── Login.js
│   │   └── Register.js
│   ├── chat/              # Real-time chat system
│   │   └── Chat.js
│   ├── common/            # Reusable components
│   │   ├── ErrorBoundary.js
│   │   └── LoadingSpinner.js
│   ├── dashboard/         # Main dashboard
│   │   └── Dashboard.js
│   ├── driver/            # Driver-specific components
│   │   └── DriverDashboard.js
│   └── tracking/          # Vehicle tracking
│       └── VehicleTracking.js
├── context/               # React Context providers
│   └── AuthContext.js
├── firebase/              # Firebase configuration
│   └── config.js
├── hooks/                 # Custom React hooks
│   └── useFirebase.js
├── App.js                 # Main app component
└── index.js              # Entry point
```

## 🔐 Authentication & Authorization

The system supports role-based authentication with the following roles:

- **ADMIN**: Full system access, user management, vehicle management
- **DRIVER**: Vehicle control, trip management, passenger interaction
- **TEACHER**: Vehicle tracking, announcements, chat access
- **STUDENT**: Basic tracking, chat access, announcements viewing

### Demo Credentials
- **Admin**: admin@university.edu / admin123
- **Driver**: driver@university.edu / driver123
- **Student**: student@university.edu / student123

## 💬 Real-time Features

### Chat System
- Multi-channel communication (General, Drivers, Students, Announcements)
- Real-time message delivery via WebSocket
- Role-based channel access
- Online user status
- Message history

### Live Tracking
- Real-time GPS position updates
- Vehicle status monitoring
- Route visualization
- Speed and fuel level tracking
- Maintenance alerts

### Push Notifications
- Firebase Cloud Messaging integration
- Role-based notification targeting
- Announcement notifications
- Emergency alerts

## 📊 Dashboard Features

### Real-time Statistics
- Active vehicles count
- Online drivers status
- Active users
- Daily trip statistics
- Fuel consumption monitoring
- System uptime metrics

### Interactive Charts
- Vehicle distribution by type
- User role distribution
- Trip analytics
- Maintenance schedules

## 🔧 Configuration Options

### API Endpoints
All API endpoints are configurable via environment variables:
- Dashboard stats: `/dashboard/stats`
- Vehicle tracking: `/tracking/vehicles`
- User management: `/admin/users`
- Chat system: WebSocket connection to backend

### Firebase Configuration
Optional Firebase integration for:
- Push notifications
- Real-time database sync
- Cloud messaging
- Analytics





