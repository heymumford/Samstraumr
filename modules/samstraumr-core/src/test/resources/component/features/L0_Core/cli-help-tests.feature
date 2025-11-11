@L0_CLI @Functional
Feature: S8r CLI Help System
  As a new S8r user
  I want to get helpful information about commands by typing 's8r'
  So that I can understand what options are available without reading documentation

  Background:
    Given the S8r CLI is installed and available on the system path

  # Core behavior tests

  @HelpSystem @EmptyCommand @Positive @Core @L0
  Scenario: Typing 's8r' without arguments should display help information
    When I run "s8r" without any arguments
    Then the command should succeed
    And the output should contain a man page style guide
    And the output should contain the section "Core Commands"
    And the output should contain the section "Additional Commands"
    And the output should contain the section "Global Options"
    And the output should list the "init" command with description
    And the output should list the "list" command with description
    And the output should list the "build" command with description
    And the output should list the "test" command with description
    And the output should list the "version" command with description

  @HelpSystem @ExplicitHelp @Positive @Equivalence @L0
  Scenario: Typing 's8r --help' should display the same help information as 's8r' without arguments
    When I run "s8r --help"
    Then the command should succeed
    And the output should be identical to running "s8r" without arguments
    And the output should contain a man page style guide
    And the output should contain the section "Core Commands"
    And the output should contain the section "Global Options"

  @HelpSystem @ShortHelp @Positive @Equivalence @L0
  Scenario: Typing 's8r -h' should display the same help information as 's8r' without arguments
    When I run "s8r -h"
    Then the command should succeed
    And the output should be identical to running "s8r" without arguments
    And the output should be identical to running "s8r --help"

  @HelpSystem @EmptyArgsCode @Positive @Implementation @L0
  Scenario: Implementation should translate 's8r' without arguments to 's8r --help'
    When I run "s8r" without any arguments
    Then the output should be identical to running "s8r --help"
    # This test verifies the implementation detail that 's8r' is translated to 's8r --help'

  # Command-specific help tests

  @HelpSystem @CommandSpecificHelp @Positive @L1
  Scenario Outline: Typing 's8r help <command>' should display command-specific help
    When I run "s8r help <command>"
    Then the command should succeed
    And the output should contain specific help for the "<command>" command
    And the output should show usage information for "<command>"
    And the output should show available options for "<command>"
    And the output should contain example commands for "<command>"

    Examples:
      | command   |
      | init      |
      | list      |
      | build     |
      | test      |
      | version   |
      | component |
      | composite |
      | machine   |

  @HelpSystem @CommandSpecificHelp @Alternative @Positive @L1
  Scenario Outline: Command help can also be accessed via '<command> --help'
    When I run "s8r <command> --help"
    Then the command should succeed
    And the output should contain specific help for the "<command>" command
    And the output should be identical to running "s8r help <command>"

    Examples:
      | command   |
      | init      |
      | list      |
      | build     |
      | test      |
      | version   |

  # Help formatting and structure tests

  @HelpSystem @Formatting @Positive @L1
  Scenario: Help output should use proper formatting with colors and sections
    When I run "s8r"
    Then the command should succeed
    And the output should contain formatted headers
    And the output should contain colored sections
    And command names should be highlighted
    And the output should be well-structured with clear sections

  @HelpSystem @Examples @Positive @L1
  Scenario: Help output should include practical examples
    When I run "s8r"
    Then the command should succeed
    And the output should contain an "EXAMPLES" section
    And the examples should demonstrate common use cases
    And the examples should use proper command syntax

  @HelpSystem @CoreCommands @Positive @L1
  Scenario: Help should include both core and additional commands
    When I run "s8r"
    Then the command should succeed
    And the output should contain the section "Core Commands"
    And the output should list the "init" command with description
    And the output should list the "list" command with description
    And the output should list the "build" command with description
    And the output should list the "test" command with description
    And the output should list the "version" command with description
    And the output should contain the section "Additional Commands"
    And the output should list the "quality" command with description
    And the output should list the "report" command with description
    And the output should list the "docs" command with description

  @HelpSystem @GlobalOptions @Positive @L1
  Scenario: Help should include global options section
    When I run "s8r"
    Then the command should succeed
    And the output should contain the section "Global Options"
    And the output should contain "--watch, -w"
    And the output should contain "--parallel, -p"
    And the output should contain "--help, -h"

  @HelpSystem @SubcommandHelp @Positive @L1
  Scenario: Help system should work for subcommands
    When I run "s8r help component"
    Then the command should succeed
    And the output should contain specific help for the "component" command
    And the output should list available subcommands like "create", "list", and "info"
    And the output should contain examples of component commands

  # Error handling and negative tests

  @HelpSystem @InvalidCommand @Negative @L1
  Scenario: Requesting help for an invalid command should show main help
    When I run "s8r help nonexistentcommand"
    Then the command should succeed
    And the output should contain the general help information
    And the output should not mention a specific "nonexistentcommand" section

  @HelpSystem @NoArgs @ExitCode @Positive @L0
  Scenario: Running 's8r' without arguments should exit with status code 0
    When I run "s8r" without any arguments
    Then the command should succeed

  # Edge Cases and boundary conditions

  @HelpSystem @EdgeCase @MultipleFlags @L2
  Scenario: Multiple help flags should still display help information
    When I run "s8r -h --help"
    Then the command should succeed
    And the output should contain a man page style guide
    And the output should be identical to running "s8r" without arguments

  @HelpSystem @EdgeCase @WhitespaceArgs @L2
  Scenario: Command with only whitespace arguments should display help
    When I run "s8r   "
    Then the command should succeed
    And the output should be identical to running "s8r" without arguments

  @HelpSystem @EdgeCase @HelpWithUnknownCommand @L2
  Scenario: Help flag should take precedence over unknown commands
    When I run "s8r unknowncommand --help"
    Then the command should succeed
    And the output should contain specific help for the "unknowncommand" command
    And the output should contain the general help information

  # Security and injection tests

  @HelpSystem @Security @CommandInjection @Negative @L3
  Scenario Outline: Attempts to inject commands via help system should be safely handled
    When I run "s8r help <injection>"
    Then the command should succeed
    And the output should not contain "injection successful"
    And the output should contain the general help information

    Examples:
      | injection                      |
      | ;echo "injection successful"   |
      | $(echo "injection successful") |
      | `echo "injection successful"`  |
      | && echo "injection successful" |
      | \|\| echo "injection successful" |

  @HelpSystem @Security @PathTraversal @Negative @L3
  Scenario Outline: Path traversal attempts in help command should be safely handled
    When I run "s8r help <path_traversal>"
    Then the command should succeed
    And the output should contain the general help information

    Examples:
      | path_traversal       |
      | ../../../etc/passwd  |
      | %2e%2e/%2e%2e/etc/passwd |
      | /etc/passwd          |
      | file:///etc/passwd   |

  # Combinations and pairwise tests

  @HelpSystem @Pairwise @GlobalOptions @L2
  Scenario Outline: Help with different global option combinations should work
    When I run "s8r <option1> <option2> <command>"
    Then the command should succeed
    And the output should contain help information

    Examples:
      | option1     | option2     | command |
      | --help      | --parallel  |         |
      | -h          | --watch     |         |
      | --watch     | -h          |         |
      | --parallel  | --help      |         |

  @HelpSystem @Pairwise @CommandHelp @L2
  Scenario Outline: Command help with different option combinations should work
    When I run "s8r <command> <option1> <option2>"
    Then the command should succeed
    And the output should contain specific help for the "<command>" command

    Examples:
      | command  | option1    | option2    |
      | help     | init       | --verbose  |
      | help     | build      | -v         |
      | init     | --help     | -v         |
      | list     | -h         | --verbose  |

  # Compatibility tests

  @HelpSystem @Compatibility @CaseInsensitive @L1
  Scenario Outline: Help commands should be case-insensitive
    When I run "s8r <help_command>"
    Then the command should succeed
    And the output should contain a man page style guide
    And the output should be identical to running "s8r --help"

    Examples:
      | help_command |
      | --HELP       |
      | --Help       |
      | -H           |
      | HELP         |
      | Help         |

  @HelpSystem @NewFeatures @Positive @L2
  Scenario: Help system should include recently added commands
    When I run "s8r"
    Then the command should succeed
    And the output should list the "init" command with description
    And the output should list the "list" command with description
    
  # Performance tests
    
  @HelpSystem @Performance @L3
  Scenario: Help command should respond within acceptable time
    When I run "s8r" and measure execution time
    Then the command should complete in less than 500 milliseconds
    And the output should contain a man page style guide