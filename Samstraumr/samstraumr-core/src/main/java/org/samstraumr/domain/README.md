# Purpose
Domain layer containing core business entities, value objects, and business rules following Clean Architecture principles.

## Contents
Files in this directory should be core domain models, domain services, and business logic implementing the core problem domain of the S8r framework.

## Naming Convention
Files should follow: `[entity]-[related concern].[extension]`

Example: `Component.java`, `ComponentFactory.java`

## Do Not Include
- Infrastructure concerns (databases, frameworks, etc.)
- Application services that orchestrate use cases
- External system adapters or DTOs
- Technical utilities or helpers without domain meaning

## Related Directories
- `../application`: Contains application services that use domain objects
- `../infrastructure`: Contains implementations of domain interfaces