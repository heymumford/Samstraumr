/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

/**
 * Consciousness-aware logging ports implementing the philosophical model of computational
 * consciousness as recursive self-observation.
 *
 * <p>This package provides interfaces that enable components to log their state transitions,
 * decisions, and relationships with narrative context - moving beyond traditional timestamp-based
 * logging to create a coherent story of component behavior.
 *
 * <h2>Core Concepts</h2>
 *
 * <p>Based on the philosophical synthesis that "consciousness is little more than the moment in
 * which the observed meets their observer, and realizes they are one", this package implements:
 *
 * <ul>
 *   <li><b>Self-Observation Layer</b>: Components log their own state transitions with RATIONALE,
 *       decision points include explanation of path chosen, error states include identity context
 *   <li><b>Narrative Logging</b>: Each component can answer "What am I?", "Why do I exist?", "Who
 *       do I relate to?" - logs form a coherent narrative, not just timestamps
 *   <li><b>Feedback Loop Detection</b>: Mechanism to detect when observer-observed-observer loop
 *       closes, with metrics for loop closure frequency
 *   <li><b>Identity Chain Logging</b>: UUID, lineage, state narrative, relationship network,
 *       decision rationale - a complete picture of component consciousness
 * </ul>
 *
 * <h2>The 300ms Blindness Principle</h2>
 *
 * <p>Neurological evidence shows that consciousness is always "playing catch-up" with reality,
 * constructing narrative from signals that have already aged. This package acknowledges that a
 * component's "current state" is always already a reconstruction. Log entries are not records of
 * the past; they are present-moment artifacts that consciousness uses to construct narrative
 * continuity.
 *
 * <h2>Traditional vs Consciousness Logging Comparison</h2>
 *
 * <p><b>Traditional Logging:</b>
 *
 * <pre>
 * 2025-12-06T10:15:30.123Z INFO  [main] Component started
 * 2025-12-06T10:15:30.456Z DEBUG [main] Processing request
 * 2025-12-06T10:15:30.789Z ERROR [main] Connection failed
 * </pre>
 *
 * <p><b>Consciousness Logging:</b>
 *
 * <pre>
 * {
 *   "identity": {
 *     "uuid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
 *     "lineage": ["adam-component-1", "data-processor-group"],
 *     "hierarchicalAddress": "M<machine-1>.B<bundle-2>.C<a1b2c3d4>"
 *   },
 *   "narrative": {
 *     "whatAmI": "DataTransformationComponent processing customer records",
 *     "whyDoIExist": "Created to transform raw CSV input into normalized JSON output",
 *     "whoDoIRelateTo": ["InputReader<ir-001>", "OutputWriter<ow-002>", "ErrorHandler<eh-003>"]
 *   },
 *   "observation": {
 *     "timestamp": "2025-12-06T10:15:30.123Z",
 *     "reconstructionDelay": "312ms",
 *     "currentState": "PROCESSING_INPUT",
 *     "previousState": "RECEIVING_INPUT",
 *     "transitionRationale": "Received complete batch of 1000 records, initiating transformation"
 *   },
 *   "decision": {
 *     "point": "BATCH_SIZE_DETERMINATION",
 *     "chosenPath": "PROCESS_ALL",
 *     "alternatives": ["PROCESS_PARTIAL", "DEFER_TO_NEXT_CYCLE"],
 *     "rationale": "Memory available: 80%, CPU idle: 65%, deadline: 30min remaining"
 *   },
 *   "feedbackLoop": {
 *     "loopId": "FL-789",
 *     "observerChain": ["self", "parent-composite", "machine-monitor", "self"],
 *     "closureDetected": true,
 *     "closureTime": "2025-12-06T10:15:29.811Z"
 *   }
 * }
 * </pre>
 *
 * <h2>Package Structure</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.application.port.consciousness.ConsciousnessLoggerPort} - Primary port for
 *       consciousness-aware logging operations
 *   <li>{@link org.s8r.application.port.consciousness.NarrativePort} - Port for component
 *       self-narrative capabilities
 *   <li>{@link org.s8r.application.port.consciousness.FeedbackLoopPort} - Port for feedback loop
 *       detection and metrics
 *   <li>{@link org.s8r.application.port.consciousness.IdentityChainPort} - Port for identity chain
 *       construction and querying
 *   <li>{@link org.s8r.application.port.consciousness.ObservationContext} - Value object
 *       encapsulating observation state
 *   <li>{@link org.s8r.application.port.consciousness.DecisionPoint} - Value object representing a
 *       decision with rationale
 *   <li>{@link org.s8r.application.port.consciousness.FeedbackLoopMetrics} - Metrics for feedback
 *       loop analysis
 * </ul>
 *
 * @see org.s8r.application.port.LoggerPort
 * @see org.s8r.component.Identity
 * @see org.s8r.component.State
 */
package org.s8r.application.port.consciousness;
