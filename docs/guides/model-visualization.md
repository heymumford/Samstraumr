<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->



# Model Visualization

This guide explains how to use the S8r CLI tools to create, visualize, and manage models efficiently. The S8r framework provides powerful visualization tools that help model designers understand their component structures and relationships.

## Table of Contents

- [Installation](#installation)
- [Creating a New Model](#creating-a-new-model)
- [Visualizing Models](#visualizing-models)
- [Component Management](#component-management)
- [Composite Management](#composite-management)
- [Machine Management](#machine-management)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Installation

To use the S8r model visualization tools, you need to have a working Samstraumr installation:

```bash
# Model Visualization
git clone https://github.com/heymumford/Samstraumr.git
cd Samstraumr

# Model Visualization
./s8r-build
```

The S8r CLI tools will be available in the project root directory:

- `s8r` - Main command interface
- `s8r-init` - Model initialization
- `s8r-list` - Model visualization

## Creating a New Model

The `s8r init` command creates a new S8r model with the necessary directory structure and boilerplate files:

```bash
# Model Visualization
./s8r init

# Model Visualization
./s8r init ~/my-new-model

# Model Visualization
./s8r init --package com.example.model

# Model Visualization
./s8r init --name "Customer Data Model" --package com.example.customer
```

### Command options

| Option | Description |
|--------|-------------|
| `--package`, `-p` | Specify the base package name (default: org.example) |
| `--name`, `-n` | Specify a model name (default: derived from directory name) |
| `--verbose`, `-v` | Enable verbose output during initialization |
| `--help`, `-h` | Show help information |

### Directory structure

The initialization creates the following structure:

```
my-new-model/
├── .s8r/                      # S8r configuration 
│   └── config.json            # Model configuration
├── src/
│   ├── main/java/org/example/ # Or your custom package
│   │   ├── component/         # Component implementations
│   │   │   └── AdamComponent.java  # Initial component
│   │   ├── composite/         # Composite implementations
│   │   └── machine/           # Machine implementations
│   └── test/                  # Test resources
└── README.md                  # Generated documentation
```

## Visualizing Models

The `s8r list` command generates visual representations of your S8r model:

```bash
# Model Visualization
./s8r list

# Model Visualization
./s8r list ~/my-model

# Model Visualization
./s8r list --format tree

# Model Visualization
./s8r list --detailed
```

### Command options

| Option | Description |
|--------|-------------|
| `--format`, `-f` | Output format: ascii, tree, json (default: ascii) |
| `--detailed`, `-d` | Show detailed component information |
| `--verbose`, `-v` | Enable verbose output |
| `--help`, `-h` | Show help information |

### Sample output

ASCII format:
```
S8r Model Visualization
======================

Machine: CustomerDataProcessor
  State: ACTIVE

Composites:
  [DataInput: standard]
  [DataValidator: validator]
  [DataEnricher: transformer]
  [DataOutput: standard]

Connections:
```

Tree format (with `--format tree`):
```
S8r Model
└── Machine: CustomerDataProcessor
    ├── Composite: DataInput
    │   ├── Component: FileReader
    │   └── Component: DataParser
    ├── Composite: DataValidator
    │   ├── Component: SchemaValidator
    │   └── Component: BusinessRuleChecker
    ├── Composite: DataEnricher
    │   └── Component: CustomerEnricher
    └── Composite: DataOutput
        ├── Component: DataFormatter
        └── Component: DatabaseWriter
```

## Component Management

Components are the basic building blocks of your model. Use the following commands to manage them:

```bash
# Model Visualization
./s8r component create --type transformer DataProcessor

# Model Visualization
./s8r component list

# Model Visualization
./s8r component info DataProcessor

# Model Visualization
./s8r component delete DataProcessor
```

## Composite Management

Composites group related components together:

```bash
# Model Visualization
./s8r composite create --type processing DataFlow

# Model Visualization
./s8r composite add --component DataProcessor --composite DataFlow

# Model Visualization
./s8r composite connect --source Parser --target Validator --composite DataFlow

# Model Visualization
./s8r composite list
```

## Machine Management

Machines orchestrate composites to form complete processing systems:

```bash
# Model Visualization
./s8r machine create --template flow DataPipeline

# Model Visualization
./s8r machine add --composite DataFlow --machine DataPipeline

# Model Visualization
./s8r machine connect --source InputFlow --target ProcessingFlow --machine DataPipeline
```

## Best Practices

1. **Start with a Plan**: Sketch your model structure before implementation
2. **Use Meaningful Names**: Name components, composites, and machines clearly
3. **Visualize Regularly**: Use `s8r list` frequently to understand your model
4. **Component Granularity**: Keep components focused on a single responsibility
5. **Document as You Go**: Add comments and documentation to your model

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `Not a valid S8r project` error | Ensure you're in a directory initialized with `s8r init` |
| Visualization shows no components | Verify that components are properly registered in your code |
| Cannot find commands | Make sure S8r CLI tools are executable (`chmod +x s8r*`) |
| Java errors during initialization | Check that you're using Java 17 or higher |
