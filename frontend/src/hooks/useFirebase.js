import { useState, useEffect, useCallback } from 'react';
import { 
  ref, 
  onValue, 
  push, 
  set, 
  update, 
  remove,
  query,
  orderByChild,
  equalTo,
  limitToLast,
  get
} from 'firebase/database';
import { 
  signInWithEmailAndPassword, 
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged
} from 'firebase/auth';
import { auth, database, requestNotificationPermission, onMessageListener } from '../firebase/config';
import toast from 'react-hot-toast';

// Custom hook for Firebase operations
export const useFirebase = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [vehicles, setVehicles] = useState([]);
  const [notifications, setNotifications] = useState([]);

  // Authentication state listener
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      setUser(user);
      setLoading(false);
      
      if (user) {
        // Request notification permission when user logs in
        requestNotificationPermission().then(token => {
          if (token) {
            // Store FCM token in database
            const userTokenRef = ref(database, `userTokens/${user.uid}`);
            set(userTokenRef, {
              token: token,
              lastUpdated: Date.now()
            });
          }
        });
      }
    });

    return () => unsubscribe();
  }, []);

  // Listen for foreground messages
  useEffect(() => {
    onMessageListener().then(payload => {
      if (payload) {
        toast.success(`${payload.notification.title}: ${payload.notification.body}`);
      }
    }).catch(err => console.log('failed: ', err));
  }, []);

  // Authentication methods
  const login = async (email, password) => {
    try {
      setLoading(true);
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      toast.success('Login successful!');
      return { success: true, user: userCredential.user };
    } catch (error) {
      console.error('Login error:', error);
      toast.error(error.message);
      return { success: false, error: error.message };
    } finally {
      setLoading(false);
    }
  };

  const register = async (email, password, userData) => {
    try {
      setLoading(true);
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      
      // Store additional user data in Realtime Database
      const userRef = ref(database, `users/${userCredential.user.uid}`);
      await set(userRef, {
        ...userData,
        email: email,
        createdAt: Date.now(),
        isActive: true
      });
      
      toast.success('Registration successful!');
      return { success: true, user: userCredential.user };
    } catch (error) {
      console.error('Registration error:', error);
      toast.error(error.message);
      return { success: false, error: error.message };
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await signOut(auth);
      setUser(null);
      setVehicles([]);
      setNotifications([]);
      toast.success('Logged out successfully');
    } catch (error) {
      console.error('Logout error:', error);
      toast.error('Error logging out');
    }
  };

  // Real-time data hooks
  const useVehicles = () => {
    useEffect(() => {
      const vehiclesRef = ref(database, 'vehicles');
      const vehiclesQuery = query(vehiclesRef, orderByChild('isActive'), equalTo(true));
      
      const unsubscribe = onValue(vehiclesQuery, (snapshot) => {
        const data = snapshot.val();
        if (data) {
          const vehiclesList = Object.keys(data).map(key => ({
            id: key,
            ...data[key]
          }));
          setVehicles(vehiclesList);
        } else {
          setVehicles([]);
        }
      });

      return () => unsubscribe();
    }, []);

    return vehicles;
  };

  const useVehicleLocation = (vehicleId) => {
    const [location, setLocation] = useState(null);

    useEffect(() => {
      if (!vehicleId) return;

      const locationRef = ref(database, `vehicles/${vehicleId}/location`);
      
      const unsubscribe = onValue(locationRef, (snapshot) => {
        const locationData = snapshot.val();
        setLocation(locationData);
      });

      return () => unsubscribe();
    }, [vehicleId]);

    return location;
  };

  const useNotifications = (userId) => {
    useEffect(() => {
      if (!userId) return;

      const notificationsRef = ref(database, 'notifications');
      const notificationsQuery = query(
        notificationsRef, 
        orderByChild('recipient'), 
        equalTo(userId),
        limitToLast(50)
      );
      
      const unsubscribe = onValue(notificationsQuery, (snapshot) => {
        const data = snapshot.val();
        if (data) {
          const notificationsList = Object.keys(data).map(key => ({
            id: key,
            ...data[key]
          })).sort((a, b) => b.timestamp - a.timestamp);
          setNotifications(notificationsList);
        } else {
          setNotifications([]);
        }
      });

      return () => unsubscribe();
    }, [userId]);

    return notifications;
  };

  // Data manipulation methods
  const addVehicle = async (vehicleData) => {
    try {
      const vehiclesRef = ref(database, 'vehicles');
      const newVehicleRef = push(vehiclesRef);
      
      await set(newVehicleRef, {
        ...vehicleData,
        createdAt: Date.now(),
        updatedAt: Date.now(),
        isActive: true
      });
      
      toast.success('Vehicle added successfully!');
      return { success: true, id: newVehicleRef.key };
    } catch (error) {
      console.error('Error adding vehicle:', error);
      toast.error('Failed to add vehicle');
      return { success: false, error: error.message };
    }
  };

  const updateVehicle = async (vehicleId, updates) => {
    try {
      const vehicleRef = ref(database, `vehicles/${vehicleId}`);
      await update(vehicleRef, {
        ...updates,
        updatedAt: Date.now()
      });
      
      toast.success('Vehicle updated successfully!');
      return { success: true };
    } catch (error) {
      console.error('Error updating vehicle:', error);
      toast.error('Failed to update vehicle');
      return { success: false, error: error.message };
    }
  };

  const updateVehicleLocation = async (vehicleId, locationData) => {
    try {
      const locationRef = ref(database, `vehicles/${vehicleId}/location`);
      await set(locationRef, {
        ...locationData,
        timestamp: Date.now()
      });
      
      // Also update the main vehicle record
      const vehicleRef = ref(database, `vehicles/${vehicleId}`);
      await update(vehicleRef, {
        updatedAt: Date.now()
      });
      
      return { success: true };
    } catch (error) {
      console.error('Error updating vehicle location:', error);
      return { success: false, error: error.message };
    }
  };

  const deleteVehicle = async (vehicleId) => {
    try {
      const vehicleRef = ref(database, `vehicles/${vehicleId}`);
      await update(vehicleRef, {
        isActive: false,
        updatedAt: Date.now()
      });
      
      toast.success('Vehicle deleted successfully!');
      return { success: true };
    } catch (error) {
      console.error('Error deleting vehicle:', error);
      toast.error('Failed to delete vehicle');
      return { success: false, error: error.message };
    }
  };

  // Real-time location tracking for drivers
  const startLocationTracking = useCallback((vehicleId) => {
    if (!navigator.geolocation) {
      toast.error('Geolocation is not supported by this browser');
      return null;
    }

    const watchId = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude, speed } = position.coords;
        
        updateVehicleLocation(vehicleId, {
          latitude,
          longitude,
          speed: speed || 0,
          accuracy: position.coords.accuracy
        });
      },
      (error) => {
        console.error('Geolocation error:', error);
        toast.error('Error getting location');
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 5000
      }
    );

    return watchId;
  }, []);

  const stopLocationTracking = useCallback((watchId) => {
    if (watchId && navigator.geolocation) {
      navigator.geolocation.clearWatch(watchId);
    }
  }, []);

  // Get user data
  const getUserData = async (userId) => {
    try {
      const userRef = ref(database, `users/${userId}`);
      const snapshot = await get(userRef);
      return snapshot.val();
    } catch (error) {
      console.error('Error getting user data:', error);
      return null;
    }
  };

  return {
    // Authentication
    user,
    loading,
    login,
    register,
    logout,
    
    // Real-time data hooks
    useVehicles,
    useVehicleLocation,
    useNotifications,
    
    // Data manipulation
    addVehicle,
    updateVehicle,
    updateVehicleLocation,
    deleteVehicle,
    
    // Location tracking
    startLocationTracking,
    stopLocationTracking,
    
    // Utilities
    getUserData,
    
    // State
    vehicles,
    notifications
  };
};

export default useFirebase; 