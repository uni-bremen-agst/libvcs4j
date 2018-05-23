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
    range.getRevision().getFiles();
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

The library is divided into an API and implementation, as well as further submodules providing additional features (e.g. aggregation of different metrics). The API has no external dependencies and defines a common data model that allows to decouple analysis tools from particular repository systems. The implementation, on the other hand, provides the actual version control system engines (`GitEngine`, `HGEngine`, `SVNEngine`, `SingleEngine`), issue tracker engines (`GithubEngine`, `GitlabEngine`), and engine builder (`VCSEngineBuilder` and `ITEngineBuilder`) that are used to configure the repository to process (see Quickstart).

### Supported Repositories

#### Version Control Systems

The following version control systems (and protocols) are supported:

- Git: `file://`, `http(s)://`, `ssh://`, `git@`
- Mercurial: `file://`, `http(s)://`, `ssh://`
- Subversion: `file://`, `http(s)://`, `svn://`, `svn+ssh://`

The `VCSEngineBuilder`, for the sake of convenience, automatically maps regular file paths to the `file://` protocol. For example, a local Mercurial repository may be configured with:

```java
// The path is mapped to 'file:///path/to/repository'.
VCSEngineBuilder.ofHG("/path/to/repository")
```

There is a special engine called `SingleEngine`. It is used to process a local directory or file. When using this engine, a single Revision is generated where all files are reported as *added*.

#### Issue Tracker

The following issue tracker (and authentication mechanisms) are supported:

- Github: anonymous, username/password, token
- Gitlab: token

Note that, due to the server limitations of some providers, extracting issues from an issue tracker may noticeably slow down an analysis (1 -- 2 seconds per request). Hence, enable this feature only if required (see Quickstart). Also, some providers permit a maximum number of requests per day. If exceeded, subsequent requests are ignored.

### Installation

Releases are available at [Maven Central](https://repo1.maven.org/maven2/de/uni-bremen/informatik/st/).

To add the API submodule to your classpath, paste the following snippet into your pom.xml:

```xml
<dependency>
  <groupId>de.uni-bremen.informatik.st</groupId>
  <artifactId>libvcs4j-api</artifactId>
  <version>1.1.2</version>
</dependency>
```

Likewise, the implementation submodule is added as follows:

```xml
<dependency>
  <groupId>de.uni-bremen.informatik.st</groupId>
  <artifactId>libvcs4j</artifactId>
  <version>1.1.1</version>
</dependency>
```
