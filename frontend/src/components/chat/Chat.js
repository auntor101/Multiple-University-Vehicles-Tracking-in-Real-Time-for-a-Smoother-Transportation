import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const Chat = () => {
  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>
          Chat System
        </Typography>
        <Typography variant="body1">
          Real-time chat with personal messaging, group chats, and global chat coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default Chat; 