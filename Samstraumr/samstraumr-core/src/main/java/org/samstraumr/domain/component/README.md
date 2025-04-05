# Purpose
Core domain components representing the fundamental building blocks of the S8r framework.

## Contents
Files in this directory should be component entity implementations, component-specific value objects, and component domain services.

## Naming Convention
Files should follow: `[component type]-[specific concern].[extension]`

Example: `Component.java`, `ComponentValidator.java`

## Do Not Include
- Infrastructure implementations of component repositories
- Application services orchestrating component operations
- DTOs for component external representation
- UI/presentation concerns for components

## Related Directories
- `../identity`: Contains component identity-related classes
- `../lifecycle`: Contains component lifecycle state management
- `../event`: Contains domain events related to components
- `../exception`: Contains component-specific exceptions