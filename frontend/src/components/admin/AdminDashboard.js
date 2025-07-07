import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const AdminDashboard = () => {
  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>
          Admin Dashboard
        </Typography>
        <Typography variant="body1">
          Admin dashboard with vehicle management, user management, and system overview coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default AdminDashboard; 