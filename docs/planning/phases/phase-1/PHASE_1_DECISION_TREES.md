# Phase 1 Decision Trees: Pre-Built If-Then Logic for Every Failure Scenario

**Purpose**: Eliminate analysis paralysis during Feb 10-13 execution. Pre-made decisions for every contingency.

**Status**: READY (reference during daily standup Feb 10-13)

---

## MASTER DECISION TREE: Phase 1 Staffing Execution

```
START: Monday, Feb 10, 8 AM

║
╠═══ DAILY DECISION CHECKPOINT (each morning, 8:30 AM)
║    │
║    ├─ FINANCE APPROVAL ACHIEVED?
║    │  ├─ YES → Proceed with day's recruiting plan
║    │  └─ NO → Execute DECISION TREE 1 (Finance Escalation)
║    │
║    ├─ INTERVIEW FUNNEL HEALTHY (on pace for ≥6 by EOD tomorrow)?
║    │  ├─ YES → Continue current recruiting velocity
║    │  └─ NO → Execute DECISION TREE 5 (Volume Contingency)
║    │
║    └─ ANY CANDIDATES WITHDRAWN?
║       ├─ YES → Replace immediately from backup tier
║       └─ NO → Continue normal tracking
║
╠═══ EOD DECISION CHECKPOINT (each day, 5 PM)
║    │
║    ├─ APPLICANTS RECEIVED TODAY?
║    │  ├─ YES (1+) → Log in tracker; schedule phone screen if qualified
║    │  ├─ NO → Send evening follow-up to targets (whichever outstanding)
║    │  └─ CRITICAL: If 0 apps by EOD Feb 11 → Execute DECISION TREE 5
║    │
║    ├─ PHONE SCREENS COMPLETED?
║    │  ├─ YES → Score immediately; log in PHASE_1_INTERVIEW_SCORECARD.md
║    │  └─ NO → Reschedule for next available slot
║    │
║    └─ DECISIONS MADE? (Hire/Pass/Hold for all screened candidates)
║       ├─ YES → Log decision; begin offer preparation if hiring
║       ├─ MAYBE → Collect additional references; decide by next morning
║       └─ NO → Don't procrastinate; decide within 2 hours of screen
║
└─ CONTINUE UNTIL: Feb 13, 5 PM (all staff decisions finalized)

```

---

## DECISION TREE 1: Finance Approval Delayed

**Trigger**: Finance hasn't approved $164.1K budget by EOD Feb 10

```
IF Finance not approved by EOD Feb 10, 5 PM:
│
├─ IMMEDIATE ACTION (Feb 10, 5:30 PM): Send reminder email to Finance + CFO
│  └─ Subject: "URGENT: Samstraumr Phase 1 Budget Approval ($164.1K) - 48h Deadline"
│  └─ Message: "Recruitment begins Feb 10. Budget approval required by 10 AM Feb 11. Please confirm."
│  └─ Escalation: If no response by Feb 11, 10 AM → Call CFO directly
│
├─ SCENARIO A: Approved by Feb 11, 10 AM
│  └─ Action: Proceed immediately with full recruitment plan
│  └─ Impact: 0 hours lost; no cascading delay
│
├─ SCENARIO B: Approved by Feb 11, 5 PM (end of day)
│  └─ Action: Begin recruitment Feb 11 evening (email outreach to candidates)
│  └─ Impact: 1-day delay; phone screens shift to Feb 13 (compressed timeline)
│  └─ Adjustment: Accept lower-quality phone screens (single-day turnaround) or defer to Feb 24 start
│
├─ SCENARIO C: Approved by Feb 12 (next day)
│  └─ Action: Emergency recruitment Feb 12 (personal calls, not email)
│  └─ Impact: 2-day delay; extremely compressed timeline
│  └─ Adjustment: MUST defer kickoff to Feb 24 (Feb 17 deadline now unachievable)
│
└─ SCENARIO D: NOT APPROVED by Feb 12, 5 PM (critical failure)
   ├─ Action: ESCALATE TO EXECUTIVE SPONSOR immediately
   │  └─ Decision authority: Who has budget override?
   │  └─ Message: "Budget approval blocked. Phase 1 cannot proceed on Feb 17."
   │  └─ Options:
   │     ├─ Option 1: Fund from discretionary budget (executive action)
   │     ├─ Option 2: Defer Phase 1 to Mar 3 (1-week delay recoverable)
   │     └─ Option 3: Reduce budget scope (e.g., hire Collaborator only, defer contractors)
   │
   └─ DECISION BY FEB 12, 5 PM (cannot delay further)
      ├─ If approved: Resume recruitment immediately (Feb 12 evening)
      ├─ If deferred: Announce Feb 24 kickoff by Feb 13, 9 AM
      └─ If scoped down: Hire Collaborator only; contractors defer to Phase 2

ESCALATION SCRIPT (if calling CFO):
"Hi [Name], Samstraumr Phase 1 needs $164.1K budget approval by 10 AM tomorrow.
Without approval by then, we miss Feb 17 start date. What's the blocker?
Can we get approval today or do we need to defer the project?"
```

