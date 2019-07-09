package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Allows to represent a collection of {@link VCSFile} instances as a file
 * system tree with a generic value that may be attached to a file.
 *
 * @param <V>
 * 		The type of the values attached to the files.
 */
public class FSTree<V> {

	/**
	 * This path is used for empty trees.
	 */
	public static final String EMPTY_DIRECTORY = "<empty>";

	/**
	 * This path is used for trees with multiple root nodes.
	 */
	public static final String ROOT_DIRECTORY = "/";

	/**
	 * A simple visitor to process {@link FSTree} instances.
	 *
	 * @param <E>
	 * 		The type of the values attached to the files.
	 */
	public static class Visitor<E> {

		/**
		 * Delegates {@code pTree} to {@link #visitDirectory(FSTree)} or
		 * {@link #visitFile(FSTree)} depending on whether {@code pTree}
		 * represents a directory or a file.
		 *
		 * @param pTree
		 * 		The tree to visit and delegate.
		 */
		public void visit(final FSTree<E> pTree) {
			if (pTree.isDirectory()) {
				visitDirectory(pTree);
			} else {
				visitFile(pTree);
			}
		}

		/**
		 * Visits the given directory.
		 *
		 * @param pDirectory
		 * 		The directory to visit.
		 */
		protected void visitDirectory(final FSTree<E> pDirectory) {
			assert pDirectory.nodes != null;
			pDirectory.nodes.forEach(this::visit);
		}

		/**
		 * Visits the given file.
		 *
		 * @param pFile
		 * 		The tree containing the file to visit.
		 */
		protected void visitFile(final FSTree<E> pFile) {
			visitFile(pFile.file);
		}

		/**
		 * Visits the given file.
		 *
		 * @param pFile
		 * 		The file to visit.
		 */
		protected void visitFile(final VCSFile pFile) {
			// Do nothing.
		}
	}

	/**
	 * The parent of a tree. Is {@code null} for the root node.
	 */
	private final FSTree<V> parent;

	/**
	 * The relative path of the referenced file (if {@link #file} is present)
	 * or directory (if {@link #nodes} is present). The path is relative to
	 * {@link VCSEngine#getRoot()}, except for the root node which may use
	 * {@link #ROOT_DIRECTORY} in case of a tree with multiple root nodes or
	 * {@link #EMPTY_DIRECTORY} in case of an "empty" tree.
	 */
	private final String path;

	/**
	 * The referenced file. Is {@code null} if {@link #nodes} is present.
	 */
	private final VCSFile file;

	/**
	 * The value attached to {@link #file}. May be {@code null}.
	 */
	private final V value;

	/**
	 * The referenced sub files and directories. Is {@code null} if
	 * {@link #file} is present.
	 */
	private final List<FSTree<V>> nodes;

	/**
	 * Is used to calculate the value of a directory by aggregating the values
	 * of all sub files and directories.
	 */
	private final BinaryOperator<V> aggregator;

	/**
	 * Creates a file with given parent, {@link VCSFile}, and value function.
	 *
	 * @param pParent
	 * 		The parent of the file to create. Pass {@code null} for root nodes.
	 * @param pFile
	 * 		The referenced {@link VCSFile} instance.
	 * @param pValueOf
	 * 		The function that is used to map a file to its value. The function
	 * 		may return {@code null}.
	 * @throws NullPointerException
	 *      If {@code pFile} or {@code pValueOf} is {@code null}.
	 */
	private FSTree(final FSTree<V> pParent, final VCSFile pFile,
			final Function<VCSFile, V> pValueOf) {
		parent = pParent;
		file = Validate.notNull(pFile);
		path = file.toRelativePath().toString();
		value = Validate.notNull(pValueOf).apply(file);
		nodes = null;
		aggregator = null;
	}

	/**
	 * Creates a directory with given parent, relative path, and aggregation
	 * function.
	 *
	 * @param pParent
	 * 		The parent of the directory to create. Pass {@code null} for root
	 * 		nodes.
	 * @param pPath
	 *      The relative path of the directory to create.
	 * @param pAggregator
	 * 		The aggregation function used to calculate the value of a
	 * 		directory. The function must not handle {@code null} values.
	 * @throws NullPointerException
	 *      If {@code pPath} or {@code pAggregator} is {@code null}.
	 */
	private FSTree(final FSTree<V> pParent, final String pPath,
			final BinaryOperator<V> pAggregator) {
		parent = pParent;
		path = Validate.notNull(pPath);
		nodes = new ArrayList<>();
		aggregator = Validate.notNull(pAggregator);
		file = null;
		value = null;
	}

