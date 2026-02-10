# Phase 1 Backup Playbooks: Pre-Staged Contingency Execution Plans

**Purpose**: Don't say "activate backup" without specific playbook. Pre-built, granular, executable steps for every failure scenario.

**Status**: READY (reference if contingencies trigger Feb 10-13)

---

## PLAYBOOK 1: Collaborator Backup Activation

**Trigger**: Primary Collaborator candidate rejects offer OR scores <3.5/5 OR unavailable.

**Timeline**: Activation by EOD Feb 13 (cannot delay).

### Stage 1: Immediate Response (0-15 minutes)

```
ACTION STEP 1: Document Rejection
├─ Time of rejection: ________
├─ Reason: ☐ Competing offer | ☐ Salary | ☐ Timing | ☐ Role misfit | ☐ Other
├─ Note: _______________________________________________________________
└─ DECISION: Did candidate provide feedback? ☐ YES → Use for backup pitch

ACTION STEP 2: Identify Backup Collaborator
├─ Check: Do we have backup candidate identified in PHASE_1_INTERVIEW_SCORECARD.md?
│   ├─ If YES: ☐ [Name of backup candidate]
│   └─ If NO: ☐ Call external recruiter for 1-2 emergency candidates
│
├─ Backup candidate qualifications:
│   ├─ Data science background (minimum: published 1-2 papers OR 5+ A/B tests)
│   ├─ Can start Feb 17 (absolute requirement)
│   ├─ Available for 20-week commitment (no conflicts after Feb 17)
│   └─ Comfort with ambiguity (null results are okay)
│
└─ Find: Phone number, email, LinkedIn (verify contact method)

ACTION STEP 3: Call Backup Candidate Within 15 Minutes
├─ Script (see PHASE_1_PRE_WRITTEN_COMMUNICATIONS.md, Template 10)
├─ Tone: Friendly, urgent but not panicked
├─ Opening: "Hi [Name], this might be out of the blue, but something came available
│   and I wanted to reach out directly before we move to the next candidate."
├─ Pitch: [Use PHASE_1_TALENT_POSITIONING_STRATEGY.md value props]
│   ├─ Novel research (consciousness-aware systems)
│   ├─ 3 first-author publications by June
│   ├─ 20-week defined sprint
│   └─ "We need someone to start Feb 17. Interested in talking?"
│
├─ Next step: If interested → Schedule phone screen Feb 14 AM (compressed timeline)
├─ SLA: 1-hour callback expected (if no response in 1h, call again or text)
└─ If unavailable: Move to Backup #2 immediately (don't wait)

═══════════════════════════════════════════════════════════════════════════════
```

### Stage 2: Compressed Hiring (Feb 14, if needed)

```
IF BACKUP INTERESTED: Fast-Track Hiring

ACTION STEP 4: Phone Screen (Feb 14, morning)
├─ Duration: 30 minutes (same as primary interview)
├─ Format: Candidate on speaker; Eric takes notes
├─ Use: PHASE_1_INTERVIEW_SCORECARD.md (Q1-4 only; abbreviated)
├─ Scoring: Must score ≥3.3/5.0 to proceed (lower bar than 3.5 because of time pressure)
├─ Decision: ☐ HIRE | ☐ PASS | ☐ GET REFERENCES (2h max for decision)
└─ Timeline: Complete by 1 PM Feb 14

ACTION STEP 5: Offer (Feb 14, afternoon)
├─ If scorecard ≥3.3: Make offer by 2 PM Feb 14
├─ Terms: Same as original ($3,000/week × 20 weeks = $60,000)
│   OR Premium: $3,500/week if backup is higher quality AND score ≥3.8
├─ Payment: Accelerated (offer electronically same day)
├─ Confirmation: Must sign by EOD Feb 14
└─ Start: Feb 17 (confirmed in writing)

ACTION STEP 6: Contract & Onboarding (Feb 15)
├─ [ ] Send contract (PHASE_1_CONTRACT_TEMPLATE.md)
├─ [ ] Send W-9 form
├─ [ ] Confirm availability Feb 17, 9 AM kickoff
├─ [ ] Send onboarding prep materials (by 5 PM Feb 15)
│   ├─ QUICK_REFERENCE_CARD.md
│   ├─ Samstraumr overview (3-page doc)
│   └─ 3 experiment design examples (for context)
└─ [ ] Confirm Slack invite + calendar setup

═══════════════════════════════════════════════════════════════════════════════
```

