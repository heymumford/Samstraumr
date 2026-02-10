# WRITING_MANIFESTO.md

## Technical Writing Manifesto for the Age of AI (2025+)

**Scope:**  
This manifesto defines a shared standard for **human and agentic (AI) technical writing** in modern software product development.

**Core idea:**  
> **Value ≈ Clarity ÷ Volume**

The job of any spec, story, or prompt is to **maximize clarity** while keeping **volume as low as possible**. AI can generate large amounts of text; this standard exists to ensure that what remains is *useful*.

Use this manifesto when writing or refactoring:

- Feature briefs and specs  
- User stories, tasks, bugs  
- Command / agent documentation (`command*.md`, `AGENTS.md`, `CLAUDE.md`, etc.)  
- Prompts and instructions for AI agents


---

## Principle 1 – Clarity Before Everything

If a smart, busy reader cannot explain it back after one pass, it isn’t done.

- State clearly:
  - **Who** it is for  
  - **What** changes  
  - **Why** it matters  
  - **How** we know it is done
- Prefer short sentences and direct language.
- Remove ambiguity first; polish later.


## Principle 2 – Value = Clarity ÷ Volume

Say the most with the least.

- Core description blocks (story/feature/command summary) should aim for **≤ 4 sentences**.
- Extra explanation belongs in:
  - Appendices  
  - Examples  
  - Linked docs
- Long is not “thorough” by default; it is usually “unfiltered.”


## Principle 3 – Outcome-First, in Domain Language

Lead with **business and user outcomes**, not implementation details.

- First sentence: the **business outcome** in **business terms**.
- Second sentence: the **user-visible behavior** that realizes that outcome.
- Technical decisions are **supporting details**, not the headline.
- Use precise domain language (e.g., *borrower*, *loan file*, *pos*, *underwriting*) instead of generic words (*user*, *data*, *system*) whenever possible.


## Principle 4 – Make It Falsifiable and Measurable

If no one can prove it failed, no one can prove it’s done.

- Acceptance criteria must be:
  - **Testable**  
  - **Observable**  
  - Clear enough to automate or check manually
- Prefer behavioral formats like **Given / When / Then**.
- Avoid vague language:  
  - Weak: “The UI is more intuitive.”  
  - Strong: “80% of new users complete onboarding without tooltips in ≤ 3 minutes.”


## Principle 5 – One Change, One Story (Cohesion)

Each artifact should do one conceptual job well.

- A **story** describes one coherent behavioral change for one primary persona.
- An **epic** describes one coherent business capability.
- If a story mixes unrelated outcomes (e.g., UX tweak + infra overhaul), split it.
- This mirrors *single responsibility* for code, applied to writing.


## Principle 6 – Stable Structure, Flexible Voice

Format is rigid; language is human.

Use consistent sections for specs and stories. For example:

**Command / capability spec**

- Title  
- Outcome (Business)  
- Behavior (User/Agent)  
- Inputs / Parameters  
- Outputs / Side Effects  
- Acceptance Criteria (falsifiable)  
- Examples  
- Risks / Limits (esp. for AI or destructive actions)

**User story / feature**

- Title  
- Outcome (Business)  
- Behavior (User)  
- Acceptance Criteria (falsifiable)  
- Examples  
- Risks / Notes

Do not invent new section labels for each document. Reuse the same skeleton so humans and agents know where to look.


## Principle 7 – Examples Over Abstractions

Concrete stories beat abstract claims.

- For every non-trivial requirement, add at least one **example**.
- For tricky boundaries, add a **counterexample** (“this is out of scope”).
- Use realistic domain data, not lorem ipsum.
- Examples double as:
  - Informal test cases  
  - Shared mental models across product, dev, QA, and agents


## Principle 8 – Human-Led Compression, AI-Led Exploration

AI may explore widely; humans must distill ruthlessly.

Use AI for:

- Brainstorming behaviors, edge cases, risks
- Generating alternative phrasings
- Proposing test scenarios

Use human judgment for:

- Selecting what actually matters
- Compressing to minimal, sufficient text
- Aligning language with domain reality and constraints

Treat large AI outputs as **raw material**, not finished writing.


## Principle 9 – Name Things Like They Matter

Taxonomy is not decoration; it is how the system thinks.

- Maintain a **controlled vocabulary** for:
  - Products, capabilities, workflows
  - Roles/personas
  - Key concepts and entities
- Avoid accidental synonyms. If two names mean the same thing, choose one canonical term and treat others as aliases.
- Use the same domain terms in:
  - Specs  
  - Code  
  - Tests  
  - Metrics  
  - Agent prompts

Language drift creates bugs.


## Principle 10 – Write for the Least-Informed Responsible Reader

Optimize for the person who must say “yes” but does not live in the code.

- Assume a smart reader with:
  - Limited domain context  
  - Limited time
- They should be able to answer quickly:
  - “What will this do?”  
  - “Why do we care?”  
  - “What is the risk if we don’t do it?”
- Provide **1–2 sentences of inline context** when needed instead of sending them through a link maze.


## Principle 11 – Declare Ethics, Risks, and Limits Explicitly (for AI)

In the age of AI, silence about impact is itself a bug.

For AI-related features, commands, or agents:

- Call out:
  - Data sensitivity and privacy constraints  
  - Failure modes (e.g., false positives/negatives, hallucinations)  
  - Hard limits (things the system must never do automatically)
- Example:
  - “This agent may **suggest** loan decisions but must never **approve** them.”
- Treat risk language as part of the spec, not an afterthought.


## Principle 12 – Continuous Refactoring of Language

You refactor code over time; do the same for your writing.

- On a regular cadence, review:
  - Templates  
  - Definitions of Done / Ready  
  - Common phrases and patterns
- Merge overlapping constructs; remove outdated wording.
- Use historical issues, misunderstandings, and rework as input:
  - “What wording or structure caused confusion?”
  - “What ACs proved untestable?”
- Update the style guide accordingly and propagate small improvements.


---

## Practical Checklist

Before considering a spec/story/prompt “done”, ask:

1. **Clarity**
   - Can a smart outsider restate the intent after one pass?

2. **Outcome**
   - Is the business outcome stated first, in domain language?

3. **Falsifiability**
   - Are acceptance criteria testable and observable?

4. **Scope**
   - Does this artifact describe one coherent change?

5. **Compression**
   - Can I remove a sentence or phrase without losing meaning?

6. **Examples**
   - Is there at least one concrete example (and a counterexample if scope is tricky)?

7. **Risks (for AI)**
   - Are privacy, safety, and autonomy limits stated?

If any answer is “no”, refactor the text before shipping it to humans or agents.

---

## Intended Use

- Treat this manifesto as a **reference standard** for:
  - Human authors
  - Agent prompts
  - Automated linting / review agents (e.g., a `/mumford` style enforcer)
- When in doubt, prefer:
  - Fewer words  
  - Clearer outcomes  
  - Stronger tests  
  - More honest risks

Tools will change. The craft of writing clearly and precisely—especially when machines are reading as well as humans—remains non-negotiable.

