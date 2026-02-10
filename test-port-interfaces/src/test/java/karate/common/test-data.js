/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/**
 * Test data generator utilities for Karate tests
 * 
 * This file contains utility functions for generating test data
 * for use in Karate port interface tests.
 */

/**
 * Generates a random UUID string
 * @returns {string} A random UUID
 */
function randomUuid() {
  return Java.type('java.util.UUID').randomUUID().toString();
}

/**
 * Generates a shortened UUID (first 8 characters)
 * @returns {string} A shortened UUID
 */
function shortUuid() {
  return randomUuid().substring(0, 8);
}

/**
 * Generates a random string of specified length
 * @param {number} length - The length of the string to generate
 * @returns {string} A random string
 */
function randomString(length) {
  if (!length) length = 10;
  var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  var result = '';
  for (var i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * Generates a random integer between min and max (inclusive)
 * @param {number} min - The minimum value
 * @param {number} max - The maximum value
 * @returns {number} A random integer
 */
function randomInt(min, max) {
  if (!min) min = 1;
  if (!max) max = 1000;
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * Generates common test file paths for use with filesystem tests
 * @param {string} baseDir - The base directory path
 * @returns {Object} An object containing various file paths
 */
function generateFilePaths(baseDir) {
  var id = shortUuid();
  return {
    textFile: baseDir + '/test-file-' + id + '.txt',
    jsonFile: baseDir + '/test-data-' + id + '.json',
    xmlFile: baseDir + '/config-' + id + '.xml',
    directory: baseDir + '/test-dir-' + id,
    nestedFile: baseDir + '/test-dir-' + id + '/nested-file.txt',
    backupFile: baseDir + '/backup-' + id + '.bak'
  };
}

/**
 * Generates sample component data for testing
 * @param {string} id - Optional component ID, random if not provided
 * @returns {Object} A component object
 */
function generateComponentData(id) {
  if (!id) id = 'comp-' + shortUuid();
  
  return {
    id: id,
    name: 'Test Component ' + shortUuid(),
    state: 'ACTIVE',
    properties: {
      createdAt: new Date().toISOString(),
      priority: randomInt(1, 10),
      category: ['test', 'component', 'sample'][randomInt(0, 2)]
    },
    version: '1.' + randomInt(0, 9) + '.' + randomInt(0, 9)
  };
}

/**
 * Generates sample user data for testing
 * @param {string} id - Optional user ID, random if not provided
 * @returns {Object} A user object
 */
function generateUserData(id) {
  if (!id) id = 'user-' + shortUuid();
  
  var firstNames = ['John', 'Jane', 'Alice', 'Bob', 'Charlie', 'Diana', 'Edward', 'Fiona'];
  var lastNames = ['Smith', 'Johnson', 'Williams', 'Jones', 'Brown', 'Davis', 'Miller', 'Wilson'];
  
  var firstName = firstNames[randomInt(0, firstNames.length - 1)];
  var lastName = lastNames[randomInt(0, lastNames.length - 1)];
  
  return {
    id: id,
    username: (firstName + lastName).toLowerCase() + randomInt(1, 999),
    firstName: firstName,
    lastName: lastName,
    email: firstName.toLowerCase() + '.' + lastName.toLowerCase() + '@example.com',
    active: Math.random() > 0.2, // 80% chance of being active
    role: ['USER', 'ADMIN', 'GUEST'][randomInt(0, 2)],
    preferences: {
      theme: ['light', 'dark', 'system'][randomInt(0, 2)],
      notifications: Math.random() > 0.3,
      language: ['en', 'fr', 'es', 'de'][randomInt(0, 3)]
    }
  };
}

/**
 * Generates sample configuration data for testing
 * @returns {Object} A configuration object
 */
function generateConfigData() {
  return {
    'app.name': 'Samstraumr',
    'app.version': '2.' + randomInt(0, 9) + '.' + randomInt(0, 9),
    'app.debug': '' + (Math.random() > 0.5),
    'app.maxConnections': '' + randomInt(50, 200),
    'app.tags': 'java,testing,karate',
    'module.cache.enabled': 'true',
    'module.cache.size': '' + randomInt(512, 2048),
    'module.auth.enabled': '' + (Math.random() > 0.3)
  };
}

/**
 * Generates sample event data for testing
 * @param {string} type - Optional event type, random if not provided
 * @returns {Object} An event object
 */
function generateEventData(type) {
  if (!type) {
    var types = ['COMPONENT_CREATED', 'COMPONENT_UPDATED', 'COMPONENT_DELETED', 'CACHE_CLEARED', 'CONFIG_CHANGED'];
    type = types[randomInt(0, types.length - 1)];
  }
  
  return {
    id: 'event-' + shortUuid(),
    type: type,
    timestamp: new Date().toISOString(),
    source: 'test-generator',
    payload: {
      entityId: 'entity-' + shortUuid(),
      action: type.split('_')[1],
      user: 'test-user-' + shortUuid()
    }
  };
}