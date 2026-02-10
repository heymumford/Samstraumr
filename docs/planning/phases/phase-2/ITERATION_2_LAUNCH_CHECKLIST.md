# ITERATION 2 LAUNCH CHECKLIST
## Pre-Week-1 Preparation (Week 0 Tasks)

**Purpose**: Get everything in place so Week 1 starts cleanly.
**Owner**: Eric (Research Prioritizer transitioning to Implementation Realist)
**Timeline**: 5-7 days before Week 1 starts

---

## SECTION 1: STAFFING & CONTRACTING (CRITICAL PATH)

- [ ] **Collaborator (Experimentalist) — Recruit & Hire**
  - [ ] Define role: Experimentalist, data science background
  - [ ] Post job description (internal + external)
  - [ ] Interview 3+ candidates
  - [ ] Offer extended; contract signed
  - [ ] Start date: Week 1, Monday
  - [ ] Onboarding: 4 hours on RESOURCE_ALLOCATION document
  - **Owner**: You
  - **Deadline**: 5 days before Week 1

- [ ] **Contractor 1 (Tooling Engineer) — Contract Signed**
  - [ ] Scope: Smart linter development (Weeks 5-12, 96 hours)
  - [ ] Budget: ~$4,800 @ $50/hr
  - [ ] Start date: Week 5, Monday (but prep begins Week 3)
  - [ ] Contract signed by: 7 days before Week 5
  - [ ] Backup contractor identified (if primary unavailable)
  - **Owner**: You
  - **Deadline**: 2 weeks before Week 5

- [ ] **Contractor 2 (Technical Writer) — Contract Signed**
  - [ ] Scope: Paper drafting + revisions (Weeks 8-24, 256 hours)
  - [ ] Budget: ~$19,200 @ $75/hr
  - [ ] Start date: Week 8, Monday (but prep begins Week 5)
  - [ ] Contract signed by: 7 days before Week 8
  - [ ] Portfolio review: Academic writing samples required
  - **Owner**: You
  - **Deadline**: 3 weeks before Week 8

- [ ] **Contractor 3 (Data Scientist) — Contract Signed**
  - [ ] Scope: Statistical analysis (Weeks 3-8, 48 hours)
  - [ ] Budget: ~$2,100 @ $43.75/hr
  - [ ] Start date: Week 3, Wednesday
  - [ ] Contract signed by: 7 days before Week 3
  - [ ] Skill verification: Can run statistical tests in R/Python?
  - **Owner**: You
  - **Deadline**: 2 weeks before Week 3

- [ ] **Budget Approval**
  - [ ] Total: ~$156,100 (salary + contractors)
  - [ ] Approve Eric salary (1.0 FTE @ $3,500/wk × 20 = $70K)
  - [ ] Approve Collaborator salary (1.0 FTE @ $3,000/wk × 20 = $60K)
  - [ ] Approve contractor fees ($4.8K + $19.2K + $2.1K = $26.1K)
  - [ ] Any budget adjustments needed? (Document why)
  - **Owner**: You
  - **Deadline**: 2 weeks before Week 1

---

## SECTION 2: INFRASTRUCTURE & TOOLING (BLOCKING)

- [ ] **Maven / Build System Ready**
  - [ ] Maven 3.9+ installed locally
  - [ ] Java 21 installed and verified
  - [ ] `mvn clean verify` runs successfully (0 errors)
  - [ ] JaCoCo coverage plugin configured (target: 80%)
  - [ ] Spotless code formatter configured
  - **Owner**: You (or Collaborator)
  - **Deadline**: 3 days before Week 1

- [ ] **Git Repository Prepared**
  - [ ] Main branch is clean (no uncommitted changes)
  - [ ] Branch naming convention decided (feature/TASK-ID-description)
  - [ ] .gitignore includes all IDE files + build artifacts
  - [ ] Pre-commit hooks installed? (Optional but recommended)
  - [ ] CI/CD pipeline passes on main
  - **Owner**: You
  - **Deadline**: 3 days before Week 1

- [ ] **Issue Tracking System Populated**
  - [ ] Jira (or GitHub Issues) project created: "Samstraumr Iteration 2"
  - [ ] 20 top-priority items created as issues:
    - [ ] Rank 36 (ConsciousnessLoggerAdapter)
    - [ ] Rank 1 (Consciousness Temporal Logic)
    - [ ] Rank 6 (Consciousness Separation)
    - [ ] Rank 8 (DDD Bounded Contexts)
    - [ ] Rank 9 (Consciousness Aggregate)
    - [ ] ... (15 more)
  - [ ] Each issue links to RESOURCE_ALLOCATION_ITERATION_2.md
  - [ ] Story points estimated (see Gantt chart)
  - [ ] Assignees assigned (Eric, Collaborator, or contractor name)
  - [ ] Sprint planning configured (Weeks 1-5, 6-12, 13-15, 16-20)
  - **Owner**: You
  - **Deadline**: 2 days before Week 1

