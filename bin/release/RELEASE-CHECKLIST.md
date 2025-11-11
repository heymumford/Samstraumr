# Samstraumr Release Checklist

This document provides a comprehensive checklist for preparing and publishing a new Samstraumr release.

## Pre-Release Phase

### Code Preparation

- [ ] Ensure all planned features for this release are complete
- [ ] Verify all critical bugs have been fixed and closed
- [ ] Run static code analysis (PMD, SpotBugs, Checkstyle)
- [ ] Fix any code quality issues
- [ ] Update any deprecated APIs with appropriate warnings
- [ ] Ensure code is compatible with target Java versions (21)

### Testing

- [ ] Run the full test suite:
  ```bash
  ./bin/test/run-full-test-suite
  ```
- [ ] Verify test coverage meets minimum threshold (80%):
  ```bash
  ./bin/test/analyze-test-coverage
  ```
- [ ] Run integration tests
- [ ] Perform manual validation of core functionality
- [ ] Test with multiple Java versions if applicable

### Documentation

- [ ] Update version references in documentation:
  - README.md
  - CLAUDE.md
  - docs/reference/* files
- [ ] Create or update release notes:
  - `docs/reference/release/release-X.Y.Z.md`
- [ ] Update changelog with all significant changes:
  - `docs/reference/release/changelog.md` or `CHANGELOG.md`
- [ ] Verify documentation site builds correctly
- [ ] Ensure all new features are documented
- [ ] Update migration guides if applicable
- [ ] Update any tutorials or examples

### Scientific Accessibility

- [ ] Verify all user-facing documentation follows scientific documentation standards:
  - Use scientific analogies instead of technical jargon
  - Include domain-specific examples from scientific fields
  - Provide visual explanations for complex concepts
  - Assume minimal programming knowledge
- [ ] Review documentation with scientists from target domains:
  - Biology/genomics use cases
  - Physics/simulation use cases
  - Environmental science/monitoring use cases
- [ ] Check guides and tutorials for accessibility:
  - Getting started guide
  - Core concepts documentation
  - Configuration examples
  - API documentation
- [ ] Create or update domain-specific examples for new features
- [ ] Ensure error messages and troubleshooting guides are accessible
- [ ] Verify visual aids complement text explanations

### Build & Artifacts

- [ ] Verify the project builds cleanly:
  ```bash
  ./bin/s8r build
  ```
- [ ] Check for any build warnings
- [ ] Ensure all Maven POM files have correct versions
- [ ] Validate Maven dependencies are up to date
- [ ] Check for any dependency vulnerabilities:
  ```bash
  mvn dependency:analyze
  mvn org.owasp:dependency-check-maven:check
  ```

## Release Preparation

### Version Management

- [ ] Update version in version.properties:
  ```bash
  ./bin/s8r-version set X.Y.Z
  ```
- [ ] Verify version is updated across all files:
  ```bash
  ./bin/s8r-version verify
  ```
- [ ] Commit version changes:
  ```bash
  git commit -m "chore: prepare release X.Y.Z"
  ```

### Final Verification

- [ ] Run release preparation verification:
  ```bash
  ./bin/release/prepare-release X.Y.Z
  ```
- [ ] Fix any issues reported by verification
- [ ] Run verification again with fix option:
  ```bash
  ./bin/release/prepare-release --fix X.Y.Z
  ```

## Publishing

### Git & GitHub

- [ ] Create and push Git tag:
  ```bash
  git tag -a vX.Y.Z -m "Release vX.Y.Z"
  git push origin vX.Y.Z
  ```
- [ ] Create GitHub release:
  - Use tag vX.Y.Z
  - Title: "Samstraumr X.Y.Z"
  - Copy content from release-X.Y.Z.md

### Maven Deployment

- [ ] Deploy to Maven repository:
  ```bash
  mvn clean deploy -P release
  ```
- [ ] Verify artifacts are correctly published
- [ ] Check Maven Central or internal repository

### Documentation Deployment

- [ ] Deploy updated documentation site:
  ```bash
  ./bin/s8r-docs generate --deploy
  ```
- [ ] Verify documentation site is accessible

### All-in-One Publishing

- [ ] Run the publish release script:
  ```bash
  ./bin/release/publish-release X.Y.Z
  ```

## Post-Release

### Version Bump

- [ ] Update to next development version:
  ```bash
  ./bin/s8r-version bump minor
  ```
- [ ] Commit version change:
  ```bash
  git commit -m "chore: bump version to next development cycle"
  git push origin main
  ```

### Announcements & Tracking

- [ ] Send announcement to stakeholders
- [ ] Close milestone in issue tracker
- [ ] Create new milestone for next release
- [ ] Update roadmap if applicable

### Clean Up

- [ ] Archive completed tasks
- [ ] Update KANBAN board
- [ ] Check for any leftover TODOs

## Additional Resources

- [Version Management Guide](../docs/reference/version-management.md)
- [Release Process Documentation](../docs/reference/release/readme.md)
- [CI/CD Integration](../docs/architecture/ci-cd-integration.md)