### Stage 3: If Backup Also Unavailable

```
DECISION POINT: Backup #2 Call (Feb 14, 5 PM)

IF Backup #1 says NO:
├─ Immediately call Backup #2 (don't wait)
├─ Timeline: 2nd backup must be contacted by 5 PM Feb 14 latest
│
└─ If Backup #2 also unavailable by Feb 14, 9 PM:
   ├─ EXECUTIVE DECISION NEEDED (call Executive Sponsor immediately)
   ├─ OPTIONS:
   │  ├─ Option A: DEFER KICKOFF TO FEB 24 (1-week delay)
   │  │  └─ Announce by 8 AM Feb 15
   │  │
   │  ├─ Option B: DEFER TO MAR 3 (2-week delay)
   │  │  └─ Announce by 8 AM Feb 15; impacts OOPSLA deadline
   │  │
   │  └─ Option C: CANCEL PHASE 1 (reassess in Q3)
   │     └─ Announce by 8 AM Feb 15
   │
   └─ DECISION BY FEB 14, 9 PM (cannot delay; impacts all downstream)

═══════════════════════════════════════════════════════════════════════════════

COMMUNICATION TEMPLATE (If Deferring Kickoff):

Subject: Samstraumr Phase 1 Timeline Adjustment - Collaborator Hiring Extended

Hi Team,

Due to collaborator hiring complexity, we're adjusting the Phase 1 start date.

NEW TIMELINE:
• Kickoff: [Feb 24 or Mar 3, 9 AM PT]
• All-hands: [date], Zoom [link]
• Impact: Phase 1 gates shift [1 or 2] week(s) later

RATIONALE:
Better to hire the right person late than the wrong person on time. Collaborator
quality is critical for experimental design (Gate 1). We're ensuring we get a strong fit.

NEXT STEPS:
1. Update your calendars to [new kickoff date]
2. Contractors: Your start dates shift to [new dates]
3. Questions: Let me know ASAP

Thanks for your flexibility.

Eric

═══════════════════════════════════════════════════════════════════════════════
```

---

## PLAYBOOK 2: Contractor 1 (Maven Engineer) Backup Activation

**Trigger**: Primary Maven engineer declines offer OR code review shows <3.5/5 quality.

**Timeline**: Activation within 24 hours of rejection.

### Backup Maven Engineer Hiring (Expedited)

```
ACTION STEP 1: Identify Backup Maven Engineer
├─ Standup candidates from initial 3-person pool:
│  ├─ If Candidate #2 was close: Interview them immediately (same day)
│  ├─ If no secondary from pool: Contact external (GitHub Maven PMC, StackOverflow)
│  └─ Timeline: Must have backup identified within 1 hour of primary rejection

ACTION STEP 2: Portfolio Review (1-2 hours)
├─ Request past Maven plugin code samples (email + phone call)
├─ Review for: Architecture quality (3.5+/5), test coverage (80%+)
├─ Decision: ≥3.5 quality → Proceed to phone screen
│          <3.5 quality → PASS; move to Backup #2

ACTION STEP 3: Phone Screen (same day, if possible)
├─ Duration: 30 minutes
├─ Format: Technical deep dive (PHASE_1_DECISION_TREES.md, Decision Tree 3)
├─ Scoring: ≥3.8/5.0 recommended (higher bar for backup; less time to evaluate)
└─ Decision: ☐ HIRE | ☐ PASS

ACTION STEP 4: Offer (same day, if qualified)
├─ Terms: $50/hr fixed-price $4,800 (same as primary)
├─ Counter-offer: If candidate asks ≥$60/hr, PASS (activate Backup #2)
│             If ≤$57/hr acceptable, OFFER at $5.2K (slight premium)
├─ Timeline: Contract signed by EOD next day (Feb 13 or 14)
└─ Start: Week 5 (unchanged; Feb 24)

ACTION STEP 5: If Backup Also Declines
├─ MANUAL IMPLEMENTATION PATH:
│  ├─ Eric implements Maven plugin manually (20-25h cost)
│  ├─ Timeline: Weeks 5-8 (instead of 5-7 with contractor)
│  ├─ Quality: Adequate (not expert-level)
│  ├─ Mitigation: Open-source after project → community feedback improves it
│  └─ Scope: Reduce if timeline critical; defer to Phase 2
│
└─ DECISION: Implement manual OR defer feature
   ├─ If implement: Announce 1-week timeline slip for smart linter deliverable
   ├─ If defer: Paper 29 (SREcon) scope reduced (publish 4 papers instead of 5)
   └─ DECISION NEEDED BY: EOD day of rejection

═══════════════════════════════════════════════════════════════════════════════
```

