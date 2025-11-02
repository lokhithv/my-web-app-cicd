const request = require('supertest');
const { app, server } = require('./server');

describe('API Endpoints', () => {
  afterAll((done) => {
    server.close(done);
  });

  test('GET / should return welcome message', async () => {
    const response = await request(app).get('/');
    expect(response.statusCode).toBe(200);
    expect(response.body.message).toBe('Welcome to CI/CD Pipeline Demo!');
    expect(response.body.status).toBe('success');
  });

  test('GET /health should return UP status', async () => {
    const response = await request(app).get('/health');
    expect(response.statusCode).toBe(200);
    expect(response.body.status).toBe('UP');
  });

  test('GET /api/info should return app information', async () => {
    const response = await request(app).get('/api/info');
    expect(response.statusCode).toBe(200);
    expect(response.body.name).toBe('My Web Application');
    expect(response.body).toHaveProperty('timestamp');
  });
});