---

## DECISION TREE 2: Collaborator Hiring (Main Path)

**Trigger**: Collaborator recruitment and hiring decision (Feb 10-13)

```
MONDAY, FEB 10 (Outreach)
│
├─ CONDITION: Identified 6 potential Collaborator candidates?
│  ├─ YES → Send personalized recruitment emails by 5 PM
│  └─ NO → Work with recruiter to identify; extend deadline to 8 AM Feb 11
│
├─ EMAIL CONTENT CHECK (use PHASE_1_RECRUITMENT_EMAIL.md as template)
│  ├─ ✓ Personalized (reference their specific work, publication, background)?
│  ├─ ✓ Clear role description (Experimentalist, 20 weeks, $3K/week)?
│  ├─ ✓ Publication upside emphasized (first-author papers)?
│  ├─ ✓ Deadline clear (applications due Feb 11, 5 PM)?
│  └─ ✓ Contact method provided (reply to email)?
│
└─ TARGET: ≥4 applications by EOD Feb 11

TUESDAY, FEB 11 (Portfolio & Application Review)
│
├─ APPLICATIONS RECEIVED: How many by 5 PM?
│  ├─ ≥6 (strong): Proceed to screening all; select top 3 for phone screen
│  ├─ 4-5 (adequate): Proceed to screening all; select all for phone screen
│  ├─ 2-3 (weak): Screen all; consider activating backup tier candidates
│  └─ 0-1 (critical): ACTIVATE DECISION TREE 5 (Volume Contingency) immediately
│
├─ APPLICATION SCORING (use PHASE_1_INTERVIEW_SCORECARD.md, phone screen section Q1-4)
│  ├─ For each applicant: Read CV, cover letter, any provided materials
│  ├─ Preliminary assessment (before phone):
│  │  ├─ Background & motivation (published research? A/B testing?)
│  │  ├─ Statistical fluency (Python/R? Recent analysis work?)
│  │  ├─ Openness to ambiguity (publications, approach to null results?)
│  │  └─ Availability (can start Feb 17? Any conflicts?)
│  │
│  └─ Decision: Who gets phone screen?
│     ├─ Top 3 candidates: ALL get phone screens (even if 5 applications)
│     └─ If <3 strong applicants: Activate backup tier immediately
│
└─ TARGET: Phone screens scheduled by EOD Feb 11

WEDNESDAY, FEB 12 (Phone Screens)
│
├─ SCHEDULE: 3 x 30-min phone screens (9-9:30, 10-10:30, 11-11:30 AM)
│  └─ Format: Candidate on speaker; Eric takes notes during call
│  └─ Interviewer: Eric (primary); consider Collaborator input if available
│
├─ PHONE SCREEN PROTOCOL (use PHASE_1_INTERVIEW_SCORECARD.md)
│  ├─ Q1: Background & Motivation (3 min answer)
│  │   └─ Question: "Tell me about your experience designing and executing experiments.
│  │       What's an example where your analysis changed a product decision?"
│  │   └─ Score: 1-5 (1=vague, 5=published empirical research)
│  │
│  ├─ Q2: Python/R Fluency (3 min answer)
│  │   └─ Question: "What's your go-to statistical language? Walk me through a recent analysis."
│  │   └─ Score: 1-5 (1=theoretical, 5=published code libraries)
│  │
│  ├─ Q3: Comfort with Ambiguity (3 min answer)
│  │   └─ Question: "We're validating a claim that a software system can 'self-heal' from failures.
│  │       Our experiment might show it doesn't. How would you approach that result?"
│  │   └─ Score: 1-5 (1=defensive, 5=treats failure as research opportunity)
│  │
│  ├─ Q4: Availability (2 min answer)
│  │   └─ Question: "This is a 20-week commitment starting Feb 17. Do you have conflicts?
│  │       What's your typical weekly availability?"
│  │   └─ Score: 1-5 (1=conflicts, 5="I can drop everything")
│  │
│  └─ CLOSE: "We're deciding Feb 13. I'll follow up with next steps."
│     └─ DO NOT make offer in same call
│     └─ DO wait 24h to decide; let subconscious process
│
├─ SCORING LOGIC (use PHASE_1_INTERVIEW_SCORECARD.md, line 152-162)
│  └─ Sum 4 scores, weight: (Q1 20%) + (Q2 20%) + (Q3 20%) + (Q4 15%) = total/5.0
│
└─ DECISION THRESHOLD BY EOD FEB 12
   ├─ Score 4.0-5.0: STRONG OFFER tier → Make offer Feb 13, 9 AM
   ├─ Score 3.5-3.9: OFFER tier → Make offer Feb 13, 10 AM
   ├─ Score 3.0-3.4: MAYBE tier → Get reference check; decide by EOD Feb 13
   └─ Score <3.0: PASS → Move to backup candidate immediately

THURSDAY, FEB 13 (Offers & Decisions)
│
├─ TIER 1 CANDIDATE (Score 4.0+):
│  ├─ ACTION: Call by 9 AM Feb 13 with offer
│  │  ├─ Opening: "We'd like to offer you the Collaborator role on Samstraumr."
│  │  ├─ Offer terms: "$3,000/week × 20 weeks = $60,000. Start Feb 17."
│  │  ├─ If counter-offer (request $3.5K/week):
│  │  │   └─ Decision: ACCEPT ($3.5K = $70K total) if candidate score 4.0+
│  │  │       (quality worth premium; secure talent)
│  │  │   └─ Decision: PASS if candidate score 3.8-3.9
│  │  │       (activate backup instead; not worth $10K premium)
│  │  ├─ Response SLA: 1-hour callback expected
│  │  └─ If accepted: Send offer letter immediately (email + hard copy)
│  │  └─ If rejected: Activate backup candidate within 15 minutes (see below)
│  │
│  └─ TARGET: Signed offer by EOD Feb 13
│
├─ TIER 2 CANDIDATE (Score 3.5-3.9):
│  ├─ ACTION: Call by 10 AM Feb 13 with offer
│  │  └─ Same terms as Tier 1
│  │  └─ Counter-offer handling: Stay firm at $3K/week (don't overpay for marginal candidate)
│  │  └─ If rejected: Activate backup candidate immediately
│  │
│  └─ TARGET: Signed offer by EOD Feb 13
│
├─ TIER 3 CANDIDATE (Score 3.0-3.4):
│  ├─ ACTION: Get reference checks before offer (take 2-3 hours)
│  │  ├─ Contact 2 references: "How does [candidate] handle ambiguous research? Revisions?"
│  │  ├─ Feedback: Does it upgrade score to 3.5+?
│  │  │   ├─ YES → Make offer (same as Tier 1)
│  │  │   └─ NO → Activate backup candidate immediately
│  │
│  └─ TARGET: Decision made by 3 PM Feb 13
│
├─ BACKUP CANDIDATE ACTIVATION (if Tier 1-3 all rejected or unavailable):
│  ├─ ACTION TIMING: Call within 15 minutes of rejection (speed matters)
│  ├─ OFFER TERMS:
│  │   ├─ Option A: Same as declined candidate ($3K/week)
│  │   ├─ Option B: Premium ($3.5K/week) if backup is higher quality
│  │   └─ Decision: If backup was Tier 1 candidate initially → Option B (premium justified)
│  ├─ RESPONSE SLA: 1-hour callback expected
│  ├─ If accepted: Same offer letter process
│  └─ If rejected: Move to backup #2 immediately
│
├─ ESCALATION IF ALL CANDIDATES REJECT:
│  ├─ TIMELINE DECISION (by 5 PM Feb 13): Which deferral path?
│  │   ├─ Path A: Defer start date to Feb 24 (1-week delay; kickoff movable)
│  │   ├─ Path B: Defer start date to Mar 3 (2-week delay; cascades gates)
│  │   └─ Path C: Cancel Phase 1 (reassess in Q3; not recommended)
│  │
│  ├─ COMMUNICATE: Announce decision to team by 5:30 PM Feb 13
│  │  └─ Message: "Collaborator hire timing adjusted. New start date: [Feb 24 or Mar 3]."
│  │  └─ Impact: "All Phase 1 gates shift by [1 or 2] week(s). Publication timeline adjusted."
│  │
│  └─ ACTIVATE BACKUP RECRUITING: Resume search for Week 2 onward if any availability
│
└─ SUCCESS CRITERIA: Signed offer from Tier 1, 2, or backup candidate by EOD Feb 13, 5 PM

DECISION RULE SUMMARY:
├─ Score 4.0+: Make offer, accept counter if ≤$3.5K/week
├─ Score 3.5-3.9: Make offer, stay firm at $3K/week
├─ Score 3.0-3.4: Get references; offer only if references upgrade score
├─ Score <3.0: PASS immediately; activate backup
├─ All candidates reject: Defer start date by 1-2 weeks; announce by 5:30 PM Feb 13
└─ Timeline: Each decision made within 2 hours of trigger event (no procrastination)
```