---

## PLAYBOOK 3: Contractor 2 (Technical Writer) Backup Activation

**Trigger**: Primary writer declines offer OR portfolio review reveals quality <3.5/5.

**Timeline**: Activation within 24 hours; writer pool is largest so backup likely available.

### Backup Writer Hiring (Abundant Pool)

```
ACTION STEP 1: Identify 2-3 Backup Writers
├─ From initial 5-person pool: ≥2 candidates remain (larger pool than other roles)
├─ Secondary candidates for phone screen (already rated during portfolio review)
├─ Contact by phone (faster than email)

ACTION STEP 2: Phone Screen Backup(s)
├─ Duration: 30 minutes (same as primary)
├─ Format: PHASE_1_DECISION_TREES.md, Decision Tree 4
├─ Score: ≥3.3/5.0 minimum (slightly lower bar for backup)
├─ Question: Gauge revision tolerance (critical for papers)
└─ Decision: ☐ HIRE | ☐ PASS

ACTION STEP 3: Offer to Best Backup
├─ Terms: $75/hr T&M (same as primary)
├─ Counter-offer: ☐ If $80-85/hr: NEGOTIATE to $80/hr (papers critical)
│              ☐ If >$85/hr: OFFER to next backup instead
├─ Timeline: Offer + contract signature by EOD next day
└─ Start: Week 8 (Mar 17, unchanged)

ACTION STEP 4: Parallel Backup Offers
├─ If primary writer might delay: Offer to 2 backups simultaneously
│  (rare but justified when papers are existential)
├─ Note: "We have slots for 1-2 writers. First to sign gets priority."
│  (not unethical; just clear about capacity)
├─ Rationale: Paper delivery is critical; better to have 2 writers than 0
└─ Timeline: Compress entire hiring to same day if needed

ACTION STEP 5: If All Writers Decline
├─ CONTINGENCY PLAN:
│  ├─ Eric + Collaborator write papers themselves (40-60h each)
│  ├─ Quality: Adequate to good (both have research background)
│  ├─ Timeline: Weeks 12-20 (instead of 8-20 with writer)
│  ├─ Impact: Gate 3 (Week 16 draft review) may slip 2-3 weeks
│  └─ Mitigation: Start writing earlier (Week 8) with reduced external support
│
└─ DECISION: DIY writing OR delay papers
   ├─ If DIY: Announce 1-2 week timeline shift for paper drafts
   ├─ If delay: OOPSLA 2026 deadline at severe risk; defer to ESEM 2026 (Aug 1)
   └─ DECISION NEEDED BY: EOD day of rejection

═══════════════════════════════════════════════════════════════════════════════
```

---

## PLAYBOOK 4: Contractor 3 (Data Scientist) Backup Activation

**Trigger**: Primary statistician declines offer OR references downgrade quality.

**Timeline**: Activation within 24 hours; largest talent pool so fast replacement expected.

### Backup Data Scientist Hiring

