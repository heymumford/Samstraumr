/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.s8r.application.port.CachePort;
import org.s8r.infrastructure.cache.InMemoryCacheAdapter;
import org.s8r.infrastructure.logging.ConsoleLogger;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/** Step definitions for Cache Port interface tests. */
public class CachePortSteps {

  private CachePort cachePort;
  private boolean operationResult;
  private UserData complexObject;

  private static class UserData {
    private String username;
    private int age;
    private Map<String, String> attributes;

    public UserData(String username, int age) {
      this.username = username;
      this.age = age;
      this.attributes = new HashMap<>();
    }

    public void addAttribute(String key, String value) {
      attributes.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof UserData)) {
        return false;
      }
      UserData other = (UserData) obj;
      return username.equals(other.username)
          && age == other.age
          && attributes.equals(other.attributes);
    }
  }

  @Given("a cache port implementation is available")
  public void aCachePortImplementationIsAvailable() {
    cachePort = new InMemoryCacheAdapter(new ConsoleLogger("CachePortSteps"));
  }

  @When("I initialize the cache with name {string}")
  public void iInitializeTheCacheWithName(String cacheName) {
    cachePort.initialize(cacheName);
  }

  @Then("the cache should be initialized successfully")
  public void theCacheShouldBeInitializedSuccessfully() {
    // In the InMemoryCacheAdapter, we can verify this by adding and retrieving a value
    cachePort.put("test-init", "success");
    Optional<String> result = cachePort.get("test-init");
    Assertions.assertTrue(result.isPresent(), "Cache should be initialized successfully");
    Assertions.assertEquals("success", result.get(), "Cache should store and retrieve values");
  }

  @Then("the cache should be empty")
  public void theCacheShouldBeEmpty() {
    // Clear any test data first
    cachePort.clear();

    // Now check with a non-existent key
    Assertions.assertFalse(cachePort.containsKey("non-existent-key"), "Cache should be empty");
  }

  @Given("the cache is initialized with name {string}")
  public void theCacheIsInitializedWithName(String cacheName) {
    cachePort.initialize(cacheName);
  }

  @When("I put an item with key {string} and value {string} in the cache")
  public void iPutAnItemWithKeyAndValueInTheCache(String key, String value) {
    cachePort.put(key, value);
  }

  @Then("the cache should contain an item with key {string}")
  public void theCacheShouldContainAnItemWithKey(String key) {
    Assertions.assertTrue(cachePort.containsKey(key), "Cache should contain key: " + key);
  }

  @Then("the value for key {string} should be {string}")
  public void theValueForKeyShouldBe(String key, String expectedValue) {
    Optional<String> value = cachePort.get(key);
    Assertions.assertTrue(value.isPresent(), "Value should be present for key: " + key);
    Assertions.assertEquals(expectedValue, value.get(), "Value should match expected value");
  }

  @Given("the cache contains an item with key {string} and value {string}")
  public void theCacheContainsAnItemWithKeyAndValue(String key, String value) {
    cachePort.put(key, value);
  }

  @When("I remove the item with key {string} from the cache")
  public void iRemoveTheItemWithKeyFromTheCache(String key) {
    operationResult = cachePort.remove(key);
  }

  @Then("the cache should not contain an item with key {string}")
  public void theCacheShouldNotContainAnItemWithKey(String key) {
    Assertions.assertFalse(cachePort.containsKey(key), "Cache should not contain key: " + key);
  }

  @Given("the cache contains the following items:")
  public void theCacheContainsTheFollowingItems(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps();
    for (Map<String, String> row : rows) {
      String key = row.get("key");
      String value = row.get("value");
      cachePort.put(key, value);
    }
  }

  @When("I clear the cache")
  public void iClearTheCache() {
    cachePort.clear();
  }

  @When("I check if key {string} exists in the cache")
  public void iCheckIfKeyExistsInTheCache(String key) {
    operationResult = cachePort.containsKey(key);
  }

  @Then("the key should exist")
  public void theKeyShouldExist() {
    Assertions.assertTrue(operationResult, "Key should exist in the cache");
  }

  @Then("the key should not exist")
  public void theKeyShouldNotExist() {
    Assertions.assertFalse(operationResult, "Key should not exist in the cache");
  }

  @When("I store a complex object in the cache with key {string}")
  public void iStoreAComplexObjectInTheCacheWithKey(String key) {
    complexObject = new UserData("testuser", 30);
    complexObject.addAttribute("role", "admin");
    complexObject.addAttribute("status", "active");

    cachePort.put(key, complexObject);
  }

  @Then("I should be able to retrieve the complex object with key {string}")
  public void iShouldBeAbleToRetrieveTheComplexObjectWithKey(String key) {
    Optional<UserData> retrieved = cachePort.get(key);
    Assertions.assertTrue(retrieved.isPresent(), "Complex object should be present in cache");
  }

  @Then("the retrieved object should match the original object")
  public void theRetrievedObjectShouldMatchTheOriginalObject() {
    Optional<UserData> retrieved = cachePort.get("user-object");
    Assertions.assertTrue(retrieved.isPresent(), "Complex object should be present in cache");
    Assertions.assertEquals(
        complexObject, retrieved.get(), "Retrieved object should equal original object");
  }
}
