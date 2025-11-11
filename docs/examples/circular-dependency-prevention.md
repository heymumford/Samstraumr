# Circular Dependency Prevention

This document provides practical examples of how to detect and prevent circular dependencies in Samstraumr projects.

## Detecting Circular Dependencies

You can check your codebase for circular dependencies at any time using the `check-circular-dependencies.sh` script:

```bash
./util/scripts/check-circular-dependencies.sh
```

This will analyze both your main source code and test code for circular dependencies.

## Example Circular Dependency

Consider the following problematic code structure:

```java
// In org.s8r.service.UserService
package org.s8r.service;

import org.s8r.repository.UserRepository;

public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    
    public User getUser(String id) {
        return repository.findById(id);
    }
    
    public void validateUser(User user) {
        // Validation logic
    }
}

// In org.s8r.repository.UserRepository
package org.s8r.repository;

import org.s8r.service.UserService;

public class UserRepository {
    private final UserService service;
    
    public UserRepository(UserService service) {
        this.service = service;
    }
    
    public User findById(String id) {
        // Find user
        User user = // ... database lookup
        
        // Validate before returning
        service.validateUser(user);
        return user;
    }
}
```

Running the circular dependency detector would identify the cycle:

```
Found 1 circular dependency:
  org.s8r.service -> org.s8r.repository -> org.s8r.service
```

## Resolving with Dependency Inversion

To solve this, we apply the Dependency Inversion Principle:

1. Create interfaces in a common package or in the dependent package
2. Invert the dependencies so both classes depend on the interfaces
3. Break the circular dependency

```java
// In org.s8r.service.api.UserRepositoryPort
package org.s8r.service.api;

public interface UserRepositoryPort {
    User findById(String id);
}

// In org.s8r.service.api.UserValidationService
package org.s8r.service.api;

public interface UserValidationService {
    void validateUser(User user);
}

// In org.s8r.service.UserService
package org.s8r.service;

import org.s8r.service.api.UserRepositoryPort;
import org.s8r.service.api.UserValidationService;

public class UserService implements UserValidationService {
    private final UserRepositoryPort repository;
    
    public UserService(UserRepositoryPort repository) {
        this.repository = repository;
    }
    
    public User getUser(String id) {
        return repository.findById(id);
    }
    
    @Override
    public void validateUser(User user) {
        // Validation logic
    }
}

// In org.s8r.repository.UserRepository
package org.s8r.repository;

import org.s8r.service.api.UserRepositoryPort;
import org.s8r.service.api.UserValidationService;

public class UserRepository implements UserRepositoryPort {
    private final UserValidationService validationService;
    
    public UserRepository(UserValidationService validationService) {
        this.validationService = validationService;
    }
    
    @Override
    public User findById(String id) {
        // Find user
        User user = // ... database lookup
        
        // Validate before returning
        validationService.validateUser(user);
        return user;
    }
}
```

Now, both `UserService` and `UserRepository` depend on interfaces, not on each other directly, breaking the circular dependency.

## Resolving with Events

An alternative approach is to use events to communicate between components:

```java
// In org.s8r.events.UserRetrievedEvent
package org.s8r.events;

public class UserRetrievedEvent {
    private final User user;
    
    public UserRetrievedEvent(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}

// In org.s8r.service.UserService
package org.s8r.service;

import org.s8r.events.UserRetrievedEvent;
import org.s8r.service.api.UserRepositoryPort;

public class UserService {
    private final UserRepositoryPort repository;
    private final EventDispatcher eventDispatcher;
    
    public UserService(UserRepositoryPort repository, EventDispatcher eventDispatcher) {
        this.repository = repository;
        this.eventDispatcher = eventDispatcher;
        
        // Subscribe to events
        eventDispatcher.subscribe(UserRetrievedEvent.class, this::validateRetrievedUser);
    }
    
    public User getUser(String id) {
        return repository.findById(id);
    }
    
    private void validateRetrievedUser(UserRetrievedEvent event) {
        User user = event.getUser();
        // Validation logic
    }
}

// In org.s8r.repository.UserRepository
package org.s8r.repository;

import org.s8r.events.UserRetrievedEvent;
import org.s8r.service.api.UserRepositoryPort;

public class UserRepository implements UserRepositoryPort {
    private final EventDispatcher eventDispatcher;
    
    public UserRepository(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }
    
    @Override
    public User findById(String id) {
        // Find user
        User user = // ... database lookup
        
        // Publish event instead of direct call
        eventDispatcher.publish(new UserRetrievedEvent(user));
        
        return user;
    }
}
```

## Preventing Circular Dependencies with Git Hooks

Our Git hooks automatically check for circular dependencies:

1. The **pre-commit hook** checks files being committed for circular dependencies
2. The **pre-push hook** performs a more thorough check before pushing to shared branches

These hooks are installed with:

```bash
./util/scripts/install-git-hooks.sh
```

## Validating with Architecture Tests

Our architecture test suite includes checks for circular dependencies:

```bash
./run-architecture-tests.sh
```

This will run the `AcyclicDependencyTest` along with other architecture validation tests.

## Conclusion