```
ACTION STEP 1: Identify Backup Statistician
├─ From initial 4-5 pool: ≥1 backup available (large pool)
├─ Check: Did backup pass credential check? ☐ YES → call immediately
│                                            ☐ NO → ID new candidate from broader search

ACTION STEP 2: Quick Phone Screen (15-20 min)
├─ Format: Abbreviated (Q1-2 only from PHASE_1_INTERVIEW_SCORECARD.md)
├─ Key question: "Have you worked on resilience systems or chaos engineering?"
│  ├─ YES → Good fit; proceed
│  └─ NO → "Willing to learn?" If YES, still proceed (low-risk hire)
└─ Decision: ≥3.0 score → HIRE

ACTION STEP 3: Offer (same day)
├─ Terms: $43.75/hr fixed-price $2,100 (same as primary)
├─ Counter-offer: ☐ If $48/hr+ asked: PASS (activate next backup)
│              ☐ If ≤$45/hr: ACCEPT
├─ Timeline: Offer + signature by EOD next day
└─ Start: Week 3 (Feb 24, unchanged)

ACTION STEP 4: If Backup Also Declines
├─ CONTINGENCY PLAN:
│  ├─ Eric + Collaborator conduct statistical analysis themselves
│  ├─ Quality: Adequate (Collaborator has data science background)
│  ├─ Timeline: No change (analysis is in-scope anyway)
│  ├─ Impact: Minimal (data scientist role was supporting, not critical path)
│  └─ Decision: Defer this hire; low-risk
│
└─ DECISION: DIY OR Find alternative
   ├─ If DIY: No announcement needed (minimal impact)
   ├─ If find alternative: Contact 3rd backup candidate within 24h
   └─ DECISION NEEDED BY: EOD day of rejection

═══════════════════════════════════════════════════════════════════════════════
```

---

## PLAYBOOK 5: Finance Approval Delayed (Budget Escalation)

**Trigger**: Finance approval not received by EOD Feb 10.

**Timeline**: Escalation required by 10 AM Feb 11 (cannot delay hiring past 1 day).

### Finance Escalation Protocol

```
ACTION STEP 1: Send Reminder Email (Feb 10, 5 PM)
├─ To: CFO/Finance Director
├─ Subject: "URGENT: Samstraumr Phase 1 Budget Approval ($164.1K) - Tomorrow Morning"
├─ Message:
│  "Budget requires approval by 10 AM tomorrow (Feb 11) to meet Feb 17 kickoff.
│   Recruitment begins Feb 10. If delayed past Feb 11, we lose hiring window
│   and kickoff moves to Feb 24 (cascades all gates).
│   Can you approve by tomorrow morning?"
│
└─ Tone: Urgent but professional (not panicked)

ACTION STEP 2: If No Response by 8 AM Feb 11
├─ ACTION: Call CFO directly
├─ Script: "Hi [Name], following up on Samstraumr budget email. Can you approve $164.1K
│           by 10 AM? Feb 17 kickoff depends on it."
├─ Ask: "What's the blocker? What do you need from me?"
└─ SLA: 2-hour decision needed

ACTION STEP 3: If Approved by 10 AM Feb 11
├─ Proceed with normal recruitment plan
├─ No timeline impact (recruiting starts as planned)
└─ SUCCESS

ACTION STEP 4: If Delayed Past 10 AM Feb 11
├─ EXECUTIVE ESCALATION:
│  ├─ Who: CEO, VP Product, Board (whoever has budget override authority)
│  ├─ Message: "Finance approvals blocking Phase 1. Options:
│  │           A) Override approval (emergency authorization)
│  │           B) Defer Phase 1 to Mar 3 (1-week delay recoverable)
│  │           C) Scope down: Hire Collaborator only, defer contractors
│  │           Which?"
│  ├─ Timeline: Decision needed by 5 PM Feb 11
│  └─ SLA: Same-day decision required
│
└─ Possible outcomes:
   ├─ A) Approval granted → Proceed normal (hiring starts Feb 11 evening)
   ├─ B) Defer to Mar 3 → Announce by 8 AM Feb 12; hiring timeline shifts
   └─ C) Scope down → Hire Collaborator; contractors deferred

ACTION STEP 5: If Approval Not Obtained by 5 PM Feb 11
├─ DECISION: Cannot proceed with any hiring (legal/financial requirement)
├─ ACTION: Announce defer to Executive Sponsor + team by 6 PM Feb 11
├─ Message:
│  "Budget approval is delayed. Phase 1 kickoff moves to Mar 3, 2026.
│   All gates shift 2 weeks later. We'll resume recruitment Feb 17-20
│   for Mar 3 start."
│
└─ Timeline impact: Significant (2-week slip, impacts OOPSLA deadline)

═══════════════════════════════════════════════════════════════════════════════

COMMUNICATION TO TEAM (If Finance Delayed):

Subject: Phase 1 Timeline Update: Budget Approval Status

Hi Team,

Finance approval for Phase 1 budget is in progress. Potential timeline impacts:

IF APPROVED BY 10 AM FEB 11: Phase 1 proceeds Feb 17 (no change)
IF DELAYED TO FEB 11 AFTERNOON: Recruiting compressed; Feb 17 kickoff still possible
IF DELAYED PAST FEB 11: Kickoff deferred to Mar 3 (all gates shift 2 weeks)

I'll update you by 5 PM Feb 11 with final timeline.

Eric

═══════════════════════════════════════════════════════════════════════════════
```

