import React, { useState, useEffect, useMemo } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  CircularProgress,
  Alert,
  Button,
  AppBar,
  Toolbar,
  IconButton
} from '@mui/material';
import {
  DirectionsBus,
  LocationOn,
  Speed,
  LocalGasStation,
  Refresh,
  ArrowBack
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const VehicleTracking = () => {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const fetchVehicles = async () => {
    try {
      setLoading(true);
      const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';
      const response = await axios.get(`${API_BASE_URL}/tracking/vehicles`);
      setVehicles(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch vehicle data');
      console.error('Error fetching vehicles:', err);
      // Mock data for development/demo
      setVehicles([
        {
          id: 1,
          vehicleNumber: 'UNI-001',
          brand: 'Mercedes',
          model: 'Sprinter',
          vehicleType: 'STUDENT_BUS',
          status: 'ACTIVE',
          driverName: 'John Doe',
          driverPhone: '+1-555-0101',
          currentLatitude: 40.7128,
          currentLongitude: -74.0060,
          currentSpeed: 25,
          fuelLevel: 75,
          direction: 'Northeast',
          lastLocationUpdate: new Date().toISOString()
        },
        {
          id: 2,
          vehicleNumber: 'UNI-002',
          brand: 'Toyota',
          model: 'Hiace',
          vehicleType: 'TEACHER_BUS',
          status: 'MAINTENANCE',
          driverName: null,
          driverPhone: null,
          currentLatitude: null,
          currentLongitude: null,
          currentSpeed: 0,
          fuelLevel: 45,
          direction: null,
          lastLocationUpdate: new Date(Date.now() - 3600000).toISOString()
        },
        {
          id: 3,
          vehicleNumber: 'UNI-003',
          brand: 'Ford',
          model: 'Transit',
          vehicleType: 'GENERAL_TRANSPORT',
          status: 'ACTIVE',
          driverName: 'Jane Smith',
          driverPhone: '+1-555-0102',
          currentLatitude: 40.7589,
          currentLongitude: -73.9851,
          currentSpeed: 35,
          fuelLevel: 92,
          direction: 'South',
          lastLocationUpdate: new Date(Date.now() - 300000).toISOString()
        },
        {
          id: 4,
          vehicleNumber: 'UNI-004',
          brand: 'Chevrolet',
          model: 'Express',
          vehicleType: 'OFFICE_ADMIN_VEHICLE',
          status: 'ACTIVE',
          driverName: 'Mike Johnson',
          driverPhone: '+1-555-0103',
          currentLatitude: 40.7505,
          currentLongitude: -73.9934,
          currentSpeed: 0,
          fuelLevel: 68,
          direction: 'Stationary',
          lastLocationUpdate: new Date(Date.now() - 120000).toISOString()
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVehicles();
    
    // Auto-refresh every 30 seconds
    const interval = setInterval(fetchVehicles, 30000);
    return () => clearInterval(interval);
  }, []);

  // Optimize re-renders by memoizing calculations
  const vehicleStats = useMemo(() => ({
    total: vehicles.length,
    active: vehicles.filter(v => v.status === 'ACTIVE').length,
    moving: vehicles.filter(v => v.currentSpeed > 0).length,
    avgFuel: vehicles.length > 0 
      ? Math.round(vehicles.reduce((sum, v) => sum + (v.fuelLevel || 0), 0) / vehicles.length)
      : 0
  }), [vehicles]);

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'INACTIVE': return 'default';
      case 'MAINTENANCE': return 'warning';
      case 'OUT_OF_SERVICE': return 'error';
      default: return 'default';
    }
  };

  const getVehicleTypeColor = (type) => {
    switch (type) {
      case 'STUDENT_BUS': return 'primary';
      case 'TEACHER_BUS': return 'secondary';
      case 'OFFICE_ADMIN_VEHICLE': return 'info';
      case 'GENERAL_TRANSPORT': return 'default';
      default: return 'default';
    }
  };

  const formatLastUpdate = (dateString) => {
    if (!dateString) return 'No data';
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    
    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} min ago`;
    if (diffMins < 1440) return `${Math.floor(diffMins / 60)} hr ago`;
    return date.toLocaleDateString();
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
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
            Real-Time Vehicle Tracking
          </Typography>
          <Button
            color="inherit"
            startIcon={<Refresh />}
            onClick={fetchVehicles}
            disabled={loading}
          >
            Refresh
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {/* Summary Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <DirectionsBus color="primary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" component="div">
                  {vehicleStats.total}
                </Typography>
                <Typography color="textSecondary">
                  Tracked Vehicles
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <LocationOn color="success" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" component="div">
                  {vehicleStats.active}
                </Typography>
                <Typography color="textSecondary">
                  Active Vehicles
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <Speed color="warning" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" component="div">
                  {vehicleStats.moving}
                </Typography>
                <Typography color="textSecondary">
                  Moving Vehicles
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <LocalGasStation color="info" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4" component="div">
                  {vehicleStats.avgFuel}%
                </Typography>
                <Typography color="textSecondary">
                  Avg Fuel Level
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Vehicle Table */}
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Vehicle Status & Locations
            </Typography>
            
            {vehicles.length === 0 ? (
              <Alert severity="info">
                No vehicles with location data found.
              </Alert>
            ) : (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Vehicle</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell>Driver</TableCell>
                      <TableCell>Location</TableCell>
                      <TableCell>Speed</TableCell>
                      <TableCell>Fuel</TableCell>
                      <TableCell>Last Update</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {vehicles.map((vehicle) => (
                      <TableRow key={vehicle.id} hover>
                        <TableCell>
                          <Box>
                            <Typography variant="subtitle2" fontWeight="bold">
                              {vehicle.vehicleNumber}
                            </Typography>
                            <Typography variant="caption" color="textSecondary">
                              {vehicle.brand} {vehicle.model}
                            </Typography>
                          </Box>
                        </TableCell>
                        
                        <TableCell>
                          <Chip 
                            label={vehicle.vehicleType?.replace('_', ' ')} 
                            color={getVehicleTypeColor(vehicle.vehicleType)}
                            size="small"
                          />
                        </TableCell>
                        
                        <TableCell>
                          <Chip 
                            label={vehicle.status} 
                            color={getStatusColor(vehicle.status)}
                            size="small"
                          />
                        </TableCell>
                        
                        <TableCell>
                          {vehicle.driverName ? (
                            <Box>
                              <Typography variant="body2">
                                {vehicle.driverName}
                              </Typography>
                              <Typography variant="caption" color="textSecondary">
                                {vehicle.driverPhone}
                              </Typography>
                            </Box>
                          ) : (
                            <Typography variant="body2" color="textSecondary">
                              No driver assigned
                            </Typography>
                          )}
                        </TableCell>
                        
                        <TableCell>
                          {vehicle.currentLatitude && vehicle.currentLongitude ? (
                            <Box>
                              <Typography variant="caption" display="block">
                                Lat: {vehicle.currentLatitude.toFixed(4)}
                              </Typography>
                              <Typography variant="caption" display="block">
                                Lng: {vehicle.currentLongitude.toFixed(4)}
                              </Typography>
                              {vehicle.direction && (
                                <Typography variant="caption" color="primary">
                                  {vehicle.direction}
                                </Typography>
                              )}
                            </Box>
                          ) : (
                            <Typography variant="body2" color="textSecondary">
                              No location data
                            </Typography>
                          )}
                        </TableCell>
                        
                        <TableCell>
                          {vehicle.currentSpeed !== null ? (
                            <Typography variant="body2">
                              {vehicle.currentSpeed} km/h
                            </Typography>
                          ) : (
                            <Typography variant="body2" color="textSecondary">
                              -
                            </Typography>
                          )}
                        </TableCell>
                        
                        <TableCell>
                          {vehicle.fuelLevel !== null ? (
                            <Box display="flex" alignItems="center">
                              <Typography variant="body2" sx={{ mr: 1 }}>
                                {vehicle.fuelLevel}%
                              </Typography>
                              <Box
                                sx={{
                                  width: 40,
                                  height: 8,
                                  bgcolor: 'grey.300',
                                  borderRadius: 1,
                                  overflow: 'hidden'
                                }}
                              >
                                <Box
                                  sx={{
                                    width: `${vehicle.fuelLevel}%`,
                                    height: '100%',
                                    bgcolor: vehicle.fuelLevel > 30 ? 'success.main' : 
                                             vehicle.fuelLevel > 15 ? 'warning.main' : 'error.main'
                                  }}
                                />
                              </Box>
                            </Box>
                          ) : (
                            <Typography variant="body2" color="textSecondary">
                              -
                            </Typography>
                          )}
                        </TableCell>
                        
                        <TableCell>
                          <Typography variant="caption">
                            {formatLastUpdate(vehicle.lastLocationUpdate)}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </CardContent>
        </Card>
        
        <Box sx={{ mt: 2 }}>
          <Typography variant="caption" color="textSecondary">
            Last updated: {new Date().toLocaleTimeString()} • Auto-refresh every 30 seconds
          </Typography>
        </Box>
      </Container>
    </Box>
  );
};

export default VehicleTracking; 