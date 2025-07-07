import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const DriverDashboard = () => {
  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>
          Driver Dashboard
        </Typography>
        <Typography variant="body1">
          Driver dashboard with vehicle status, route management, and passenger info coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default DriverDashboard; 