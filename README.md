# LibVCS4j
![Build Status](https://github.com/uni-bremen-agst/libvcs4j/actions/workflows/ci.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=uni-bremen-agst_libvcs4j&metric=alert_status)](https://sonarcloud.io/dashboard?id=uni-bremen-agst_libvcs4j)

LibVCS4j is a Java programming library for repository mining with a common API for different version control systems and issue trackers. The library integrates existing software (e.g. JGit) to access repository routines, adds additional features for data analysis, and, ultimately, makes subsequent analysis tools independent from particular repository systems.

|                | Package       | Javadoc       | Description | License |
| -------------- | ------------- | ------------- | ----------- | ------- |
| API            | [![Maven Central](https://img.shields.io/maven-central/v/de.uni-bremen.informatik.st/libvcs4j-api.svg)](https://maven-badges.herokuapp.com/maven-central/de.uni-bremen.informatik.st/libvcs4j-api) | [![Javadocs](https://www.javadoc.io/badge/de.uni-bremen.informatik.st/libvcs4j-api.svg)](https://www.javadoc.io/doc/de.uni-bremen.informatik.st/libvcs4j-api) | Defines the common data model. | MIT |
| Implementation | [![Maven Central](https://img.shields.io/maven-central/v/de.uni-bremen.informatik.st/libvcs4j.svg)](https://maven-badges.herokuapp.com/maven-central/de.uni-bremen.informatik.st/libvcs4j) | [![Javadocs](https://www.javadoc.io/badge/de.uni-bremen.informatik.st/libvcs4j.svg)](https://www.javadoc.io/doc/de.uni-bremen.informatik.st/libvcs4j) | Implemenations the API. | GNU GPLv3 |
| Tools | [![Maven Central](https://img.shields.io/maven-central/v/de.uni-bremen.informatik.st/libvcs4j-tools.svg)](https://maven-badges.herokuapp.com/maven-central/de.uni-bremen.informatik.st/libvcs4j-tools) | [![Javadocs](https://www.javadoc.io/badge/de.uni-bremen.informatik.st/libvcs4j-tools.svg)](https://www.javadoc.io/doc/de.uni-bremen.informatik.st/libvcs4j-tools) | Additional tools. | GNU LGPLv3 |

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

In order to extract issues referenced in commit messages, you need to assign an `ITEngine`:

```java
ITEngine it = ITEngineBuilder
    .ofGithub("https://github.com/amaembo/streamex")
    .build();

VCSEngine vcs = ...
vcs.setITEngine(it);

for (RevisionRange range : vcs) {
    // Returns an empty list if no ITEngine is assigned to `vcs`.
    range.getLatestCommit().getIssues();
    ...
}
```

While processing a repository, LibVCS4j not only generates metadata such as file change information, but also allows to access the files of the currently checked out revision:

```java
VCSEngine vcs = ...

for (RevisionRange range : vcs) {
    // Path to the root of the currently checked out revivion.
    range.getCurrent().getOutput();

    // Returns the files of the currenlty checked out revision as list.
    range.getCurrent().getFiles();
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

The library is divided into an API and implementation, as well as further submodules that are supposed to provide additional features (e.g. aggregation of different metrics). The API has no external dependencies and defines a common data model that allows to decouple analysis tools from particular repository systems. The implementation, on the other hand, provides the actual version control system engines (`GitEngine`, `HGEngine`, `SVNEngine`, `SingleEngine`), issue tracker engines (`GithubEngine`, `GitlabEngine`), and engine builder (`VCSEngineBuilder` and `ITEngineBuilder`).

### Data Model

The following UML diagram depicts the data model defined by the API submodule. For the sake of clarity, the modifier of the attributes and methods are not shown since they are public anyway (in fact attributes are not public, but can be accessed with corresponding getter methods which, in turn, are public). Furthermore, all attributes are readonly.

![Data Model](res/model.svg)

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

There is a special engine called `SingleEngine`. It is used to process a local directory or file. When using this engine, a single revision is generated with all files being reported as *added*.

#### Issue Tracker

The following issue tracker (and authentication mechanisms) are supported:

- Github: anonymous, username/password, token
- Gitlab: token

Note that, due to the server limitations of some providers, extracting issues from an issue tracker may noticeably slow down an analysis (1 -- 2 seconds per request). Hence, it is recommended to enable this feature only if required (see Quickstart). Also, some providers permit only a certain number of requests per day. If exceeded, subsequent requests are ignored.

### Installation

Releases are available at [Maven Central](https://repo1.maven.org/maven2/de/uni-bremen/informatik/st/).