---

## DECISION TREE 3: Contractor 1 (Maven Engineer) Hiring

**Trigger**: Contractor 1 identification, screening, offer (Feb 10-13)

```
MONDAY, FEB 10 (Candidate Identification)
│
├─ IDENTIFY 3-4 Maven Plugin Engineers
│  ├─ Source 1: Maven PMC (Project Management Committee) members (top 3)
│  ├─ Source 2: GitHub searches ("Maven plugin" + high stars + recent commits)
│  ├─ Source 3: Personal network (previous contractors, colleagues)
│  └─ Action: Send portfolio requests by 5 PM Feb 10
│     └─ "Please share 2-3 past Maven plugin projects for code review."
│
└─ TARGET: 3 candidates identified; portfolio samples requested by EOD Feb 10

TUESDAY, FEB 11 (Portfolio Review)
│
├─ EVALUATION CRITERIA (for each code sample):
│  ├─ Architecture quality:
│  │   └─ Is it Clean Architecture? Clean separation of concerns?
│  │   └─ Score: 1-5 (1=monolithic, 5=exemplary architecture)
│  │
│  ├─ Test coverage:
│  │   └─ What percentage of code has tests? (target ≥80%)
│  │   └─ Score: 1-5 (1=no tests, 5=80%+ with integration tests)
│  │
│  ├─ Code quality (readability, naming, patterns):
│  │   └─ Can a new developer understand intent within 10 minutes?
│  │   └─ Score: 1-5 (1=cryptic, 5=self-documenting)
│  │
│  ├─ Error handling:
│  │   └─ How are edge cases + exceptions handled?
│  │   └─ Score: 1-5 (1=none, 5=comprehensive with recovery)
│  │
│  └─ OVERALL CODE ASSESSMENT:
│     └─ Average of 4 criteria
│     └─ ≥3.5/5.0: Advance to phone screen
│     └─ <3.5/5.0: Mark PASS (disqualify)
│
├─ ACTION: For candidates ≥3.5, schedule phone screens
│  └─ Target: 2 candidates for Feb 12 afternoon (1-2 PM each)
│
└─ TARGET: 1-2 candidates advance to phone screen by EOD Feb 11

WEDNESDAY, FEB 12 (Phone Screen)
│
├─ SCHEDULE: 30-min phone screens, 1 PM + 2 PM (or 2 PM + 3 PM if only 1 candidate)
│
├─ PHONE SCREEN PROTOCOL:
│  ├─ Intro (2 min): Brief project overview (consciousness-aware linter)
│  ├─ Technical Deep Dive (20 min):
│  │   ├─ Question 1: "Walk me through how you'd architect a Maven plugin that analyzes
│  │   │                code violations in real-time."
│  │   │   └─ What we're looking for: Can they explain plugin lifecycle, integration points?
│  │   │
│  │   ├─ Question 2: "How would you handle edge cases? E.g., what if plugin detects
│  │   │                conflicting rules in different modules?"
│  │   │   └─ What we're looking for: Do they think about failure modes, robustness?
│  │   │
│  │   └─ Question 3 (based on portfolio): "I reviewed your [plugin name] project.
│  │       Walk me through your testing strategy for the [specific component]."
│  │       └─ What we're looking for: Did they actually write those tests? Can they explain them?
│  │
│  ├─ Availability Check (5 min):
│  │   └─ "96 hours, Weeks 5-12, starting Feb 24. Any conflicts?"
│  │   └─ If hedging: PASS (you need commitment)
│  │
│  ├─ Scoring:
│  │   ├─ Architecture depth: 1-5
│  │   ├─ Edge case thinking: 1-5
│  │   ├─ Technical communication: 1-5
│  │   └─ Commitment: 1-5 (Yes=5, Hesitant=2, No=1)
│  │
│  └─ DECISION THRESHOLD (average of 4 scores):
│     ├─ ≥4.0/5.0: STRONG OFFER tier
│     ├─ 3.5-3.9/5.0: OFFER tier
│     ├─ 3.0-3.4/5.0: MAYBE (ask for code sample demo)
│     └─ <3.0/5.0: PASS
│
└─ TARGET: Phone screens completed + decision made by EOD Feb 12

THURSDAY, FEB 13 (Offers & Contracting)
│
├─ TIER 1 (≥4.0): Make offer by 9 AM Feb 13
│  ├─ Terms: "$50/hr fixed-price $4,800 (96h), Weeks 5-12, deliverables per scope."
│  ├─ Counter-offer expected?
│  │   ├─ If YES (counter >$60/hr):
│  │   │   └─ DECISION: If scorecard ≥4.2/5: PAY $5.5K ($57/hr) to secure
│  │   │              If scorecard 4.0-4.2: PASS and activate backup
│  │   │
│  │   └─ If NO: Proceed to contract
│  │
│  ├─ Contract process:
│  │   ├─ Send PHASE_1_CONTRACT_TEMPLATE.md (customized for Contractor 1)
│  │   ├─ Include scope: PHASE_1_CONTRACTOR_SCOPE_DOCUMENTS.md (Contractor 1 section)
│  │   ├─ Payment terms: Invoice upon milestone 1 (Week 7) and milestone 2 (Week 12)
│  │   └─ Signature required by EOD Feb 13
│  │
│  └─ If offer rejected: Activate backup within 24h
│
├─ TIER 2 (3.5-3.9): Make offer by 10 AM Feb 13
│  ├─ Same terms as Tier 1
│  ├─ Counter-offer handling: Stay firm at $50/hr (don't overpay)
│  └─ If rejected: Activate backup immediately
│
├─ BACKUP ACTIVATION (if Tier 1-2 rejected or unavailable):
│  ├─ Identify backup Maven engineer from standby list
│  ├─ Call within 24h of rejection
│  ├─ Offer: $50/hr (don't negotiate up if backup is lower quality)
│  └─ If backup also declines: Escalate to Eric for manual implementation decision
│     └─ Option A: Eric implements manually (20h cost to his time)
│     └─ Option B: Defer smart linter to Phase 2 (1-year later)
│
└─ SUCCESS CRITERIA: Signed contract by EOD Feb 13, 5 PM

DECISION RULE SUMMARY:
├─ Portfolio ≥3.5: Advance to phone screen
├─ Phone screen ≥4.0: Make offer immediately
├─ Phone screen 3.5-3.9: Make offer, stay firm at $50/hr
├─ Counter-offer >$60/hr: Only accept if scorecard ≥4.2
├─ Both tiers reject: Activate backup; if backup declines, defer feature to Phase 2
└─ Timeline: Each decision within 2 hours of trigger (no delays)
```

