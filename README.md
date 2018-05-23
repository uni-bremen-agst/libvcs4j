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
    // Returns an empty list if no ITEngine has been assigned to `vcs`.
    range.getLatestCommit().getIssues();
    ...
}
```

While processing a repository, LibVCS4j not only generates different metadata such as file change information, but also allows to access the files of the currently checked out revision:

```java
VCSEngine vcs = ...

for (RevisionRange range : vcs) {
    // Path to the root of the currently checked out revivion.
    range.getRevision().getOutput();

    // Returns the files of the currenlty checked out revision as list.
    range.getFiles();
}
```

If required, the target directory (i.e. the SVN working copy or the Git/Mercurial clone directory) can be configured as follows:

```java
VCSEngine vcs = VCSEngineBuilder
    .ofGit("https://github.com/amaembo/streamex.git")
    .withTarget("path/to/clone/directory")
    .build();
```
If no target directory is specified, a temporary directory is created (and deleted using a [shutdown hook](https://docs.oracle.com/javase/8/docs/api/java/lang/Runtime.html#addShutdownHook-java.lang.Thread-)).

### Project Structure

The library is divided into an API and implementation as well as other subprojects providing additional features (e.g. aggregation of different metrics). The API has no external dependencies and allows you to decouple your analysis tool from particular repository systems. The implementation, on the other hand, provides all features necessary to setup and process different version control systems and issue trackers.