	/**
	 * Creates a tree from the given list of {@link VCSFile} instances.
	 * {@code null} values and duplicates (according to
	 * {@link Object#equals(Object)}) are filtered.
	 *
	 * @param pFiles
	 * 		The files to create the tree from.
	 * @param pValueOf
	 * 		The function that is used to map a file to its value. The function
	 * 		may return {@code null}.
	 * @param pAggregator
	 * 		The aggregation function used to calculate the value of a
	 * 		directory. The function must not handle {@code null} values.
	 * @param <V>
	 *     	The type of the values attached to files.
	 * @return
	 * 		A tree representing the list of files.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	public static <V> FSTree<V> of(final Collection<VCSFile> pFiles,
			final Function<VCSFile, V> pValueOf,
			final BinaryOperator<V> pAggregator)
			throws NullPointerException {
		Validate.notNull(pFiles);
		Validate.notNull(pValueOf);
		Validate.notNull(pAggregator);

		final Set<FSTree<V>> treesWithoutParent = new HashSet<>();
		final Map<String, FSTree<V>> cache = new HashMap<>();
		pFiles.stream().filter(Objects::nonNull).distinct().forEach(f -> {
			// (1) "home/user/file.txt" -> "home", "user", "file.txt"
			final Path relativePath = f.toRelativePath();
			final List<String> parts = new ArrayList<>();
			relativePath.forEach(p -> parts.add(p.toString()));

			// (2) For each directory ("home", "user")...
			FSTree<V> parent = null;
			for (int i = 0; i < parts.size() - 1; i++) {
				final String path = String.join(
						File.separator, parts.subList(0, i + 1));
				FSTree<V> dir = cache.get(path);
				if (dir == null) {
					dir = new FSTree<>(parent, path, pAggregator);
					cache.put(path, dir);
					if (parent != null) {
						parent.nodes.add(dir);
					}
				}
				if (parent == null) {
					treesWithoutParent.add(dir);
				}
				parent = dir;
			}

			// (3) Add file ("file.txt").
			final String relativePathStr = relativePath.toString();
			FSTree<V> file = cache.get(relativePathStr);
			if (file == null) {
				file = new FSTree<>(parent, f, pValueOf);
				cache.put(relativePathStr, file);
				if (parent != null) {
					parent.nodes.add(file);
				}
			}
			if (parent == null) {
				treesWithoutParent.add(file);
			}
		});

		if (treesWithoutParent.isEmpty()) {
			return new FSTree<>(null, EMPTY_DIRECTORY, pAggregator);
		} else if (treesWithoutParent.size() > 1) {
			final FSTree<V> root = new FSTree<>(
					null, ROOT_DIRECTORY, pAggregator);
			root.nodes.addAll(treesWithoutParent);
			return root;
		} else {
			return treesWithoutParent.iterator().next();
		}
	}

	/**
	 * Creates a tree from the given list of {@link VCSFile} instances. The
	 * created tree has no value (see {@link #getValue()}). {@code null} values
	 * and duplicates (according to {@link Object#equals(Object)}) are
	 * filtered.
	 *
	 * @param pFiles
	 * 		The files to create the tree from.
	 * @return
	 * 		A tree representing the list of files.
	 * @throws NullPointerException
	 * 		If {@code pFile} is {@code null}.
	 */
	public static FSTree<Void> of(final Collection<VCSFile> pFiles)
			throws NullPointerException {
		return of(pFiles, f -> null, (v1, v2) -> null);
	}

	/**
	 * Returns the parent of this tree.
	 *
	 * @return
	 * 		The parent of this tree.
	 */
	public Optional<FSTree<V>> getParent() {
		return Optional.ofNullable(parent);
	}

	/**
	 * Returns the relative path of this file (if {@link #getFile()} is
	 * present) or directory (if {@link #getNodes()} is present).
	 *
	 * @return
	 * 		The relative path of this file or directory.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the name of this file (if {@link #getFile()} is present) or
	 * directory (if {@link #getNodes()} is present).
	 *
	 * @return
	 * 		The name of this file or directory.
	 */
	public String getName() {
		return isVirtualRoot()
				? path
				: Paths.get(path).getFileName().toString();
	}

