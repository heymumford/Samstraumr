# Publication Venues & Submission Checklist

**Last Updated:** February 6, 2026
**Target Window:** 24-36 months (Feb 2026 - Oct 2027)

---

## Paper 1: Blockbuster Venue Strategy

### PRIMARY VENUE: OOPSLA 2026

**Conference:** Object-Oriented Programming Systems, Languages & Applications
**Deadline:** July 1, 2026 (notification Jan 2027)
**Timeline for Paper 1:** Months 12-15 → submit Month 14-15

| Requirement | Status | Notes |
|-------------|--------|-------|
| **Call for Papers** | ACTIVE | Spring 2026 submission window |
| **Submission Format** | Paper (25pp) | Requires ACM LaTeX template |
| **Review Process** | Double-blind | Identify all authors |
| **Notification** | January 2027 | Results ~6 months after deadline |
| **Expected Desk Rejects** | ~40% | High bar; needs strong empirical validation |

**Why OOPSLA:** Values innovative OO design patterns + production implementation + empirical validation. Component lifecycle is novel pattern family grounded in systems theory with working Samstraumr implementation.

**Submission Checklist:**
- [ ] Novelty positioning (systems theory bridge to architecture)
- [ ] Empirical data collected (resilience metrics, MTTR improvements)
- [ ] Figure: Lifecycle state machine with transitions
- [ ] Figure: Resilience metrics comparison vs baseline
- [ ] Production case study anonymized
- [ ] Literature map complete (Bertalanffy, Wiener, Martin, Evans)
- [ ] Related work acknowledges prior resilience research
- [ ] Abstract <150 words, compelling hook on systems theory
- [ ] Introduction motivates "why enterprise software is fragile"
- [ ] Conclusion connects back to natural systems inspiration

---

### SECONDARY VENUES: ICSE 2027, PLDI 2027

#### ICSE 2027: International Conference on Software Engineering

**Deadline:** September 1, 2026 (notification Dec 2026)
**Timeline:** Can target for Conference + Research Tracks
**Track:** Architecture or Self-Healing Systems track preferred

| Aspect | Details |
|--------|---------|
| **Relevance** | Enterprise architecture focus; values empirical studies |
| **Positioning** | Resilience architecture with measurable outcomes |
| **Strength** | Broad software engineering audience; high impact factor |
| **Risk** | May prioritize process/methods over architecture innovation |

**Submit to ICSE if:** OOPSLA desk rejects or if wanting parallel submission (allowed after OOPSLA rejects)

---

#### PLDI 2027: Programming Language Design & Implementation

**Deadline:** November 15, 2026 (notification Feb 2027)
**Timeline:** Later timeline; allows OOPSLA results before deciding

| Aspect | Details |
|--------|---------|
| **Relevance** | Values language/type system innovations; less traditional fit |
| **Positioning** | Frame as "type system for lifecycle states" or "domain language for resilience" |
| **Risk** | Architecture angles may not fit PLDI scope |

**Submit to PLDI if:** Have novel language/type contribution (e.g., formalized lifecycle state machine as type system)

---

## Paper 2: Architecture Testing Venue Strategy

### PRIMARY VENUE: ESEM 2026

**Conference:** ACM Sigsoft Empirical Software Engineering and Measurement
**Deadline:** April 1, 2026 (notification June 2026)
**Track:** Empirical Studies / Tools & Techniques

| Requirement | Status | Notes |
|-------------|--------|-------|
| **Call for Papers** | ACTIVE | Submit Month 14-15 (target April 2026 deadline) |
| **Submission Format** | Paper (20pp) + appendix | Empirical studies especially welcomed |
| **Key Metrics** | Test coverage, violations caught, cost/time savings | Quantitative data essential |
| **Review Process** | Double-blind | 3-4 reviewers |
| **Notification** | June 2026 | ~2 months turnaround |

