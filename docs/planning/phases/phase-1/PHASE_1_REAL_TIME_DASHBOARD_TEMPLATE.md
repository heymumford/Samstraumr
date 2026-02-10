# Phase 1 Real-Time Dashboard: Hiring Funnel & KPI Tracker

**Purpose**: Track Phase 1 staffing execution daily. Identify blockers in real-time. Trigger decision thresholds.

**Format**: Google Sheets template (copy to your drive; update daily)

**Status**: READY (open Feb 10 AM; update EOD each day through Feb 13)

---

## SHEET 1: HIRING FUNNEL TRACKER

```
═══════════════════════════════════════════════════════════════════════════════
SAMSTRAUMR PHASE 1 HIRING FUNNEL (Updated: [TODAY'S DATE])
═══════════════════════════════════════════════════════════════════════════════

ROLE: COLLABORATOR (Experimentalist) | Deadline: Feb 13, 5 PM | Target: 1 hire

| Stage | Count | % of Funnel | Status | Notes | Decision |
|-------|-------|-----------|--------|-------|----------|
| TARGETS | 6 | 100% | ☐ On track ☐ At risk | Names: [list] | Proceed |
| APPLICATIONS | __/6 | _% | ☐ On track ☐ Slow | Received by EOD Feb 11 | Proceed if ≥4 |
| PHONE SCREENS | __/6 | _% | ☐ Done ☐ In progress | Completed Feb 12 | Proceed |
| SCORECARDS | __/6 | _% | ☐ All scored ☐ Pending | ≥3.5 required | Proceed if 1+ ≥3.5 |
| OFFERS MADE | __/6 | _% | ☐ Sent ☐ Pending | Date sent: _____ | WAIT for response |
| SIGNED OFFERS | __/1 | _% | ☐ SIGNED ☐ PENDING | Date signed: _____ | SUCCESS ✓ |

CONVERSION METRICS:
├─ Targets → Applications: __% (baseline: 67%)
├─ Applications → Phone screens: __% (baseline: 75%)
├─ Phone screens → Offers: __% (baseline: 33%)
├─ Offers → Signed: __% (baseline: 100% if score ≥3.5)
└─ OVERALL TARGETS → SIGNED: __% (baseline: 52%)

RISK INDICATORS:
├─ Applications < 4 by EOD Feb 11: ⚠ ACTIVATE DECISION TREE 5
├─ No scorecards ≥3.5 by EOD Feb 12: ⚠ ACTIVATE BACKUP TIER
├─ No signed offer by EOD Feb 13: ⚠ DEFER KICKOFF TO FEB 24

═══════════════════════════════════════════════════════════════════════════════

ROLE: CONTRACTOR 1 (Maven Engineer) | Deadline: Feb 13, 5 PM | Target: 1 contract

| Stage | Count | Status | Decision |
|-------|-------|--------|----------|
| PORTFOLIO REVIEWS | __/3 | ☐ In progress ☐ Complete | ≥3.5 quality → phone screen |
| PHONE SCREENS | __/2 | ☐ Done ☐ Pending | ≥4.0 score → offer |
| OFFERS | __/1 | ☐ Sent ☐ Pending | Counter-offer: ☐ Expected ☐ None |
| SIGNED | __/1 | ☐ SIGNED ☐ PENDING | SUCCESS if signed by 5 PM |

═══════════════════════════════════════════════════════════════════════════════

ROLE: CONTRACTOR 2 (Technical Writer) | Deadline: Feb 13, 5 PM | Target: 1 contract

| Stage | Count | Status | Decision |
|-------|-------|--------|----------|
| PORTFOLIO REVIEWS | __/5 | ☐ In progress ☐ Complete | ≥2 papers + ≥3.5 quality → phone screen |
| PHONE SCREENS | __/2 | ☐ Done ☐ Pending | ≥3.5 → offer |
| OFFERS | __/2 | ☐ Sent ☐ Pending | Counter-offer: ☐ Expected ☐ None |
| SIGNED | __/1 | ☐ SIGNED ☐ PENDING | SUCCESS if signed by 5 PM |

═══════════════════════════════════════════════════════════════════════════════

ROLE: CONTRACTOR 3 (Data Scientist) | Deadline: Feb 13, 5 PM | Target: 1 contract

| Stage | Count | Status | Decision |
|-------|-------|--------|----------|
| CREDENTIAL CHECKS | __/4 | ☐ Done ☐ Pending | PhD or papers → phone screen |
| PHONE SCREENS | __/1 | ☐ Done ☐ Pending | ≥3.0 → offer |
| OFFERS | __/1 | ☐ Sent ☐ Pending | Counter: ☐ Expected ☐ None |
| SIGNED | __/1 | ☐ SIGNED ☐ PENDING | SUCCESS if signed by 5 PM |

═══════════════════════════════════════════════════════════════════════════════

OVERALL HIRING STATUS (Feb 13, EOD)

| Outcome | Probability | Status | Action |
|---------|------------|--------|--------|
| All 4 hired | 53% baseline, 64% with recruiter | __% | ☐ GO (proceed to Week 1) |
| 3 hired | 39% | __% | ☐ ACCEPTABLE (1 contractor delay) |
| 2 hired | 8% | __% | ⚠ CRITICAL (rescope project) |
| <2 hired | <1% | __% | ⚠ DEFER KICKOFF |

SUCCESS CRITERIA: ☐ ALL 4 HIRED & SIGNED by EOD Feb 13
ACCEPTABLE: ☐ 3+ HIRED & SIGNED by EOD Feb 13
FAILURE: ☐ <3 HIRED by EOD Feb 13 → DEFER TO FEB 24

═══════════════════════════════════════════════════════════════════════════════
```

