# Vehicle Tracking System - Local Development Setup

## Prerequisites

1. **Java 17+** - Make sure Java 17 or higher is installed
2. **Maven** - For building and running the application
3. **Node.js & npm** - For running the frontend
4. **Firebase Project** - You'll need a Firebase project with:
   - Realtime Database
   - Authentication
   - Cloud Storage

## Setup Instructions

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Multiple-University-Vehicles-Tracking-in-Real-Time-for-a-Smoother-Transportation
   ```

2. **Configure Firebase**
   - Download your Firebase service account key JSON file
   - Place it in `backend/src/main/resources/firebase-service-account.json`
   - Update the Firebase configuration in `backend/src/main/resources/application.properties` with your project details

3. **Update Google Maps API Key**
   - Get a Google Maps API key from Google Cloud Console
   - Replace `YOUR_GOOGLE_MAPS_API_KEY` in `application.properties`

4. **Start the backend**
   ```bash
   cd backend
   # On Windows
   start-local.bat
   
   # On Linux/Mac
   chmod +x start-local.sh
   ./start-local.sh
   
   # Or manually
   mvn clean spring-boot:run
   ```

### Frontend Setup

1. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Start the frontend**
   ```bash
   # On Windows
   run.bat
   
   # On Linux/Mac
   chmod +x run.sh
   ./run.sh
   
   # Or manually
   npm start
   ```

## Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/firebase/vehicles/health

## Configuration Files

- `backend/src/main/resources/application.properties` - Main configuration
- `frontend/src/firebase/config.js` - Frontend Firebase configuration

## Notes

- This setup is configured for local development only
- All production/deployment related configurations have been removed
- Firebase is used as the primary database
- Real-time features are implemented using Firebase Realtime Database and WebSockets
