# Purpose
Domain events representing significant occurrences within the domain model.

## Contents
Files in this directory should be domain event classes that represent something that happened in the domain that may be of interest to other domain objects or application services.

## Naming Convention
Files should follow: `[entity]-[what happened]-Event.[extension]`

Example: `ComponentCreatedEvent.java`, `MachineStateChangedEvent.java`

## Do Not Include
- Event handling infrastructure (use the application layer)
- Event dispatching mechanisms (infrastructure concern)
- Application events not directly tied to domain concepts
- Technical/system events unrelated to domain concepts

## Related Directories
- `../../application/port/EventDispatcher.java`: Port for dispatching events
- `../../infrastructure/event/`: Event handling implementations
- `../component/`: Domain entities that raise events