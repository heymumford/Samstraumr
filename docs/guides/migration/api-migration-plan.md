# API Migration Plan for Test Suite

## Overview

This document outlines a comprehensive plan for migrating test code to work with the updated API structure in the Samstraumr framework. The goal is to resolve compilation issues and allow the full test suite to run successfully.

## Current Status

1. **Environment API Migration**: 
   - Migration from `getValue()/setValue()` to `getParameter()/setParameter()` partially completed
   - Compatibility layer implemented via `EnvironmentCompatUtil`
   - Core `Environment` class has backward-compatible aliases

2. **Component API Changes**:
   - Package reorganization (e.g., `org.s8r.domain.lifecycle` to `org.s8r.component`)
   - Method signature changes in `Component` and related classes
   - State management changes (from `LifecycleState` to `State` enum)

3. **Notification System**:
   - Changes to `NotificationResult` constructor and required methods
   - Changes to notification delivery status handling

4. **Identity Management**:
   - Package structure changes for `Identity` and related classes
   - Method signature changes in hierarchy management

## Migration Strategy

### 1. Create Compatibility Utilities

Develop compatibility utilities similar to `EnvironmentCompatUtil` for other changed APIs:

- **ComponentCompatUtil**: Bridge between old and new Component APIs
- **IdentityCompatUtil**: Handle changes to identity management APIs
- **NotificationCompatUtil**: Address notification API changes

### 2. Apply Systematic Changes

Use automated scripts to perform the following transformations:

#### Package Imports
```
org.s8r.domain.lifecycle → org.s8r.component
org.s8r.domain.identity → org.s8r.component.identity
org.s8r.domain.notification → org.s8r.component.notification
```

#### Method Calls
```
getValue(key) → getParameter(key)
setValue(key, value) → setParameter(key, value)
createChild() → createChild(String reason)
```

#### Enum References
```
LifecycleState.XXX → State.XXX
```

### 3. Implement Missing Methods

Add any missing methods required by tests:

- Add `isSent()` to `NotificationResult`
- Add `sendSystemNotification()` to `NotificationAdapter`
- Implement `process_data` operation for Components based on state

### 4. Isolation Strategy

For test files with complex dependencies that cannot be easily fixed:

1. Temporarily move them to a backup location
2. Create simplified test implementations focusing on core functionality
3. Gradually reintegrate fixed tests

## Migration Process

### Phase 1: Environment API (Completed)

- ✅ Create `EnvironmentCompatUtil`
- ✅ Update core Environment implementation
- ✅ Document migration approach

### Phase 2: Component API (In Progress)

1. ✅ Create `ComponentCompatUtil` with:
   - ✅ Method signature adapters
   - ✅ State conversion helpers
   - ✅ Lifecycle management utilities

2. 🔄 Fix Component-related test files:
   - ✅ Update imports
   - ✅ Replace method calls with compatibility utilities
   - ✅ Update state references

3. Test execution outcomes:
   - ✅ Successfully migrated `LifecycleStateTest` to use the new Component API
   - ❗ Some tests fail due to differences in expected vs. actual behavior:
     - State categorization differences (INITIALIZING is now OPERATIONAL, not LIFECYCLE)
     - State transition rules have changed (some previously disallowed transitions are now allowed)
     - Component CONCEPTION state cannot be set after initialization

### Phase 3: Identity API (Next Priority)

1. ✅ Create `IdentityCompatUtil` with:
   - ✅ Hierarchy management helpers
   - ✅ ID conversion utilities

2. 🔄 Fix Identity-related test files:
   - Update imports
   - Replace method calls with compatibility utilities
   - Add missing methods `isChild()` and `isValid()`
   - Fix Component.resolve(Identity) method usage

### Phase 4: Notification API

1. ✅ Create placeholder `NotificationCompatUtil` with:
   - Generic enum conversion utilities
   - Map-based result representation
   - String formatting utilities

2. 🔄 Fix Notification-related test files:
   - Fix constructor signatures in `NotificationResult`
   - Add missing methods like `isSent()`
   - Update status handling

