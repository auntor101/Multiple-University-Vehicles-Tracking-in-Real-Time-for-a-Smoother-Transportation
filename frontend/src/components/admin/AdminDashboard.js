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
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Fab,
  Tabs,
  Tab,
  LinearProgress
} from '@mui/material';
import {
  ArrowBack,
  Dashboard as DashboardIcon,
  DirectionsBus,
  People,
  Add,
  Edit,
  Delete,
  Refresh,
  TrendingUp,
  Warning,
  CheckCircle
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { DataGrid } from '@mui/x-data-grid';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement } from 'chart.js';
import { Bar, Doughnut } from 'react-chartjs-2';
import axios from 'axios';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement);

const AdminDashboard = () => {
  const [tabValue, setTabValue] = useState(0);
  const [vehicles, setVehicles] = useState([]);
  const [users, setUsers] = useState([]);
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [dialogType, setDialogType] = useState('');
  const [selectedItem, setSelectedItem] = useState(null);
  const [formData, setFormData] = useState({});
  
  const { user } = useAuth();
  const navigate = useNavigate();

  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [vehiclesRes, usersRes, statsRes] = await Promise.all([
        axios.get(`${API_BASE_URL}/admin/vehicles`),
        axios.get(`${API_BASE_URL}/admin/users`),
        axios.get(`${API_BASE_URL}/admin/stats`)
      ]);
      
      setVehicles(vehiclesRes.data);
      setUsers(usersRes.data);
      setStats(statsRes.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch dashboard data');
      console.error('Error fetching data:', err);
      // Mock data for development
      setVehicles([
        { id: 1, vehicleNumber: 'UNI-001', brand: 'Mercedes', model: 'Sprinter', vehicleType: 'STUDENT_BUS', status: 'ACTIVE', driverName: 'John Doe', capacity: 30 },
        { id: 2, vehicleNumber: 'UNI-002', brand: 'Toyota', model: 'Hiace', vehicleType: 'TEACHER_BUS', status: 'MAINTENANCE', driverName: null, capacity: 15 },
        { id: 3, vehicleNumber: 'UNI-003', brand: 'Ford', model: 'Transit', vehicleType: 'GENERAL_TRANSPORT', status: 'ACTIVE', driverName: 'Jane Smith', capacity: 20 }
      ]);
      setUsers([
        { id: 1, username: 'admin', email: 'admin@university.edu', role: 'ADMIN', fullName: 'Admin User', isActive: true },
        { id: 2, username: 'driver1', email: 'driver1@university.edu', role: 'DRIVER', fullName: 'John Doe', isActive: true },
        { id: 3, username: 'student1', email: 'student1@university.edu', role: 'STUDENT', fullName: 'Alice Johnson', isActive: true }
      ]);
      setStats({
        totalVehicles: 12,
        activeVehicles: 8,
        totalUsers: 145,
        activeDrivers: 6,
        maintenanceRequests: 3,
        totalTrips: 234,
        avgFuelConsumption: 15.2,
        systemUptime: 99.5
      });
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleAddVehicle = () => {
    setDialogType('vehicle');
    setSelectedItem(null);
    setFormData({
      vehicleNumber: '',
      brand: '',
      model: '',
      vehicleType: 'STUDENT_BUS',
      capacity: '',
      registrationNumber: '',
      fuelType: 'DIESEL',
      status: 'ACTIVE'
    });
    setOpenDialog(true);
  };

  const handleEditVehicle = (vehicle) => {
    setDialogType('vehicle');
    setSelectedItem(vehicle);
    setFormData(vehicle);
    setOpenDialog(true);
  };

  const handleAddUser = () => {
    setDialogType('user');
    setSelectedItem(null);
    setFormData({
      username: '',
      email: '',
      fullName: '',
      role: 'STUDENT',
      phoneNumber: '',
      isActive: true
    });
    setOpenDialog(true);
  };

  const handleEditUser = (userData) => {
    setDialogType('user');
    setSelectedItem(userData);
    setFormData(userData);
    setOpenDialog(true);
  };

  const handleSave = async () => {
    try {
      const endpoint = dialogType === 'vehicle' ? 'vehicles' : 'users';
      const method = selectedItem ? 'put' : 'post';
      const url = selectedItem 
        ? `${API_BASE_URL}/admin/${endpoint}/${selectedItem.id}`
        : `${API_BASE_URL}/admin/${endpoint}`;
      
      await axios[method](url, formData);
      await fetchDashboardData();
      setOpenDialog(false);
    } catch (err) {
      console.error('Error saving:', err);
      setError('Failed to save changes');
    }
  };

  const handleDelete = async (type, id) => {
    if (window.confirm('Are you sure you want to delete this item?')) {
      try {
        await axios.delete(`${API_BASE_URL}/admin/${type}/${id}`);
        await fetchDashboardData();
      } catch (err) {
        console.error('Error deleting:', err);
        setError('Failed to delete item');
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'INACTIVE': return 'default';
      case 'MAINTENANCE': return 'warning';
      case 'OUT_OF_SERVICE': return 'error';
      default: return 'default';
    }
  };

  const getRoleColor = (role) => {
    switch (role) {
      case 'ADMIN': return 'error';
      case 'DRIVER': return 'warning';
      case 'TEACHER': return 'info';
      case 'STUDENT': return 'primary';
      default: return 'default';
    }
  };

  // Chart data
  const vehicleTypeData = {
    labels: ['Student Bus', 'Teacher Bus', 'Office Vehicle', 'General Transport'],
    datasets: [{
      data: [
        vehicles.filter(v => v.vehicleType === 'STUDENT_BUS').length,
        vehicles.filter(v => v.vehicleType === 'TEACHER_BUS').length,
        vehicles.filter(v => v.vehicleType === 'OFFICE_ADMIN_VEHICLE').length,
        vehicles.filter(v => v.vehicleType === 'GENERAL_TRANSPORT').length
      ],
      backgroundColor: ['#2196f3', '#ff9800', '#4caf50', '#9c27b0']
    }]
  };

  const userRoleData = {
    labels: ['Students', 'Teachers', 'Drivers', 'Admins'],
    datasets: [{
      data: [
        users.filter(u => u.role === 'STUDENT').length,
        users.filter(u => u.role === 'TEACHER').length,
        users.filter(u => u.role === 'DRIVER').length,
        users.filter(u => u.role === 'ADMIN').length
      ],
      backgroundColor: ['#2196f3', '#00bcd4', '#ff9800', '#f44336']
    }]
  };

  const TabPanel = ({ children, value, index }) => (
    <div hidden={value !== index}>
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );

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
          <DashboardIcon sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Admin Dashboard
          </Typography>
          <Button
            color="inherit"
            startIcon={<Refresh />}
            onClick={fetchDashboardData}
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

        {/* Stats Cards */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <DirectionsBus color="primary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{stats.totalVehicles || 0}</Typography>
                <Typography color="textSecondary">Total Vehicles</Typography>
                <LinearProgress 
                  variant="determinate" 
                  value={(stats.activeVehicles / stats.totalVehicles) * 100} 
                  sx={{ mt: 1 }} 
                />
                <Typography variant="caption">{stats.activeVehicles} Active</Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <People color="success" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{stats.totalUsers || 0}</Typography>
                <Typography color="textSecondary">Total Users</Typography>
                <Typography variant="caption" color="success.main">
                  {stats.activeDrivers} Active Drivers
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <TrendingUp color="warning" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{stats.totalTrips || 0}</Typography>
                <Typography color="textSecondary">Total Trips</Typography>
                <Typography variant="caption" color="warning.main">
                  {stats.avgFuelConsumption}L avg consumption
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center' }}>
                <CheckCircle color="info" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h4">{stats.systemUptime}%</Typography>
                <Typography color="textSecondary">System Uptime</Typography>
                <Typography variant="caption" color="error.main">
                  {stats.maintenanceRequests} Maintenance Requests
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Charts */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>Vehicle Distribution</Typography>
                <Box sx={{ height: 300, display: 'flex', justifyContent: 'center' }}>
                  <Doughnut data={vehicleTypeData} options={{ maintainAspectRatio: false }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>
          
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>User Roles</Typography>
                <Box sx={{ height: 300, display: 'flex', justifyContent: 'center' }}>
                  <Doughnut data={userRoleData} options={{ maintainAspectRatio: false }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Tabs */}
        <Card>
          <Tabs value={tabValue} onChange={handleTabChange} variant="fullWidth">
            <Tab label="Vehicles" icon={<DirectionsBus />} />
            <Tab label="Users" icon={<People />} />
          </Tabs>

          <TabPanel value={tabValue} index={0}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h6">Vehicle Management</Typography>
              <Button variant="contained" startIcon={<Add />} onClick={handleAddVehicle}>
                Add Vehicle
              </Button>
            </Box>
            
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Vehicle Number</TableCell>
                    <TableCell>Brand/Model</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Driver</TableCell>
                    <TableCell>Capacity</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {vehicles.map((vehicle) => (
                    <TableRow key={vehicle.id}>
                      <TableCell>{vehicle.vehicleNumber}</TableCell>
                      <TableCell>{vehicle.brand} {vehicle.model}</TableCell>
                      <TableCell>
                        <Chip label={vehicle.vehicleType?.replace('_', ' ')} size="small" />
                      </TableCell>
                      <TableCell>
                        <Chip 
                          label={vehicle.status} 
                          color={getStatusColor(vehicle.status)} 
                          size="small" 
                        />
                      </TableCell>
                      <TableCell>{vehicle.driverName || 'Unassigned'}</TableCell>
                      <TableCell>{vehicle.capacity}</TableCell>
                      <TableCell>
                        <IconButton onClick={() => handleEditVehicle(vehicle)} size="small">
                          <Edit />
                        </IconButton>
                        <IconButton 
                          onClick={() => handleDelete('vehicles', vehicle.id)} 
                          size="small"
                          color="error"
                        >
                          <Delete />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h6">User Management</Typography>
              <Button variant="contained" startIcon={<Add />} onClick={handleAddUser}>
                Add User
              </Button>
            </Box>
            
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Username</TableCell>
                    <TableCell>Full Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Role</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {users.map((userData) => (
                    <TableRow key={userData.id}>
                      <TableCell>{userData.username}</TableCell>
                      <TableCell>{userData.fullName}</TableCell>
                      <TableCell>{userData.email}</TableCell>
                      <TableCell>
                        <Chip 
                          label={userData.role} 
                          color={getRoleColor(userData.role)} 
                          size="small" 
                        />
                      </TableCell>
                      <TableCell>
                        <Chip 
                          label={userData.isActive ? 'Active' : 'Inactive'} 
                          color={userData.isActive ? 'success' : 'default'} 
                          size="small" 
                        />
                      </TableCell>
                      <TableCell>
                        <IconButton onClick={() => handleEditUser(userData)} size="small">
                          <Edit />
                        </IconButton>
                        <IconButton 
                          onClick={() => handleDelete('users', userData.id)} 
                          size="small"
                          color="error"
                        >
                          <Delete />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </TabPanel>
        </Card>
      </Container>

      {/* Add/Edit Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {selectedItem ? 'Edit' : 'Add'} {dialogType === 'vehicle' ? 'Vehicle' : 'User'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            {dialogType === 'vehicle' ? (
              <>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Vehicle Number"
                    value={formData.vehicleNumber || ''}
                    onChange={(e) => setFormData({...formData, vehicleNumber: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Brand"
                    value={formData.brand || ''}
                    onChange={(e) => setFormData({...formData, brand: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Model"
                    value={formData.model || ''}
                    onChange={(e) => setFormData({...formData, model: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth>
                    <InputLabel>Vehicle Type</InputLabel>
                    <Select
                      value={formData.vehicleType || ''}
                      onChange={(e) => setFormData({...formData, vehicleType: e.target.value})}
                    >
                      <MenuItem value="STUDENT_BUS">Student Bus</MenuItem>
                      <MenuItem value="TEACHER_BUS">Teacher Bus</MenuItem>
                      <MenuItem value="OFFICE_ADMIN_VEHICLE">Office Vehicle</MenuItem>
                      <MenuItem value="GENERAL_TRANSPORT">General Transport</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Capacity"
                    type="number"
                    value={formData.capacity || ''}
                    onChange={(e) => setFormData({...formData, capacity: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth>
                    <InputLabel>Status</InputLabel>
                    <Select
                      value={formData.status || ''}
                      onChange={(e) => setFormData({...formData, status: e.target.value})}
                    >
                      <MenuItem value="ACTIVE">Active</MenuItem>
                      <MenuItem value="INACTIVE">Inactive</MenuItem>
                      <MenuItem value="MAINTENANCE">Maintenance</MenuItem>
                      <MenuItem value="OUT_OF_SERVICE">Out of Service</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
              </>
            ) : (
              <>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Username"
                    value={formData.username || ''}
                    onChange={(e) => setFormData({...formData, username: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Full Name"
                    value={formData.fullName || ''}
                    onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Email"
                    type="email"
                    value={formData.email || ''}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth>
                    <InputLabel>Role</InputLabel>
                    <Select
                      value={formData.role || ''}
                      onChange={(e) => setFormData({...formData, role: e.target.value})}
                    >
                      <MenuItem value="STUDENT">Student</MenuItem>
                      <MenuItem value="TEACHER">Teacher</MenuItem>
                      <MenuItem value="DRIVER">Driver</MenuItem>
                      <MenuItem value="ADMIN">Admin</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Phone Number"
                    value={formData.phoneNumber || ''}
                    onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
                  />
                </Grid>
              </>
            )}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleSave} variant="contained">Save</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default AdminDashboard; 