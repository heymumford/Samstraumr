# Post-Merge Cleanup Instructions

**Execute these commands AFTER the PR merges to main**

## Branch Deletion Commands

Once the PR is merged and you've verified everything works correctly, delete these 4 redundant remote branches:

### Option 1: Using GitHub Web UI (Recommended)

1. Go to https://github.com/heymumford/Samstraumr/branches
2. Find each branch below and click the trash icon:
   - `claude/phase3-refactoring-014QMV2b1Pnx8qzRNqJn22CC`
   - `copilot/sub-pr-55`
   - `copilot/sub-pr-58`
   - `copilot/sub-pr-88`

### Option 2: Using Git Command Line

```bash
# Delete the duplicate refactoring branch
git push origin --delete claude/phase3-refactoring-014QMV2b1Pnx8qzRNqJn22CC

# Delete superseded Copilot sub-PR branches
git push origin --delete copilot/sub-pr-55
git push origin --delete copilot/sub-pr-58

# Delete orphaned plan branch
git push origin --delete copilot/sub-pr-88
```

### Option 3: Delete All At Once

```bash
git push origin --delete \
  claude/phase3-refactoring-014QMV2b1Pnx8qzRNqJn22CC \
  copilot/sub-pr-55 \
  copilot/sub-pr-58 \
  copilot/sub-pr-88
```

## Cleanup Local References

After deleting remote branches, clean up local tracking references:

```bash
# Fetch with prune to remove stale remote-tracking branches
git fetch --prune

# Optional: Remove local copies if they exist
git branch -D claude/phase3-refactoring-014QMV2b1Pnx8qzRNqJn22CC 2>/dev/null
git branch -D copilot/sub-pr-55 2>/dev/null
git branch -D copilot/sub-pr-58 2>/dev/null
git branch -D copilot/sub-pr-88 2>/dev/null

# Verify cleanup
git branch -a | grep -E "claude|copilot"
```

## Branch Deletion Rationale

| Branch | Reason for Deletion |
|--------|-------------------|
| `claude/phase3-refactoring-014QMV2b1Pnx8qzRNqJn22CC` | **Duplicate:** Identical content to `code-repository-assessment` (0 diff). Same refactoring work saved under different name. |
| `copilot/sub-pr-55` | **Superseded:** exec-maven-plugin update incorporated into `phase2-ci-improvements` via merge commit `5571d5e`. |
| `copilot/sub-pr-58` | **Superseded:** maven-site-plugin + fluido-skin updates incorporated into `phase2-ci-improvements` via merge commit `15cdb7d`. |
| `copilot/sub-pr-88` | **Orphaned:** Contains only 1 commit ("Initial plan") with no implementation. Abandoned work. |

## Verification After Cleanup

Verify the repository is in a clean state:

```bash
# Check remaining branches
git branch -r | grep -v "main"

# Should only see:
# - origin/claude/branch-consolidation-014QMV2b1Pnx8qzRNqJn22CC (can delete after verification)
# - origin/claude/review-merge-branches-01SPom6inm1gU1VUxS2x1AVD (can delete after PR merge)

# Update main
git checkout main
git pull origin main

# Verify all changes are present
ls -la REFACTORING_ASSESSMENT.md
ls -la BRANCH_CONSOLIDATION_SUMMARY.md
ls -la docs/CI_CD_STRATEGY.md
ls -la scripts/local-quality-check.sh
```

## Optional: Delete Integration Branches

After verification, you may also delete these integration branches:

```bash
# Delete the branch-consolidation source (already merged)
git push origin --delete claude/branch-consolidation-014QMV2b1Pnx8qzRNqJn22CC

# Delete the session branch (after PR is merged)
git push origin --delete claude/review-merge-branches-01SPom6inm1gU1VUxS2x1AVD
```

## Final State

After complete cleanup, you should have:

**Remote Branches:**
- `origin/main` (with all consolidated work)

**Local Branches:**
- `main` (tracking origin/main)

**Result:** Clean repository with all work unified, no branch debt.

## Troubleshooting

### If deletion fails with 403 error
This indicates branch protection rules. Use the GitHub Web UI (Option 1) instead.

### If you get "remote ref does not exist"
The branch may already be deleted. Use `git fetch --prune` to update your local view.

### If you want to verify before deletion
```bash
# Check if branch has unique commits
git log main..origin/<branch-name> --oneline

# If output is empty, the branch is fully merged
```

---

**Safety Note:** All these branches are safe to delete because:
1. Their work is fully incorporated in main via this PR
2. No unique commits will be lost
3. Git preserves the merge history and commit SHAs
4. You can always recover via git reflog if needed (within 90 days)
