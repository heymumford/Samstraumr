# Interview Scorecard: Collaborator (Experimentalist)

---

## Candidate Info
- **Name**: ________________
- **Date**: ________________
- **Round**: ☐ Phone Screen (30 min) | ☐ Technical (60 min)
- **Interviewer**: ________________

---

## Phone Screen (30 min)

### Q1: Background & Motivation
**Question**: "Tell me about your experience designing and executing experiments. What's an example where your analysis changed a product decision?"

**What We're Looking For**:
- Evidence of actual hypothesis testing (not just reporting dashboards)
- Comfort explaining statistical significance to non-statisticians
- Sense of ownership over findings ("I recommended X because the data showed...")

**Scoring**:
- [ ] 1 = Vague, data-analytic but not experimental
- [ ] 2 = Some A/B testing experience, basic understanding
- [ ] 3 = Strong example, clear methodology
- [ ] 4 = Published work or high-stakes decisions; deep rigor
- [ ] 5 = Exceptional: published empirical research with novel methods

**Notes**: _______________________________________________

---

### Q2: Python/R Fluency
**Question**: "What's your go-to statistical language? Walk me through a recent analysis you did."

**What We're Looking For**:
- Hands-on coding (not just using tools)
- Can handle data cleaning, visualization, modeling
- Comfortable with statistical libraries (scipy, statsmodels, tidyverse)

**Scoring**:
- [ ] 1 = Theoretical knowledge, minimal hands-on
- [ ] 2 = Basic scripts, some plotting
- [ ] 3 = Comfortable with modeling, debugging
- [ ] 4 = Can optimize code, write reusable analysis functions
- [ ] 5 = Can teach others, published code libraries

**Notes**: _______________________________________________

---

### Q3: Comfort with Ambiguity
**Question**: "We're validating a claim that a software system can 'self-heal' from failures. Our experiment might show it doesn't. How would you approach that result?"

**What We're Looking For**:
- Not defensive or dismissive of negative results
- Focus on rigorous methodology over desired outcome
- Curiosity about *why* something failed
- Comfort with reframing (self-healing → recovery-enabling)

**Scoring**:
- [ ] 1 = Would try to make data fit desired narrative
- [ ] 2 = Understands value of null results, but hesitant
- [ ] 3 = "Publish what the data says, move on"
- [ ] 4 = "This failure mode is interesting; let's design a follow-up experiment"
- [ ] 5 = "This refutes our hypothesis and opens new research questions. Exciting."

**Notes**: _______________________________________________

---

### Q4: Availability & Timeline
**Question**: "This is a 20-week commitment starting Feb 17. Do you have conflicts with that timeline? What's your typical weekly hour availability?"

**What We're Looking For**:
- Full-time commitment (40 hrs/week) possible
- No extended travel or sabbaticals planned
- Flexibility for ad-hoc gate meetings (Weeks 4, 8, 12, 15, 20)

**Scoring**:
- [ ] 1 = Major conflicts, part-time only
- [ ] 2 = Available but some constraints
- [ ] 3 = Full-time available, standard weekly hours
- [ ] 4 = Full-time, flexible for extra effort during critical weeks
- [ ] 5 = "I can drop everything for this; it's a priority"

**Notes**: _______________________________________________

---

## Technical Interview (60 min)

### Exercise: Analyze & Critique (30 min)
**Setup**: Provide candidate with:
1. A flawed A/B test analysis (wrong sample size, p-hacking, confounds)
2. Raw data file + analysis script

**Task**: "Review this analysis. What's wrong? How would you fix it? What would you recommend to leadership?"

**Scoring**:
- [ ] 1 = Misses major flaws, can't articulate issues
- [ ] 2 = Identifies some issues (p-hacking, confounds)
- [ ] 3 = Spots most flaws; proposes fixes; explains to non-statistician
- [ ] 4 = Spots all flaws + suggests robust methodology
- [ ] 5 = Spots flaws, proposes novel approach, discusses statistical power trade-offs

**Notes**: _______________________________________________

---

### Open-Ended: Design an Experiment (20 min)
**Scenario**: "We claim our system recovers from single-point failures in <5 minutes. Design an experiment to validate or refute this."

**What We're Looking For**:
- Hypothesis clarity (what exactly are we testing?)
- Measurement approach (how do we measure recovery time?)
- Control group / baseline (what's the comparison?)
- Sample size & statistical power (how confident in results?)
- Threat to validity (what could go wrong?)

**Scoring**:
- [ ] 1 = Vague, no clear method
- [ ] 2 = Basic idea, missing controls or statistical rigor
- [ ] 3 = Solid design with clear hypothesis, metrics, sample size estimate
- [ ] 4 = Above + considers confounds, failure modes, ceiling effects
- [ ] 5 = Above + proposes multi-phase approach, pre-registration, replication strategy

**Notes**: _______________________________________________

---

### Question: Resilience Systems Familiarity (10 min)
**Question**: "Have you worked on high-availability or fault-tolerant systems? What did you measure?"

**What We're Looking For**:
- NICE to have (not required): chaos engineering, failure injection, MTTF/MTTR metrics
- Willingness to learn domain knowledge
- Can translate statistical concepts to systems context

**Scoring**:
- [ ] 1 = No background, nervous about domain
- [ ] 2 = Aware of concepts, minimal hands-on
- [ ] 3 = Some experience (e.g., worked with SRE teams)
- [ ] 4 = Strong background in resilience testing
- [ ] 5 = Can discuss chaos engineering + statistical validation in detail

**Notes**: _______________________________________________

---

## Summary Scores

| Category | Score | Weight | Weighted |
|----------|-------|--------|----------|
| Background & Motivation (Q1) | __/5 | 20% | __/1.0 |
| Python/R Fluency (Q2) | __/5 | 20% | __/1.0 |
| Comfort with Ambiguity (Q3) | __/5 | 20% | __/1.0 |
| Availability (Q4) | __/5 | 15% | __/0.75 |
| Technical Analysis (Exercise) | __/5 | 15% | __/0.75 |
| Experiment Design (Open-ended) | __/5 | 10% | __/0.5 |
| **TOTAL** | — | 100% | **__/5.0** |

---

## Final Assessment

**Overall Score**: __/5.0

**Recommendation**:
- [ ] ⭐⭐⭐⭐⭐ **STRONG OFFER** (4.5-5.0) — Make offer immediately
- [ ] ⭐⭐⭐⭐ **OFFER** (3.5-4.4) — Good fit, proceed with offer
- [ ] ⭐⭐⭐ **MAYBE** (2.5-3.4) — Borderline, check references
- [ ] ⭐⭐ **PASS** (1.5-2.4) — Lacks key skills, decline
- [ ] ⭐ **STRONG PASS** (0-1.4) — Not a fit, decline

**Key Strengths**: _______________________________________________________________

**Key Concerns**: ________________________________________________________________

**Suggested Questions for Reference Check**: _____________________________________

**Negotiation Points** (if applicable):
- Salary flexibility? ________
- Start date flexibility? ________
- Equity/bonus? ________

---

**Decision Made By**: Eric Mumford
**Date**: ________________
**Status**: ☐ Offer Sent | ☐ Rejected | ☐ Hold (more info needed)