### Phase 5: Complex Integration Tests (ALZ001)

1. Create mock implementations matching expected interfaces
2. Use reflection-based adapters for complex cases
3. Implement facade patterns where needed

## Lessons Learned

1. **API Changes Impact**: The Component state machine API changes have significant impact across the codebase. Key changes include:
   - State enum values and categories
   - State transition rules and allowed paths
   - Component initialization flow (now reaches READY automatically)
   - Termination behavior and exception handling

2. **Testing Challenges**: The new API presents several testing challenges:
   - Components cannot be set back to CONCEPTION state after initialization
   - Test setup requires more careful state management
   - Termination testing requires special handling

3. **Successful Strategies**:
   - Using compatibility utilities works well for bridging old and new APIs
   - Creating isolated test modules helps independently verify components
   - Parameterized tests help validate multiple state transitions more easily

## Implementation Plan

### Immediate Actions

1. **Create Compatibility Utilities**:
   ```java
   // ComponentCompatUtil.java
   public class ComponentCompatUtil {
       public static Component createAdam(String reason) {
           return Component.createAdam(reason);
       }
       
       public static Component createChild(Component parent, String reason) {
           return parent.createChild(reason);
       }
       
       public static State convertState(String oldStateName) {
           // Convert from string to State enum
           try {
               return State.valueOf(oldStateName);
           } catch (IllegalArgumentException e) {
               // Handle legacy state names
               if ("CONCEPTION_PHASE".equals(oldStateName)) 
                   return State.CONCEPTION;
               // ...other mappings
               return State.READY; // Default
           }
       }
   }
   ```

2. **Create Migration Utilities**:
   - Scripts to automate package updates
   - Scripts to update method calls

3. **Fix Core Test Classes**:
   - Address `ComponentHierarchyTest` and `LifecycleStateTest` first
   - Then fix step definitions that are used across multiple tests

### Script-Based Approach

Create automated migration scripts:

```bash
#!/bin/bash
# migrate-component-api.sh
# Updates imports and method calls in test code

find modules/samstraumr-core/src/test -name "*.java" -type f -exec sed -i 's/import org.s8r.domain.lifecycle/import org.s8r.component/g' {} \;
find modules/samstraumr-core/src/test -name "*.java" -type f -exec sed -i 's/LifecycleState\./State\./g' {} \;
find modules/samstraumr-core/src/test -name "*.java" -type f -exec sed -i 's/component.getValue(/component.getParameter(/g' {} \;
# Additional replacements...
```

## Testing the Migration

For each phase:

1. Apply changes to a small set of files first
2. Run specific tests to verify behavior
3. Create comprehensive verification tests
4. Document any issues or edge cases

## Timeline

1. **Phase 1 (Environment API)**: Completed
2. **Phase 2 (Component API)**: 1-2 days
3. **Phase 3 (Identity API)**: 1 day
4. **Phase 4 (Notification API)**: 1 day
5. **Phase 5 (Complex Integration)**: 2-3 days

Total estimated time: 5-7 days

## Success Criteria

- All tests compile successfully
- Core functionality tests pass
- State machine tests pass
- ALZ001 integration tests pass
- No regressions in existing functionality

## Appendix: Common Patterns and Transformations

### Import Statements

```java
// Old
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.identity.Identity;

// New
import org.s8r.component.State;
import org.s8r.component.identity.Identity;
```

### Method Calls

```java
// Old
component.getValue("key");
component.setValue("key", "value");
Identity id = Identity.create("component1");

// New
component.getParameter("key");
component.setParameter("key", "value");
Identity id = Identity.createAdamIdentity("component1", null);
```

### Enum References

```java
// Old
LifecycleState.CONCEPTION_PHASE
LifecycleState.ACTIVE_PHASE

// New
State.CONCEPTION
State.ACTIVE
```

### State Transitions

```java
// Old
component.transitionTo(LifecycleState.ACTIVE_PHASE);

// New
component.setState(State.ACTIVE);
```