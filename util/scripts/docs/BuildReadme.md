<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Build Scripts

This directory contains scripts for building the Samstraumr project with different configurations and optimizations.

## Available Scripts

### `build-optimal.sh`

Optimized build script with thread and memory settings for faster builds.

```bash
./build-optimal.sh [clean] [fast|compile|test] [additional maven flags]
```

Examples:
- `./build-optimal.sh` - Build the project with optimal settings
- `./build-optimal.sh clean` - Clean and build the project
- `./build-optimal.sh fast` - Fast development mode (skips tests and quality checks)
- `./build-optimal.sh clean test -P atl-tests` - Clean, build, and run ATL tests

### `build-performance.sh`

Performance-focused build script with more advanced settings.

```bash
./build-performance.sh [options]
```

### `build-performance.bat`

Windows version of the performance build script.

```cmd
build-performance.bat
```

### `java17-compat.sh`

Java 17 compatibility script for systems running Java 21. This script is sourced by the build scripts.

## How the Build Scripts Work

The build scripts provide several benefits:

1. **Parallel execution**: Uses `-T 1C` to utilize all available CPU cores
2. **Memory optimization**: Sets appropriate JVM memory settings
3. **Build profiles**: Supports different profiles for faster development
4. **Incremental compilation**: Enabled for faster builds

Scripts automatically detect the project root directory and configure the environment for optimal builds.
