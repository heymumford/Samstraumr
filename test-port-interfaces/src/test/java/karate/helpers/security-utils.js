/**
 * Utility functions for working with security in Karate tests
 */

function generateAuthToken(username, roles) {
  var base64 = Java.type('java.util.Base64');
  var encoder = base64.getEncoder();
  
  var currentTime = new Date().getTime();
  var expiryTime = currentTime + (30 * 60 * 1000); // 30 minutes
  
  var tokenData = {
    sub: username,
    roles: roles || ['USER'],
    iat: Math.floor(currentTime / 1000),
    exp: Math.floor(expiryTime / 1000)
  };
  
  var tokenString = JSON.stringify(tokenData);
  var encodedToken = encoder.encodeToString(tokenString.getBytes('UTF-8'));
  
  return 'Bearer ' + encodedToken;
}

function createSecurityEvent(eventType, username, details) {
  return {
    eventType: eventType,
    timestamp: new Date().toISOString(),
    username: username,
    sourceIp: '127.0.0.1',
    details: details || {}
  };
}

function validateAuthenticationEvent(event) {
  var validationErrors = [];
  
  if (!event.eventType) validationErrors.push('Event is missing eventType');
  if (!event.timestamp) validationErrors.push('Event is missing timestamp');
  if (!event.username) validationErrors.push('Event is missing username');
  if (!event.sourceIp) validationErrors.push('Event is missing sourceIp');
  
  return {
    isValid: validationErrors.length === 0,
    errors: validationErrors
  };
}

// Export all functions
module.exports = {
  generateAuthToken: generateAuthToken,
  createSecurityEvent: createSecurityEvent,
  validateAuthenticationEvent: validateAuthenticationEvent
};