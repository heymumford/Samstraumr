# Phase 1 Collaborator Evaluation Harness: Rigorous Weighted Scorecard with Calibration

**Purpose**: Eliminate subjectivity from collaborator hiring. Calibrated scorecard with worked examples, rubrics per question, and decision thresholds.

**Status**: READY (use during Feb 12 phone screens and Feb 13 decisions)

---

## PART 1: WEIGHTED CRITERIA & DECISION FRAMEWORK

### Scoring Architecture

```
TOTAL SCORE (out of 5.0):

Background & Motivation (20% weight)            [Score: __/5]  ×  0.20  =  __/1.0
Python/R Fluency (20% weight)                    [Score: __/5]  ×  0.20  =  __/1.0
Comfort with Ambiguity (20% weight)              [Score: __/5]  ×  0.20  =  __/1.0
Availability & Timeline (15% weight)             [Score: __/5]  ×  0.15  =  __/0.75
Technical Analysis (Exercise) (15% weight)       [Score: __/5]  ×  0.15  =  __/0.75
Experiment Design (Open-Ended) (10% weight)      [Score: __/5]  ×  0.10  =  __/0.5

TOTAL COMPOSITE SCORE:  _____ / 5.0

DECISION THRESHOLD:
├─ 4.5-5.0: STRONG OFFER (top 10% of candidates; hire immediately)
├─ 4.0-4.4: OFFER (top 20%; good fit; proceed with offer)
├─ 3.5-3.9: MAYBE (top 40%; marginal fit; get references; decide based on context)
├─ 3.0-3.4: RISKY (below bar for standalone hire; pass unless backup desperate)
└─ <3.0: PASS (does not meet quality threshold)
```

---

## PART 2: PER-QUESTION RUBRICS WITH WORKED EXAMPLES

### Question 1: Background & Motivation (Weight: 20%)

**Full Question**: "Tell me about your experience designing and executing experiments. What's an example where your analysis changed a product decision?"

**Why We Ask**: Testing ability to formulate hypotheses, execute with rigor, and influence stakeholders with data.

#### Rubric: 1-5 Scale

```
SCORE 5 (Exceptional):
├─ Candidate has published empirical research with novel statistical methods
├─ Example: "I published in [top venue] about A/B testing in [domain]"
├─ Shows: Clear hypothesis, multiple iterations, peer review, follow-up studies
├─ Communication: Explains findings to non-statisticians; influenced executive decisions
├─ Red flags: NONE
└─ Hiring recommendation: STRONG HIRE (rare talent)

SCORE 4 (Strong):
├─ Candidate has designed and executed 5+ A/B tests in production systems
├─ Example: "At [company], I designed experiment validating [hypothesis]. Became company policy."
├─ Shows: Clear methodology, statistical rigor, understands confounds, drove decisions
├─ Communication: Can explain results to product managers, executives
├─ Confidence: High; can lead experiments independently
└─ Hiring recommendation: HIRE (top tier candidate)

SCORE 3 (Adequate):
├─ Candidate has executed 2-3 A/B tests; understands basic methodology
├─ Example: "I ran an experiment on [feature]. Recommended [action] based on results."
├─ Shows: Knows hypothesis testing, understands significance, executed on time
├─ Limitations: May lack rigor on confounds; didn't lead design (was assigned)
├─ Communication: Can explain results but may struggle with edge cases
└─ Hiring recommendation: BORDERLINE (depends on other factors)

SCORE 2 (Weak):
├─ Candidate has experience with A/B testing but mostly analytical (not design)
├─ Example: "I analyzed [company's] A/B test data. Wrote a report."
├─ Shows: Can use statistical tools; understands basic concepts
├─ Limitations: Didn't design experiment; didn't influence decision; passive role
├─ Communication: Can discuss results but may be uncomfortable with ambiguity
└─ Hiring recommendation: PROBABLY PASS (lacks autonomy)

SCORE 1 (Inadequate):
├─ Candidate has theoretical knowledge but minimal hands-on experimental work
├─ Example: "I studied statistical methods in grad school. Haven't run A/B tests professionally."
├─ Shows: Understands concepts but hasn't applied at scale
├─ Limitations: No real-world experience; may struggle with practical tradeoffs
├─ Communication: Academic but not practical
└─ Hiring recommendation: STRONG PASS (quality gap too large)
```

#### Worked Example: Evaluating Real Answers