---

## PLAYBOOK 6: Interview Volume Contingency (<6 Applications by EOD Feb 12)

**Trigger**: Fewer than 6 qualified applications received by EOD Feb 12.

**Timeline**: Activation by 3 PM Feb 12 (allows emergency outreach before EOD).

### Volume Contingency Protocol

```
ACTION STEP 1: Assess Volume (by 2 PM Feb 12)
├─ Count qualified applications received to date: _____ (target: ≥6)
├─ IF ≥6: ✓ No action needed; proceed normal hiring
└─ IF <6: Activate contingency protocol (next steps)

ACTION STEP 2: Personal Outreach to Secondary Candidates (2-4 PM Feb 12)
├─ Who: 3-5 secondary targets not yet contacted (from extended network)
├─ Method: Phone calls (faster than email)
├─ Script:
│  "Hi [Name], Eric from Samstraumr. I'm reaching out about a 20-week research
│   opportunity on consciousness-aware systems. Interested in a quick conversation?"
│
├─ If interested: Schedule phone screen for Feb 13 morning (if possible)
├─ If not available: Ask for referral: "Know anyone else with empirical research background?"
└─ Goal: Get 2-3 additional candidates into pipeline by EOD Feb 12

ACTION STEP 3: Activate External Recruiter (if not already engaged)
├─ Call recruiter immediately (2:30 PM Feb 12)
├─ Brief: "We need 3-4 qualified candidates by EOD tomorrow. Budget: [hourly rate].
│          Priority: Collaborators (experimentalists) + backup tiers for contractors."
├─ Task: Overnight sourcing (recruiter works evening/night)
└─ Payment: Agree on hourly rate (typically $100-150/hr for emergency sourcing)

ACTION STEP 4: Compress Phone Screen Timeline
├─ IF we get to 6+ applications by EOD Feb 12: Proceed normal (Feb 12-13)
├─ IF still <6 by 5 PM Feb 12: Extend phone screens to Feb 13 morning
│  ├─ Schedule: 9-10 AM, 10-11 AM, 11-12 PM (compressed morning)
│  ├─ Speed: Accept slightly lower-quality screens (single-day turnaround)
│  └─ Decision: Same day (Feb 13, 12 PM) instead of Feb 12 EOD
│
└─ Trade-off: Less time to evaluate, but maintains Feb 13 offer deadline

ACTION STEP 5: Hiring Threshold Adjustment (if needed)
├─ Normal: Hire only candidates scoring ≥3.5/5.0
├─ IF <6 applications: Lower bar to ≥3.3/5.0 (accept marginal fit)
├─ IF <4 applications: DEFER KICKOFF (cannot hire below quality threshold)
│
└─ Decision logic:
   ├─ 6+ apps & ≥1 scores 3.5+ = Proceed normal
   ├─ 4-5 apps & ≥1 scores 3.3+ = Proceed compressed (accept slightly lower quality)
   └─ <4 apps OR no scores 3.3+ = DEFER KICKOFF (quality floor is immovable)

ACTION STEP 6: If Still <4 Qualified Applications by EOD Feb 13
├─ DECISION: Cannot proceed with Feb 17 kickoff
├─ ACTION: Announce defer to Feb 24 or Mar 3 (Executive Sponsor decision)
├─ Message:
│  "Application volume was lower than expected. Rather than hire below
│   our quality threshold, we're deferring hiring to Feb 17-20. New kickoff: Mar 3."
│
└─ Timeline: Announce by 6 PM Feb 13; gives 1-week buffer before new start date

═══════════════════════════════════════════════════════════════════════════════
```

---

## PLAYBOOK 7: Cascading Failure (2+ Candidates Reject Simultaneously)

**Trigger**: Both Collaborator AND Contractor 1 reject by EOD Feb 13.

**Timeline**: Emergency decision required by 5 PM Feb 13 (same day as failures).

### Cascading Failure Protocol