---

## DECISION TREE 4: Contractor 2 (Technical Writer) Hiring

**Trigger**: Writer identification, portfolio review, offers (Feb 10-13)

```
MONDAY, FEB 10 (Candidate Identification)
│
├─ IDENTIFY 5-6 Academic Technical Writers
│  ├─ Source 1: University CS department postdocs/researchers (academic.com searches)
│  ├─ Source 2: Previous co-authors (if available in network)
│  ├─ Source 3: Technical writing job boards (TechWriting.net, LessWrong)
│  ├─ Source 4: LinkedIn search ("technical writer" + "published papers")
│  └─ Action: Send portfolio requests by 5 PM Feb 10
│     └─ "Please share 2-3 recent papers for writing quality assessment."
│
└─ TARGET: 5-6 candidates identified; writing samples requested by EOD Feb 10

TUESDAY, FEB 11 (Writing Portfolio Review)
│
├─ EVALUATION CRITERIA (for each writing sample):
│  ├─ Publication track record:
│  │   └─ How many papers published? (Target ≥2 published papers)
│  │   └─ Are they first/lead author or contributor?
│  │   └─ Venues: Conference (top-tier like OOPSLA) or journal?
│  │
│  ├─ Writing clarity (first read):
│  │   └─ Can you understand the contribution within 2 pages?
│  │   └─ Score: 1-5 (1=incomprehensible, 5=crystal clear)
│  │
│  ├─ Revision receptiveness (from references):
│  │   └─ Ask references: "How many rounds of revision does this person go through?"
│  │   └─ Target: ≥10 revision rounds acceptable; <5 rounds = red flag (lacks perfectionism)
│  │   └─ Score: 1-5 (1=won't revise, 5=15+ rounds without complaint)
│  │
│  ├─ Statistical rigor:
│  │   └─ Are claims supported by evidence/data?
│  │   └─ Score: 1-5 (1=hand-wavy, 5=rigorous + reproducible)
│  │
│  └─ OVERALL WRITING ASSESSMENT:
│     └─ Average of 4 criteria
│     └─ ≥3.5/5.0 AND ≥2 published papers: Advance to phone screen
│     └─ <3.5/5.0 OR <2 published papers: Mark PASS (disqualify)
│
├─ ACTION: For candidates passing both criteria, schedule phone screens
│  └─ Target: 2-3 candidates for Feb 12 afternoon (2-3 PM each)
│
└─ TARGET: 2-3 candidates advance to phone screen by EOD Feb 11

WEDNESDAY, FEB 12 (Phone Screen)
│
├─ SCHEDULE: 30-min phone screens, 2 PM + 3 PM + 4 PM (staggered to avoid fatigue)
│
├─ PHONE SCREEN PROTOCOL:
│  ├─ Intro (2 min): Brief project overview (5 papers, OOPSLA/ESEM tier)
│  ├─ Research Fit (15 min):
│  │   ├─ Question 1: "Tell me about your experience with empirical research papers.
│  │   │                What's the most revision-heavy paper you've worked on?"
│  │   │   └─ What we're looking for: Comfort with iterative writing; revision tolerance.
│  │   │
│  │   ├─ Question 2: "We're writing about consciousness-aware systems architecture.
│  │   │                Have you worked on novel/emerging topics before? How do you approach
│  │   │                explaining complex ideas clearly?"
│  │   │   └─ What we're looking for: Can they explain hard things? Willing to learn domain?
│  │   │
│  │   ├─ Question 3: "Walk me through your revision process. If a reviewer says 'this
│  │   │                section is unclear,' how do you approach the rewrite?"
│  │   │   └─ What we're looking for: Do they take criticism well? Iterate quickly?
│  │   │
│  │   └─ Question 4: "This is T&M, ~256 hours over 16 weeks. Can you maintain consistency
│  │       across multiple papers + handle tight OOPSLA deadlines (May 1)?"
│  │       └─ What we're looking for: Realistic about timeline? Commitment?
│  │
│  ├─ Scoring:
│  │   ├─ Publication fit: 1-5
│  │   ├─ Domain flexibility: 1-5
│  │   ├─ Revision receptiveness: 1-5
│  │   ├─ Timeline commitment: 1-5
│  │   └─ AVERAGE: ___/5.0
│  │
│  └─ DECISION THRESHOLD:
│     ├─ ≥4.0/5.0: STRONG OFFER tier (rare talent)
│     ├─ 3.5-3.9/5.0: OFFER tier (solid fit)
│     ├─ 3.0-3.4/5.0: MAYBE (ask about specific papers, decide later)
│     └─ <3.0/5.0: PASS
│
└─ TARGET: Phone screens completed + decision made by EOD Feb 12

THURSDAY, FEB 13 (Offers & Contracting)
│
├─ TIER 1 (≥4.0): Make offer by 9 AM Feb 13
│  ├─ Terms: "$75/hr T&M, ~256 hours (16 weeks), Weeks 8-20"
│  ├─ Counter-offer EXPECTED (likely $85-100/hr)?
│  │   ├─ If YES (counter at $85-90/hr):
│  │   │   ├─ DECISION: If scorecard ≥4.5: NEGOTIATE to $85/hr (papers critical)
│  │   │   ├─ If scorecard 4.0-4.4: Offer $80/hr (compromise)
│  │   │   └─ If scorecard <4.0: PASS and activate backup
│  │   │
│  │   └─ If NO: Proceed to contract
│  │
│  ├─ Contract process:
│  │   ├─ Send PHASE_1_CONTRACT_TEMPLATE.md (customized for Contractor 2)
│  │   ├─ Include scope: PHASE_1_CONTRACTOR_SCOPE_DOCUMENTS.md (Contractor 2 section)
│  │   ├─ Hourly rate: Specify $75/hr (or $80/$85/hr if negotiated)
│  │   ├─ Payment terms: Bi-weekly invoicing (2 weeks in arrears)
│  │   └─ Signature required by EOD Feb 13
│  │
│  └─ If offer rejected: Activate backup immediately
│
├─ TIER 2 (3.5-3.9): Make offer by 10 AM Feb 13
│  ├─ Terms: $75/hr (same as Tier 1, no premium)
│  ├─ Counter-offer handling:
│  │   ├─ If counter ≤$80/hr: ACCEPT (papers important, writer quality matters)
│  │   └─ If counter >$80/hr: PASS and activate backup
│  │
│  └─ If rejected: Activate backup immediately
│
├─ BACKUP ACTIVATION (if Tier 1-2 rejected or unavailable):
│  ├─ Identify backup academic writer from standby list
│  ├─ Call within 24h of rejection
│  ├─ Offer: $75/hr (can go to $80/hr if writer has 5+ publications)
│  ├─ If backup also declines: Evaluate secondary backup
│  │
│  └─ CRITICAL: If no writer available by Feb 13:
│     ├─ ESCALATE: Eric + Collaborator will draft papers (lower quality, but recoverable)
│     ├─ Cost: Eric + Collaborator spend 40-60h each on writing (MAJOR TIME SINK)
│     └─ Decision: Accept this risk OR defer one paper to post-project publication
│
└─ SUCCESS CRITERIA: Signed contract by EOD Feb 13, 5 PM

DECISION RULE SUMMARY:
├─ Portfolio: ≥2 papers + ≥3.5 writing quality → phone screen
├─ Phone screen ≥4.0: Make offer; counter-offer up to $85/hr acceptable
├─ Phone screen 3.5-3.9: Make offer at $75/hr; counter only up to $80/hr
├─ Both tiers reject: Activate backup; if backup also declines, Eric + Collaborator write papers
└─ Timeline: Each decision within 2 hours of trigger (papers are CRITICAL; no delays)
```