**Candidate A (Phone Screen Response)**:
> "I designed and ran about 15 A/B tests at [Startup]. Most were around retention features. My analysis showed that feature X increased user retention by 8% (p < 0.01), so the product team shipped it. I also recommended abandoning feature Y because the effect size was too small despite statistical significance. That decision probably saved the company engineering time."

**Scoring A**:
- Quantifies experience (15 tests) ✓
- Shows hypothesis formulation (retention features) ✓
- Understands statistical significance + effect size (8%, p < 0.01, significance ≠ impact) ✓✓
- Drove decisions (shipped feature, killed feature) ✓
- Communication is clear and confident ✓
- **SCORE: 4/5** (Strong; lacks published research, but solid practitioner)

---

**Candidate B (Phone Screen Response)**:
> "I've worked with data in the past. I've seen A/B tests happen at [Company]. I think they're important. I understand how hypothesis testing works from my statistics classes."

**Scoring B**:
- No quantified experience ✗
- Passive observer, not designer ✗
- Theoretical knowledge without application ✗
- No examples of influencing decisions ✗
- Vague communication ✗
- **SCORE: 1/5** (Inadequate; theory without practice)

---

### Question 2: Python/R Fluency (Weight: 20%)

**Full Question**: "What's your go-to statistical language? Walk me through a recent analysis you did."

**Why We Ask**: Candidate must code independently. Can't outsource analysis; needs hands-on capability.

#### Rubric: 1-5 Scale

```
SCORE 5 (Expert):
├─ Candidate has published code libraries (PyPI, CRAN, GitHub 100+ stars)
├─ Example: "I maintain [library]. It's used in [context]."
├─ Shows: Can design APIs, optimize performance, teach others
├─ Real-world: Handles edge cases, writes comprehensive tests
└─ Hiring recommendation: ELITE (rare; publish-quality code)

SCORE 4 (Advanced):
├─ Candidate writes production-grade code; can optimize for performance
├─ Example: "I wrote statistical analysis pipeline in Python. Handles 1M data points. Used pandas, scipy, numpy efficiently."
├─ Shows: Can debug complex code, write reusable functions, understands data structures
├─ Real-world: Delivers clean, documented code; peers review & approve
└─ Hiring recommendation: STRONG HIRE

SCORE 3 (Competent):
├─ Candidate writes functional statistical code; adequate style
├─ Example: "I use R for analysis. Write scripts that load data, fit models, generate plots."
├─ Shows: Comfortable with core tools (tidyverse, caret, ggplot2 for R; pandas, sklearn for Python)
├─ Real-world: Can debug own code; may need help optimizing
└─ Hiring recommendation: ACCEPTABLE (meets job requirements)

SCORE 2 (Basic):
├─ Candidate uses statistical software (SPSS, Excel, Jupyter notebooks with copy-paste)
├─ Example: "I use Python notebooks. Downloaded some packages. Run other people's scripts."
├─ Shows: Can follow tutorials; may struggle with edge cases or custom analyses
├─ Real-world: Mostly uses pre-built functions; can't easily extend
└─ Hiring recommendation: BELOW BAR (quality concern)

SCORE 1 (Inadequate):
├─ Candidate has theoretical knowledge but can't code
├─ Example: "I understand statistics. Haven't really programmed. Used statistical software in school."
├─ Shows: Knows concepts but can't implement
└─ Hiring recommendation: STRONG PASS (deal breaker)
```

#### Worked Example: Evaluating Real Answers

**Candidate A (Phone Screen Response)**:
> "I primarily use Python. For a recent analysis, I loaded experiment data from CSV, cleaned missing values using pandas, fit a logistic regression with scikit-learn, did cross-validation, and plotted results with matplotlib. Code is pretty clean; I use type hints and docstrings. I also wrote unit tests for the data cleaning pipeline."

**Scoring A**:
- Primary language specified (Python) ✓
- Recent, real-world example ✓
- Knows relevant libraries (pandas, sklearn, matplotlib) ✓
- Practices code quality (type hints, docstrings, unit tests) ✓✓
- Understands validation (cross-validation) ✓
- **SCORE: 4/5** (Advanced; production-ready code)

---

**Candidate B (Phone Screen Response)**:
> "I've used R and Python. I'm not super fluent in either. I can follow tutorials and run analyses, but I usually rely on people who know programming better to help with anything complicated."

**Scoring B**:
- Vague language preference ✗
- No specific example ✗
- Admits dependency on others ✗
- Lacks confidence in coding ✗
- **SCORE: 1-2/5** (Basic at best; quality concern)