	/**
	 * Returns the referenced {@link VCSFile} if this tree is a file.
	 *
	 * @return
	 * 		The referenced {@link VCSFile}.
	 */
	public Optional<VCSFile> getFile() {
		return Optional.ofNullable(file);
	}

	/**
	 * Either returns the value of this file (if this tree represents a file)
	 * or the aggregated values of all (recursively) sub files (if this tree
	 * represents a directory).
	 *
	 * @return
	 * 		The value of this file or the aggregated values of all
	 * 		(recursively) sub files. If this file has no value (or this
	 * 		directory contains only files without a value), an empty
	 * 		{@link Optional} is returned.
	 */
	public Optional<V> getValue() {
		final List<V> values = new ArrayList<>();
		final Visitor<V> visitor = new Visitor<>() {
			@Override
			protected void visitFile(final FSTree<V> pTree) {
				if (pTree.value != null) {
					values.add(pTree.value);
				}
				super.visitFile(pTree);
			}
		};
		visitor.visit(this);
		if (values.isEmpty()) {
			return Optional.empty();
		} else if (values.size() == 1) {
			return Optional.ofNullable(values.get(0));
		} else {
			return values.stream().reduce(aggregator);
		}
	}

	/**
	 * Returns the sub files and directories of this tree if this tree is a
	 * directory. If this tree is a file, an empty list is returned.
	 *
	 * @return
	 *      The sub files and directories of this tree.
	 */
	public List<FSTree<V>> getNodes() {
		return nodes == null
				? Collections.emptyList()
				: new ArrayList<>(nodes);
	}

