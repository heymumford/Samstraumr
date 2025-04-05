<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Package Structure Simplification

This document provides a clean, minimalist overview of the package simplification strategy for Samstraumr.

## Current Status

- Implementation in progress (Phase 2-3)
- New structure established at `org.s8r.*`
- Core components being migrated

## Target Structure

```
org.s8r                         (← replaces org.samstraumr)
├── core                        (← core functionality)
│   ├── tube                    (← component implementation)
│   │   ├── impl               (← concrete implementations)
│   │   ├── identity           (← identity functionality)
│   │   └── logging            (← component logging)
│   ├── env                     (← environment abstraction)
│   ├── composite               (← composite implementation)
│   ├── machine                 (← machine implementation)
│   └── exception               (← centralized exceptions)
├── util                        (← common utilities)
└── test                        (← testing infrastructure)
    ├── annotation              (← annotations)
    ├── cucumber                (← cucumber infrastructure)
    └── runner                  (← test runners)
```

## Migration Approach

**Complete Replacement Strategy:**
1. Create new implementations in `org.s8r` namespace
2. Replace entire components with clean implementations
3. No adapters or compatibility layers
4. Delete legacy implementations when replaced

## Key Components

|                      Legacy Structure                       |                  New Structure                   |   Status   |
|-------------------------------------------------------------|--------------------------------------------------|------------|
| `org.samstraumr.tube.Tube`                                  | `org.s8r.core.tube.impl.Component`               | ✅ Complete |
| `org.samstraumr.tube.TubeIdentity`                          | `org.s8r.core.tube.identity.Identity`            | ✅ Complete |
| `org.samstraumr.tube.TubeStatus`                            | `org.s8r.core.tube.Status`                       | ✅ Complete |
| `org.samstraumr.tube.TubeLifecycleState`                    | `org.s8r.core.tube.LifecycleState`               | ✅ Complete |
| `org.samstraumr.tube.Environment`                           | `org.s8r.core.env.Environment`                   | ✅ Complete |
| `org.samstraumr.tube.TubeLogger`                            | `org.s8r.core.tube.logging.Logger`               | ✅ Complete |
| `org.samstraumr.tube.TubeLoggerInfo`                        | `org.s8r.core.tube.logging.LoggerInfo`           | ✅ Complete |
| `org.samstraumr.tube.exception.TubeInitializationException` | `org.s8r.core.exception.InitializationException` | ✅ Complete |
| Test annotations                                            | `org.s8r.test.annotation.*`                      | ✅ Complete |

## Next Steps

1. Implement composite functionality in new structure
2. Create machine abstraction in new structure
3. Implement test infrastructure in new structure
4. Delete legacy implementations
5. Update documentation

## Benefits

- Reduced package nesting depth (from 6+ to 4 max)
- More intuitive naming (`Component` vs `Tube`)
- Cleaner separation of concerns
- Improved code maintainability
- Easier navigation and imports