---

### Question 3: Comfort with Ambiguity (Weight: 20%)

**Full Question**: "We're validating a claim that a software system can 'self-heal' from failures. Our experiment might show it doesn't. How would you approach that result?"

**Why We Ask**: Samstraumr's research is novel. Null results likely. Need someone who treats negative findings as scientific gold, not failure.

#### Rubric: 1-5 Scale

```
SCORE 5 (Research-Grade Mindset):
├─ Candidate reframes negative result as opportunity for new hypothesis
├─ Example: "If self-healing fails, the interesting question becomes: Under what conditions
├─           does it succeed? That becomes the next experiment."
├─ Shows: Scientist's curiosity; sees failure as data; designs follow-up study immediately
├─ Maturity: Comfortable with being wrong; excited by refutations
└─ Hiring recommendation: ELITE RESEARCH MINDSET (exactly what we need)

SCORE 4 (Professional Scientist):
├─ Candidate publishes finding; investigates root cause
├─ Example: "If it doesn't self-heal, I'd first verify the experiment design. Then ask:
├─           Is the failure condition-specific? When *does* it work? What are we learning?"
├─ Shows: Scientific rigor; doesn't blame methodology; leans into understanding
├─ Confidence: Comfortable presenting null results to stakeholders
└─ Hiring recommendation: STRONG HIRE (mature mindset)

SCORE 3 (Professional Pragmatist):
├─ Candidate would report finding but might be defensive
├─ Example: "I'd report that self-healing doesn't work under these conditions. Of course,
├─           the architecture might be different in production, so this is just one scenario."
├─ Shows: Understands generalization; maybe makes excuses for failed hypothesis
├─ Maturity: Okay with null results but prefers positive findings
└─ Hiring recommendation: ACCEPTABLE (will report truth, but not excited by it)

SCORE 2 (Hypothesis-Attached):
├─ Candidate might try to make data fit narrative
├─ Example: "That's surprising. I'd re-run the experiment. Maybe there was a confound.
├─           Or maybe the hypothesis is right but we need a longer timeline."
├─ Shows: Attached to preferred outcome; reaches for excuses before exploring
├─ Red flag: Might p-hack or cherry-pick conditions until positive result emerges
└─ Hiring recommendation: RISK (scientific integrity concern)

SCORE 1 (Anti-Science):
├─ Candidate dismisses null result or avoids reporting
├─ Example: "That would be bad. I'd probably not include that experiment in the paper.
├─           We need to show the system works."
├─ Shows: Outcome-driven thinking; ignores inconvenient evidence
├─ Red flag: Would commit publication bias / p-hacking
└─ Hiring recommendation: STRONG PASS (incompatible with research integrity)
```

#### Worked Example: Evaluating Real Answers

**Candidate A (Phone Screen Response)**:
> "If self-healing doesn't work, that's the finding. I'd first verify the experiment is sound: Did we actually test what we think we tested? Did we measure correctly? Once I'm confident in the methodology, I'd dig into why. Does it fail in all scenarios or just some? What conditions trigger failure? That refutation is actually more valuable than confirmation because it bounds the claim."

**Scoring A**:
- Accepts null result as valid ✓
- Verifies methodology rigorously ✓
- Reframes as learning opportunity ✓✓
- Asks constructive follow-up questions ✓
- Comfortable with limitation-bounding ✓
- **SCORE: 5/5** (Research-grade mindset; exactly what Samstraumr needs)

---

**Candidate B (Phone Screen Response)**:
> "Hmm, that would be concerning. I'd want to understand why. Maybe we need to adjust the architecture? Or try different conditions? We should keep experimenting until we find conditions where it works."

**Scoring B**:
- Defensive posture toward failure ✗
- Suggests changing hypothesis rather than exploring ✗
- Implies continuing until positive result (p-hacking) ✗
- Lacks genuine curiosity about refutation ✗
- **SCORE: 2/5** (Hypothesis-attached; problematic for research integrity)

---

### Question 4: Availability & Timeline (Weight: 15%)

**Full Question**: "This is a 20-week commitment starting Feb 17. Do you have conflicts with that timeline? What's your typical weekly hour availability?"

**Why We Ask**: Feb 17 kickoff is immovable. Must confirm candidate can deliver 40 hrs/week minimum + flexible for gate meetings.

#### Rubric: 1-5 Scale