**Why ESEM:** Values empirical validation, practical DevOps concerns, continuous testing. Perfect fit for architecture compliance testing paper.

**Submission Checklist:**
- [ ] Empirical data collection complete (violations by type, costs)
- [ ] Comparative study protocol finalized (vs traditional reviews)
- [ ] Metrics dashboard created (time-to-violation, cost-to-fix, cycle time)
- [ ] Figure: Architecture test results over time
- [ ] Figure: Cost comparison (test-driven vs review-driven)
- [ ] Table: All 19+ architecture compliance tests documented
- [ ] Threats to validity section (why results are trustworthy)
- [ ] Replication package available (allows others to reproduce)
- [ ] Abstract emphasizes "real teams, real data" angle

**Timeline Advantage:** ESEM April deadline comes BEFORE Paper 1 OOPSLA feedback (July deadline). Can incorporate lessons learned.

---

### SECONDARY VENUE: TOSEM (ACM Transactions on Software Engineering and Methodology)

**Type:** Journal (not conference)
**Timeline:** Rolling submissions; ~6-month review cycle
**Positioning:** Deeper empirical study; longer paper allowed (30pp)

**Submit to TOSEM if:**
- ESEM accepts → expand to journal version
- ESEM rejects → revise and submit to journal (slower but higher prestige)
- Want longer treatment of empirical findings

---

### TERTIARY VENUE: FormaliSE 2027

**Workshop:** Formal Specification and Testing of Advanced AI Systems
**Deadline:** 2027 (dates TBD)
**Track:** Best for formal specification angle (ADR→test formalization)

---

## Paper 3: Observability & Consciousness Venue Strategy

### PRIMARY VENUE: SREcon 2026

**Conference:** Site Reliability Engineering, sponsored by Usenix
**Deadline:** April 1, 2026 (early) OR rolling submissions
**Format:** 30-40 minute talk + paper (10-15pp)

| Requirement | Status | Notes |
|-------------|--------|-------|
| **Audience** | SREs, platform engineers, DevOps | Strong practical focus |
| **Content** | Observability, autonomous recovery, consciousness | Novel angle |
| **Submission** | Abstract (300 words) + brief CV | Light review process |
| **Key Benefit** | Practitioner feedback; high-signal audience | Not academic rigor, but impact |

**Why SREcon:** SRE community cares deeply about observability and autonomous recovery. Consciousness-aware logging is novel SRE practice. Paper positions consciousness as SRE engineering problem.

**Submission Checklist:**
- [ ] ConsciousnessLoggerAdapter working end-to-end
- [ ] Demo prepared: system detecting own degradation
- [ ] Metrics: MTTR improvement vs traditional logging
- [ ] Case study from production Samstraumr usage
- [ ] Philosophy grounding optional but appreciated (positions as thought leadership)
- [ ] Abstract hook: "What happens when systems observe themselves?"

**Timeline Note:** SREcon is year-round selection; earlier submission better (April 2026 realistic)

---

### SECONDARY VENUES: ICSE DevOps Track 2027

**Conference:** ICSE with DevOps/SRE emphasis track
**Deadline:** September 2026
**Format:** Full paper to concurrent track

---

### PHILOSOPHICAL POSITIONING: Consciousness Philosophy Venues

**Alternative angle:** If SREcon reception strong, position Paper 3 also at philosophy venues:

| Venue | Type | Deadline | Notes |
|-------|------|----------|-------|
| **Minds & Machines** | Journal | Rolling | Top-tier consciousness philosophy |
| **Philosophy of Science** | Journal | Quarterly | Systems theory bridge angle |
| **Synthese** | Journal | Rolling | Synthesis of philosophy & empirical science |
| **AI & Society** | Journal | Rolling | Computational consciousness focus |

**Strategy:** Submit engineering version to SREcon (practical validation), philosophy version to journals (theoretical grounding)

---

## Paper 4: Patterns Venue Strategy