---

## SHEET 2: DAILY DECISION LOG

```
═══════════════════════════════════════════════════════════════════════════════
DAILY DECISION LOG (Feb 10-13)
═══════════════════════════════════════════════════════════════════════════════

DATE: ____________ | TIME: _________ | CHECKPOINT: ☐ 8:30 AM ☐ 5:00 PM

QUESTIONS ANSWERED TODAY:

Finance Approved? ☐ YES | ☐ NO (if NO, escalate → CFO)
├─ Status: ____________________________
└─ Action: ____________________________

Applications Received? ☐ YES (__qty) | ☐ NO
├─ Total received to date: ____
└─ On track for ≥6 by EOD Feb 12? ☐ YES | ☐ NO (if NO, activate Decision Tree 5)

Phone Screens Scheduled? ☐ YES (__qty) | ☐ NO
├─ Scheduled for: ____________________________
└─ Confirmed attendees: ____________________________

Scorecards Completed? ☐ YES (__qty) | ☐ NO
├─ Scores: ____________________________
└─ Any ≥3.5? ☐ YES | ☐ NO

Decisions Made? ☐ YES | ☐ NO
├─ Collaborator: ☐ Hire | ☐ Pass | ☐ Hold for references
├─ Contractor 1: ☐ Hire | ☐ Pass | ☐ Hold for code review
├─ Contractor 2: ☐ Hire | ☐ Pass | ☐ Hold for references
└─ Contractor 3: ☐ Hire | ☐ Pass | ☐ Hold

BLOCKERS TODAY:
├─ ☐ Slow application volume
├─ ☐ Low scorecard quality
├─ ☐ Finance approval delayed
├─ ☐ Candidate withdrawal
├─ ☐ Other: ____________________________

ESCALATIONS TRIGGERED:
├─ ☐ None
├─ ☐ Volume contingency (Decision Tree 5)
├─ ☐ Finance escalation (Decision Tree 1)
└─ ☐ Backup activation (Decision Tree X)

ACTIONS FOR TOMORROW:
├─ [ ] ____________________________
├─ [ ] ____________________________
└─ [ ] ____________________________

═══════════════════════════════════════════════════════════════════════════════
```

---

## SHEET 3: CANDIDATE TRACKER (Detailed)

