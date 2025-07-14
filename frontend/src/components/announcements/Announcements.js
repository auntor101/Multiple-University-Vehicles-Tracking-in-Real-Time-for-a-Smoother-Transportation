import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  CardActions,
  Button,
  Fab,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  AppBar,
  Toolbar,
  IconButton,
  Grid,
  Alert,
  CircularProgress,
  Avatar,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Menu,
  Switch,
  FormControlLabel
} from '@mui/material';
import {
  Add,
  ArrowBack,
  Announcement,
  Edit,
  Delete,
  MoreVert,
  PushPin,
  Visibility,
  Schedule,
  Person,
  Refresh
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { formatDistanceToNow } from 'date-fns';
import axios from 'axios';

const Announcements = () => {
  const [announcements, setAnnouncements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedAnnouncement, setSelectedAnnouncement] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    priority: 'NORMAL',
    targetRoles: [],
    isPinned: false,
    expiresAt: ''
  });
  const [menuAnchor, setMenuAnchor] = useState(null);
  const [filterRole, setFilterRole] = useState('ALL');
  const [showExpired, setShowExpired] = useState(false);
  
  const { user } = useAuth();
  const navigate = useNavigate();
  const canCreateAnnouncements = user?.role === 'ADMIN' || user?.role === 'DRIVER';

  const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

  const priorities = [
    { value: 'LOW', label: 'Low', color: 'info' },
    { value: 'NORMAL', label: 'Normal', color: 'primary' },
    { value: 'HIGH', label: 'High', color: 'warning' },
    { value: 'URGENT', label: 'Urgent', color: 'error' }
  ];

  const roles = [
    { value: 'STUDENT', label: 'Students' },
    { value: 'TEACHER', label: 'Teachers' },
    { value: 'DRIVER', label: 'Drivers' },
    { value: 'ADMIN', label: 'Admins' }
  ];

  useEffect(() => {
    fetchAnnouncements();
  }, []);

  const fetchAnnouncements = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE_URL}/announcements`);
      setAnnouncements(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch announcements');
      console.error('Error fetching announcements:', err);
      // Mock data for development
      setAnnouncements([
        {
          id: 1,
          title: 'University Bus Schedule Change',
          content: 'Starting Monday, the 8:00 AM bus from Main Campus to Downtown will be delayed by 15 minutes due to construction on University Ave. The new departure time will be 8:15 AM. This change will be in effect until further notice.',
          priority: 'HIGH',
          targetRoles: ['STUDENT', 'TEACHER'],
          isPinned: true,
          authorName: 'Admin User',
          authorRole: 'ADMIN',
          createdAt: '2024-01-20T10:00:00Z',
          expiresAt: '2024-02-20T23:59:59Z',
          views: 156
        },
        {
          id: 2,
          title: 'New Driver Safety Training',
          content: 'All drivers are required to attend the mandatory safety training session on January 25th at 2:00 PM in the Main Conference Room. Topics will include emergency procedures, passenger safety, and new traffic regulations.',
          priority: 'URGENT',
          targetRoles: ['DRIVER'],
          isPinned: false,
          authorName: 'Safety Officer',
          authorRole: 'ADMIN',
          createdAt: '2024-01-18T14:30:00Z',
          expiresAt: '2024-01-25T17:00:00Z',
          views: 23
        },
        {
          id: 3,
          title: 'Vehicle Maintenance Notice',
          content: 'Bus UNI-003 will be out of service for scheduled maintenance from January 22-24. Alternative transportation arrangements have been made for affected routes.',
          priority: 'NORMAL',
          targetRoles: ['STUDENT', 'TEACHER', 'DRIVER'],
          isPinned: false,
          authorName: 'Fleet Manager',
          authorRole: 'ADMIN',
          createdAt: '2024-01-17T09:15:00Z',
          expiresAt: '2024-01-25T00:00:00Z',
          views: 89
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateAnnouncement = () => {
    setSelectedAnnouncement(null);
    setFormData({
      title: '',
      content: '',
      priority: 'NORMAL',
      targetRoles: [],
      isPinned: false,
      expiresAt: ''
    });
    setOpenDialog(true);
  };

  const handleEditAnnouncement = (announcement) => {
    setSelectedAnnouncement(announcement);
    setFormData({
      title: announcement.title,
      content: announcement.content,
      priority: announcement.priority,
      targetRoles: announcement.targetRoles,
      isPinned: announcement.isPinned,
      expiresAt: announcement.expiresAt?.slice(0, 16) || ''
    });
    setOpenDialog(true);
    setMenuAnchor(null);
  };

  const handleDeleteAnnouncement = async (id) => {
    if (window.confirm('Are you sure you want to delete this announcement?')) {
      try {
        await axios.delete(`${API_BASE_URL}/announcements/${id}`);
        await fetchAnnouncements();
      } catch (err) {
        setError('Failed to delete announcement');
      }
    }
    setMenuAnchor(null);
  };

  const handleSaveAnnouncement = async () => {
    try {
      const announcementData = {
        ...formData,
        expiresAt: formData.expiresAt ? new Date(formData.expiresAt).toISOString() : null,
        authorName: user.username || user.fullName,
        authorRole: user.role
      };

      if (selectedAnnouncement) {
        await axios.put(`${API_BASE_URL}/announcements/${selectedAnnouncement.id}`, announcementData);
      } else {
        await axios.post(`${API_BASE_URL}/announcements`, announcementData);
      }

      await fetchAnnouncements();
      setOpenDialog(false);
    } catch (err) {
      setError('Failed to save announcement');
    }
  };

  const handleTogglePin = async (id, isPinned) => {
    try {
      await axios.patch(`${API_BASE_URL}/announcements/${id}/pin`, { isPinned: !isPinned });
      await fetchAnnouncements();
    } catch (err) {
      setError('Failed to update pin status');
    }
    setMenuAnchor(null);
  };

  const filteredAnnouncements = announcements.filter(announcement => {
    const roleMatch = filterRole === 'ALL' || announcement.targetRoles.includes(filterRole) || announcement.targetRoles.includes(user.role);
    const expiredMatch = showExpired || !announcement.expiresAt || new Date(announcement.expiresAt) > new Date();
    return roleMatch && expiredMatch;
  }).sort((a, b) => {
    // Sort by pinned first, then by creation date
    if (a.isPinned && !b.isPinned) return -1;
    if (!a.isPinned && b.isPinned) return 1;
    return new Date(b.createdAt) - new Date(a.createdAt);
  });

  const getPriorityColor = (priority) => {
    const p = priorities.find(p => p.value === priority);
    return p ? p.color : 'default';
  };

  const isExpired = (expiresAt) => {
    return expiresAt && new Date(expiresAt) < new Date();
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
          <Announcement sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Announcements
          </Typography>
          <Button
            color="inherit"
            startIcon={<Refresh />}
            onClick={fetchAnnouncements}
          >
            Refresh
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ mt: 2, mb: 4 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {/* Filters */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} sm={4}>
                <FormControl fullWidth size="small">
                  <InputLabel>Filter by Role</InputLabel>
                  <Select
                    value={filterRole}
                    onChange={(e) => setFilterRole(e.target.value)}
                    label="Filter by Role"
                  >
                    <MenuItem value="ALL">All Roles</MenuItem>
                    {roles.map(role => (
                      <MenuItem key={role.value} value={role.value}>
                        {role.label}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={showExpired}
                      onChange={(e) => setShowExpired(e.target.checked)}
                    />
                  }
                  label="Show Expired"
                />
              </Grid>
              <Grid item xs={12} sm={4}>
                <Typography variant="body2" color="textSecondary">
                  Showing {filteredAnnouncements.length} announcements
                </Typography>
              </Grid>
            </Grid>
          </CardContent>
        </Card>

        {/* Announcements List */}
        {filteredAnnouncements.length === 0 ? (
          <Alert severity="info">
            No announcements found. {canCreateAnnouncements && 'Create the first announcement!'}
          </Alert>
        ) : (
          <Grid container spacing={3}>
            {filteredAnnouncements.map((announcement) => (
              <Grid item xs={12} key={announcement.id}>
                <Card 
                  sx={{ 
                    position: 'relative',
                    opacity: isExpired(announcement.expiresAt) ? 0.7 : 1,
                    border: announcement.isPinned ? '2px solid' : 'none',
                    borderColor: announcement.isPinned ? 'primary.main' : 'transparent'
                  }}
                >
                  {announcement.isPinned && (
                    <Chip
                      icon={<PushPin />}
                      label="Pinned"
                      color="primary"
                      size="small"
                      sx={{ position: 'absolute', top: 8, right: 8, zIndex: 1 }}
                    />
                  )}
                  
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                      <Box>
                        <Typography variant="h6" gutterBottom>
                          {announcement.title}
                        </Typography>
                        <Box display="flex" alignItems="center" gap={1} mb={1}>
                          <Chip
                            label={announcement.priority}
                            color={getPriorityColor(announcement.priority)}
                            size="small"
                          />
                          {announcement.targetRoles.map(role => (
                            <Chip
                              key={role}
                              label={roles.find(r => r.value === role)?.label || role}
                              variant="outlined"
                              size="small"
                            />
                          ))}
                          {isExpired(announcement.expiresAt) && (
                            <Chip
                              label="Expired"
                              color="error"
                              variant="outlined"
                              size="small"
                            />
                          )}
                        </Box>
                      </Box>
                      
                      {canCreateAnnouncements && (
                        <IconButton
                          onClick={(e) => setMenuAnchor(e.currentTarget)}
                          size="small"
                        >
                          <MoreVert />
                        </IconButton>
                      )}
                    </Box>

                    <Typography variant="body1" paragraph>
                      {announcement.content}
                    </Typography>

                    <Divider sx={{ my: 2 }} />

                    <Box display="flex" alignItems="center" justifyContent="space-between">
                      <Box display="flex" alignItems="center" gap={2}>
                        <Box display="flex" alignItems="center" gap={1}>
                          <Avatar sx={{ width: 24, height: 24, fontSize: '0.75rem' }}>
                            {announcement.authorName.charAt(0)}
                          </Avatar>
                          <Typography variant="caption">
                            {announcement.authorName} ({announcement.authorRole})
                          </Typography>
                        </Box>
                        
                        <Box display="flex" alignItems="center" gap={1}>
                          <Schedule sx={{ fontSize: 16 }} />
                          <Typography variant="caption">
                            {formatDistanceToNow(new Date(announcement.createdAt), { addSuffix: true })}
                          </Typography>
                        </Box>
                      </Box>

                      <Box display="flex" alignItems="center" gap={2}>
                        {announcement.expiresAt && (
                          <Typography variant="caption" color="textSecondary">
                            Expires: {new Date(announcement.expiresAt).toLocaleDateString()}
                          </Typography>
                        )}
                        
                        <Box display="flex" alignItems="center" gap={0.5}>
                          <Visibility sx={{ fontSize: 16 }} />
                          <Typography variant="caption">
                            {announcement.views || 0}
                          </Typography>
                        </Box>
                      </Box>
                    </Box>
                  </CardContent>

                  <Menu
                    anchorEl={menuAnchor}
                    open={Boolean(menuAnchor)}
                    onClose={() => setMenuAnchor(null)}
                  >
                    <MenuItem onClick={() => handleEditAnnouncement(announcement)}>
                      <Edit sx={{ mr: 1 }} fontSize="small" />
                      Edit
                    </MenuItem>
                    <MenuItem onClick={() => handleTogglePin(announcement.id, announcement.isPinned)}>
                      <PushPin sx={{ mr: 1 }} fontSize="small" />
                      {announcement.isPinned ? 'Unpin' : 'Pin'}
                    </MenuItem>
                    <MenuItem 
                      onClick={() => handleDeleteAnnouncement(announcement.id)}
                      sx={{ color: 'error.main' }}
                    >
                      <Delete sx={{ mr: 1 }} fontSize="small" />
                      Delete
                    </MenuItem>
                  </Menu>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}

        {/* Floating Action Button */}
        {canCreateAnnouncements && (
          <Fab
            color="primary"
            aria-label="add announcement"
            onClick={handleCreateAnnouncement}
            sx={{ position: 'fixed', bottom: 16, right: 16 }}
          >
            <Add />
          </Fab>
        )}
      </Container>

      {/* Create/Edit Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          {selectedAnnouncement ? 'Edit Announcement' : 'Create New Announcement'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Title"
                value={formData.title}
                onChange={(e) => setFormData({...formData, title: e.target.value})}
                required
              />
            </Grid>
            
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={6}
                label="Content"
                value={formData.content}
                onChange={(e) => setFormData({...formData, content: e.target.value})}
                required
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Priority</InputLabel>
                <Select
                  value={formData.priority}
                  onChange={(e) => setFormData({...formData, priority: e.target.value})}
                  label="Priority"
                >
                  {priorities.map(priority => (
                    <MenuItem key={priority.value} value={priority.value}>
                      {priority.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Target Roles</InputLabel>
                <Select
                  multiple
                  value={formData.targetRoles}
                  onChange={(e) => setFormData({...formData, targetRoles: e.target.value})}
                  label="Target Roles"
                  renderValue={(selected) => (
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                      {selected.map((value) => (
                        <Chip 
                          key={value} 
                          label={roles.find(r => r.value === value)?.label || value} 
                          size="small" 
                        />
                      ))}
                    </Box>
                  )}
                >
                  {roles.map(role => (
                    <MenuItem key={role.value} value={role.value}>
                      {role.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                type="datetime-local"
                label="Expires At"
                value={formData.expiresAt}
                onChange={(e) => setFormData({...formData, expiresAt: e.target.value})}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={formData.isPinned}
                    onChange={(e) => setFormData({...formData, isPinned: e.target.checked})}
                  />
                }
                label="Pin to top"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button 
            onClick={handleSaveAnnouncement} 
            variant="contained"
            disabled={!formData.title || !formData.content || formData.targetRoles.length === 0}
          >
            {selectedAnnouncement ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Announcements; 