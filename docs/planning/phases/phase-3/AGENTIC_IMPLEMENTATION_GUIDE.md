# Agentic Implementation Guide

**Status**: Planning-with-files structure complete. Ready for agent dispatch.

**Created**: 2026-02-08  
**Scope**: Samstraumr consciousness research program (20 weeks, 1,760 hours, 100 features)

---

## Quick Start (Read This First)

### For the Solo Architect
1. **Distribute this structure**: Send `QUICK_REFERENCE_CARD.md` to team
2. **Load task_plan.md**: Read before every major decision
3. **Update phase_tracker.md**: After each week/gate
4. **Dispatch agents**: Use feature_tracking_sheet.md to brief them

### For Agents Dispatched by the Architect
1. Read: `task_plan.md` (your charter + timeline)
2. Read: `findings_catalog.md` (what analysis exists)
3. Read: `phase_tracker.md` (where we are now)
4. Read: `feature_tracking_sheet.md` (your assignment)
5. Reference: `FINAL_RESEARCH_RANKINGS.md` + `IMPLEMENTATION_PLAN_ITERATION_3.md` (source truth)

---

## The 4-File Master Structure

### 1. task_plan.md (You Are Here)
**Purpose**: Phases, staffing, gates, decisions, status  
**Update**: After each gate or major decision  
**Read Before**: Any decision spanning phases

**Key Sections**:
- Phases 1-4 with checkboxes
- Staffing model (2 FTE + 3 contractors, 1,760 hours)
- 5 critical gates (Week 4, 8, 12, 15, 20)
- Decisions locked
- Errors encountered

**Agent Usage**: This IS your contract. Read it to understand scope + timeline.

---

### 2. findings_catalog.md
**Purpose**: Catalog all 87 analysis files, organized by purpose  
**Update**: Only if new files added  
**Read When**: You need to reference a specific analysis

**Key Sections**:
- Master planning documents (8 files — start here)
- Phase 1 analysis (11 files — Martin Fowler debate)
- Phase 2 expert panel (3 iterations)
- Decision artifacts + extended planning
- JSON data files

**Agent Usage**: "Where is analysis X?" → Check this file. It tells you the file name + purpose.

---

### 3. feature_tracking_sheet.md
**Purpose**: All 100 features with effort, owner, dependencies, status  
**Update**: Weekly (mark features complete/blocked)  
**Read When**: Assigning work or checking what's due

**Key Sections**:
- Phase 1-4 features (31, 28, 27, 14 features)
- Each feature: ID, name, type, effort, owner, week, dependencies
- Status legend (Pending, In Progress, Code Review, Blocked, Complete)
- Effort verification (by type + owner)

**Agent Usage**: Dispatch agents from this sheet. Update after features complete.

---

### 4. phase_tracker.md
**Purpose**: Weekly progress + gate checkpoints + effort tracking  
**Update**: Weekly (after work done)  
**Read When**: Status update needed

**Key Sections**:
- Phase 1-4 weekly effort allocation (53 weeks mapped)
- 5 gate decision criteria + contingencies
- Effort tracking dashboard (Planned vs Actual)
- Gate status (GO/NO-GO/MODIFIED/PIVOT)
- Critical path visualization

**Agent Usage**: This shows the deadline for YOUR feature. Miss it, escalate.

---

## Dispatch Protocol (How to Send Agents to Work)

### Before Dispatch
1. **Read task_plan.md** — Confirm goal + phase
2. **Read phase_tracker.md** — Confirm deadline + gate dependencies
3. **Read feature_tracking_sheet.md** — Identify features to assign
4. **Select agent** from `/snap` pool based on feature type

### During Dispatch
```
AGENT ASSIGNMENT:
- Feature ID: [36.1]
- Feature: Create consciousness-logger-adapter module structure
- Type: Code
- Effort: 20 hours
- Owner: [Agent name]
- Week: 1
- Dependencies: None
- Gate: 1 (due Week 4)
- Success Criteria: Module exists, package structure clear, README written

CONTEXT PROVIDED:
- task_plan.md (charter)
- FINAL_RESEARCH_RANKINGS.md (why this ranks #36)
- IMPLEMENTATION_PLAN_ITERATION_3.md (detailed feature spec)
- QUICK_REFERENCE_CARD.md (1-page operator guide)
```

