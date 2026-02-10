# Task Plan: Phase 1 Validation Sprint (TDD Execution)

## Goal
Execute 4 validation tests TODAY to decide GO (Feb 10-14 launch) or DEFER (Feb 18 restart) by Sunday 6 PM.

---

## Phases
- [ ] **Phase 1: TEST SETUP** — Define 4 assertions + templates (TODAY, 30 min)
- [ ] **Phase 2: TEST 1 — Finance Pre-Approval** (TODAY, 0.5h)
- [ ] **Phase 3: TEST 2 — Warm Pipeline** (TODAY-SUN, 3h total)
- [ ] **Phase 4: TEST 3 — Team Alignment** (SAT-SUN, 1h scheduling + call)
- [ ] **Phase 5: TEST 4 — Decision Capacity** (SUN, 0.5h reflection)
- [ ] **Phase 6: GO/DEFER DECISION** (SUN 6 PM, announce to team)

---

## TEST 1: Finance Pre-Approval (Highest Criticality)

**TDD Structure**:

```
ARRANGE:
  - Finance Director name: [Get from Guild email]
  - Email template ready: YES

ACT:
  - Send email: "Are 4 FTE in FY budget?"
  - Track response time
  - Expected SLA: 24h (by Mon 2 PM)

ASSERT:
  - Response status: RECEIVED / NOT_RECEIVED
  - Answer content: YES / NO / UNCLEAR

PASS (Green):
  - Answer = YES → SLA 1 day, proceed
  - Answer = NO + Exec Sponsor override by Mon 10 AM → SLA 1-2 days, proceed with risk

FAIL (Red):
  - Answer = NO + no override → SLA 7 days, DEFER to Feb 18
  - No response by Mon 2 PM → Escalate to Exec Sponsor immediately
```

**Email Template** (send TODAY):
```
Subject: Question: Are 4 FTE Samstraumr Phase 1 in FY budget?

Hi [Finance Director],

Quick question for timeline purposes: Are the 4 FTE headcount for Samstraumr Phase 1 already approved in the FY2025 budget?

If YES → Finance SLA for $164.1K spend request is ~1 day. Perfect.
If NO → New headcount approval cycle (~5-10 days). Need to know for planning.

Can you confirm by tomorrow EOD?

Thanks,
Eric
```

**Action TODAY**:
- [ ] Identify Finance Director name + email (5 min)
- [ ] Send email (2 min)
- [ ] Document sent time + note in task_plan.md (1 min)
- [ ] Expected response by Mon 2 PM (track daily)

**Next**: TEST 1 PASS → Proceed to TEST 2. TEST 1 FAIL → Escalate.

---

## TEST 2: Warm Candidate Pipeline (Second Criticality)

**TDD Structure**:

```
ARRANGE:
  - Contact list: Map 20-30 people in data science/research/statistics
  - From: Internal Guild + universities + prior team + LinkedIn

ACT:
  - Send 15-20 personalized outreach messages (Fri, 2h)
  - Track response rate daily (Sat-Sun, 1h)

ASSERT:
  - Response count by Fri 5 PM
  - Interest level: Confirmed / Maybe / No

PASS (Green):
  - 15+ positive responses → Proceed Feb 10
  - 8-14 responses + recruiter backup confirmed → Proceed with contingency

FAIL (Red):
  - <8 responses + no recruiter backup → DEFER to Feb 18
```

**Outreach Template** (send Fri):
```
Hi [Name],

I'm hiring for a unique 20-week engagement: data scientist/experimentalist for consciousness research (self-healing systems, clean architecture, recovery dynamics).

$3K/week, starts Feb 17, fully remote.

Interested in chatting? Or know someone who might be?

Looking to move fast (decisions by Feb 14), so timing matters.

Thanks,
Eric
```

**Action FRI-SUN**:
- [ ] List 20-30 contacts (Fri morning, 30 min)
- [ ] Send 15-20 personalized messages (Fri, 1.5h)
- [ ] Track responses Fri evening (10 min)
- [ ] Follow up Sat morning (30 min)
- [ ] Final count by Fri 5 PM (10 min)
- [ ] If <8: Call external recruiter TODAY to activate backup (30 min)

**Next**: TEST 2 PASS (15+) → Proceed Feb 10. TEST 2 PARTIAL (8-14) → Proceed with recruiter. TEST 2 FAIL (<8) → DEFER.

---

## TEST 3: Team Alignment (Medium Criticality)

**TDD Structure**:

```
ARRANGE:
  - Hiring team identified: [Names]
  - Meeting scheduled: [SAT or SUN, time]

ACT:
  - Run 30-min team alignment call
  - Lock 3 items: role definitions, scorecard, decision authority

ASSERT:
  - Role 1 (Collaborator): Defined 1 sentence? YES/NO
  - Role 2 (Contractor 1): Defined 1 sentence? YES/NO
  - Role 3 (Contractor 2): Defined 1 sentence? YES/NO
  - Role 4 (Contractor 3): Defined 1 sentence? YES/NO
  - Scorecard locked (3.5/5.0 with example)? YES/NO
  - Decision authority clear (Solo/Delegated)? YES/NO

PASS (Green):
  - All 3 items locked → Proceed Feb 10

PARTIAL (Yellow):
  - 2 of 3 items locked → Clarify, proceed

FAIL (Red):
  - <2 items locked + unable to align → DEFER to Feb 18
```