---

## DECISION TREE 5: Interview Volume Contingency (<6 Applications by EOD Feb 12)

**Trigger**: Fewer than 6 qualified applications received by EOD Feb 12

```
IF INTERVIEW VOLUME < 6 BY EOD FEB 12:
│
├─ ASSESSMENT: Why are applications low?
│  ├─ Reason 1: Recruitment outreach was insufficient (targets too few)
│  ├─ Reason 2: Messaging wasn't compelling (value prop not clear)
│  ├─ Reason 3: Candidates need more time (Feb 11 deadline was too aggressive)
│  └─ Reason 4: Market conditions (niche roles; few available)
│
├─ IMMEDIATE ACTIONS (Feb 12, 3 PM — before EOD):
│  ├─ Action 1: Personal outreach calls to 3-5 secondary targets
│  │   ├─ Script: "Hi [Name], Eric from Samstraumr. I'm reaching out about a unique
│  │   │           opportunity for a 20-week collaboration on consciousness-aware systems research.
│  │   │           Would you have 30 minutes this week to chat?"
│  │   ├─ Target: Schedule phone screens for Feb 13 morning (if qualified)
│  │   └─ URGENCY: These are backup candidates; acknowledge timeline pressure
│  │
│  ├─ Action 2: Activate external recruiter immediately (if not already engaged)
│  │   ├─ Brief: "We need ≥3 additional qualified candidates by EOD Feb 13."
│  │   ├─ Payment: Hourly ($100-150/hr) rather than placement fee (faster)
│  │   └─ Task: Parallel recruiting; they work tonight/tomorrow while you continue
│  │
│  └─ Action 3: Extend phone screen availability
│     ├─ If Tier 1 candidate already committed (yes = schedule Friday, Feb 14)
│     └─ If no offers yet: Extend decision deadline to Feb 14, allowing more candidates through
│
├─ FORK 1: IF You Get to 6-8 Applications by EOD Feb 12 (recovery successful)
│  ├─ Proceed with normal phone screen schedule (Feb 12-13)
│  ├─ Higher selectivity: Can choose top candidates only (≥3.5 scorecard)
│  └─ Timeline: No change; Feb 13 offers proceed on schedule
│
├─ FORK 2: IF You Have 3-5 Applications by EOD Feb 12 (marginal recovery)
│  ├─ Decision: Interview ALL candidates (even <3.5 on paper)
│  ├─ Adjusted timeline:
│  │   ├─ Phone screens: Feb 12 afternoon + Feb 13 morning (compressed)
│  │   ├─ Decision deadline: Feb 13, 12 PM (rushed but doable)
│  │   ├─ Offers: Feb 13, 1 PM (same day as screening)
│  │
│  ├─ Quality adjustment:
│  │   ├─ If top candidate scores 3.3-3.4 (MAYBE tier): Consider hiring (lower bar)
│  │   ├─ But: Do NOT hire below 3.0 (quality floor)
│  │   └─ Trade-off: Accept moderate-quality hire to meet timeline
│
│  └─ Risk: If you hire <3.5 candidate, probability of poor outcomes increases
│     (see PHASE_1_QUANTIFIED_RISK_ANALYSIS.md for impact modeling)
│
└─ FORK 3: IF You Have <3 Applications by EOD Feb 12 (critical failure)
   ├─ Decision: DEFER KICKOFF TO FEB 24 (immediately announce)
   │
   ├─ Rationale: Better to hire right person late than wrong person on time
   │
   ├─ Action: Feb 13, 9 AM announcement
   │   ├─ Message: "Collaborator hiring extended. New start date: Feb 24, 2026."
   │   ├─ To: Team, Finance, all stakeholders
   │   ├─ Tone: "We prioritize hiring quality over speed. Feb 24 ensures best fit."
   │   └─ Impact: "All Phase 1 gates shift 1 week later. Publication timeline adjusts."
   │
   ├─ Resume recruiting: Feb 13-20 (more time to find good candidate)
   ├─ Phone screens: Feb 20-21 (extended window)
   └─ Offers: Feb 21-23 (finalize by week's end)

DECISION RULE SUMMARY:
├─ ≥6 applications by EOD Feb 12: Continue normal schedule
├─ 3-5 applications by EOD Feb 12: Interview all; accept moderate-quality hire
├─ <3 applications by EOD Feb 12: Defer kickoff to Feb 24; resume recruiting
└─ CRITICAL: Do NOT hire below 3.0 scorecard (quality floor is immovable)
```

