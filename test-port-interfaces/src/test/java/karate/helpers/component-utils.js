/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/**
 * Utility functions for working with components in Karate tests
 */

function generateComponentId() {
  var uuid = Java.type('java.util.UUID').randomUUID();
  return 'COMP-' + uuid.toString().substring(0, 8);
}

function getComponentStatusString(status) {
  if (status === 'READY') return 'Ready';
  if (status === 'ACTIVE') return 'Active';
  if (status === 'INITIALIZING') return 'Initializing';
  if (status === 'CONCEPTION') return 'Conception';
  if (status === 'EMBRYONIC') return 'Embryonic';
  if (status === 'TERMINATED') return 'Terminated';
  if (status === 'ERROR') return 'Error';
  return 'Unknown';
}

function createTestComponent(componentId, name, type) {
  var id = componentId || generateComponentId();
  var componentName = name || 'Test Component';
  var componentType = type || 'STANDARD';
  
  var Component = Java.type('org.s8r.component.Component');
  return Component.createForTest(id, componentName, componentType);
}

function validateComponentStructure(component) {
  var validationErrors = [];
  
  if (!component.id) validationErrors.push('Component is missing ID');
  if (!component.name) validationErrors.push('Component is missing name');
  if (!component.status) validationErrors.push('Component is missing status');
  if (!component.type) validationErrors.push('Component is missing type');
  if (!component.creationTimestamp) validationErrors.push('Component is missing creationTimestamp');
  
  return {
    isValid: validationErrors.length === 0,
    errors: validationErrors
  };
}

// Export all functions
module.exports = {
  generateComponentId: generateComponentId,
  getComponentStatusString: getComponentStatusString,
  createTestComponent: createTestComponent,
  validateComponentStructure: validateComponentStructure
};