```
SCORE 5 (Fully Committed):
├─ Candidate: "I can drop everything for this. It's a priority."
├─ Shows: Full-time availability; flexibility for extra hours during critical weeks
├─ Confidence: No conflicts; can start Monday Feb 17
└─ Hiring recommendation: HIGHEST RELIABILITY

SCORE 4 (Committed with Flexibility):
├─ Candidate: "I'm full-time available. Some weeks might need 45-50 hours during gates."
├─ Shows: Full-time, understands intensity spikes
├─ Confidence: Clear yes; no hidden conflicts
└─ Hiring recommendation: STRONG (reliable)

SCORE 3 (Available but with Constraints):
├─ Candidate: "I can do it but I have [X] commitment. Can work around it."
├─ Examples: Teaching obligation (4h/week), conference trip (Mar 15-17), family commitment
├─ Shows: Can make it work but with trade-offs
├─ Confidence: Probably fine; may need to adjust other commitments
└─ Hiring recommendation: ACCEPTABLE (get commitment in writing)

SCORE 2 (Concerned About Availability):
├─ Candidate: "I think so? I'd need to check my calendar."
├─ Shows: Uncertain; potential conflicts not evaluated
├─ Confidence: RED FLAG; they haven't thought this through
└─ Hiring recommendation: RISKY (high likelihood of mid-project conflicts)

SCORE 1 (Unavailable or Partial):
├─ Candidate: "I can only do 20 hours/week" OR "I have a conflict Mar 15-Apr 15"
├─ Shows: Insufficient availability
├─ Confidence: Cannot meet project requirements
└─ Hiring recommendation: STRONG PASS (not viable)
```

#### Worked Example: Evaluating Real Answers

**Candidate A (Phone Screen Response)**:
> "Yes, I can commit to this. I'm currently between roles, so I'm available full-time. I understand there will be intensive weeks, and I'm prepared for that. I can start Monday, Feb 17 with no issues."

**Scoring A**:
- Clear yes, no conflicts ✓
- Between roles = genuine full-time availability ✓
- Understands intensity ✓
- Immediate start capability ✓
- **SCORE: 5/5** (Fully committed; no red flags)

---

**Candidate B (Phone Screen Response)**:
> "I think so? I'd have to check if I have any other projects scheduled. Also, my partner and I might travel in April. But I can probably make it work around my consulting gigs."

**Scoring B**:
- Uncertain ("I think so?") ✗
- Hasn't checked calendar ✗
- Multiple potential conflicts (April travel, consulting) ✗
- Implies juggling other projects ✗
- **SCORE: 2/5** (Concerned about availability; risky)

---

### Question 5: Technical Analysis Exercise (Weight: 15%)

**Format**: Provide a flawed A/B test analysis (p-hacking, confounds, small sample size); ask candidate to identify and fix.

**Material to Provide** (prepared in advance):

```
SCENARIO: Company claims "Feature X increases user engagement by 25%"

FLAWED ANALYSIS PROVIDED:
├─ Sample size: n=50 per group (A and B)
├─ Metric: "Engagement" (undefined; mix of clicks, time-on-page, shares)
├─ Statistical test: t-test with p-value = 0.042 (just barely significant!)
├─ Report conclusion: "Feature X is statistically significant (p=0.042). Recommend ship."
├─ Issue 1: Multiple comparisons (they tested 5 different features; reported only the significant one)
├─ Issue 2: Small sample size (n=50 lacks power for 25% effect size)
├─ Issue 3: Undefined metric (mixing 3 signals into 1; inflates effect)
├─ Issue 4: p=0.042 is just barely sig; effect size not reported

CANDIDATE TASK (20 min):
├─ Identify all issues (issues 1-4 listed above)
├─ Explain why each is a problem
├─ Propose how to fix the analysis
├─ Recommend decision to leadership
```

#### Rubric: 1-5 Scale