---

## DECISION TREE 6: Contractor 3 (Data Scientist) Hiring (Fallback Only)

**Note**: Contractor 3 is lowest-risk hire (large talent pool, quick turnaround). Use this tree only if Tier 1 unavailable.

```
TUESDAY, FEB 11 (Screening)
│
├─ IDENTIFY 4-5 Statisticians / Data Scientists
│  ├─ Source: University statistics departments (local universities)
│  ├─ Action: Contact by Feb 10 evening; request references + credentials
│  └─ Filter: PhD in Statistics OR published papers on empirical research
│
├─ CREDENTIAL VERIFICATION (Feb 11):
│  ├─ Check: PhD? Published papers? Professional certifications?
│  ├─ Decision: ≥2 of 3 criteria met = advance to phone screen
│  └─ Target: 1-2 candidates for Feb 12 afternoon
│
└─ TARGET: 1-2 qualified candidates by EOD Feb 11

WEDNESDAY, FEB 12 (Phone Screen & Offers)
│
├─ SCHEDULE: 30-min phone screen (2:30-3:00 PM or 3:00-3:30 PM)
│
├─ PHONE SCREEN:
│  ├─ Question 1: "Have you worked on resilience systems, chaos engineering, or fault injection?"
│  │   └─ Target: Ideally YES; if NO, ask if willing to learn (YES is acceptable)
│  │
│  ├─ Question 2: "Walk me through an empirical study you designed. How did you measure success?"
│  │   └─ Target: Clear methodology, understanding of statistical power
│  │
│  └─ Question 3: "48 hours, Weeks 3-8, starting Feb 24. Doable?"
│     └─ Target: Clear YES (no hedging)
│
├─ OFFER (if candidate scores ≥3.0):
│  ├─ Terms: "$43.75/hr fixed-price $2,100 (48h), Weeks 3-8"
│  ├─ Counter-offer unlikely (budget rate + niche role)
│  │   ├─ If counter: PASS immediately (activate backup)
│  │   └─ If NO counter: Proceed to contract
│  │
│  └─ Contract signature by EOD Feb 13
│
├─ BACKUP (if primary unavailable):
│  ├─ Call backup within 24h
│  ├─ Offer: $43.75/hr (same terms)
│  └─ If backup declines: Statistical analysis deferred; Eric + Collaborator handle internally
│
└─ SUCCESS: Signed contract by EOD Feb 13, 5 PM (or deferred to Eric's workload)

DECISION RULE SUMMARY:
├─ Credential check: PhD OR published papers → phone screen
├─ Phone screen ≥3.0: Make offer immediately
├─ Counter-offer: PASS and activate backup
├─ LOWEST-RISK: Large pool; easy to replace if needed
└─ Fallback: If no statistician available, Eric + Collaborator conduct analysis themselves
```

