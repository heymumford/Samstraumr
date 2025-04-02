# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands
- Build project: `mvn clean install`
- Run all tests: `mvn test`
- Run tagged tests: `mvn test -Dcucumber.filter.tags="@ATL"`
- Run specific test categories: `mvn test -Dcucumber.filter.tags="@UUID"` or `mvn test -Dcucumber.filter.tags="@Logging"`

## Code Style Guidelines
- **Imports**: Specific imports (no wildcards). Standard Java first, then third-party, then project imports.
- **Naming**: PascalCase for classes, camelCase for methods/variables, ALL_CAPS for constants.
- **Error Handling**: Custom exceptions with contextual messages; consistent logging before throwing exceptions.
- **Logging**: Use SLF4J with Log4j2 implementation; consistent log levels; parameterized logging.
- **Testing**: Cucumber for BDD testing; feature files with descriptive scenarios.
- **Documentation**: Javadoc for public APIs; comments for complex operations.
- **Structure**: Final fields for immutability; private methods for implementation details.

## Project Organization
- Multi-module Maven project (Java 21)
- BDD testing approach with Cucumber
- Feature files contain detailed test documentation with tags for selective test execution