```
SCORE 5 (Expert Analysis):
├─ Identifies all 4 flaws correctly
├─ Explains multiple comparisons problem (p-hacking)
├─ Notes sample size inadequacy (power analysis)
├─ Proposes clear methodology: pre-register, single metric, adequate sample, report effect size
├─ Recommendation to leadership: "Inconclusive. Recommend retest with proper sample size."
└─ Hiring recommendation: ELITE ANALYST

SCORE 4 (Strong Analysis):
├─ Identifies 3 of 4 flaws correctly
├─ Explains p-hacking and small sample size
├─ Proposes improvements: bigger sample, single metric, report effect size
├─ Recommendation: "Results inconclusive; need larger sample."
└─ Hiring recommendation: STRONG HIRE

SCORE 3 (Adequate Analysis):
├─ Identifies 2 of 4 flaws
├─ Mentions sample size or p-hacking but not both
├─ Proposes some improvements but may miss key issues
├─ Recommendation: "Results are borderline; should be careful before shipping."
└─ Hiring recommendation: ACCEPTABLE

SCORE 2 (Weak Analysis):
├─ Identifies 1 flaw only
├─ Misses p-hacking, confounding, sample size issues
├─ Proposes vague improvements ("do more testing")
├─ Recommendation: "Looks pretty good; ship it?" (doesn't catch major issues)
└─ Hiring recommendation: BELOW BAR

SCORE 1 (Inadequate):
├─ Misses all major flaws
├─ Concludes "Feature is significant; ship it"
├─ Doesn't understand statistical limitations
└─ Hiring recommendation: STRONG PASS
```

#### Worked Example: Evaluating Real Answers

**Candidate A Response**:
> "Several problems here. First, if they tested 5 features and only reported the one with p<0.05, that's p-hacking. The real alpha should be 0.01 to correct for multiple comparisons. Second, n=50 is small for detecting a 25% effect. What's the power? Probably 60-70%? You want 80%+ power, so you'd need n=150+ per group. Third, 'engagement' is undefined. Mixing clicks, time-on-page, and shares into one metric waters down the signal. I'd measure each separately and pre-register which metric is primary before the test. Finally, p=0.042 is just barely significant. The effect size (25%) is based on this small, underpowered sample. Confidence interval probably includes much smaller effects. My recommendation: Inconclusive. Run a proper test with pre-registered metric, adequate power, single hypothesis. Don't ship on this evidence."

**Scoring A**:
- Identifies p-hacking (multiple comparisons) ✓
- Identifies sample size problem + power concept ✓✓
- Identifies metric ambiguity ✓
- Mentions effect size + CI ✓✓
- Clear recommendation with reasoning ✓
- **SCORE: 5/5** (Expert; catches subtle issues)

---

**Candidate B Response**:
> "The sample size seems small. Also, p=0.042 is kind of close to 0.05. I'm not sure what the issue is, but I'd probably recommend running another test to be safe."

**Scoring B**:
- Notes sample size vaguely ✗
- Mentions p=0.042 is close but doesn't articulate why ✗
- Misses p-hacking, metric confusion, power analysis ✗✗
- Vague recommendation ✗
- **SCORE: 2/5** (Weak; misses major flaws)

---

### Question 6: Experiment Design (Open-Ended) (Weight: 10%)

**Full Question**: "We claim our system recovers from single-point failures in <5 minutes. Design an experiment to validate or refute this."

**What We're Assessing**: Can they design rigorous experiments from scratch?

#### Rubric: 1-5 Scale

```
SCORE 5 (Research Design Excellence):
├─ Strong hypothesis clarity: "Recovery time < 5 min" (operationalized precisely)
├─ Methodology: Inject faults systematically; measure recovery latency from fault injection to system responsive
├─ Controls: Baseline (no failure); multiple failure types (single node, network partition, disk failure)
├─ Sample size: Justifies n=100 trials minimum ("need enough variation to estimate distribution")
├─ Threats to validity: Addresses confounds (clock skew, measurement overhead, warm vs. cold start)
├─ Multi-phase: Phase 1 proof of concept; Phase 2 production validation
├─ Communication: Clearly articulates why each choice matters
└─ Hiring recommendation: ELITE RESEARCHER

SCORE 4 (Strong Design):
├─ Clear hypothesis and metrics
├─ Reasonable methodology (fault injection + latency measurement)
├─ Considers multiple failure scenarios
├─ Sample size adequate (≥30 trials)
├─ Discusses some threats to validity
├─ Clear recommendation for decision-making
└─ Hiring recommendation: STRONG HIRE

SCORE 3 (Adequate Design):
├─ Clear hypothesis
├─ Basic methodology (inject failures, measure recovery)
├─ May oversimplify (single failure type; limited sample)
├─ Sketches some controls but may miss confounds
├─ Sample size vague or small
└─ Hiring recommendation: ACCEPTABLE

SCORE 2 (Weak Design):
├─ Vague hypothesis ("see if recovery works")
├─ Unclear methodology
├─ Minimal controls or confound consideration
├─ No mention of sample size, statistical power
├─ Would struggle to execute experimentally
└─ Hiring recommendation: BELOW BAR

SCORE 1 (Inadequate):
├─ No clear methodology
├─ Doesn't understand fault injection or experimental design
├─ Unfeasible or unscientific approach
└─ Hiring recommendation: STRONG PASS
```