```
SITUATION:
├─ Primary Collaborator rejected
├─ Backup Collaborator also rejected
├─ AND/OR Primary Contractor 1 rejected
├─ AND/OR Primary Contractor 2 rejected

DECISION TREE: What to Do?

OPTION A: DEFER KICKOFF (Recommended)
├─ Delay to Feb 24 (1 week) or Mar 3 (2 weeks)
├─ Resume recruiting with expanded timelines
├─ RATIONALE: Better to hire right people late than wrong people on time
├─ IMPACT: 1-2 week slip on all gates; OOPSLA deadline at risk if Mar 3
└─ DECISION: Announce by 6 PM Feb 13

OPTION B: SCOPE DOWN (Alternative)
├─ Hire Collaborator ONLY; defer all contractors to Phase 2
├─ Keep Feb 17 kickoff
├─ RATIONALE: Collaborator is critical path; contractors are secondary
├─ IMPACT: Phase 1 loses tooling + writing contractors; Collaborator does more manual work
│          (Eric + Collaborator write papers; defer smart linter to Phase 2)
└─ DECISION: Only if 1 contractor unavailable; not if Collaborator unavailable

OPTION C: CANCEL PHASE 1 (Last Resort)
├─ Halt project; reassess in Q3 2026
├─ RATIONALE: Cascading failures suggest market/timing issues
├─ IMPACT: All gates cancelled; publication timeline abandoned for 2026
└─ DECISION: Only if >2 critical hires fail (rare)

RECOMMENDED: OPTION A (DEFER)

═══════════════════════════════════════════════════════════════════════════════

ANNOUNCEMENT TO TEAM (If Deferring):

Subject: SAMSTRAUMR PHASE 1 TIMELINE ADJUSTMENT

Hi Team,

Due to unexpected contractor availability challenges, we're adjusting Phase 1 timeline.

NEW START DATE: [Feb 24 or Mar 3, 2026] | KICKOFF: 9 AM PT
RATIONALE: Better to hire right talent late than compromise on quality
IMPACT: All gates shift [1 or 2] weeks later

NEXT STEPS:
1. Update calendars now
2. Contractors: Your start dates shift accordingly
3. Q&A: Happy to discuss impact

Thanks for your flexibility. Quality > Speed on this project.

Eric

═══════════════════════════════════════════════════════════════════════════════

EXECUTIVE COMMUNICATION (If Option A):

Hi [Executive Sponsor],

Due to higher-than-expected contractor selection rigor, we're deferring Phase 1 to [Feb 24
or Mar 3]. This allows us to hire A-tier talent vs. settling for B-tier on deadline pressure.

IMPACT:
• Gates shift [1-2] weeks later
• OOPSLA deadline at risk if Mar 3 start (likely defer to ESEM 2026)
• Quality of research team improves with extended timeline

DECISION NEEDED: Which deferred start date?
☐ Feb 24 (recoverable; OOPSLA still possible)
☐ Mar 3 (2-week slip; ESEM 2026 more likely)

Recommend Feb 24. Let me know.

Eric

═══════════════════════════════════════════════════════════════════════════════
```

---

## QUICK REFERENCE: DECISION MATRIX

```
FAILURE SCENARIO → ACTIVATION PLAYBOOK | TIMELINE | DECISION CRITERIA

1. Collaborator rejects           → Playbook 1           | 0-15 min    | Backup available?
2. Contractor 1 unavailable       → Playbook 2           | 0-24h       | Manual implementation OK?
3. Contractor 2 poor quality      → Playbook 3           | 0-24h       | DIY writing acceptable?
4. Contractor 3 unavailable       → Playbook 4           | 0-24h       | Low-risk (defer hire)
5. Finance delayed                → Playbook 5           | 0-24h       | Budget override possible?
6. Applications <6                → Playbook 6           | 2-4 PM      | Extend timeline?
7. Multiple rejects (2+)          → Playbook 7           | EOD         | Defer or scope down?

ESCALATION PATH:
├─ Single hire failure → Activate specific playbook (don't escalate yet)
├─ 2+ failures → Notify Executive Sponsor (decision needed on deferral)
└─ Finance blocked → Escalate to CFO + Executive Sponsor immediately

═══════════════════════════════════════════════════════════════════════════════
```

---

**READY TO DEPLOY**: Print all playbooks. Keep at desk Feb 10-13. Reference immediately if trigger activated. Each playbook includes specific timelines, scripts, and decision criteria.