### After Dispatch
- Feature status → update to "In Progress"
- Weekly: Ask agent for hours spent + blockers
- When complete: Update status → "Code Review"
- After code review: Update status → "Complete"
- If blocked: Update status → "Blocked" + add error to task_plan.md

---

## Gate Decision Protocol

### Before Each Gate
1. Read gate criteria in phase_tracker.md
2. Verify all features due before gate are complete or blocked
3. Collect agent feedback + data
4. Run contingency if criteria not met

### Gate Decision Template
```
GATE [N] DECISION (Week [X])

GO Criteria Met?
  [ ] Criterion 1: [verified Y/N]
  [ ] Criterion 2: [verified Y/N]
  [ ] Criterion 3: [verified Y/N]

Decision: [GO | NO-GO | MODIFIED | PIVOT]

Rationale: [Brief explanation]

Action Items:
- [ ] Update phase_tracker.md gate status
- [ ] Brief stakeholders (same day if NO-GO)
- [ ] Execute contingency if needed
- [ ] Update task_plan.md decisions section
```

### Critical Gates
- **Gate 1 (Week 4)**: Consciousness falsifiable? (Rank 1 + philosophy review)
- **Gate 2 (Week 8)**: A/B test shows effect? (Rank 11 results)
- **Gate 3 (Week 12)**: Architecture sound? (Ranks 8, 9)
- **Gate 4 (Week 15)**: Recovery ≥70%? [EXISTENTIAL — determines publication narrative]
- **Gate 5 (Week 20)**: All papers submitted?

---

## Agent Model Selection (From Registry)

**MANDATORY: Before dispatch, consult `~/.claude/model_registry.yaml`**

| Agent Type | Model | Cost | When |
|-----------|-------|------|------|
| Code execution | haiku | Baseline | Build features, tests, infra |
| Research/Analysis | haiku | Baseline | Experiments, measurements, validation |
| Architecture design | opus | 60x baseline | System design, decisions (rare) |
| Planning/Synthesis | opus | 60x baseline | Design strategies (rare) |

**Default**: Haiku for 90% of work. Only opus for critical architectural decisions.

---

## Emergency Protocols

### Feature Blocked (Can't Proceed)
1. **Log to task_plan.md** → "Errors Encountered" section
2. **Update feature_tracking_sheet.md** → Status = "Blocked"
3. **Escalate immediately** — Blocking feature is on critical path
4. **Contingency**: Run backup implementation or extend phase

### Gate Fails (NO-GO)
1. **Stop work immediately** — Don't proceed to next phase
2. **Execute contingency playbook** → See `PHASE_1_BACKUP_PLAYBOOKS.md`
3. **Brief stakeholders same day** — No surprises
4. **Update task_plan.md** → Log decision + rationale

### Gate 4 Fails (Recovery <70%)
**EXISTENTIAL DECISION — Publication narrative changes**
1. If 60-70%: Reframe as "recovery-enabling" (still publishable, venue changes)
2. If <60%: Use alternative narrative (Rank 42a pre-written, 2-3 week rewrites)
3. **Pre-written alternatives** exist in `PHASE_1_BACKUP_PLAYBOOKS.md`
4. Extend Week 16 start by 1-2 weeks if major rewrites needed

---

## Agent Efficiency Checklist

Before dispatching ANY agent, verify:

- [ ] Read task_plan.md (not just summary)
- [ ] Consulted model registry (correct model selected)
- [ ] No redundant dispatch (same feature already assigned?)
- [ ] Dependencies checked (no wait-on-missing blocker?)
- [ ] Success criteria clear (agent knows what "done" means)
- [ ] Contingency identified (what if this feature fails?)

---

## Weekly Sync Checklist

**Every Monday, 30 minutes**:

1. **Effort tracking**: Collect hours from Eric, Collaborator, Contractors
2. **Feature status**: Mark complete/in-progress/blocked
3. **Blockers**: Surface issues + escalate if needed
4. **Next week**: Preview upcoming features + assignments
5. **Gate prep**: If gate due this week, verify criteria

**Update files**:
- phase_tracker.md (Actual Hours column)
- feature_tracking_sheet.md (Status column)
- task_plan.md (Errors Encountered section if blockers)

---

## File Editing Protocol

**MANDATORY for all agents/architects**:

1. **Before editing**: Run `git status` (see untracked files)
2. **Editing task_plan.md**: Only update [x] checkboxes + Status + Errors
3. **Editing phase_tracker.md**: Only update Effort Tracking + Gate Status
4. **Editing feature_tracking_sheet.md**: Only update Status column
5. **After editing**: Commit with message: `update [filename]: [what changed]`
6. **Do NOT edit**: findings_catalog.md (archive only), master analysis files

---

## Success Indicators

### Week 1 (Foundation Kickoff)
- [ ] All 67.5 hours tracked (Eric 30h + Collab 37.5h)
- [ ] Rank 36.1-36.2 started (consciousness logger)
- [ ] Rank 1.1-1.2 started (consciousness formalization)
- [ ] Rank 8.1-8.2 started (DDD mapping)

### Week 4 (Gate 1 Decision)
- [ ] Ranks 36, 1, 6, 8 complete or near-complete
- [ ] Philosophy expert review scheduled
- [ ] Gate 1 decision made (GO/NO-GO)
- [ ] If GO: Proceed to Phase 2 Rank 14 (chaos)

### Week 8 (Gate 2 Decision)
- [ ] Rank 11 A/B test: N=50 developers, results analyzed
- [ ] Rank 13 coverage: State transition baseline measured
- [ ] Gate 2 decision made (GO/NO-GO)
- [ ] If GO: Confirm refactoring will proceed (or save 150h)

### Week 15 (Gate 4 Decision) [EXISTENTIAL]
- [ ] Ranks 26-28 experiments: 20 failures tested
- [ ] Recovery rate measured (≥70%? <70%?)
- [ ] Alternative narratives ready if needed
- [ ] Gate 4 decision: Publication narrative locked

### Week 20 (Gate 5 + Publication)
- [ ] 4 papers submitted (Ranks 41-44)
- [ ] All data archived + reproducible
- [ ] Venue confirmations received
- [ ] Year 2 planning begins

---

## Artifacts Available for Agent Reference

**Always provide these when dispatching agents:**

| File | Purpose | Agent Should Read |
|------|---------|------------------|
| QUICK_REFERENCE_CARD.md | 1-page operator guide | YES (context) |
| FINAL_RESEARCH_RANKINGS.md | Why this feature exists | YES (motivation) |
| IMPLEMENTATION_PLAN_ITERATION_3.md | Detailed specs for all 100 features | YES (detailed brief) |
| EXPERT_PANEL_SYNTHESIS_MASTER.md | How plan was created | MAYBE (optional context) |
| PHASE_1_BACKUP_PLAYBOOKS.md | What to do if blocked | MAYBE (contingency) |
| VALUE_FRAMEWORK_ITERATION_1.md | Value philosophy | NO (reference only) |
| RESOURCE_ALLOCATION_ITERATION_2.md | Staffing + schedule | NO (reference only) |

---

## Status: Ready for Execution

✅ All 4 master planning files created  
✅ 87 analysis files cataloged  
✅ 100 features decomposed + estimated  
✅ 5 gates with criteria + contingencies  
✅ Staffing model locked (2 FTE + 3 contractors)  
✅ 20-week timeline defined  
✅ Agent dispatch protocol ready

**Next Action**: Week 0 pre-work checklist (see `PHASE_1_EXECUTION_CHECKLIST.md`)

---

**Questions?** Read the relevant master document (task_plan.md, findings_catalog.md, phase_tracker.md, feature_tracking_sheet.md) or reference QUICK_REFERENCE_CARD.md.

**Status**: READY FOR WEEK 1 KICKOFF (Feb 17, 2026)
