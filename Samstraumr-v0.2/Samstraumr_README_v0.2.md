
# Samstraumr v0.2

### Overview

**Samstraumr** is a modular, adaptable design framework for building scalable, resilient software using Tube-Based Development principles. Version 0.2 introduces the first draft of the core tube structure, represented by the `BaseTube` class and its sibling classes, which are built using Java 21. These classes form the foundation for future expansions and integrations of more complex tube systems.

### Key Components in Version 0.2

- **BaseTube Class**: 
  - A modular and adaptable abstract class that defines the core functionality for all tubes in the system. 
  - It handles input, processing, output, and allows for tube-to-tube connections. 
  - Serves as the blueprint for extending tubes with specific behaviors in various domains.
  
- **Sister Classes (Samstraumr-Sisters)**: 
  - Sister classes will extend the `BaseTube` and implement domain-specific logic and interactions.
  - These classes demonstrate the power of Tube-Based Development in managing modular systems that can "flow" and adapt to changing inputs.

### Folder Structure

- **`samstraumr-core`**: Contains the core classes and logic that drive the base system (e.g., `BaseTube`).
- **`samstraumr-sisters`**: Houses the sister classes, which extend the `BaseTube` for different processing logic.
- **`samstraumr-cli`**: Command-line interface components for interacting with the Samstraumr framework.
- **`samstraumr-utils`**: Utilities and helper classes used to support the framework's functionality.
- **`docs/`**: Documentation related to the framework, providing details on how to use and extend Samstraumr.

### Version 0.2 Features

- Initial implementation of the **BaseTube** class with generic input/output handling and basic tube connection methods.
- A flexible structure that allows for chaining tubes and creating dynamic, adaptive systems.
- Preparation for introducing more complex tubes and utility classes.
  
### Next Steps

- Expand `BaseTube` into specialized sister tubes.
- Improve visualization and execution methods.
- Explore integration with external systems and test automation frameworks.

### License

This project is licensed under the [Mozilla Public License 2.0](./LICENSE).