#### Worked Example: Evaluating Real Answers

**Candidate A Response**:
> "I'd design it like this. First, operationalize: 'Recovery time' is the interval from fault injection to first successful response. 'Single-point failure' is [specific types: primary DB down, network partition, storage failure]. Hypothesis: median recovery <5 min across failure types.

> Methodology: Run chaos engineering tool (inject fault), record time to first successful API response, repeat. At least 50 trials per failure type (150 total) to estimate distribution quantiles.

> Controls: Baseline is system without failure to measure normal latency. Measure recovery time as: (first_success_time - fault_injection_time). Also measure mean and 95th percentile (not just median).

> Threats to validity: Clock skew (use synchronized clocks), measurement overhead (run measurements in parallel process to avoid biasing latency), warm vs. cold start (repeat after system is warm). Also, production vs. staging (current plan tests in staging; need production validation in Phase 2).

> Success threshold: Median recovery < 5 min AND 95th percentile < 10 min (accommodate tail latency).

> If it fails: Diagnose which failure type is slowest; focus optimizations there."

**Scoring A**:
- Hypothesis is operationalized (specific failure types, metrics) ✓✓
- Methodology is clear and executable ✓
- Controls are well-thought (baseline, multiple metrics) ✓
- Sample size justified (50 per type) ✓
- Threats to validity explicitly addressed ✓✓
- Multi-phase approach (staging + production) ✓
- Decision logic is clear ✓
- **SCORE: 5/5** (Excellent research design)

---

**Candidate B Response**:
> "I'd run some tests to see if the system recovers. Probably inject some failures and see if it recovers quickly. If it works, great. If not, we'd need to investigate."

**Scoring B**:
- Hypothesis not operationalized ✗
- Methodology is vague ✗
- No mention of sample size, controls, or confounds ✗✗
- No success criteria ✗
- **SCORE: 1-2/5** (Inadequate; lacks experimental rigor)

---

## PART 3: INTER-RATER RELIABILITY PROTOCOL

### If Multiple Interviewers (Eric + Collaborator)

**Protocol**:
1. Each interviewer scores independently (no discussion during screen)
2. After all screens complete, compare scores
3. If difference >0.5 points on any criterion: Discuss and reconcile
4. Final score = average of both raters (or dominant rater if one is expert in domain)

**Example Reconciliation**:
```
Interviewer 1 (Eric): Comfort with Ambiguity = 4.0 (confident candidate likes exploratory research)
Interviewer 2 (Collaborator): Comfort with Ambiguity = 3.5 (saw some defensiveness when hypothesis questioned)

Reconciliation conversation:
├─ Eric: "I thought the candidate reframed null results well. Said 'That's the finding.'"
├─ Collaborator: "I agree they said it, but tone seemed resigned, not excited."
├─ Decision: SPLIT THE DIFFERENCE = 3.75 (round to 3.8 for simplicity)
├─ Note in scorecard: "Adequate but not enthusiastic about negative results"
└─ Implication: Not top-tier researcher (4.0+ needed for elite). Acceptable for standard role.
```

---

## PART 4: DECISION THRESHOLDS WITH CONTEXT

### Primary Hiring Decision

