<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Understanding S8r Machines: A Guide for Scientists

This document explains the S8r Machine concept using analogies and examples relevant to scientific research.

## What is a Machine?

In S8r, a Machine is like a specialized scientific instrument or processing station in your lab workflow that performs a specific function on your data.

![Machine concept diagram](../assets/machine-concept-scientific.png)

**Scientific analogy**: Think of a Machine as similar to a PCR machine in a genomics lab, a spectrophotometer in a chemistry lab, or a weather station in environmental monitoring. Each has:

- A specific function it performs (amplification, spectral analysis, measurement)
- Input requirements (DNA samples, chemical solutions, atmospheric conditions)
- Output formats (copied DNA, absorption spectra, weather data)
- Configuration options (temperature cycles, wavelength ranges, sampling frequency)

## When to Use Machines

Use Machines when you need to:

- Process data in a consistent, repeatable way
- Transform inputs into different outputs
- Apply the same operation to different data sets
- Isolate complex processing logic

**Real-world example**: In environmental monitoring, you might create different Machines for:
- Temperature normalization
- Anomaly detection
- Trend analysis
- Alert generation

Each Machine focuses on one specific task, like individual instruments in your lab.

## Creating Your First Machine

Here's how to create a basic Machine that processes scientific data:

```java
// Create a simple temperature conversion Machine
Machine<Double, Double> celsiusToKelvinMachine = Machine.builder()
    .name("Celsius to Kelvin Converter")
    .inputType(Double.class)  // Temperature in Celsius
    .outputType(Double.class) // Temperature in Kelvin
    .processor(celsius -> celsius + 273.15)  // The conversion formula
    .build();

// Use the Machine to convert a temperature
Double kelvinValue = celsiusToKelvinMachine.process(25.0);  // Convert 25°C to Kelvin
System.out.println("25°C = " + kelvinValue + "K");  // Outputs: 25°C = 298.15K
```

This is similar to how you might use a specialized instrument that takes a measurement in one unit and converts it to another.

## Connecting Machines Together

Just as you connect instruments in a laboratory, you can connect Machines to create processing pipelines:

![Machine pipeline diagram](../assets/machine-pipeline-scientific.png)

```java
// Create a Machine that detects temperature anomalies
Machine<Double, Boolean> anomalyDetector = Machine.builder()
    .name("Temperature Anomaly Detector")
    .inputType(Double.class)  // Temperature in Kelvin
    .outputType(Boolean.class)  // True if anomaly detected
    .processor(kelvin -> kelvin > 310.0)  // Detect high temperatures (>310K or ~37°C)
    .build();

// Connect the machines into a pipeline
Machine<Double, Boolean> temperatureMonitor = Machine.builder()
    .name("Temperature Monitoring Pipeline")
    .inputType(Double.class)  // Temperature in Celsius
    .outputType(Boolean.class)  // Anomaly detection result
    .processor(celsius -> {
        // First convert to Kelvin
        Double kelvin = celsiusToKelvinMachine.process(celsius);
        // Then detect anomalies
        return anomalyDetector.process(kelvin);
    })
    .build();

// Use the pipeline
Boolean isAnomalous = temperatureMonitor.process(40.0);  // Check if 40°C is anomalous
System.out.println("Is 40°C anomalous? " + isAnomalous);  // Outputs: true
```

This is similar to how you might connect a temperature sensor to a data logger and then to an alert system in a laboratory setup.

## Machine States

Machines can be in different states, similar to how laboratory equipment can be in different operational modes:

| Machine State | Scientific Instrument Analogy |
|---------------|-------------------------------|
| CREATED | Instrument unpacked but not set up |
| CONFIGURED | Instrument calibrated and ready |
| RUNNING | Instrument actively processing |
| PAUSED | Instrument temporarily stopped |
| STOPPED | Instrument powered down |
| ERROR | Instrument malfunction |

```java
// Create a Machine and check its state
Machine<Double, Double> machine = Machine.builder()
    .name("Data Normalizer")
    .inputType(Double.class)
    .outputType(Double.class)
    .processor(value -> value / 100.0)
    .build();

// Check the state - initially CREATED
System.out.println(machine.getState());  // Outputs: CREATED

// Configure the Machine (like calibrating an instrument)
machine.configure();
System.out.println(machine.getState());  // Outputs: CONFIGURED

// Now it's ready to process data
```

## Advanced Patterns: Machine Ensembles

For complex scientific workflows, you can create ensembles of Machines that work together, similar to how you might use multiple instruments together in a laboratory:

![Machine ensemble diagram](../assets/machine-ensemble-scientific.png)

**Environmental monitoring example**:
- Temperature Machine processes temperature data
- Humidity Machine processes humidity data
- Pressure Machine processes pressure data
- Integration Machine combines all three for climate analysis
- Alert Machine monitors for concerning patterns
- Storage Machine archives the data

## Troubleshooting Machine Issues

When your Machines aren't working as expected:

| Problem | Scientific Analogy | Solution |
|---------|-------------------|----------|
| NullPointerException | Missing sample in your experiment | Ensure all inputs are provided |
| ClassCastException | Trying to view bacteria with a telescope | Verify input/output types match |
| IllegalStateException | Using an uncalibrated instrument | Ensure Machine is configured before processing |
| OutOfMemoryError | Sample storage cabinet overflow | Process data in smaller batches |

## Next Steps

Now that you understand Machine basics:

1. Try creating a Machine relevant to your specific scientific domain
2. Experiment with connecting multiple Machines into a workflow
3. Explore different Machine types for specialized processing
4. Consider how to integrate S8r Machines with your existing scientific tools

## Domain-Specific Examples

### Genomics
```java
// Create a DNA sequence analysis pipeline
Machine<String, Integer> gcContentAnalyzer = Machine.builder()
    .name("GC Content Analyzer")
    .inputType(String.class)  // DNA sequence
    .outputType(Integer.class)  // GC percentage
    .processor(sequence -> {
        long gcCount = sequence.chars()
            .filter(c -> c == 'G' || c == 'C')
            .count();
        return (int)(gcCount * 100 / sequence.length());
    })
    .build();

// Use the Machine
String dnaSequence = "ATGCTAGCTAGCTAGC";
Integer gcPercentage = gcContentAnalyzer.process(dnaSequence);
System.out.println("GC content: " + gcPercentage + "%");
```

### Physics
```java
// Create a kinetic energy calculator
Machine<Double[], Double> kineticEnergyMachine = Machine.builder()
    .name("Kinetic Energy Calculator")
    .inputType(Double[].class)  // [mass, velocity]
    .outputType(Double.class)   // Kinetic energy
    .processor(params -> {
        double mass = params[0];
        double velocity = params[1];
        return 0.5 * mass * velocity * velocity;
    })
    .build();

// Use the Machine
Double[] parameters = {10.0, 5.0};  // 10kg object moving at 5 m/s
Double energy = kineticEnergyMachine.process(parameters);
System.out.println("Kinetic energy: " + energy + " joules");
```

Remember, Machines are simply tools to help organize and structure your scientific data processing - they're designed to work the way scientists think, allowing you to focus on your research questions rather than software implementation details.