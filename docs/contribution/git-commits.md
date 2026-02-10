<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->




# Git Commits

## Commit Message Format

All commits should follow the conventional commit format:

```
<type>: <subject>

<body>

<footer>
```

### Types

- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, missing semicolons, etc.) that don't affect code behavior
- `refactor`: Code changes that neither fix bugs nor add features
- `test`: Adding or correcting tests
- `chore`: Changes to build process, auxiliary tools, or libraries

### Examples

```
feat: add new tube lifecycle states
fix: resolve parent-child hierarchy tracking issue
docs: update lifecycle documentation
```

## Preventing Auto-Generated Commit Messages

When using AI assistant tools like Claude Code to generate commit messages:

1. **Do not accept Claude's default signatures**
   - Remove any lines like `Generated with [Claude Code]`
   - Remove any `Co-Authored-By: Claude <noreply@anthropic.com>` lines
2. **Use manual git commit commands**
   - Use `git commit -m "Your message"` directly
   - Avoid using `-t` flag with commit templates when working with AI tools
3. **Set up a git hook**
   - A pre-commit hook is included in this repository that automatically strips Claude signatures
   - You can verify it's active by checking that `.git/hooks/prepare-commit-msg` exists and is executable
   - If missing, you can restore it from the version in `docs/contribution/git-hooks/`

## Git Workflow

1. **Create a feature branch**

   ```bash
   git checkout -b feature/your-feature-name
   ```
2. **Make changes and commit**

   ```bash
   git add .
   git commit -m "feat: implement your feature"
   ```
3. **Push changes and create PR**

   ```bash
   git push -u origin feature/your-feature-name
   ```

## Troubleshooting

If you notice AI tool signatures in your commits:
1. Ensure the prepare-commit-msg hook is active
2. If you need to amend the last commit:

```bash
git commit --amend
```

3. For older commits, you may need to use interactive rebase

   ```bash
   git rebase -i HEAD~3  # Replace 3 with the number of commits to edit
   ```
