# Contractor Scope Documents

---

## CONTRACTOR 1: Tooling Engineer (Smart Linter)

### Project: Samstraumr Smart Linter (Rank 29)

**Scope**: Develop an intelligent linting tool that enforces Clean Architecture boundaries, detects consciousness code leakage, and provides developer-friendly remediation guidance.

**Deliverables**:
- [ ] Linter plugin/rule set (96-100 hours, $4,800 @ $50/hr)
- [ ] Architecture boundary enforcement (detects package violations)
- [ ] Consciousness-code pattern detection (flags leakage into non-consciousness packages)
- [ ] Remediation guidance (suggests fixes, not just errors)
- [ ] Documentation + examples
- [ ] Integration with Maven build (`mvn verify`)
- [ ] Test coverage (unit tests for all rules)

**Timeline**:
- Weeks 5-12 (8 weeks, ~12 hours/week)
- Kickoff: Week 2 (brief Eric on rules)
- Delivery: Week 12 (must integrate before final validation)

**Contract Type**: Fixed-price ($4,800) or time-and-materials (TBD)

**Key Criteria**:
- Familiarity with Maven plugins OR ability to learn quickly
- Java 21 + Google Style compliance
- Clear documentation for developers
- Can defend design decisions (architecture rules aren't arbitrary)

**Testing**:
- Plugin works on current codebase (341 classes)
- Detects at least 5 known architecture violations
- False positive rate <5%

---

## CONTRACTOR 2: Technical Writer (Papers + Documentation)

### Project: Publication Support (Ranks 38, 42, 43)

**Scope**: Draft, revise, and polish 3 academic papers for publication + supporting documentation.

**Deliverables**:
- [ ] Paper 1: Empirical Validation (Rank 42) — 8,000-10,000 words
  - Experimental design, results, statistical analysis, discussion
  - Venue: ESEM, TSE, or ICSE (depends on Gate 4 outcome)
  - Timeline: Weeks 8-16

- [ ] Paper 2: Consciousness-Aware Logging (Rank 38) — 6,000-8,000 words
  - Architecture contribution, case study, metrics
  - Venue: SREcon, ICSE, or industry conference
  - Timeline: Weeks 12-18

- [ ] Paper 3: Education Outcomes (Rank 43) — 5,000-7,000 words
  - Cognitive load validation, metaphor utility, learning outcomes
  - Venue: SIGCSE, ITiCSE
  - Timeline: Weeks 14-18

- [ ] Supporting docs:
  - Literature review synthesis
  - Figure/table creation (publication-ready)
  - Reference formatting (Chicago or IEEE, TBD)
  - Response letters to reviewers (if revisions needed)

**Total Effort**: 256 hours (~$19,200 @ $75/hr)

**Timeline**:
- Week 1: Onboarding (understand project, read core papers)
- Week 5: Outline review + skeleton drafts
- Weeks 8-20: Drafting, revision cycles
- Week 18-20: Final polish + submission support

**Contract Type**: Time-and-materials preferred (papers evolve based on data)

**Key Criteria**:
- Portfolio: Academic paper writing (2+ publications)
- Familiarity with LaTeX or Overleaf preferred
- Can write for technical audience (CS researchers)
- Comfort with iteration (multiple revision rounds)
- Deadline-oriented (submission dates firm)

**Communication**:
- Weekly sync (Mondays)
- Drafts reviewed by Eric (turnaround 48h)
- Final decision: Eric (not writer) on when to submit

---

## CONTRACTOR 3: Data Scientist (Statistical Analysis)

### Project: Statistical Analysis & Modeling (Rank 20)

**Scope**: Provide statistical expertise for experimental design, data analysis, and model validation.

**Deliverables**:
- [ ] Statistical power analysis (sample size calculation for experiments)
- [ ] A/B test analysis (cognitive load study, Week 8)
- [ ] Recovery time analysis (distribution modeling, confidence intervals)
- [ ] Effect size estimation (for publication)
- [ ] Threat-to-validity assessment
- [ ] Python/R code for reproducibility
- [ ] Consultation on statistical methodology

**Specific Tasks**:
1. **Week 3**: Review cognitive load study protocol (power analysis, sample size)
2. **Weeks 5-8**: Analyze A/B test results (statistical significance, effect size)
3. **Weeks 9-12**: Model recovery time distributions (parametric vs non-parametric)
4. **Week 15**: Analyze Gate 4 recovery data (critical decision data)
5. **Weeks 16-20**: Review paper statistics (catch p-hacking, report effect sizes properly)

**Total Effort**: 48 hours (~$2,100 @ $43.75/hr)

**Timeline**:
- Weeks 3-8 (primary engagement during measurement phase)
- On-call Weeks 9-20 (ad-hoc questions, ~1-2 hrs/week)

**Contract Type**: Fixed-price preferred with hourly overage cap

**Key Criteria**:
- Expert-level statistical methodology (can advise on nuanced choices)
- Can code in R or Python (analysis must be reproducible)
- Publication experience (knows what peer reviewers will scrutinize)
- Clear communicator (explain statistics to non-statisticians)

**Communication**:
- Async: Email + shared Google Docs for analysis notes
- Sync: 30-min calls as needed (not weekly)
- Deliverables: Annotated code + statistical summaries

---

## Contract Timeline Summary

| Contractor | Role | Effort | Cost | Start | End |
|-----------|------|--------|------|-------|-----|
| 1 | Tooling | 96-100h | $4,800 | Week 5 | Week 12 |
| 2 | Writer | 256h | $19,200 | Week 8 | Week 20 |
| 3 | Data Sci | 48h | $2,100 | Week 3 | Week 20 |
| **TOTAL** | — | 400-404h | **$26,100** | — | — |

---

## Vetting Checklist

### Contractor 1 (Tooling Engineer)
- [ ] Show examples of Maven plugin development OR documentation showing learning path
- [ ] Verify Java proficiency (code review of sample project)
- [ ] Ask: "How would you handle edge cases in architecture violation detection?"
- [ ] Reference check: Quality of previous linting/static analysis work

### Contractor 2 (Technical Writer)
- [ ] Request portfolio: 2+ published papers or technical reports
- [ ] Writing sample: Ask them to review/edit excerpt from existing paper
- [ ] Verify LaTeX proficiency (or willingness to learn quickly)
- [ ] Ask: "Tell me about a time you disagreed with an author on wording. How did you handle it?"
- [ ] Reference: Previous PI/advisor on publication timelines

### Contractor 3 (Data Scientist)
- [ ] Verify statistical credentials (degrees, certifications, or published research)
- [ ] Ask them to critique a flawed statistical analysis (same as interviewer exercise)
- [ ] Code review: Share example Python/R statistical script
- [ ] Ask: "What's the most common statistical mistake you see in papers?"
- [ ] Reference: Previous collaborator on peer-reviewed work

---

## Contracting Details (To Complete)

### For All Contractors:
- [ ] NDA (if handling sensitive project data)
- [ ] IP assignment (code/writing belongs to Samstraumr/Guild)
- [ ] Confidentiality clause (don't discuss publicly until publication)
- [ ] Conflict of interest statement
- [ ] W-9 / tax documentation (US contractors)

### Payment Terms:
- [ ] Milestone-based or time-and-materials?
- [ ] Invoice schedule (weekly, bi-weekly, monthly)?
- [ ] Late payment penalties? (Standard: 1.5% after 30 days)
- [ ] Termination clause? (Notice period, severance)

---

## Next Steps (Feb 10-13)

1. **Contractor 1**:
   - Identify 2-3 Maven plugin engineers
   - Request work samples by Feb 11
   - Phone screen Feb 12
   - Decision + offer by Feb 13

2. **Contractor 2**:
   - Post writing opportunity on academic job boards
   - Request 2-3 paper samples by Feb 11
   - Phone screen Feb 12
   - Decision + offer by Feb 13

3. **Contractor 3**:
   - Reach out to 2-3 academic statisticians (e.g., local university)
   - Request credentials + references by Feb 10
   - Phone screen Feb 11
   - Decision + offer by Feb 12

**Goal**: All 3 contractors signed by Feb 13, onboarding begins Week 2.