### PRIMARY VENUE: ECOOP 2027

**Conference:** European Conference on Object-Oriented Programming
**Deadline:** January 15, 2027 (notification April 2027)
**Track:** Design Patterns or Domain-Specific Languages

| Requirement | Status | Notes |
|-------------|--------|-------|
| **Audience** | OO design researchers, practitioners | Patterns community |
| **Format** | Paper (20pp) + pattern documentation | Pattern template essential |
| **Key Content** | Lifecycle pattern language formalization | Grounded in Gang of Four tradition |
| **Novelty** | New pattern family with biological inspiration | Strong fit |

**Why ECOOP:** ECOOP values novel design patterns. Component lifecycle is new pattern family grounded in biological development. Perfect venue for patterns research.

**Submission Checklist:**
- [ ] Pattern language formalized (each transition is pattern)
- [ ] Pattern template completed (intent, motivation, structure, participants, consequences)
- [ ] Figure: State machine with guards, actions, invariants for each transition
- [ ] Biological grounding section (ontogeny → software patterns)
- [ ] Component examples showing pattern applications
- [ ] Related patterns (references to Gang of Four, EIP)
- [ ] Abstract mentions "pattern language" explicitly
- [ ] References to Alexander, Gamma et al. pattern traditions

---

### SECONDARY VENUE: PLoP (Pattern Languages of Programs)

**Type:** Conference workshop (more pattern-friendly than ECOOP)
**Deadline:** March 2027 (rolling)
**Format:** Pattern paper (10-15pp) + structured workshopping

**Why PLoP:** Dedicated patterns community. More receptive to biological metaphor. Workshopping provides feedback.

**Strategy:** Submit to PLoP first (March 2027), then ECOOP (January 2027) in parallel.

---

## Paper 5: Consciousness & Philosophy Venue Strategy

### PRIMARY VENUE: Philosophy of AI Workshops (Major Conferences)

**Option A: IJCAI 2027 Workshop on Consciousness in AI**
**Option B: AAAI 2027 Workshop on AI and Consciousness**
**Option C: Standalone symposium on computational consciousness**

| Aspect | Details |
|--------|---------|
| **Timing** | Workshops typically March-August 2027 CFPs |
| **Format** | 15-20pp paper + presentation |
| **Audience** | AI researchers + philosophers of mind |
| **Advantage** | Faster publication; high-signal feedback; builds community |

---

### SECONDARY VENUE: Philosophy Journals

| Journal | Tier | Timeline | Best For |
|---------|------|----------|----------|
| **Minds & Machines** | Tier 1 | Rolling | Consciousness + computation |
| **Philosophy of Science** | Tier 1 | Rolling | Systems theory bridge |
| **Journal of Consciousness Studies** | Tier 2 | Rolling | Consciousness focus |
| **Synthese** | Tier 1 | Rolling | Synthesis of theory + evidence |

**Strategy:** Workshop first (gets feedback, builds community), then journal submission (permanent record, higher prestige)

---

### TERTIARY VENUE: AI & Society Conference

**Type:** Interdisciplinary conference
**Timeline:** 2027 dates emerging
**Focus:** Societal implications of conscious AI systems

---

## Master Submission Timeline

### Year 1 (2026)

| Month | Paper 1 | Paper 2 | Paper 3 | Papers 4-5 | Action |
|-------|---------|---------|---------|-----------|--------|
| **Feb** | Lit review | Study protocol | Implementation | Formalization | START |
| **Mar** | Lit review | Study protocol | Implementation | Formalization | |
| **Apr** | Lit review | ✅ **SUBMIT to ESEM** | ✅ **SUBMIT to SREcon** | Framework | |
| **May** | Framework | Study starts | Deploy logger | Framework | |
| **Jun** | Framework | Study | Data collection | Pattern docs | |
| **Jul** | ✅ **SUBMIT to OOPSLA** | Study | Data collection | Draft | |
| **Aug** | Revisions | Study | Analysis | Draft | |
| **Sep** | Revisions | ✅ **SUBMIT to ICSE** | Case study | ✅ **SUBMIT to PLoP** | |
| **Oct** | Awaiting feedback | Study | Case study | Revisions | |
| **Nov** | Feedback | Results | Writing | Revisions | |
| **Dec** | Writing | Writing | ✅ **SUBMIT to philosophy journal** | Philosophy positioning | |

