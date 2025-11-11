# ALZ001 Mock Components

This directory contains mock implementations for all ALZ001 test components. These mock classes simulate the behavior of real components without requiring actual implementation.

## Contents

- `ALZ001MockComponent.java`: Base class for all mock components
- `ALZ001MockFactory.java`: Factory for creating mock components with consistent configuration
- `/component`: Component-level mock implementations
- `/composite`: Composite-level mock implementations
- `/machine`: Machine-level mock implementations

## Usage

Mock components should be created using the factory methods in `ALZ001MockFactory` to ensure consistent configuration. Each mock component extends `ALZ001MockComponent` to inherit common functionality.