/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/**
 * Common utilities for validating operation results
 * 
 * This file contains helper functions for validating results
 * returned by various port adapters.
 */

/**
 * Transforms a ValidationResult object into a JavaScript-friendly object
 * @param {Object} result - The ValidationResult object from the ValidationPort
 * @returns {Object} A JavaScript object with valid flag and errors array
 */
function processValidationResult(result) {
  return { 
    valid: result.isValid(), 
    errors: result.getErrors() 
  };
}

/**
 * Checks the success of an operation result
 * @param {Object} result - The operation result to check
 * @returns {boolean} True if the operation was successful
 */
function isSuccessful(result) {
  return result && result.isSuccessful && result.isSuccessful();
}

/**
 * Extracts error information from an operation result
 * @param {Object} result - The operation result
 * @returns {Object} Error details including message and exception if available
 */
function getErrorDetails(result) {
  if (!result) return { message: 'No result object provided' };
  
  return {
    message: result.getErrorMessage ? 
      (result.getErrorMessage().isPresent() ? result.getErrorMessage().get() : null) : null,
    exception: result.getException ? 
      (result.getException().isPresent() ? result.getException().get() : null) : null
  };
}

/**
 * Extracts a specific attribute from an operation result
 * @param {Object} result - The operation result
 * @param {string} attributeName - The name of the attribute to extract
 * @returns {any} The attribute value or null if not found
 */
function getAttribute(result, attributeName) {
  if (!result || !result.getAttributes || !result.getAttributes()) return null;
  return result.getAttributes().get(attributeName);
}

/**
 * Extracts all attributes from an operation result
 * @param {Object} result - The operation result
 * @returns {Object} All attributes as a JavaScript object
 */
function getAllAttributes(result) {
  if (!result || !result.getAttributes || !result.getAttributes()) return {};
  
  // Convert Java Map to JavaScript object
  var attributes = {};
  var keys = result.getAttributes().keySet().toArray();
  for (var i = 0; i < keys.length; i++) {
    var key = keys[i];
    attributes[key] = result.getAttributes().get(key);
  }
  return attributes;
}

/**
 * Checks if an Optional result has a value
 * @param {Object} optional - The Optional object to check
 * @returns {boolean} True if the Optional has a value
 */
function optionalHasValue(optional) {
  return optional && optional.isPresent && optional.isPresent();
}

/**
 * Gets the value from an Optional, with an optional default
 * @param {Object} optional - The Optional object
 * @param {any} defaultValue - The default value to return if Optional is empty
 * @returns {any} The value from the Optional or the default value
 */
function optionalValue(optional, defaultValue) {
  if (optional && optional.isPresent && optional.isPresent()) {
    return optional.get();
  }
  return defaultValue;
}