```
═══════════════════════════════════════════════════════════════════════════════
CANDIDATE TRACKER: DETAILED INFORMATION
═══════════════════════════════════════════════════════════════════════════════

COLLABORATOR CANDIDATES:

| Name | Email | Source | Applied | Phone Date | Score | Decision | Notes |
|------|-------|--------|---------|-----------|-------|----------|-------|
| [C1] | [E1] | Referral | ☐ Yes/No | Feb 12, 9 AM | __/5 | ☐ Hire | [Background summary] |
| [C2] | [E2] | LinkedIn | ☐ Yes/No | Feb 12, 10 AM | __/5 | ☐ Pass | [Background summary] |
| [C3] | [E3] | Recruiter | ☐ Yes/No | Feb 12, 11 AM | __/5 | ☐ Hold | [Background summary] |
| BACKUP 1 | [B1] | Personal | TBD | TBD | TBD | ☐ Hold | [Backup tier] |
| BACKUP 2 | [B2] | Personal | TBD | TBD | TBD | ☐ Hold | [Backup tier] |

CONTRACTOR 1 (Maven) CANDIDATES:

| Name | Portfolio | Source | Code Quality | Phone Date | Score | Decision | Notes |
|------|-----------|--------|---|-----------|-------|----------|-------|
| [C1] | [GitHub] | Maven PMC | ☐ 3.5+ / ☐ <3.5 | Feb 12, 1 PM | __/5 | ☐ Hire | |
| [C2] | [GitHub] | Referral | ☐ 3.5+ / ☐ <3.5 | Feb 12, 2 PM | __/5 | ☐ Pass | |

... [Similar structure for Contractor 2, Contractor 3] ...

═══════════════════════════════════════════════════════════════════════════════
```

---

## SHEET 4: TIMELINE CRITICAL PATH

```
═══════════════════════════════════════════════════════════════════════════════
CRITICAL PATH TIMELINE (Feb 10-13)
═══════════════════════════════════════════════════════════════════════════════

MON, FEB 10 (Target: Finance approval + recruitment launch)
├─ 8:00 AM: Finance approval obtained ☐ YES ☐ NO
├─ 10:00 AM: Recruiter engaged (if using external) ☐ YES ☐ NO
├─ 2:00 PM: Recruitment emails sent to 6+ candidates ☐ YES ☐ NO
├─ 4:00 PM: Portfolio requests sent to contractors ☐ YES ☐ NO
├─ EOD: 1st applications arriving? ☐ YES (qty: __) ☐ NO (expected: 5 PM Feb 11)
└─ BLOCKER: ☐ Finance not approved | ☐ Recruitment delayed | ☐ On track

TUE, FEB 11 (Target: Portfolio review + application screening)
├─ 9:00 AM: Applications closing (deadline today, 5 PM) ☐ APPROACHING
├─ 10:00 AM: Portfolio reviews started ☐ YES ☐ NO
├─ 3:00 PM: Contractor portfolio reviews complete ☐ YES ☐ NO
├─ 5:00 PM: Applications closed ☐ Total received: __ (target ≥4)
├─ 6:00 PM: Phone screens scheduled for Feb 12 ☐ YES (qty: __) ☐ NO
└─ BLOCKER: ☐ Low applications (<4) → Activate Decision Tree 5

WED, FEB 12 (Target: Phone screens + scoring)
├─ 9:00 AM: 1st phone screen (Collaborator) ☐ DONE ☐ PENDING
├─ 10:00 AM: 2nd phone screen ☐ DONE ☐ PENDING
├─ 11:00 AM: 3rd phone screen ☐ DONE ☐ PENDING
├─ 1:00 PM: Contractor phone screens ☐ DONE ☐ PENDING
├─ EOD: All scorecards completed ☐ YES ☐ NO
├─ 5:00 PM: Hiring decisions made ☐ YES ☐ NO (all candidates scored ≥1.0, ≤5.0)
└─ BLOCKER: ☐ No scorecards ≥3.5 → Activate backup candidates

THU, FEB 13 (Target: Offers + contracting)
├─ 9:00 AM: Offer calls placed (Collaborator + Contractor 1) ☐ YES ☐ NO
├─ 10:00 AM: Offer emails sent (Contractors 2-3) ☐ YES ☐ NO
├─ 1:00 PM: Contracts distributed to offers accepted ☐ YES ☐ NO
├─ 2:00 PM: Counter-offer responses logged ☐ YES ☐ NO
├─ 3:00 PM: Backup activation (if needed) ☐ DONE ☐ N/A ☐ PENDING
├─ 4:00 PM: Contract signature deadline ☐ APPROACHING (1h remaining)
├─ 5:00 PM: DECISION POINT: How many signed?
│  ├─ ☐ 4 signed = GO (success)
│  ├─ ☐ 3 signed = ACCEPTABLE (one delayed)
│  ├─ ☐ 2 signed = CRITICAL (rescope)
│  └─ ☐ <2 signed = DEFER KICKOFF (announce immediately)
└─ 5:30 PM: Announcement to team + stakeholders ☐ DONE ☐ PENDING

═══════════════════════════════════════════════════════════════════════════════

DEADLINE SUMMARY:
├─ Feb 13, 5 PM: All contracts signed or ESCALATE
├─ Feb 13, 5:30 PM: Team announcement (GO or DEFER)
├─ Feb 17, 9 AM: Kickoff all-hands (or rescheduled to Feb 24 / Mar 3)
└─ SUCCESS = ☐ All 4 signed by 5 PM | FAILURE = ☐ <3 signed by 5 PM

═══════════════════════════════════════════════════════════════════════════════
```

