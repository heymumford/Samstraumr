# Environment API Migration Guide

## Overview

This guide outlines the changes to the Environment API in the S8r framework. The migration involves
transitioning from the `getValue`/`setValue` methods to the new `getParameter`/`setParameter` methods.

## API Changes

The Environment class API has been updated to better reflect the purpose of the class:

| Old API       | New API           | Description                                |
|---------------|-------------------|--------------------------------------------|
| `getValue`    | `getParameter`    | Gets a value from the environment          |
| `setValue`    | `setParameter`    | Sets a value in the environment            |

## Compatibility

For backward compatibility, the old API methods are still available but are marked as deprecated. 
They are implemented as wrapper methods around the new API methods.

```java
/**
 * Gets a specific value from the environment.
 * This is an alias for getParameter() that's used in tests.
 *
 * @param key the parameter key
 * @return the value, or null if not found
 * @deprecated Use getParameter() instead
 */
@Deprecated
public String getValue(String key) {
    return getParameter(key);
}

/**
 * Sets a value in the environment.
 * This is an alias for setParameter() that's used in tests.
 *
 * @param key the key
 * @param value the value
 * @deprecated Use setParameter() instead
 */
@Deprecated
public void setValue(String key, String value) {
    setParameter(key, value);
}
```

## Migration Approaches

There are several ways to migrate your code to use the new API:

### 1. Direct Replacement

The simplest approach is to replace all occurrences of `getValue` with `getParameter` and `setValue` with `setParameter`:

```java
// Old code
String value = environment.getValue("someKey");
environment.setValue("someKey", "newValue");

// New code
String value = environment.getParameter("someKey");
environment.setParameter("someKey", "newValue");
```

### 2. Using the EnvironmentCompatUtil

For test code, you can use the `EnvironmentCompatUtil` class to bridge between the old and new APIs:

```java
import org.s8r.test.util.EnvironmentCompatUtil;

// Using the utility
String value = EnvironmentCompatUtil.getValue(environment, "someKey");
EnvironmentCompatUtil.setValue(environment, "someKey", "newValue");

// Testing a value
boolean hasValue = EnvironmentCompatUtil.hasValue(environment, "someKey", "expectedValue");
```

### 3. Static Import for Readability

You can use static imports for improved readability:

```java
import static org.s8r.test.util.EnvironmentCompatUtil.getValue;
import static org.s8r.test.util.EnvironmentCompatUtil.setValue;

// Using static imports
String value = getValue(environment, "someKey");
setValue(environment, "someKey", "newValue");
```

## Automated Migration

To automatically update all test files, you can use the following sed commands:

```bash
find src/test -name "*.java" -type f -exec sed -i 's/environment\.getValue(/environment.getParameter(/g' {} \;
find src/test -name "*.java" -type f -exec sed -i 's/environment\.setValue(/environment.setParameter(/g' {} \;
```

Or use the provided migration script:

```bash
./util/scripts/migrate-environment-api.sh
```

## Identifying Files That Need Migration

To find all files that still use the old API:

```bash
grep -r "\.getValue(" --include="*.java" src/
grep -r "\.setValue(" --include="*.java" src/
```

## Testing After Migration

After migrating, run the full test suite to ensure everything works correctly:

```bash
./s8r-test all
```

## Common Issues

1. Type coercion - The new API is stricter about types. The `getParameter` method returns a string, so you may need to convert to other types explicitly.

2. Null handling - Make sure to handle null values properly, as the behavior might differ slightly between the old and new APIs.

3. Map conversions - If you were using the environment as a map directly, you'll need to update that code to use the appropriate methods.

## Report Migration Issues

If you encounter any issues during migration, please report them using the issue template:

```
Title: Environment API Migration Issue

Description:
- File: [path to file]
- Line: [line number]
- Issue: [description of the issue]
- Error message: [if applicable]

Additional context:
[Any additional information that might be helpful]
```

## Need Help?

If you need assistance with the migration, please contact the S8r team or open an issue in the repository.