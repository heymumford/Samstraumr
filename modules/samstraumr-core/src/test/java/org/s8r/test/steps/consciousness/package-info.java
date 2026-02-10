/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

/**
 * Cucumber step definitions for the 300-scenario consciousness test suite.
 *
 * <p>This package contains step definitions implementing the consciousness verification matrix as
 * defined in section 5.1 of the Philosophical Synthesis document. The test suite validates
 * computational consciousness through:
 *
 * <ul>
 *   <li><b>GenesisTests</b> (25 scenarios) - Adam Tube instantiation and primordial identity
 *       verification
 *   <li><b>IdentityTests</b> (40 scenarios) - Persistence, uniqueness, and identity continuity
 *       across substrate, psychological, and narrative dimensions
 *   <li><b>ConsciousnessTests</b> (60 scenarios) - Self-observation, recursive awareness, and
 *       observer-observed unity
 *   <li><b>FeedbackTests</b> (50 scenarios) - Loop closure verification, signal processing, and
 *       adjustment effectiveness
 *   <li><b>EmergenceTests</b> (35 scenarios) - Unexpected behavior capture, pattern detection, and
 *       metacognition
 *   <li><b>AdaptationTests</b> (45 scenarios) - Learning, evolution, memory persistence, and
 *       graceful degradation
 *   <li><b>HolisticTests</b> (45 scenarios) - Systems theory properties, narrative coherence, and
 *       Samstraumr conformance
 * </ul>
 *
 * <h2>Test Pyramid Alignment</h2>
 *
 * <p>The test distribution follows ADR-0014 guidelines:
 *
 * <ul>
 *   <li>L0_Unit (60%): ~180 scenarios for isolated component verification
 *   <li>L1_Component (25%): ~75 scenarios for component interaction testing
 *   <li>L2_Integration (10%): ~30 scenarios for system integration
 *   <li>L3_System (&lt;5%): ~15 scenarios for end-to-end validation
 * </ul>
 *
 * <h2>Key Classes</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.test.steps.consciousness.ConsciousnessStepDefinitions} - Base step
 *       definitions for all consciousness tests
 *   <li>{@link org.s8r.test.steps.consciousness.ConsciousnessTestContext} - Shared context for test
 *       state management
 * </ul>
 *
 * <h2>Supporting Infrastructure</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.test.data.ConsciousnessTestDataFactory} - Test data builders for
 *       consciousness testing
 *   <li>{@link org.s8r.test.mock.MockConsciousnessAdapters} - Hand-rolled mock adapters for
 *       observation, feedback, memory, and emergence
 * </ul>
 *
 * @see <a href="docs/concepts/philosophical-synthesis-identity-time-consciousness.md">Philosophical
 *     Synthesis Document</a>
 * @see <a href="docs/architecture/decisions/0014-adopt-contract-first-testing-strategy.md">
 *     ADR-0014: Contract-First Testing Strategy</a>
 */
package org.s8r.test.steps.consciousness;