### Year 2 (2027)

| Month | Paper 1 | Paper 2 | Paper 3 | Paper 4 | Paper 5 | Action |
|-------|---------|---------|---------|---------|---------|--------|
| **Jan** | Awaiting decision | Awaiting decision | Decision from philosophy | ✅ **SUBMIT to ECOOP** | Workshop prep | |
| **Feb** | Decision | Decision | Revisions | Awaiting decision | Workshop prep | |
| **Mar** | Revisions | Decision | Revisions | Awaiting decision | ✅ **SUBMIT to Philosophy Workshop** | |
| **Apr** | Revisions | Decision | Revisions | Decision | Decision | Resubmit if rejected |
| **May** | Final | Final | Final | Revisions | Revisions | |
| **Jun** | Camera-ready | Camera-ready | Camera-ready | Final | Final | |
| **Jul** | PUBLISHED | PUBLISHED | PUBLISHED | Revisions | Revisions | |
| **Aug** | | | | Camera-ready | Final | |
| **Sep** | | | | PUBLISHED | Camera-ready | |
| **Oct** | | | | | PUBLISHED | SUCCESS |

---

## Contingency Plan: If Primary Venues Reject

### Paper 1 Rejection Fallback
1. **Primary Plan:** Revise for ICSE 2027 (September 2026 deadline)
2. **Backup Plan:** Submit to TOSEM journal (deeper empirical study)
3. **Alternative:** Position as architecture/systems theory venue (Software & Systems Modeling journal)

### Paper 2 Rejection Fallback
1. **Primary Plan:** Revise for FormaliSE 2027 (focus on formalization angle)
2. **Backup Plan:** TOSEM journal (expanded empirical study)
3. **Alternative:** DevOps/SRE venue (emphasize continuous compliance angle)

### Paper 3 Rejection Fallback
1. **Primary Plan:** Philosophy journal submission (Minds & Machines)
2. **Backup Plan:** AI conference workshop track
3. **Alternative:** DevOps conference (position as observability technique)

### Paper 4 Rejection Fallback
1. **Primary Plan:** ECOOP 2027 after PLoP feedback
2. **Backup Plan:** Journal version (Software & Systems Modeling)
3. **Alternative:** Domain-specific patterns venue

### Paper 5 Rejection Fallback
1. **Primary Plan:** Different philosophy journal
2. **Backup Plan:** AI & Society conference
3. **Alternative:** Position as computer science paper at main AI venue (IJCAI, AAAI)

---

## Submission Quality Checklist

**Before ANY submission, verify:**

- [ ] **Novelty:** Paper makes clear contribution beyond prior work
- [ ] **Empirical Data:** Claims supported by evidence, not assertion
- [ ] **Writing Quality:** Professionally edited; no grammar errors
- [ ] **Figures:** Clear, labeled, referenced in text
- [ ] **Related Work:** Acknowledges prior work; positions novel contribution
- [ ] **Ethics:** No plagiarism; proper attribution; no undisclosed AI use
- [ ] **Replicability:** Sufficient detail for others to reproduce results
- [ ] **Format:** Follows venue template exactly (LaTeX, Word, etc.)
- [ ] **References:** All citations complete, formatted correctly
- [ ] **Conflict of Interest:** Declared truthfully
- [ ] **Author List:** All contributors acknowledged; order agreed

---

## Key Contact Information

### Likely Reviewers (Approach in Month 18-20)