- [ ] **Collaboration Tools Setup**
  - [ ] Slack channel: #samstraumr-iteration-2 (or Discord)
  - [ ] Weekly sync scheduled (Mondays, 10am, 1 hour)
  - [ ] Gate review meetings scheduled:
    - [ ] Week 4: Gate 1 (15:00-16:00)
    - [ ] Week 8: Gate 2 (15:00-16:00)
    - [ ] Week 12: Gate 3 (15:00-16:00)
    - [ ] Week 15: Gate 4 (CRITICAL, 2 hours, 14:00-16:00)
    - [ ] Week 20: Gate 5 (15:00-16:00)
  - [ ] Shared drive / Git repo for documents + data
  - [ ] Calendar invites sent to all participants
  - **Owner**: You
  - **Deadline**: 1 day before Week 1

---

## SECTION 3: DOCUMENTATION & KNOWLEDGE BASE

- [ ] **Core Documents Reviewed**
  - [ ] Eric reads: RESOURCE_ALLOCATION_ITERATION_2.md (full)
  - [ ] Eric reads: GATE_DECISION_FRAMEWORK.md (full)
  - [ ] Eric reads: ITERATION_2_EXECUTIVE_SUMMARY.md (full)
  - [ ] Collaborator reads: RESOURCE_ALLOCATION (Weeks 1-12 focus)
  - [ ] Collaborator reads: GATE_DECISION_FRAMEWORK (Gates 2, 4, 5)
  - [ ] Contractors read: Relevant sections only (tooling, writing, stats)
  - **Owner**: Each person (self-paced, 2 hours reading)
  - **Deadline**: 2 days before Week 1

- [ ] **Week 1 Prep Meeting (1 hour)**
  - [ ] Attendees: Eric, Collaborator, (optional: contractors if available)
  - [ ] Agenda:
    1. Highlight critical path (Rank 36 + Rank 1 + Rank 6)
    2. Explain 5 gates (YES/NO decisions, not suggestions)
    3. Review Week 1 tasks (read checklist below)
    4. Q&A: Clarify roles, timeline, expectations
  - [ ] Recording: Slack thread or meeting notes
  - **Owner**: You (facilitator)
  - **Deadline**: 1 day before Week 1
  - **Action**: Send agenda 24 hours in advance

- [ ] **Risk Register Initialized**
  - [ ] Spreadsheet created: "Samstraumr Iteration 2 Risks"
  - [ ] Top 5 risks documented (from ITERATION_2_EXECUTIVE_SUMMARY):
    1. Gate 4 fails (recovery <70%)
    2. Developer recruitment fails
    3. Contractor unavailability
    4. Gate 1 fails (philosophy unfalsifiable)
    5. Harsh review feedback
  - [ ] Each risk has: description, probability, impact, mitigation
  - [ ] Weekly review scheduled in sync meetings
  - **Owner**: You
  - **Deadline**: 1 day before Week 1

---

## SECTION 4: RESEARCH PREPARATION (RANK 11, 20)

