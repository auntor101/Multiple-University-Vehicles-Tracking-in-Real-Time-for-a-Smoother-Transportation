// Firebase Configuration for Vehicle Tracking System
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getDatabase } from 'firebase/database';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';
import { getStorage } from 'firebase/storage';

// Firebase configuration object
const firebaseConfig = {
  apiKey: process.env.REACT_APP_FIREBASE_API_KEY || 'demo-api-key',
  authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN || 'localhost',
  databaseURL: process.env.REACT_APP_FIREBASE_DATABASE_URL || 'http://localhost:9000',
  projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID || 'demo-vehicle-tracking',
  storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET || 'demo-vehicle-tracking.appspot.com',
  messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDER_ID || '123456789',
  appId: process.env.REACT_APP_FIREBASE_APP_ID || '1:123456789:web:abcdef123456',
  measurementId: process.env.REACT_APP_FIREBASE_MEASUREMENT_ID || 'G-MEASUREMENT'
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Connect to Firebase Emulator if in development
if (process.env.REACT_APP_USE_FIREBASE_EMULATOR === 'true' || window.location.hostname === 'localhost') {
  console.log('🔧 Using Firebase Emulator Suite');
  
  // Import emulator connection functions
  const { connectAuthEmulator } = require('firebase/auth');
  const { connectDatabaseEmulator } = require('firebase/database');
  const { connectStorageEmulator } = require('firebase/storage');
  
  try {
    // Connect Auth Emulator
    connectAuthEmulator(auth, 'http://localhost:9099', { disableWarnings: true });
    console.log('✅ Auth Emulator connected on port 9099');
  } catch (e) {
    console.log('Auth emulator already connected');
  }
  
  try {
    // Connect Database Emulator
    connectDatabaseEmulator(database, 'localhost', 9000);
    console.log('✅ Database Emulator connected on port 9000');
  } catch (e) {
    console.log('Database emulator already connected');
  }
  
  try {
    // Connect Storage Emulator
    connectStorageEmulator(storage, 'localhost', 9199);
    console.log('✅ Storage Emulator connected on port 9199');
  } catch (e) {
    console.log('Storage emulator already connected');
  }
  
  console.log('🎉 Firebase Emulator Suite ready!');
  console.log('📊 Emulator UI: http://localhost:4000');
}

// Initialize Firebase Authentication
export const auth = getAuth(app);

// Initialize Firebase Realtime Database
export const database = getDatabase(app);

// Initialize Firebase Cloud Messaging
let messaging = null;
try {
  if (typeof window !== 'undefined' && 'serviceWorker' in navigator) {
    messaging = getMessaging(app);
  }
} catch (error) {
  console.warn('Firebase Messaging not supported:', error.message);
}
export { messaging };

// Initialize Firebase Storage
export const storage = getStorage(app);

// VAPID key for push notifications
const VAPID_KEY = process.env.REACT_APP_FIREBASE_VAPID_KEY;

// Request permission for notifications
export const requestNotificationPermission = async () => {
  if (!messaging) {
    console.warn('Firebase Messaging not supported');
    return null;
  }
  
  try {
    const permission = await Notification.requestPermission();
    
    if (permission === 'granted') {
      console.log('Notification permission granted.');
      
      // Get FCM token
      const token = await getToken(messaging, {
        vapidKey: VAPID_KEY
      });
      
      if (token) {
        console.log('FCM Token:', token);
        return token;
      } else {
        console.log('No registration token available.');
        return null;
      }
    } else {
      console.log('Unable to get permission to notify.');
      return null;
    }
  } catch (error) {
    console.error('An error occurred while retrieving token:', error);
    return null;
  }
};

// Listen for foreground messages
export const onMessageListener = () => {
  if (!messaging) {
    return Promise.resolve();
  }
  
  return new Promise((resolve) => {
    onMessage(messaging, (payload) => {
      console.log('Message received in foreground:', payload);
      resolve(payload);
    });
  });
};

// Firebase database paths/references
export const DB_PATHS = {
  USERS: 'users',
  VEHICLES: 'vehicles',
  NOTIFICATIONS: 'notifications',
  USER_TOKENS: 'userTokens',
  VEHICLE_LOCATIONS: 'vehicleLocations'
};

// Subscribe to role-based topic
export const subscribeToRole = async (role) => {
  if (!messaging) {
    console.warn('Firebase Messaging not supported');
    return;
  }
  
  try {
    console.log(`Subscribed to role topic: role_${role.toLowerCase()}`);
  } catch (error) {
    console.error('Error subscribing to role topic:', error);
  }
};

// Utility function to handle Firebase errors
export const handleFirebaseError = (error) => {
  console.error('Firebase Error:', error);
  
  switch (error.code) {
    case 'auth/user-not-found':
      return 'User not found. Please check your credentials.';
    case 'auth/wrong-password':
      return 'Incorrect password. Please try again.';
    case 'auth/too-many-requests':
      return 'Too many failed attempts. Please try again later.';
    case 'permission-denied':
      return 'Permission denied. You do not have access to this resource.';
    case 'network-request-failed':
      return 'Network error. Please check your internet connection.';
    default:
      return error.message || 'An unexpected error occurred.';
  }
};

export default app; 