import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const Announcements = () => {
  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>
          Announcements
        </Typography>
        <Typography variant="body1">
          Announcement board for drivers and admins coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default Announcements; 