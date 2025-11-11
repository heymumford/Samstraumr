# ALZ001 Composite-Level Mock Implementations

This directory contains mock implementations for composite-level (L1) test classes. Each composite coordinates multiple components to provide higher-level functionality.

## Planned Contents

- `ProteinExpressionComposite.java`: Mock composite for protein expression analysis
- `NeuronalNetworkComposite.java`: Mock composite for neuronal network analysis
- `TimeSeriesAnalysisComposite.java`: Mock composite for time series pattern detection
- `EnvironmentalFactorsComposite.java`: Mock composite for environmental influence analysis
- `PredictiveModelingComposite.java`: Mock composite for predictive model combination

## Usage

All composites should extend `ALZ001MockComponent` and contain references to their component parts. They should be created using the appropriate factory method in `ALZ001MockFactory`.