**Call Agenda** (30 min):
```
1. Role Definitions (5 min)
   - Collaborator: Experimentalist designing measurement systems for recovery validation
   - Contractor 1: Maven/Java tooling engineer building smart linters for Clean Architecture
   - Contractor 2: Technical writer drafting academic papers on software + consciousness
   - Contractor 3: Data scientist modeling recovery dynamics + statistical validation

2. Scorecard (10 min)
   - Threshold: 3.5/5.0 for hire
   - Worked example: [Describe candidate worth exactly 3.5]
   - Same rubric for all 4 roles

3. Decision Authority (10 min)
   - "Eric decides all 4" OR "Eric + [co-leader] split by role" OR "Committee votes"
   - Document explicitly

4. Interview Process (5 min)
   - Phone screen (15 min) → Technical (30 min) → Debrief (10 min) → Score
   - All 4 roles same process
```

**Action SAT/SUN**:
- [ ] Identify hiring team members (today, 15 min)
- [ ] Schedule call for Sat or Sun (today, 15 min)
- [ ] Document outcomes post-call (10 min)
- [ ] If misaligned: Schedule 3-4 hour planning sprint for Mon-Tue (escalate)

**Next**: TEST 3 PASS → Proceed. TEST 3 FAIL → DEFER.

---

## TEST 4: Decision Capacity (Blocking But Self-Directed)

**TDD Structure**:

```
ARRANGE:
  - Self-reflection time: SUN evening (30 min)

ACT:
  - Ask yourself: "Can I make 4 critical hiring decisions in 48h without fatigue regret?"
  - Reference: Decision fatigue research (18-25% regret rate after 3+ major choices)

ASSERT:
  - Answer: YES / MAYBE / NO

PASS (Green):
  - YES → Eric decides all 4
  - MAYBE → Delegate Contractors (Roles 2-4) to co-leader(s); keep Collaborator for Eric

FAIL (Red):
  - NO + no co-leader available → DEFER to Feb 18 (2-week stagger is better than fatigue)
```

**Reflection Questions**:
- Have you made 4+ major decisions in 2 days before? How did you feel?
- Do you typically get decision fatigue after 2-3 big choices?
- Can you realistically interview, deliberate, and offer within 48h?
- If no: Who can co-decide on Roles 2-4 (Contractors)?

**Action SUN**:
- [ ] 30-min reflection + honest self-assessment (SUN evening)
- [ ] Document answer: YES / MAYBE / NO (5 min)
- [ ] If MAYBE: Identify co-leaders by name (15 min)
- [ ] If NO: Find co-leader OR escalate to DEFER (30 min phone call)

**Next**: TEST 4 PASS → Proceed. TEST 4 FAIL → DEFER.

---

## GO/DEFER DECISION POINT (SUN 6 PM)

**Tally Results**:

| Test | Result | Status |
|------|--------|--------|
| 1. Finance | YES / NO / UNCLEAR | [After Mon 2 PM] |
| 2. Warm | 15+ / 8-14 / <8 | [Fri 5 PM] |
| 3. Team | Aligned / Partial / Major gaps | [Sat/Sun call] |
| 4. Capacity | YES / MAYBE / NO | [Sun evening] |

**INTERPRET**:

```
GREEN (All Pass):
  Finance: YES
  Warm: 15+
  Team: Aligned
  Capacity: YES
  → LAUNCH FEB 10 (85% confidence)

YELLOW (Mixed, No Critical Blockers):
  Finance: YES + Warm: 8-14 (recruiter backup)
  OR Capacity: MAYBE (delegate contractors)
  OR Team: Partial (clarify, proceed)
  → LAUNCH FEB 10 WITH CONTINGENCY (55-65% confidence)

RED (Critical Blockers):
  Finance: NO + no override
  OR Warm: <8
  OR Team: Major gaps
  OR Capacity: NO + no co-leader
  → DEFER TO FEB 18 (75-85% confidence — better prepared)
```

**Announce Decision SUN 6 PM**:

Email to Executive Sponsor + team:

```
Subject: Phase 1 Decision: [GO / DEFER]

Phase 1 validation results:

Finance pre-approval: [YES / NO / UNCLEAR]
Warm candidate pipeline: [15+ / 8-14 / <8]
Team alignment: [Aligned / Partial / Major gaps]
Hiring capacity: [YES / MAYBE / NO]

Decision: [LAUNCH FEB 10] OR [DEFER TO FEB 18]

Next: [Proceed Mon 8 AM] OR [Use Feb 10-17 to prep, restart Feb 18]

Ready when you are.
```

---

## Success Metrics

| Metric | Target | Evidence |
|--------|--------|----------|
| Tests 1-4 executed | 100% | Results logged in task_plan (this file) |
| GO/DEFER decision clarity | 100% | Decision announced by Sun 6 PM (email sent) |
| Team alignment on Phase 1 | 90%+ | Team alignment call completed + scorecard locked |
| Finance pre-approval clarity | 100% | Finance Director response received by Mon 2 PM |
| Warm pipeline validation | 100% | Contact count by Fri 5 PM (15+, 8-14, or <8) |

---

## Execution Cadence

```
TODAY (Fri):
  - Phase 1: Test setup + TEST 1 (Finance email)
  - Phase 3: TEST 2 start (outreach 15-20 contacts)

SAT:
  - Phase 3: TEST 2 follow-up (tracking)
  - Phase 4: TEST 3 call (if available)

SUN:
  - Phase 3: TEST 2 final count (by 5 PM)
  - Phase 4: TEST 3 call (if not Sat)
  - Phase 5: TEST 4 (self-assessment by evening)
  - Phase 6: GO/DEFER decision (announce by 6 PM)

MON:
  - Implement decision (launch or prep)
```

---

## Errors Encountered
[None yet]

---

## Status
**Phase 1 active** — Setting up 4 TDD tests. Ready to execute TEST 1 now.

**Next immediate action**: Send Finance pre-approval email within 1 hour.