---

## SHEET 5: FINANCIAL TRACKING

```
═══════════════════════════════════════════════════════════════════════════════
FINANCIAL TRACKING & BUDGET BURN
═══════════════════════════════════════════════════════════════════════════════

BUDGET ALLOCATED:
├─ Collaborator: $60,000 (20 weeks × $3,000/week)
├─ Contractors: $26,100 (Contractor 1: $4.8K, Contractor 2: $19.2K, Contractor 3: $2.1K)
└─ TOTAL PHASE 1: $156,100 (locked budget)

ACTUALS (as of Feb 13):
├─ Collaborator (if hired): $0 (payment starts Feb 17)
├─ Contractor 1 (Milestone 1, due Apr 4): $0 (payment due after delivery)
├─ Contractor 2 (weeks 8-20 invoicing): $0 (payment due after invoice)
├─ Contractor 3 (fixed-price, due Apr 18): $0 (payment due after delivery)
├─ Recruiter (if used): $1,500 (optional; approve separately)
└─ TOTAL SPENT TO DATE: $____ (should be $0-1,500 before Feb 17)

RATE ADJUSTMENTS (if negotiated):
├─ Collaborator: $3,000/week → ☐ $3,500/week (if offered premium) = $10,000 additional
├─ Contractor 1: $50/hr → ☐ $57/hr (if upgraded) = $700 additional
├─ Contractor 2: $75/hr → ☐ $80/hr | ☐ $85/hr = $1,280 - $2,560 additional
├─ Contractor 3: $43.75/hr → ☐ $48/hr (if upgraded) = $192 additional
└─ REVISED BUDGET: $____ (original $156.1K + adjustments)

BUDGET VARIANCE ANALYSIS:
├─ Best case (no upgrades): $156,100
├─ Likely case (+Collab premium): $166,100
├─ Worst case (all premiums + recruiter): $171,650
└─ Budget flexibility: $15,550 (10% contingency margin)

COST SAVINGS AVAILABLE (if needed):
├─ Defer Philosophy Expert (Week 3-4): Save ~$1,000
├─ Defer Metrics Dashboard (Phase 6): Save ~$0 (internal effort)
├─ Reduce Contractor 2 scope (2 papers instead of 3): Save ~$6,400
└─ TOTAL AVAILABLE CUTS: ~$7,400 (if budget pressured)

═══════════════════════════════════════════════════════════════════════════════
```

---

## SHEET 6: DECISION THRESHOLD ALERTS

