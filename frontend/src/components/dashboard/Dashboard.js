import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Container,
  Grid,
  Card,
  CardContent,
  Box,
  Avatar,
  Menu,
  MenuItem,
  IconButton,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  DirectionsBus,
  Chat,
  LocationOn,
  Announcement,
  AccountCircle,
  ExitToApp,
  Refresh
} from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [stats, setStats] = useState({
    activeVehicles: 0,
    onlineDrivers: 0,
    activeUsers: 0,
    totalTrips: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

  useEffect(() => {
    fetchDashboardStats();
    const interval = setInterval(fetchDashboardStats, 30000);
    return () => clearInterval(interval);
  }, []);

  const fetchDashboardStats = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/dashboard/stats`);
      setStats(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch dashboard statistics');
      console.error('Error fetching stats:', err);
      setStats({
        activeVehicles: 12,
        onlineDrivers: 8,
        activeUsers: 145,
        totalTrips: 234
      });
    } finally {
      setLoading(false);
    }
  };

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
    handleClose();
  };

  const features = [
    {
      title: 'Vehicle Tracking',
      description: 'Track university vehicles in real-time',
      icon: <DirectionsBus fontSize="large" />,
      path: '/tracking',
      color: '#1976d2'
    },
    {
      title: 'Chat System',
      description: 'Communicate with other users and drivers',
      icon: <Chat fontSize="large" />,
      path: '/chat',
      color: '#388e3c'
    },
    {
      title: 'Live Location',
      description: 'View live vehicle locations on map',
      icon: <LocationOn fontSize="large" />,
      path: '/tracking',
      color: '#f57c00'
    }
  ];

  if (user?.role === 'ADMIN' || user?.role === 'DRIVER') {
    features.push({
      title: 'Announcements',
      description: 'Post and manage announcements',
      icon: <Announcement fontSize="large" />,
      path: '/announcements',
      color: '#d32f2f'
    });
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <DirectionsBus sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Vehicle Tracking System
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography variant="body2" sx={{ mr: 2 }}>
              {user?.username} ({user?.role})
            </Typography>
            <IconButton
              color="inherit"
              onClick={fetchDashboardStats}
              disabled={loading}
              sx={{ mr: 1 }}
            >
              <Refresh />
            </IconButton>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleMenu}
              color="inherit"
            >
              <AccountCircle />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorEl}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              <MenuItem onClick={handleLogout}>
                <ExitToApp sx={{ mr: 1 }} />
                Logout
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        {error && (
          <Alert severity="warning" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h4" gutterBottom>
                  Welcome, {user?.username}!
                </Typography>
                <Typography variant="h6" color="textSecondary" gutterBottom>
                  Role: {user?.role?.replace('_', ' ')}
                </Typography>
                <Typography variant="body1" paragraph>
                  Welcome to the University Vehicle Tracking System. Here you can track vehicles 
                  in real-time, communicate with drivers and other users, and stay updated with 
                  the latest announcements.
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          {features.map((feature, index) => (
            <Grid item xs={12} sm={6} md={4} key={index}>
              <Card 
                sx={{ 
                  height: '100%', 
                  cursor: 'pointer',
                  transition: 'transform 0.2s',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: 4
                  }
                }}
                onClick={() => navigate(feature.path)}
              >
                <CardContent sx={{ textAlign: 'center', py: 4 }}>
                  <Avatar
                    sx={{
                      bgcolor: feature.color,
                      width: 64,
                      height: 64,
                      mx: 'auto',
                      mb: 2
                    }}
                  >
                    {feature.icon}
                  </Avatar>
                  <Typography variant="h6" gutterBottom>
                    {feature.title}
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {feature.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        <Box sx={{ mt: 4 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Quick Stats
                {loading && <CircularProgress size={20} sx={{ ml: 2 }} />}
              </Typography>
              <Grid container spacing={3}>
                <Grid item xs={12} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="primary">
                      {stats.activeVehicles}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      Active Vehicles
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="success.main">
                      {stats.onlineDrivers}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      Online Drivers
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="warning.main">
                      {stats.activeUsers}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      Active Users
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={3}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" color="info.main">
                      {stats.totalTrips}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      Total Trips Today
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Box>
      </Container>
    </Box>
  );
};

export default Dashboard; 