---

## DAILY DECISION CHECKPOINT TEMPLATE (Print & Use Feb 10-13)

```
DATE: ________________
TIME: ________________
DECISION CHECKPOINT: ☐ 8:30 AM (start of day) | ☐ 5:00 PM (end of day)

QUESTIONS:
│
├─ Finance approval received? ☐ YES ☐ NO (if NO, escalate immediately)
│
├─ Applications received today? ☐ YES (qty: __) ☐ NO (if NO after Feb 11, activate backup)
│
├─ Phone screens scheduled? ☐ YES (qty: __) ☐ NO (if <3 by Feb 11 EOD, flag volume alert)
│
├─ Any candidates withdrawn? ☐ YES (name: _________) ☐ NO
│  └─ If YES: Replace immediately from backup tier
│
├─ Decisions made on screened candidates? ☐ YES (qty: __) ☐ NO (if NO, don't delay; decide within 2h)
│  └─ Collaborator: ☐ Hire ☐ Pass ☐ Hold for references
│  └─ Contractor 1: ☐ Hire ☐ Pass ☐ Hold for code review
│  └─ Contractor 2: ☐ Hire ☐ Pass ☐ Hold for references
│  └─ Contractor 3: ☐ Hire ☐ Pass ☐ Hold for decision
│
└─ BLOCKERS TODAY? ☐ YES (describe: _______________) ☐ NO (proceed to next day)

ACTION ITEMS FOR NEXT 24H:
│
├─ [ ] ________________________
├─ [ ] ________________________
└─ [ ] ________________________

NOTES:
│
│ ___________________________________________________________________
│
│ ___________________________________________________________________
│
│ ___________________________________________________________________

SIGNATURE: _________________ DATE: _____________
```