```
SCORE 4.5-5.0: STRONG OFFER
├─ Action: Make offer within 1 hour of completing screen
├─ Terms: "$3,000/week × 20 weeks = $60,000"
├─ Counter-offer handling: If candidate asks for $3.5K/week, PAY IT
│   └─ Rationale: Top talent at 4.5+ deserves premium to de-risk critical path
├─ Success rate: 90%+ acceptance (candidate is excited about role)
└─ Timeline: Signed offer by EOD same day

SCORE 4.0-4.4: OFFER
├─ Action: Make offer by next morning
├─ Terms: "$3,000/week × 20 weeks = $60,000"
├─ Counter-offer handling: If candidate asks for $3.5K/week, DECISION:
│   ├─ If score 4.3-4.4: PAY IT (justify cost with quality)
│   ├─ If score 4.0-4.2: PASS (good candidate but not premium quality; activate backup)
├─ Success rate: 75-80% acceptance
└─ Timeline: Signed offer by EOD next day

SCORE 3.5-3.9: MAYBE
├─ Action: Get reference checks before deciding
│   └─ Call 2 references; ask: "How does [candidate] handle ambiguity? Revisions? Long projects?"
├─ Decision logic:
│   ├─ References upgrade confidence: Make offer
│   ├─ References confirm score: Offer if no better candidates available
│   ├─ References downgrade: PASS and activate backup
├─ Counter-offer handling: If candidate asks >$3K/week, PASS (don't overpay for marginal)
├─ Success rate: 50-60% acceptance
└─ Timeline: Decision made within 2h of reference calls

SCORE 3.0-3.4: RISKY
├─ Action: Only offer if backup tier unavailable (desperation scenario)
├─ Terms: "$3,000/week × 20 weeks = $60,000" (no negotiation)
├─ Risk: High likelihood of mid-project issues (quality concerns, ambiguity intolerance)
├─ Success rate: 60-70% acceptance, but acceptance ≠ quality
└─ Decision rule: AVOID UNLESS NO OTHER OPTIONS

SCORE <3.0: PASS
├─ Action: Reject immediately; move to next candidate
├─ No counter-offer considered
├─ No references called
└─ Rationale: Quality gap is fundamental; hiring at 2.9 creates future regret
```

---

## PART 5: BONUS CALIBRATION (Fine-Tuning Edge Cases)

### When Two Candidates Score Identically

**Tie-breaker criteria** (in order of priority):

1. **Ambiguity comfort** (weight up if tied): Candidate 1 scored higher on Q3? Hire Candidate 1.
   - Rationale: Null results likely in Samstraumr; ambiguity tolerance matters most.

2. **Publication track record**: If both score 4.0, did Candidate 1 publish papers? Hire Candidate 1.
   - Rationale: Published researchers are better at handling peer review + revision cycles.

3. **Availability**: If both score 4.0, is Candidate 1 available to start Feb 17 without conflicts? Hire Candidate 1.
   - Rationale: Immovable kickoff date; no flexibility.

4. **Negotiation flexibility**: If all else equal, candidate more willing to accept $3K/week?
   - Rationale: Reduces financial risk if either rejects and requires backup hiring.

### Upgrade Path (If New Info Emerges)

**If candidate provides additional reference or portfolio sample after phone screen**:

1. Review material
2. Adjust individual criterion score (not total—preserve phone screen assessment)
3. Recalculate total if individual change >0.3 points
4. Update scorecard with "Updated: [date, reason]"

**Example**:
```
Original Experiment Design score: 3.5 (open-ended question)
New info: Candidate shares published experimental design paper
Revised Experiment Design score: 4.2 (demonstrated in published work)
New total: 3.8 → 4.0 (moves from MAYBE to OFFER threshold)
Action: Make offer (same day if possible; by next morning if late)
```

---

## FINAL SCORECARD TEMPLATE

