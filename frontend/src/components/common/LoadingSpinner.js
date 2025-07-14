import React from 'react';
import { Box, CircularProgress, Typography, Card, CardContent } from '@mui/material';

const LoadingSpinner = ({ message = 'Loading...' }) => {
  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      minHeight="300px"
      flexDirection="column"
    >
      <CircularProgress size={60} />
      <Typography variant="h6" color="textSecondary" sx={{ mt: 2 }}>
        {message}
      </Typography>
    </Box>
  );
};

const LoadingCard = ({ title, message = 'Loading data...' }) => {
  return (
    <Card>
      <CardContent>
        {title && (
          <Typography variant="h6" gutterBottom>
            {title}
          </Typography>
        )}
        <LoadingSpinner message={message} />
      </CardContent>
    </Card>
  );
};

export default LoadingSpinner;
export { LoadingCard };