**For Papers 1-2 (Architecture):**
- Darko Marinov (UIUC) - Software testing, architecture verification
- Ralph Johnson (UIUC) - Design patterns, architecture
- Poul-Henning Kamp (FreeBSD) - Systems design, practical engineering

**For Paper 3 (Observability):**
- Bryan Cantrill (Joyent/Planet Labs) - Systems thinking, observability
- Kelsey Hightower (Google Cloud) - Cloud systems, observability
- Charity Majors (Honeycomb) - Observability leadership

**For Paper 4 (Patterns):**
- Erich Gamma (Microsoft) - Design patterns pioneer
- Grady Booch (IBM) - Component models, architecture
- Linda Rising - Patterns community leadership

**For Paper 5 (Consciousness):**
- Daniel Dennett (Tufts) - Consciousness philosophy
- David Chalmers (NYU) - Consciousness research
- Thomas Nagel (NYU) - Philosophy of mind

---

## Tracking Sheet

**Use this to track submission status:**

```
Paper 1 - Blockbuster (Biological Resilience)
├─ OOPSLA 2026: Submitted [DATE] → Notification Jan 2027 → Status: [PENDING/ACCEPT/REJECT/REVISE]
├─ ICSE 2027: Submitted [DATE] → Notification Dec 2026 → Status: [PENDING/ACCEPT/REJECT/REVISE]
└─ PLDI 2027: Submitted [DATE] → Notification Feb 2027 → Status: [PENDING/ACCEPT/REJECT/REVISE]

Paper 2 - Architecture Testing (Executable Specifications)
├─ ESEM 2026: Submitted [DATE] → Notification June 2026 → Status: [PENDING/ACCEPT/REJECT/REVISE]
├─ ICSE DevOps 2027: Submitted [DATE] → Status: [PENDING/ACCEPT/REJECT/REVISE]
└─ FormaliSE 2027: Submitted [DATE] → Status: [PENDING/ACCEPT/REJECT/REVISE]

Paper 3 - Consciousness Logging (Observability)
├─ SREcon 2026: Submitted [DATE] → Notification April 2026 → Status: [PENDING/ACCEPT/REJECT]
├─ Minds & Machines: Submitted [DATE] → Status: [PENDING/REVIEW/ACCEPT/REJECT]
└─ ICSE DevOps 2027: Submitted [DATE] → Status: [PENDING/ACCEPT/REJECT/REVISE]

Paper 4 - Lifecycle Patterns (Ontogenetic Design)
├─ PLoP 2027: Submitted [DATE] → Notification May 2027 → Status: [PENDING/ACCEPT/REJECT]
├─ ECOOP 2027: Submitted [DATE] → Notification April 2027 → Status: [PENDING/ACCEPT/REJECT/REVISE]
└─ Software & Systems Modeling: Submitted [DATE] → Status: [PENDING/REVIEW/ACCEPT/REJECT]

Paper 5 - Computational Consciousness (Philosophy)
├─ AI Consciousness Workshop: Submitted [DATE] → Status: [PENDING/ACCEPT/REJECT]
├─ Minds & Machines: Submitted [DATE] → Status: [PENDING/REVIEW/ACCEPT/REJECT]
└─ Philosophy of Science: Submitted [DATE] → Status: [PENDING/REVIEW/ACCEPT/REJECT]
```

---

## Success Definition

**Minimum Success:**
- Paper 1: Accept to any Tier 1 venue (OOPSLA, ICSE, PLDI)
- Paper 2: Accept to ESEM or equivalent
- Paper 3: Accept to SREcon or philosophy journal
- Papers 4-5: At least one each to appropriate venue

**Aspirational Success:**
- All 5 papers accepted to primary venues
- Papers cited in subsequent research
- Framework adopted by multiple enterprises
- Samstraumr becomes canonical implementation of systems theory in software

---

**Created:** February 6, 2026
**Next Review:** April 1, 2026 (after first submissions)