- [ ] **Developer Recruitment Campaign (Start Immediately)**
  - [ ] Goal: Recruit 50+ developers for cognitive load A/B test
  - [ ] Recruitment message drafted (what's in it for them?)
  - [ ] Recruitment channels identified:
    - [ ] Internal Guild staff (target: 20-30)
    - [ ] Local universities (target: 10-15)
    - [ ] LinkedIn / Twitter outreach (target: 5-10)
  - [ ] Sign-up form created (name, email, experience level, timezone)
  - [ ] Target: 50 sign-ups by end of Week 2 (necessary for Week 4 study)
  - **Owner**: Collaborator
  - **Deadline**: Week 0 (start immediately)

- [ ] **Literature Review Started (Rank 1, 41)**
  - [ ] Eric begins gathering papers on:
    - [ ] Temporal logic (Lamport, Manna-Pnueli)
    - [ ] Consciousness (IIT, Hofstadter, phenomenology)
    - [ ] Autopoiesis (Maturana-Varela)
    - [ ] Clean architecture + resilience
  - [ ] Reading list compiled in shared doc
  - [ ] Annotation tool selected (Zotero / Readwise)
  - [ ] Target: 20 core papers read by end of Week 2
  - **Owner**: Eric
  - **Deadline**: Week 0 (start immediately)

- [ ] **Study Protocol Drafted (Ranks 11, 20)**
  - [ ] A/B test protocol written (methodology, statistical power)
  - [ ] Cognitive load quantification study protocol written
  - [ ] IRB approval assessed: Do we need formal review? (Probably not for internal study)
  - [ ] Informed consent form drafted (if needed)
  - [ ] Task scenarios written (3-4 scenarios, ~30 min each)
  - [ ] Measurement instruments selected (NASA-TLX, task completion time, error rate)
  - **Owner**: Collaborator + Data Scientist (contractor 3)
  - **Deadline**: End of Week 1

---

## SECTION 5: ARCHITECTURE PREPARATION (RANKS 36, 6, 8, 9)

- [ ] **ConsciousnessLoggerAdapter Audit (Rank 36)**
  - [ ] Count lines of consciousness code currently in system (767 lines per Iteration 1)
  - [ ] Identify all incomplete feature files (7 pending per Iteration 1)
  - [ ] List all missing test cases
  - [ ] Create subtasks: Finish feature file X, implement assertion Y, etc.
  - [ ] Assign to Eric; schedule Weeks 1-4
  - **Owner**: You + Eric
  - **Deadline**: 3 days before Week 1

- [ ] **DDD Context Map Prepared (Rank 8)**
  - [ ] Identify 5 domain experts (internal stakeholders) to interview
  - [ ] Interview questions drafted (what are the 5 domains in Samstraumr?)
  - [ ] Whiteboard / diagramming tool prepared (Miro, LucidChart, or pen & paper)
  - [ ] Schedule 5 x 30-min interviews (Weeks 1-2)
  - **Owner**: Collaborator
  - **Deadline**: 2 days before Week 1

- [ ] **Code Structure Baseline**
  - [ ] Run static analysis: Package structure, dependencies, class hierarchy
  - [ ] Identify where consciousness code lives (scattered across adapters)
  - [ ] Confirm Component.java size (expected: 2000+ lines, monolith)
  - [ ] Baseline metrics committed to Git (for comparison post-refactoring)
  - **Owner**: You
  - **Deadline**: 3 days before Week 1

---

## SECTION 6: MEASUREMENT INFRASTRUCTURE (RANK 34, 13)

- [ ] **JMH Benchmark Setup (Rank 34)**
  - [ ] JMH dependency added to pom.xml
  - [ ] Benchmark scaffold created (src/main/java/org/s8r/benchmark/)
  - [ ] 3 example benchmarks written (template for others to follow)
  - [ ] Baseline run completed (1 microbenchmark, confirm tools work)
  - [ ] Variance test: Same benchmark 3x, confirm variance <10%
  - **Owner**: Collaborator
  - **Deadline**: 2 days before Week 1

- [ ] **State Transition Coverage Instrumentation (Rank 13)**
  - [ ] State machine transition counter added to State enum
  - [ ] Coverage measurement code written
  - [ ] Test suite run once; transition coverage reported
  - [ ] Expected result: ~25% coverage (50 possible, 12-15 tested)
  - [ ] Baseline committed to Git
  - **Owner**: You
  - **Deadline**: 3 days before Week 1

---

## SECTION 7: CONTINGENCY PLANNING

- [ ] **Plan B Documents Drafted**
  - [ ] If Gate 1 fails: Alternative "consciousness as observability" approach (2 pages)
  - [ ] If Gate 2 fails: Reduced refactoring scope (1 page)
  - [ ] If Gate 4 fails: Alternative paper narrative "Enabling Architecture" (Rank 42a, 3 pages)
  - [ ] If contractor unavailable: Backup plan (who does their work?)
  - **Owner**: Eric
  - **Deadline**: 1 day before Week 1

- [ ] **Backup Contractors Identified**
  - [ ] Tooling: 2nd engineer identified (on standby, not hired yet)
  - [ ] Writing: 2nd writer identified (on standby)
  - [ ] Data science: 2nd statistician identified (on standby)
  - [ ] Contact info stored in secure location
  - **Owner**: You
  - **Deadline**: 1 day before Week 1

---

## SECTION 8: FINAL CHECKLIST (DAY BEFORE WEEK 1)

- [ ] **Staffing Confirmed**
  - [ ] Collaborator onboarded; knows Week 1 tasks
  - [ ] All contractors contacted; ready to start on scheduled dates
  - [ ] Backup contractors on file

- [ ] **Timeline Confirmed**
  - [ ] Gantt chart reviewed; any adjustments?
  - [ ] Weekly sync calendar set (Mondays, 1 hour, all 20 weeks)
  - [ ] Gate review meetings scheduled (Weeks 4, 8, 12, 15, 20)
  - [ ] Adjusted timeline communicated to leadership (if needed)

- [ ] **Documentation Complete**
  - [ ] All 3 Iteration 2 documents in shared repository
  - [ ] Week 1 checklist printed / shared digitally
  - [ ] RESOURCE_ALLOCATION document linked from Jira issues

- [ ] **Infrastructure Ready**
  - [ ] Maven build passes
  - [ ] Git repo clean
  - [ ] Jira populated with 20 issues
  - [ ] Slack channel created; invites sent

- [ ] **Kick-Off Meeting Completed**
  - [ ] Agenda covered (roles, timeline, gates, Week 1 tasks)
  - [ ] Q&A addressed
  - [ ] Decision: Ready to start Week 1? (YES/NO)

---

## WEEK 1 TASKS (START HERE)

### Monday, Week 1

**Eric** (40 hours):
- [ ] Complete Rank 36: 25% → 50%
  - [ ] Implement 2-3 missing methods in ConsciousnessLoggerAdapter
  - [ ] Write feature file #3-5
  - [ ] Run tests; 0 failures
- [ ] Start Rank 1: Draft 3 temporal logic formulas
  - [ ] Research Lamport TL syntax
  - [ ] Draft Formula A: "Always self-observing implies eventually state updated"
  - [ ] Draft Formula B: Alternative approach
  - [ ] Draft Formula C: Conservative fallback
- [ ] Start Rank 6: Code extraction
  - [ ] Create org.s8r.component.consciousness package
  - [ ] Move 100 lines of consciousness code (test move, not full yet)
- [ ] Start Rank 8: Schedule stakeholder interviews (Weeks 1-2)

**Collaborator** (40 hours):
- [ ] Start Rank 13: Measure state transition coverage
  - [ ] Add transition counter to State enum
  - [ ] Run test suite once
  - [ ] Report: "Found 50 possible transitions; tested 13; coverage 26%"
- [ ] Start Rank 11: Cognitive load study design
  - [ ] Draft study protocol
  - [ ] Write task scenarios (3 scenarios)
  - [ ] Create informed consent form
- [ ] Start Rank 8: Schedule stakeholder interviews
  - [ ] Contact 5 domain experts
  - [ ] Schedule 30-min interviews (Weeks 1-2)
- [ ] Confirm recruitment campaign (50+ developers signed up?)

**Contractor 3** (8 hours, starts Wednesday Week 3):
- [ ] Not yet (starts Week 3)

### End of Week 1

**Success Metrics**:
- [ ] Rank 36 at 50% completion
- [ ] Rank 1: 3 formula candidates drafted
- [ ] Rank 6: Code extraction started; 100 lines moved
- [ ] Rank 8: 5 stakeholder interviews scheduled
- [ ] Rank 13: Transition coverage measured (expect ~25%)
- [ ] Rank 11: Study protocol drafted
- [ ] 50+ developers recruited (on track for Week 4 study)
- [ ] 0 blockers preventing Week 2 progression

**Decision at Week 1 Sync**:
- [ ] On track? (YES/NO)
- [ ] Any adjustments needed?
- [ ] Any risks emerging?

---

## ESCALATION PATH

**If something breaks**:
1. **First response** (owner): Try to fix immediately
2. **If can't fix in 4 hours**: Alert Eric in Slack
3. **If still blocked after 8 hours**: Call emergency 30-min sync
4. **If blocks critical path**: Re-prioritize Iteration 2 (may affect Gates)

**Critical blockers** (call emergency meeting):
- Collaborator unavailable (need immediate replacement)
- Rank 36 or Rank 1 stall (blocks everything)
- Gate decision missed (decision must happen; contingency plan activated)
- Contractor cancels (activate backup)

---

## SUCCESS DEFINITION

**Iteration 2 is "ready to launch" when**:
- [ ] All staffing confirmed (Eric, Collaborator, 3 contractors)
- [ ] Budget approved
- [ ] Infrastructure ready (Maven, Git, Jira, Slack)
- [ ] Core documents reviewed (all participants understand framework)
- [ ] Week 1 preparation complete (Rank 36 audit, literature review started, recruitment begun)
- [ ] All 5 gate review meetings scheduled
- [ ] Risk register initialized
- [ ] Contingency plans drafted
- [ ] Week 1 kick-off meeting completed successfully

**Launch decision**: Go/No-Go on Monday, Week 1
- **GO**: Proceed with Week 1 tasks
- **NO-GO**: Address blockers; delay Week 1 start by 1 week max

---

**This checklist is your operational playbook. Use it as the source of truth for Week 0 preparation.**

**Questions?** Schedule 30-min sync with all participants before Week 0 ends.

**Ready for execution. Iteration 2 Launch Protocol: ACTIVE**
