import React, { useState, useEffect, useRef } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  TextField,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Chip,
  AppBar,
  Toolbar,
  IconButton,
  Grid,
  Card,
  CardContent,
  Divider,
  CircularProgress,
  Alert
} from '@mui/material';
import {
  Send,
  ArrowBack,
  Chat as ChatIcon,
  Group,
  Person,
  Public,
  Refresh
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import io from 'socket.io-client';

const Chat = () => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [activeChannel, setActiveChannel] = useState('general');
  const [onlineUsers, setOnlineUsers] = useState([]);
  const [socket, setSocket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const messagesEndRef = useRef(null);
  
  const { user } = useAuth();
  const navigate = useNavigate();

  const channels = [
    { id: 'general', name: 'General', icon: <Public />, description: 'General discussions' },
    { id: 'drivers', name: 'Drivers', icon: <Group />, description: 'Driver communications' },
    { id: 'students', name: 'Students', icon: <Group />, description: 'Student discussions' },
    { id: 'announcements', name: 'Announcements', icon: <ChatIcon />, description: 'Official announcements' }
  ];

  useEffect(() => {
    // Initialize socket connection
    const newSocket = io(process.env.REACT_APP_API_BASE_URL?.replace('/api', '') || 'http://localhost:8080', {
      auth: {
        token: localStorage.getItem('token')
      }
    });

    newSocket.on('connect', () => {
      console.log('Connected to chat server');
      setSocket(newSocket);
      setLoading(false);
      
      // Join the active channel
      newSocket.emit('join-channel', { channel: activeChannel, user });
    });

    newSocket.on('disconnect', () => {
      console.log('Disconnected from chat server');
    });

    newSocket.on('message', (message) => {
      setMessages(prev => [...prev, message]);
    });

    newSocket.on('users-online', (users) => {
      setOnlineUsers(users);
    });

    newSocket.on('chat-history', (history) => {
      setMessages(history);
    });

    newSocket.on('error', (error) => {
      setError(error.message);
    });

    return () => {
      newSocket.close();
    };
  }, [activeChannel, user]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (!newMessage.trim() || !socket) return;

    const messageData = {
      id: Date.now(),
      text: newMessage.trim(),
      channel: activeChannel,
      sender: {
        id: user.id,
        username: user.username,
        role: user.role
      },
      timestamp: new Date().toISOString()
    };

    socket.emit('send-message', messageData);
    setNewMessage('');
  };

  const handleChannelChange = (channelId) => {
    if (socket && channelId !== activeChannel) {
      socket.emit('leave-channel', { channel: activeChannel });
      socket.emit('join-channel', { channel: channelId, user });
      setActiveChannel(channelId);
      setMessages([]);
    }
  };

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    
    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffMins < 1440) return `${Math.floor(diffMins / 60)}h ago`;
    return date.toLocaleDateString();
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

  const getAvatarColor = (username) => {
    const colors = ['#f44336', '#e91e63', '#9c27b0', '#673ab7', '#3f51b5', '#2196f3', '#03a9f4', '#00bcd4'];
    const index = username.charCodeAt(0) % colors.length;
    return colors[index];
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
          <ChatIcon sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Chat System - {channels.find(c => c.id === activeChannel)?.name}
          </Typography>
          <Button
            color="inherit"
            startIcon={<Refresh />}
            onClick={() => window.location.reload()}
          >
            Refresh
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 2, mb: 2 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Grid container spacing={2} sx={{ height: 'calc(100vh - 200px)' }}>
          {/* Channel Sidebar */}
          <Grid item xs={12} md={3}>
            <Paper sx={{ height: '100%', p: 2 }}>
              <Typography variant="h6" gutterBottom>
                Channels
              </Typography>
              <List>
                {channels.map((channel) => (
                  <ListItem
                    key={channel.id}
                    button
                    selected={activeChannel === channel.id}
                    onClick={() => handleChannelChange(channel.id)}
                    sx={{
                      borderRadius: 1,
                      mb: 1,
                      '&.Mui-selected': {
                        backgroundColor: 'primary.light',
                        '&:hover': {
                          backgroundColor: 'primary.light',
                        },
                      },
                    }}
                  >
                    <ListItemAvatar>
                      <Avatar sx={{ bgcolor: 'primary.main' }}>
                        {channel.icon}
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      primary={channel.name}
                      secondary={channel.description}
                    />
                  </ListItem>
                ))}
              </List>

              <Divider sx={{ my: 2 }} />

              <Typography variant="h6" gutterBottom>
                Online Users ({onlineUsers.length})
              </Typography>
              <List dense>
                {onlineUsers.map((onlineUser) => (
                  <ListItem key={onlineUser.id}>
                    <ListItemAvatar>
                      <Avatar 
                        sx={{ 
                          bgcolor: getAvatarColor(onlineUser.username),
                          width: 32,
                          height: 32,
                          fontSize: '0.875rem'
                        }}
                      >
                        {onlineUser.username.charAt(0).toUpperCase()}
                      </Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      primary={
                        <Box display="flex" alignItems="center">
                          <Typography variant="body2" sx={{ mr: 1 }}>
                            {onlineUser.username}
                          </Typography>
                          <Chip
                            label={onlineUser.role}
                            size="small"
                            color={getRoleColor(onlineUser.role)}
                            variant="outlined"
                          />
                        </Box>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            </Paper>
          </Grid>

          {/* Chat Area */}
          <Grid item xs={12} md={9}>
            <Paper sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              {/* Messages */}
              <Box sx={{ flexGrow: 1, overflow: 'auto', p: 2 }}>
                {messages.length === 0 ? (
                  <Box textAlign="center" py={4}>
                    <Typography color="textSecondary">
                      No messages yet. Start the conversation!
                    </Typography>
                  </Box>
                ) : (
                  messages.map((message) => (
                    <Box key={message.id} sx={{ mb: 2 }}>
                      <Card 
                        variant="outlined" 
                        sx={{ 
                          bgcolor: message.sender.id === user.id ? 'primary.light' : 'background.default',
                          ml: message.sender.id === user.id ? 4 : 0,
                          mr: message.sender.id === user.id ? 0 : 4,
                        }}
                      >
                        <CardContent sx={{ py: 1, '&:last-child': { pb: 1 } }}>
                          <Box display="flex" alignItems="center" justifyContent="space-between" mb={1}>
                            <Box display="flex" alignItems="center">
                              <Avatar 
                                sx={{ 
                                  bgcolor: getAvatarColor(message.sender.username),
                                  width: 24,
                                  height: 24,
                                  mr: 1,
                                  fontSize: '0.75rem'
                                }}
                              >
                                {message.sender.username.charAt(0).toUpperCase()}
                              </Avatar>
                              <Typography variant="subtitle2" sx={{ mr: 1 }}>
                                {message.sender.username}
                              </Typography>
                              <Chip
                                label={message.sender.role}
                                size="small"
                                color={getRoleColor(message.sender.role)}
                                variant="outlined"
                              />
                            </Box>
                            <Typography variant="caption" color="textSecondary">
                              {formatTimestamp(message.timestamp)}
                            </Typography>
                          </Box>
                          <Typography variant="body2">
                            {message.text}
                          </Typography>
                        </CardContent>
                      </Card>
                    </Box>
                  ))
                )}
                <div ref={messagesEndRef} />
              </Box>

              {/* Message Input */}
              <Box component="form" onSubmit={handleSendMessage} sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
                <Box display="flex" gap={2}>
                  <TextField
                    fullWidth
                    variant="outlined"
                    placeholder={`Type a message in ${channels.find(c => c.id === activeChannel)?.name}...`}
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    disabled={!socket}
                    multiline
                    maxRows={3}
                  />
                  <Button
                    type="submit"
                    variant="contained"
                    endIcon={<Send />}
                    disabled={!newMessage.trim() || !socket}
                    sx={{ minWidth: 120 }}
                  >
                    Send
                  </Button>
                </Box>
              </Box>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};

export default Chat; 