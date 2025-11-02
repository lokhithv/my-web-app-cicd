const express = require('express');
const app = express();
const PORT = process.env.PORT || 8080;

app.use(express.json());

// Home endpoint
app.get('/', (req, res) => {
  res.json({
    message: 'Welcome to CI/CD Pipeline Demo!',
    status: 'success',
    version: '1.0.0'
  });
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    application: 'my-web-app'
  });
});

// Info endpoint
app.get('/api/info', (req, res) => {
  res.json({
    name: 'My Web Application',
    description: 'Deployed via Jenkins CI/CD Pipeline',
    environment: process.env.ENVIRONMENT || 'development',
    timestamp: Date.now()
  });
});

const server = app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

module.exports = { app, server };