```
═══════════════════════════════════════════════════════════════════════════════
REAL-TIME DECISION THRESHOLD ALERTS
═══════════════════════════════════════════════════════════════════════════════

ALERT 1: LOW APPLICATION VOLUME
├─ Trigger: <6 applications by EOD Feb 12
├─ Condition: ☐ Met (activate Decision Tree 5)
├─ Action: ☐ Personal outreach to backup tier
│   ├─ [ ] Call 3-5 secondary candidates
│   ├─ [ ] Schedule Feb 13 morning phone screens
│   └─ [ ] Compress decision timeline
└─ Status: ☐ TRIGGERED | ☐ NOT TRIGGERED | ☐ WATCHING

ALERT 2: LOW SCORECARD QUALITY
├─ Trigger: No candidates score ≥3.5 after phone screens
├─ Condition: ☐ Met (activate backup hiring)
├─ Action: ☐ Move to backup candidate immediately
│   ├─ [ ] Call backup Collaborator Feb 13, afternoon
│   ├─ [ ] Phone screen Feb 14 morning (if needed)
│   └─ [ ] Offer Feb 14 afternoon (compressed timeline)
└─ Status: ☐ TRIGGERED | ☐ NOT TRIGGERED | ☐ WATCHING

ALERT 3: FINANCE APPROVAL DELAYED
├─ Trigger: Approval not received by EOD Feb 10
├─ Condition: ☐ Met (escalate to CFO)
├─ Action: ☐ Call CFO by 10 AM Feb 11
│   ├─ [ ] Explain timeline criticality
│   ├─ [ ] Request urgent approval decision
│   └─ [ ] If delayed past Feb 11: DEFER KICKOFF
└─ Status: ☐ TRIGGERED | ☐ NOT TRIGGERED | ☐ APPROVED

ALERT 4: COUNTER-OFFER VOLUME
├─ Trigger: >2 candidates ask for rate increases
├─ Condition: ☐ Met (budget impact analysis needed)
├─ Action: ☐ Assess which to pay premium for
│   ├─ [ ] Collaborator ≥4.0? → Pay $3.5K/week
│   ├─ [ ] Contractor 2 ≥4.3? → Negotiate to $85/hr
│   └─ [ ] Others: PASS and activate backups
└─ Status: ☐ TRIGGERED | ☐ NOT TRIGGERED | ☐ MONITORING

ALERT 5: ALL 4 NOT SIGNED BY EOD FEB 13
├─ Trigger: <4 signed contracts by 5 PM Feb 13
├─ Condition: ☐ Met (kickoff deferral decision needed)
├─ Action: ☐ Feb 13, 5:30 PM announcement
│   ├─ If 3 signed: "1 contractor delayed; kickoff Feb 17 as planned"
│   ├─ If 2 signed: "Critical path impacted; assess Feb 24 vs. scope reduction"
│   └─ If <2 signed: "DEFER kickoff to Feb 24 or Mar 3"
└─ Status: ☐ TRIGGERED | ☐ NOT TRIGGERED | ☐ DECISION PENDING

═══════════════════════════════════════════════════════════════════════════════

IF ALERT TRIGGERED:
├─ Document the condition + time
├─ Reference appropriate decision tree
├─ Make decision within 2 hours of trigger
├─ Update this sheet + communicate to team
└─ Log decision rationale (for post-project review)

═══════════════════════════════════════════════════════════════════════════════
```

---

## COPY TO GOOGLE SHEETS (Instructions)

1. Create new Google Sheet: "SAMSTRAUMR_PHASE_1_DASHBOARD"
2. Copy each section into separate tabs:
   - Tab 1: "Hiring Funnel"
   - Tab 2: "Daily Log"
   - Tab 3: "Candidates"
   - Tab 4: "Timeline"
   - Tab 5: "Financials"
   - Tab 6: "Alerts"
3. Share read-only with Finance + Executive Sponsor (get daily visibility)
4. Share edit with Eric only (decision maker updates)
5. Set up email notifications:
   - Alert when <6 applications logged
   - Alert when decision threshold triggered
   - Daily EOD summary email

---

**READY TO DEPLOY**: Open Feb 10, 8 AM. Update EOD each day Feb 10-13. Reference during daily checkpoints.