---

## ESCALATION CONTACTS (Keep Handy)

```
ESCALATION HIERARCHY (use if decision blocked):

1. FINANCE APPROVAL BLOCKED
   ├─ Contact: CFO or Finance Director
   ├─ Message: "Samstraumr Phase 1 budget approval needed by [time]. What's the blocker?"
   ├─ SLA: 2-hour response expected
   └─ Escalation: Executive Sponsor if Finance unavailable

2. CANDIDATE UNAVAILABILITY (multiple rejections)
   ├─ Contact: Eric's executive sponsor (CEO, VP Product, Board)
   ├─ Message: "Collaborator hiring extended. Recommend Feb 24 kickoff. Impacts timeline X, Y, Z."
   ├─ Decision needed: Approve deferred start or reduce scope
   └─ SLA: Same-day decision required

3. EXTERNAL RECRUITER ENGAGEMENT
   ├─ Contact: [Recruiter name/agency]
   ├─ Message: "Need [role] candidate by [date]. Budget: $[amount]. Key criteria: [list]"
   ├─ SLA: 24-hour candidate identification
   └─ Payment: Hourly rate or placement fee (discuss upfront)

4. DECISION PARALYSIS (Can't choose between two candidates)
   ├─ Contact: [Collaborator or trusted advisor]
   ├─ Message: "Candidate A scores [X], Candidate B scores [Y]. Recommendation?"
   ├─ Process: 30-min discussion; make decision same day
   └─ Rule: Don't delay >2 hours waiting for input
```

---

**READY TO DEPLOY**: Print all trees. Reference daily Feb 10-13. Update as situations evolve.

