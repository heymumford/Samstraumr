<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Build Metrics

This page provides quantitative measures of the Samstraumr build, offering insights into the system's health and areas for improvement.

## Key Metrics Summary

|       Metric       |         Value          | Target |         Status          |
|--------------------|------------------------|--------|-------------------------|
| Test Coverage      | ${coverage.overall}%   | >80%   | ${coverage.status}      |
| Code Quality       | ${quality.score}/10    | >7/10  | ${quality.status}       |
| Build Success Rate | ${build.success.rate}% | >95%   | ${build.success.status} |
| Technical Debt     | ${debt.ratio}%         | <5%    | ${debt.status}          |
| Test Success Rate  | ${test.success.rate}%  | 100%   | ${test.success.status}  |

## Test Metrics

### Test Categories

| Category  |       Count        |       Passing        |       Failing        |       Skipped        |        Success Rate        |
|-----------|--------------------|----------------------|----------------------|----------------------|----------------------------|
| ATL Tests | ${atl.tests.count} | ${atl.tests.passing} | ${atl.tests.failing} | ${atl.tests.skipped} | ${atl.tests.success.rate}% |
| BTL Tests | ${btl.tests.count} | ${btl.tests.passing} | ${btl.tests.failing} | ${btl.tests.skipped} | ${btl.tests.success.rate}% |
| **Total** | ${tests.count}     | ${tests.passing}     | ${tests.failing}     | ${tests.skipped}     | ${tests.success.rate}%     |

### Test Types

|        Type         |           Count           |           Passing           |           Failing           |           Skipped           |           Success Rate            |
|---------------------|---------------------------|-----------------------------|-----------------------------|-----------------------------|-----------------------------------|
| Unit/Tube           | ${tube.tests.count}       | ${tube.tests.passing}       | ${tube.tests.failing}       | ${tube.tests.skipped}       | ${tube.tests.success.rate}%       |
| Component/Composite | ${composite.tests.count}  | ${composite.tests.passing}  | ${composite.tests.failing}  | ${composite.tests.skipped}  | ${composite.tests.success.rate}%  |
| Integration/Flow    | ${flow.tests.count}       | ${flow.tests.passing}       | ${flow.tests.failing}       | ${flow.tests.skipped}       | ${flow.tests.success.rate}%       |
| System/Stream       | ${stream.tests.count}     | ${stream.tests.passing}     | ${stream.tests.failing}     | ${stream.tests.skipped}     | ${stream.tests.success.rate}%     |
| E2E/Acceptance      | ${acceptance.tests.count} | ${acceptance.tests.passing} | ${acceptance.tests.failing} | ${acceptance.tests.skipped} | ${acceptance.tests.success.rate}% |

## Code Quality Metrics

### Checkstyle Issues

| Severity |         Count          |            Trend             |
|----------|------------------------|------------------------------|
| Error    | ${checkstyle.errors}   | ${checkstyle.errors.trend}   |
| Warning  | ${checkstyle.warnings} | ${checkstyle.warnings.trend} |
| Info     | ${checkstyle.info}     | ${checkstyle.info.trend}     |

### SpotBugs Issues

| Priority |       Count        |          Trend           |
|----------|--------------------|--------------------------|
| High     | ${spotbugs.high}   | ${spotbugs.high.trend}   |
| Medium   | ${spotbugs.medium} | ${spotbugs.medium.trend} |
| Low      | ${spotbugs.low}    | ${spotbugs.low.trend}    |

### Code Complexity

|           Metric            |            Value            | Threshold |               Status               |
|-----------------------------|-----------------------------|-----------|------------------------------------|
| Cyclomatic Complexity (Avg) | ${complexity.cyclomatic}    | <10       | ${complexity.cyclomatic.status}    |
| Cognitive Complexity (Avg)  | ${complexity.cognitive}     | <15       | ${complexity.cognitive.status}     |
| Method Length (Avg Lines)   | ${complexity.method.length} | <30       | ${complexity.method.length.status} |
| Class Length (Avg Lines)    | ${complexity.class.length}  | <300      | ${complexity.class.length.status}  |

## Build Performance Metrics

|       Metric        |        Current         |            Previous             |            Trend             |
|---------------------|------------------------|---------------------------------|------------------------------|
| Build Duration      | ${build.duration}      | ${build.previous.duration}      | ${build.duration.trend}      |
| Compilation Time    | ${compilation.time}    | ${compilation.previous.time}    | ${compilation.time.trend}    |
| Test Execution Time | ${test.execution.time} | ${test.execution.previous.time} | ${test.execution.time.trend} |
| Analysis Time       | ${analysis.time}       | ${analysis.previous.time}       | ${analysis.time.trend}       |

## Technical Debt Analysis

|   Category    |     Debt (hours)      |            Priority            |
|---------------|-----------------------|--------------------------------|
| Code Quality  | ${debt.code.quality}  | ${debt.code.quality.priority}  |
| Test Coverage | ${debt.test.coverage} | ${debt.test.coverage.priority} |
| Documentation | ${debt.documentation} | ${debt.documentation.priority} |
| Architecture  | ${debt.architecture}  | ${debt.architecture.priority}  |
| **Total**     | ${debt.total}         | -                              |

## Recommendations

Based on the metrics above, here are the top improvement opportunities:

1. ${recommendation.1}
2. ${recommendation.2}
3. ${recommendation.3}

These recommendations align with Samstraumr's philosophy of continuous adaptation and improvement based on system awareness.