```
═══════════════════════════════════════════════════════════
    SAMSTRAUMR COLLABORATOR INTERVIEW SCORECARD
═══════════════════════════════════════════════════════════

CANDIDATE: _________________________ | DATE: ______________
INTERVIEWER(S): _________________________ | ROUND: ☐ Phone ☐ Technical

───────────────────────────────────────────────────────────
Q1: BACKGROUND & MOTIVATION (20% weight)
───────────────────────────────────────────────────────────
Question: "Tell me about your experience designing and executing experiments..."

SCORE: __/5
Rubric evidence:
  ☐ 5: Published empirical research
  ☐ 4: 5+ production A/B tests; influenced decisions
  ☐ 3: 2-3 A/B tests; understands methodology
  ☐ 2: Mostly analytical; passive role
  ☐ 1: Theory only; no hands-on

NOTES: _________________________________________________________________

───────────────────────────────────────────────────────────
Q2: PYTHON/R FLUENCY (20% weight)
───────────────────────────────────────────────────────────
Question: "What's your go-to statistical language? Walk me through..."

SCORE: __/5
Rubric evidence:
  ☐ 5: Published code libraries
  ☐ 4: Production-grade code; optimized
  ☐ 3: Functional code; adequate style
  ☐ 2: Basic scripts; copy-paste approach
  ☐ 1: Theory only; can't code

NOTES: _________________________________________________________________

───────────────────────────────────────────────────────────
Q3: COMFORT WITH AMBIGUITY (20% weight)
───────────────────────────────────────────────────────────
Question: "System doesn't self-heal. How do you approach that result?"

SCORE: __/5
Rubric evidence:
  ☐ 5: Reframes as research opportunity; designs follow-up
  ☐ 4: Publishes finding; investigates root cause
  ☐ 3: Reports honestly; maybe defensive
  ☐ 2: Attached to hypothesis; reaches for excuses
  ☐ 1: Dismisses or avoids reporting

NOTES: _________________________________________________________________

───────────────────────────────────────────────────────────
Q4: AVAILABILITY & TIMELINE (15% weight)
───────────────────────────────────────────────────────────
Question: "20-week commitment starting Feb 17. Conflicts? Hours/week?"

SCORE: __/5
Rubric evidence:
  ☐ 5: "I can drop everything for this"
  ☐ 4: Full-time + flexible for gate weeks
  ☐ 3: Available but with constraints (document)
  ☐ 2: Concerned; needs to check calendar
  ☐ 1: Partial or unavailable

CONSTRAINTS IDENTIFIED: ____________________________________________________

───────────────────────────────────────────────────────────
Q5: TECHNICAL ANALYSIS EXERCISE (15% weight)
───────────────────────────────────────────────────────────
Exercise: Critique flawed A/B test analysis

SCORE: __/5
Rubric evidence:
  ☐ 5: Identifies all 4 flaws; proposes clear fix
  ☐ 4: Identifies 3 flaws; solid methodology
  ☐ 3: Identifies 2 flaws; adequate improvements
  ☐ 2: Identifies 1 flaw; vague improvements
  ☐ 1: Misses all major flaws

ISSUES IDENTIFIED: __________________________________________________________

───────────────────────────────────────────────────────────
Q6: EXPERIMENT DESIGN (10% weight)
───────────────────────────────────────────────────────────
Open-ended: Design experiment for recovery time <5 min claim

SCORE: __/5
Rubric evidence:
  ☐ 5: Operationalized hypothesis; controls; power; validity threats
  ☐ 4: Clear methodology; multiple scenarios; sample size justified
  ☐ 3: Basic design; may oversimplify; some controls
  ☐ 2: Vague methodology; minimal controls
  ☐ 1: No clear methodology; unfeasible

DESIGN QUALITY: ______________________________________________________________

═══════════════════════════════════════════════════════════
FINAL SCORING
═══════════════════════════════════════════════════════════

| Criterion | Score | Weight | Weighted |
|-----------|-------|--------|----------|
| Q1: Background (20%) | __/5 | 0.20 | __/1.0 |
| Q2: Fluency (20%) | __/5 | 0.20 | __/1.0 |
| Q3: Ambiguity (20%) | __/5 | 0.20 | __/1.0 |
| Q4: Availability (15%) | __/5 | 0.15 | __/0.75 |
| Q5: Analysis (15%) | __/5 | 0.15 | __/0.75 |
| Q6: Design (10%) | __/5 | 0.10 | __/0.5 |
| **TOTAL** | — | — | **__/5.0** |

═══════════════════════════════════════════════════════════
DECISION THRESHOLD
═══════════════════════════════════════════════════════════

Composite Score: ___/5.0

☐ 4.5-5.0: STRONG OFFER (make offer within 1h)
☐ 4.0-4.4: OFFER (make offer by next morning)
☐ 3.5-3.9: MAYBE (get references; decide within 2h)
☐ 3.0-3.4: RISKY (only hire if no better candidates)
☐ <3.0: PASS (move to next candidate)

═══════════════════════════════════════════════════════════
RECOMMENDATION
═══════════════════════════════════════════════════════════

Decision: ☐ Hire | ☐ Pass | ☐ Hold (for references)

Key Strengths: _______________________________________________________________

Key Concerns: _______________________________________________________________

Reference Check Needed? ☐ Yes | ☐ No

Counter-Offer Guidance:
  ├─ If candidate asks $3.5K/week: ☐ Accept | ☐ Decline
  └─ Rationale: _________________________________________________________

═══════════════════════════════════════════════════════════
INTERVIEWER SIGN-OFF
═══════════════════════════════════════════════════════════

Interviewer: _________________________ | Date: ______________

Signature: _________________________ | Time: ______________
```

---

**READY TO DEPLOY**: Print scorecard before each phone screen. Complete during call (3 min notes per Q). Final scores by EOD same day.

