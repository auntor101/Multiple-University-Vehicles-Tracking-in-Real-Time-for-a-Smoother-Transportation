import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  AppBar,
  Toolbar,
  IconButton,
  Button,
  Switch,
  FormControlLabel,
  Chip,
  Alert,
  CircularProgress,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Fab,
  LinearProgress,
  Divider
} from '@mui/material';
import {
  ArrowBack,
  DirectionsBus,
  LocationOn,
  Speed,
  LocalGasStation,
  PlayArrow,
  Stop,
  Navigation,
  Report,
  Assignment,
  People,
  Timer,
  Refresh,
  Warning
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import axios from 'axios';

const DriverDashboard = () => {
  const [vehicleData, setVehicleData] = useState(null);
  const [isOnDuty, setIsOnDuty] = useState(false);
  const [currentTrip, setCurrentTrip] = useState(null);
  const [passengers, setPassengers] = useState([]);
  const [maintenance, setMaintenance] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reportDialog, setReportDialog] = useState(false);
  const [reportForm, setReportForm] = useState({ type: '', description: '' });
  const [locationTracking, setLocationTracking] = useState(false);
  
  const { user } = useAuth();
  const navigate = useNavigate();

  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

  useEffect(() => {
    fetchDriverData();
    // Set up location tracking if on duty
    if (isOnDuty) {
      startLocationTracking();
    }
    return () => stopLocationTracking();
  }, [isOnDuty]);

  const fetchDriverData = async () => {
    try {
      setLoading(true);
      const [vehicleRes, tripRes, passengersRes, maintenanceRes] = await Promise.all([
        axios.get(`${API_BASE_URL}/driver/vehicle`),
        axios.get(`${API_BASE_URL}/driver/current-trip`),
        axios.get(`${API_BASE_URL}/driver/passengers`),
        axios.get(`${API_BASE_URL}/driver/maintenance`)
      ]);
      
      setVehicleData(vehicleRes.data);
      setCurrentTrip(tripRes.data);
      setPassengers(passengersRes.data);
      setMaintenance(maintenanceRes.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch driver data');
      console.error('Error fetching data:', err);
      // Mock data for development
      setVehicleData({
        id: 1,
        vehicleNumber: 'UNI-001',
        brand: 'Mercedes',
        model: 'Sprinter',
        capacity: 30,
        fuelLevel: 75,
        currentSpeed: 0,
        mileage: 15420,
        lastMaintenance: '2024-01-15',
        status: 'ACTIVE'
      });
      setCurrentTrip({
        id: 1,
        routeName: 'Campus to Downtown',
        startTime: '08:00 AM',
        estimatedEndTime: '09:30 AM',
        stops: [
          { name: 'Main Campus', time: '08:00 AM', completed: true },
          { name: 'Library Stop', time: '08:15 AM', completed: true },
          { name: 'Student Housing', time: '08:30 AM', completed: false },
          { name: 'Downtown Terminal', time: '09:30 AM', completed: false }
        ],
        passengersCount: 12
      });
      setPassengers([
        { id: 1, name: 'Alice Johnson', studentId: 'S001', stop: 'Library Stop', status: 'Boarded' },
        { id: 2, name: 'Bob Smith', studentId: 'S002', stop: 'Main Campus', status: 'Boarded' },
        { id: 3, name: 'Carol Davis', studentId: 'S003', stop: 'Student Housing', status: 'Waiting' }
      ]);
      setMaintenance([
        { id: 1, type: 'Engine Check', dueDate: '2024-02-15', priority: 'Medium', description: 'Regular engine maintenance due' },
        { id: 2, type: 'Brake Inspection', dueDate: '2024-01-30', priority: 'High', description: 'Brake system needs inspection' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const startLocationTracking = () => {
    if (!navigator.geolocation) {
      setError('Geolocation is not supported by this browser');
      return;
    }

    setLocationTracking(true);
    const watchId = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude, longitude, speed } = position.coords;
        // Send location to backend
        sendLocationUpdate(latitude, longitude, speed);
      },
      (error) => {
        console.error('Location error:', error);
        setError('Unable to get location. Please enable GPS.');
      },
      { enableHighAccuracy: true, timeout: 5000, maximumAge: 10000 }
    );

    // Store watchId for cleanup
    window.driverLocationWatchId = watchId;
  };

  const stopLocationTracking = () => {
    if (window.driverLocationWatchId) {
      navigator.geolocation.clearWatch(window.driverLocationWatchId);
      window.driverLocationWatchId = null;
    }
    setLocationTracking(false);
  };

  const sendLocationUpdate = async (latitude, longitude, speed) => {
    try {
      await axios.post(`${API_BASE_URL}/tracking/location`, {
        vehicleId: vehicleData?.id,
        latitude,
        longitude,
        speed: speed || 0,
        timestamp: new Date().toISOString()
      });
    } catch (err) {
      console.error('Failed to send location update:', err);
    }
  };

  const handleDutyToggle = async () => {
    try {
      const newStatus = !isOnDuty;
      await axios.post(`${API_BASE_URL}/driver/duty-status`, { onDuty: newStatus });
      setIsOnDuty(newStatus);
      
      if (newStatus) {
        startLocationTracking();
      } else {
        stopLocationTracking();
      }
    } catch (err) {
      setError('Failed to update duty status');
    }
  };

  const handleStartTrip = async () => {
    try {
      await axios.post(`${API_BASE_URL}/driver/start-trip`, { tripId: currentTrip?.id });
      await fetchDriverData();
    } catch (err) {
      setError('Failed to start trip');
    }
  };

  const handleCompleteStop = async (stopId) => {
    try {
      await axios.post(`${API_BASE_URL}/driver/complete-stop`, { stopId });
      await fetchDriverData();
    } catch (err) {
      setError('Failed to update stop status');
    }
  };

  const handleSubmitReport = async () => {
    try {
      await axios.post(`${API_BASE_URL}/driver/report`, {
        vehicleId: vehicleData?.id,
        type: reportForm.type,
        description: reportForm.description,
        timestamp: new Date().toISOString()
      });
      setReportDialog(false);
      setReportForm({ type: '', description: '' });
      await fetchDriverData();
    } catch (err) {
      setError('Failed to submit report');
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'High': return 'error';
      case 'Medium': return 'warning';
      case 'Low': return 'info';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <AppBar position="static">
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={() => navigate('/')}
            sx={{ mr: 2 }}
          >
            <ArrowBack />
          </IconButton>
          <DirectionsBus sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Driver Dashboard - {vehicleData?.vehicleNumber}
          </Typography>
          <FormControlLabel
            control={
              <Switch 
                checked={isOnDuty} 
                onChange={handleDutyToggle}
                color="secondary"
              />
            }
            label={isOnDuty ? "On Duty" : "Off Duty"}
            sx={{ color: 'white' }}
          />
          <Button
            color="inherit"
            startIcon={<Refresh />}
            onClick={fetchDriverData}
            sx={{ ml: 2 }}
          >
            Refresh
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 2, mb: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {/* Vehicle Status Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <Speed color="primary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{vehicleData?.currentSpeed || 0}</Typography>
                <Typography color="textSecondary">Current Speed (km/h)</Typography>
                {locationTracking && (
                  <Chip label="GPS Active" color="success" size="small" sx={{ mt: 1 }} />
                )}
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <LocalGasStation color="warning" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{vehicleData?.fuelLevel}%</Typography>
                <Typography color="textSecondary">Fuel Level</Typography>
                <LinearProgress 
                  variant="determinate" 
                  value={vehicleData?.fuelLevel} 
                  color={vehicleData?.fuelLevel > 30 ? 'success' : 'warning'}
                  sx={{ mt: 1 }} 
                />
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <People color="info" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{currentTrip?.passengersCount || 0}</Typography>
                <Typography color="textSecondary">Current Passengers</Typography>
                <Typography variant="caption">
                  Capacity: {vehicleData?.capacity}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <Timer color="secondary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{vehicleData?.mileage?.toLocaleString()}</Typography>
                <Typography color="textSecondary">Total Mileage</Typography>
                <Typography variant="caption">
                  Last service: {vehicleData?.lastMaintenance}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        <Grid container spacing={3}>
          {/* Current Trip */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Current Trip
                  {currentTrip && (
                    <Chip 
                      label="In Progress" 
                      color="primary" 
                      size="small" 
                      sx={{ ml: 2 }} 
                    />
                  )}
                </Typography>
                
                {currentTrip ? (
                  <>
                    <Typography variant="subtitle1" gutterBottom>
                      {currentTrip.routeName}
                    </Typography>
                    <Typography variant="body2" color="textSecondary" gutterBottom>
                      {currentTrip.startTime} - {currentTrip.estimatedEndTime}
                    </Typography>
                    
                    <List dense>
                      {currentTrip.stops.map((stop, index) => (
                        <ListItem key={index}>
                          <ListItemIcon>
                            {stop.completed ? (
                              <LocationOn color="success" />
                            ) : (
                              <LocationOn color="disabled" />
                            )}
                          </ListItemIcon>
                          <ListItemText
                            primary={stop.name}
                            secondary={stop.time}
                          />
                          {!stop.completed && isOnDuty && (
                            <Button
                              size="small"
                              onClick={() => handleCompleteStop(stop.id)}
                            >
                              Complete
                            </Button>
                          )}
                        </ListItem>
                      ))}
                    </List>
                    
                    {isOnDuty && (
                      <Box sx={{ mt: 2 }}>
                        <Button
                          variant="contained"
                          startIcon={<PlayArrow />}
                          onClick={handleStartTrip}
                          fullWidth
                        >
                          Continue Trip
                        </Button>
                      </Box>
                    )}
                  </>
                ) : (
                  <Alert severity="info">
                    No active trip assigned. Please contact dispatch.
                  </Alert>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Passengers */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Passengers ({passengers.length})
                </Typography>
                
                {passengers.length > 0 ? (
                  <List dense>
                    {passengers.map((passenger) => (
                      <ListItem key={passenger.id}>
                        <ListItemIcon>
                          <Person />
                        </ListItemIcon>
                        <ListItemText
                          primary={passenger.name}
                          secondary={`${passenger.studentId} • ${passenger.stop}`}
                        />
                        <Chip
                          label={passenger.status}
                          color={passenger.status === 'Boarded' ? 'success' : 'warning'}
                          size="small"
                        />
                      </ListItem>
                    ))}
                  </List>
                ) : (
                  <Alert severity="info">
                    No passengers currently registered for this trip.
                  </Alert>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Maintenance Alerts */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Maintenance Alerts
                </Typography>
                
                {maintenance.length > 0 ? (
                  <List dense>
                    {maintenance.map((item) => (
                      <ListItem key={item.id}>
                        <ListItemIcon>
                          <Warning color={getPriorityColor(item.priority)} />
                        </ListItemIcon>
                        <ListItemText
                          primary={item.type}
                          secondary={`Due: ${item.dueDate} • ${item.description}`}
                        />
                        <Chip
                          label={item.priority}
                          color={getPriorityColor(item.priority)}
                          size="small"
                        />
                      </ListItem>
                    ))}
                  </List>
                ) : (
                  <Alert severity="success">
                    No maintenance issues reported.
                  </Alert>
                )}
              </CardContent>
            </Card>
          </Grid>

          {/* Quick Actions */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Quick Actions
                </Typography>
                
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Button
                      variant="outlined"
                      startIcon={<Report />}
                      onClick={() => setReportDialog(true)}
                      fullWidth
                      disabled={!isOnDuty}
                    >
                      Report Issue
                    </Button>
                  </Grid>
                  <Grid item xs={6}>
                    <Button
                      variant="outlined"
                      startIcon={<Assignment />}
                      onClick={() => navigate('/announcements')}
                      fullWidth
                    >
                      Announcements
                    </Button>
                  </Grid>
                  <Grid item xs={6}>
                    <Button
                      variant="outlined"
                      startIcon={<Navigation />}
                      onClick={() => navigate('/tracking')}
                      fullWidth
                    >
                      View Map
                    </Button>
                  </Grid>
                  <Grid item xs={6}>
                    <Button
                      variant="outlined"
                      startIcon={<DirectionsBus />}
                      onClick={() => window.location.reload()}
                      fullWidth
                    >
                      Refresh Status
                    </Button>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Container>

      {/* Report Issue Dialog */}
      <Dialog open={reportDialog} onClose={() => setReportDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Report Issue</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                select
                label="Issue Type"
                value={reportForm.type}
                onChange={(e) => setReportForm({...reportForm, type: e.target.value})}
                SelectProps={{ native: true }}
              >
                <option value="">Select issue type</option>
                <option value="mechanical">Mechanical Problem</option>
                <option value="safety">Safety Concern</option>
                <option value="passenger">Passenger Issue</option>
                <option value="route">Route Problem</option>
                <option value="other">Other</option>
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={4}
                label="Description"
                value={reportForm.description}
                onChange={(e) => setReportForm({...reportForm, description: e.target.value})}
                placeholder="Please describe the issue in detail..."
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReportDialog(false)}>Cancel</Button>
          <Button 
            onClick={handleSubmitReport} 
            variant="contained"
            disabled={!reportForm.type || !reportForm.description}
          >
            Submit Report
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default DriverDashboard; 