	/**
	 * Returns the sub directories of this tree if this tree is a directory. If
	 * this tree is a file, an empty List is returned.
	 *
	 * @return
	 * 		The sub directories of this tree.
	 */
	public List<FSTree<V>> getDirectories() {
		return getNodes().stream()
				.filter(FSTree::isDirectory)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all (recursively) sub directories of this tree if this tree is a
	 * directory. If this tree is a file, an empty list is returned.
	 *
	 * @return
	 * 		All (recursively) sub directories of this tree.
	 */
	public List<FSTree<V>> getAllDirectories() {
		final List<FSTree<V>> directories = new ArrayList<>();
		final Visitor<V> visitor = new Visitor<>() {
			@Override
			protected void visitDirectory(final FSTree<V> pDirectory) {
				if (pDirectory != FSTree.this) {
					directories.add(pDirectory);
				}
				super.visitDirectory(pDirectory);
			}
		};
		visitor.visit(this);
		return directories;
	}

	/**
	 * Returns the sub files of this tree if this tree is a directory. If this
	 * tree is a file, an empty list is returned.
	 *
	 * @return
	 * 		The sub files of this tree.
	 */
	public List<FSTree<V>> getFiles() {
		return getNodes().stream()
				.filter(FSTree::isFile)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all (recursively) sub files of this tree if this tree is a
	 * directory. If this tree is a file, an empty list is returned.
	 *
	 * @return
	 * 		Al (recursively) sub files of this tree.
	 */
	public List<FSTree<V>> getAllFiles() {
		final List<FSTree<V>> files = new ArrayList<>();
		final Visitor<V> visitor = new Visitor<>() {
			@Override
			protected void visitFile(FSTree<V> pFile) {
				if (pFile != FSTree.this) {
					files.add(pFile);
				}
				super.visitFile(pFile);
			}
		};
		visitor.visit(this);
		return files;
	}

	/**
	 * Returns the root of this tree.
	 *
	 * @return
	 * 		The root of this tree.
	 */
	public FSTree<V> getRoot() {
		FSTree<V> root = this;
		Optional<FSTree<V>> op;
		while ((op = root.getParent()).isPresent()) {
			root = op.get();
		}
		return root;
	}

	/**
	 * Navigates to the tree located at {@code path}. Unlike conventional
	 * navigation rules, this method allows to navigate "beyond" a regular
	 * file. That is, for instance, a file's parent may be addressed like this:
	 *
	 *     "src/A.java/.."
	 *
	 * where "A.java" is a regular file and ".." points to its parent. If
	 * {@code path} is empty, {@code this} is returned.
	 *
	 * @param path
	 * 		The relative path of the tree to navigate to.
	 * @return
	 * 		The tree located at {@code path}, if such a tree exists.
	 * @throws NullPointerException
	 * 		If {@code path} is {@code null}.
	 */
	public Optional<FSTree<V>> navigateTo(final String path) {
		Validate.notNull(path);
		if (path.isEmpty()) {
			return Optional.of(this);
		}

		final Queue<String> parts = new ArrayDeque<>();
		parts.addAll(Arrays.asList(path.replace("\\", "/").split("/")));
		final String head = parts.poll();
		final String tail = String.join("/", parts);

		switch (head) {
			case ".":
				return navigateTo(tail);
			case "..":
				return getParent()
						// Navigate to parent and process tail.
						.map(p -> p.navigateTo(tail))
						// Stay here and process tail.
						.orElseGet(() -> navigateTo(tail));
			default:
				if (isFile()) {
					return tail.isEmpty() && hasFileName(head)
							? Optional.of(this) : Optional.empty();
				} else {
					return nodes.stream()
							.filter(n -> n.hasFileName(head))
							.findFirst()
							.map(n -> n.navigateTo(tail))
							.filter(Optional::isPresent)
							.map(Optional::get);
				}
		}
	}

	/**
	 * Compacts this tree such that each node is either a file, or a directory
	 * containing only files or at least two sub directories. If this tree is a
	 * sequence of single directories, an "empty" directory is returned. This
	 * method does not modify this tree or any of its sub nodes, but creates a
	 * flat copy it.
	 *
	 * @return
	 * 		A Tree consisting of files, and directories containing only files
	 * 		or at least two sub directories. An "empty" directory if this tree
	 * 		is a sequence of single directories.
	 */
	public FSTree<V> compact() {
		return compact(this, null);
	}

	/**
	 * Recursively computes the result specified by {@link #compact()}.
	 *
	 * @param pTree
	 * 		The tree to compact.
	 * @param pParent
	 * 		The parent to use.
	 * @param <T>
	 *     	The type of the values attached to the files.
	 * @return
	 * 		The compacted version of {@code pTree}.
	 */
	private static <T> FSTree<T> compact(final FSTree<T> pTree,
			final FSTree<T> pParent) {
		// Compact pTree...
		FSTree<T> current = pTree;
		// ...if it is a directory...
		while (current.isDirectory()
				// ...containing a single directory.
				&& current.nodes.size() == 1
				&& current.nodes.get(0).isDirectory()) {
			current = current.nodes.get(0);
		}
		final String path = current.path;
		final T value = current.value;
		final BinaryOperator<T> aggregator = current.aggregator;
		final FSTree<T> compacted = current.isDirectory()
				? new FSTree<>(pParent, path, aggregator)
				: new FSTree<>(pParent, current.file, f -> value);

		// Compact sub nodes in case of a directory. Ignore empty directories.
		if (compacted.isDirectory()) {
			current.nodes.forEach(node -> {
				// compactedNode may be an empty directory.
				final FSTree<T> compactedNode = compact(node, compacted);
				if (compactedNode.isFile() || !compactedNode.nodes.isEmpty()) {
					compacted.nodes.add(compactedNode);
				}
			});
		}

		return compacted;
	}

	/**
	 * Returns whether the filename of this tree matches the given filename.
	 *
	 * @param pFilename
	 * 		The filename to match with the filename of this tree.
	 * @return
	 * 		{@code true} if the filename of this tree matches
	 * 		{@code pFilename}, {@code false} otherwise.
	 */
	private boolean hasFileName(final String pFilename) {
		return getName().equals(pFilename);
	}

	/**
	 * Returns whether this tree is a file.
	 *
	 * @return
	 * 		{@code true} if this tree is a file, {@code false} otherwise.
	 */
	public boolean isFile() {
		return file != null;
	}

	/**
	 * Returns whether this tree is a directory.
	 *
	 * @return
	 * 		{@code true} if this tree is a directory, {@code false} otherwise.
	 */
	public boolean isDirectory() {
		return file == null;
	}

	/**
	 * Returns whether this tree is a root node.
	 *
	 * @return
	 * 		{@code true} if this tree is a root node, {@code false} otherwise.
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * Returns whether this tree is a virtual root directory that has been
	 * created to cover multiple root nodes.
	 *
	 * @return
	 * 		{@code true} if this tree is a virtual root directory,
	 * 		{@code false} otherwise.
	 */
	public boolean isVirtualRoot() {
		return path.equals(EMPTY_DIRECTORY) || path.equals(ROOT_DIRECTORY);
	}
}
