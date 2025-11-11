function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'dev'; // default environment
  }
  
  // Base configuration that applies to all environments
  var config = {
    s8rVersion: '3.0.2',
    baseUrl: 'http://localhost:8080',
    timeoutMs: 5000,
    
    // Helper utility
    generateId: function() {
      var uuid = java.util.UUID.randomUUID();
      return uuid.toString().substring(0, 8);
    }
  };
  
  // Environment-specific configuration
  if (env === 'dev') {
    config.mockMode = true;
    config.logLevel = 'debug';
  } else if (env === 'test') {
    config.mockMode = false;
    config.baseUrl = 'http://test-server:8080';
    config.logLevel = 'info';
  } else if (env === 'ci') {
    config.mockMode = true;
    config.baseUrl = 'http://localhost:8080';
    config.logLevel = 'warn';
  }
  
  // Add request/response logging
  karate.configure('logPrettyRequest', true);
  karate.configure('logPrettyResponse', true);
  
  return config;
}