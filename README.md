# LibVCS4j
[![Build Status](https://travis-ci.org/uni-bremen-agst/libvcs4j.svg?branch=master)](https://travis-ci.org/uni-bremen-agst/libvcs4j)
[![Build status](https://ci.appveyor.com/api/projects/status/qn2vd6h6o3t9wk9e/branch/master?svg=true)](https://ci.appveyor.com/project/msteinbeck/libvcs4j/branch/master)

LibVCS4j is a Java programming library for repository mining with a common API for different version control systems and issue trackers. The library integrates existing software (e.g. JGit) to access repository routines, adds additional features for data analysis, and, ultimately, makes subsequent analysis tools independent from particular repository systems.

### Quickstart

The following listing demonstrates how to iterate through the history of a Git repository:

```java
VCSEngine vcs = VCSEngineBuilder
    .ofGit("https://github.com/amaembo/streamex.git")
    .build();

for (RevisionRange range : vcs) {
    range.getAddedFiles();
    range.getRemovedFiles();
    range.getModifiedFiles();
    range.getRelocatedFiles();
    ...
}
```

You can also process a specific subdirectory and branch:

```java
VCSEngine vcs = VCSEngineBuilder
    .ofGit("https://github.com/amaembo/streamex.git")
    .withRoot("src/main")
    .withBranch("multirelease")
    .build();
```

In order to extract issues referenced in commit messages, you need assign an `ITEngine`:

```java
ITEngine it = ITEngineBuilder
    .ofGithub("https://github.com/amaembo/streamex")
    .build();

VCSEngine vcs = ...
vcs.setITEngine(it);

for (RevisionRange range : vcs) {
    range.getLatestCommit().getIssues();
    ...
}
```

### Project Structure

The library is divided into an API and implementation as well as other subprojects providing additional features (e.g. aggregation of different metrics). The API has no external dependencies and allows you to decouple your analysis tool from particular repository systems. The implementation, on the other hand, provides all features necessary to setup and process different version control systems and issue trackers.
