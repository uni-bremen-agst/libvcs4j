package de.unibremen.st.libvcs4j;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Allows to represent a collection of {@link VCSFile} instances as a file
 * system tree.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FSTree {

	/**
	 * The relative path (relative to {@link VCSEngine#getRoot()}) of the
	 * referenced file (if {@link #file} is present) or directory (if
	 * {@link #nodes} is present).
	 */
	private final String path;

	/**
	 * Path of the referenced file. Is {@code null} if {@link #nodes} is
	 * present.
	 */
	private VCSFile file = null;

	/**
	 * The referenced sub files and directories. Is {@code null} if
	 * {@link #file} is present.
	 */
	private List<FSTree> nodes = null;

	/**
	 * Creates a FSTree with given relative path.
	 *
	 * @param pPath
	 *      The relative path of the file or directory.
	 * @throws NullPointerException
	 *      If {@code pPath} is {@code null}.
	 */
	private FSTree(final String pPath) {
		if (pPath == null) {
			throw new NullPointerException();
		}
		path = pPath;
	}

	/**
	 * Creates a new tree of the given files.
	 *
	 * @param pFiles
	 *      The files to create the tree from.
	 * @return
	 *      The generated tree.
	 * @throws NullPointerException
	 *      If {@code pFiles} is {@code null}.
	 * @throws IllegalArgumentException
	 *      If {@code pFiles} contains {@code null} values.
	 */
	public static FSTree of(final Collection<VCSFile> pFiles) {
		if (pFiles == null) {
			throw new NullPointerException();
		} else if (pFiles.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException();
		}

		final Set<FSTree> dirsWithoutParent = new HashSet<>();
		final Map<String, FSTree> cache = new HashMap<>();
		pFiles.forEach(f -> {
			// (1) "home/user/file.txt" -> "home", "user", "file.txt"
			List<String> parts = new ArrayList<>();
			f.toRelativePath().forEach(p -> parts.add(p.toString()));

			// (2) For each directory ("home", "user")...
			FSTree parent = null;
			for (int i = 0; i < parts.size() - 1; i++) {
				String path = String.join(File.separator,
						parts.subList(0, i + 1));
				FSTree dir = cache.get(path);
				if (dir == null) {
					dir = new FSTree(path);
					cache.put(path, dir);
					if (parent != null) {
						if (parent.nodes == null) {
							parent.nodes = new ArrayList<>();
						}
						parent.nodes.add(dir);
					}
				}
				if (parent == null) {
					dirsWithoutParent.add(dir);
				}
				parent = dir;
			}

			// (3) Add file ("file.txt").
			final FSTree file = cache.computeIfAbsent(
					f.getRelativePath(), FSTree::new);
			file.file = f;
			if (parent != null) {
				if (parent.nodes == null) {
					parent.nodes = new ArrayList<>();
				}
				parent.nodes.add(file);
			}
		});

		if (dirsWithoutParent.isEmpty()) {
			return new FSTree("");
		} else if (dirsWithoutParent.size() > 1) {
			final FSTree root = new FSTree("/");
			root.nodes = new ArrayList<>();
			root.nodes.addAll(dirsWithoutParent);
			return root;
		} else {
			return dirsWithoutParent.iterator().next();
		}
	}

	/**
	 * Returns the relative path (relative to {@link VCSEngine#getRoot()}) of
	 * this file (if {@link #getFile()} is present) or directory (if
	 * {@link #getNodes()} is present).
	 *
	 * @return
	 *      The relative path of this file or directory.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the referenced {@link VCSFile} if this tree is a file.
	 *
	 * @return
	 *      The referenced {@link VCSFile}.
	 */
	public Optional<VCSFile> getFile() {
		return Optional.ofNullable(file);
	}

	/**
	 * Returns the sub files and directories if this tree is a directory.
	 *
	 * @return
	 *      The sub files and directories of this tree.
	 */
	public Optional<List<FSTree>> getNodes() {
		return Optional.ofNullable(nodes).map(ArrayList::new);
	}

	/**
	 * Returns all files of this tree in pre-order.
	 *
	 * @return
	 *      All files of this tree in pre-order.
	 */
	public List<VCSFile> getFilesPreOrder() {
		final List<VCSFile> files = new ArrayList<>();
		getFilesPreOrder(files, true);
		return files;
	}

	/**
	 * Returns all files of this tree in post-order.
	 *
	 * @return
	 *      All files of this tree in post-order.
	 */
	public List<VCSFile> getFilesPostOrder() {
		final List<VCSFile> files = new ArrayList<>();
		getFilesPreOrder(files, false);
		return files;
	}

	private void getFilesPreOrder(
			final List<VCSFile> pAccumulator,
			final boolean pPreOrder) {
		if (pPreOrder && file != null) {
			pAccumulator.add(file);
		}
		if (nodes != null) {
			nodes.forEach(n -> n.getFilesPreOrder(pAccumulator, pPreOrder));
		}
		if (!pPreOrder && file != null) {
			pAccumulator.add(file);
		}
	}

	/**
	 * Aggregates and returns the size of all files of this tree.
	 *
	 * @return
	 *      The aggregated size of all files of this tree.
	 * @throws IOException
	 *      Of any of the processed files throws an {@link IOException} (see
	 *      {@link VCSFile#computeSize()}).
	 */
	public Size computeSize() throws IOException {
		final List<Size> sizes = new ArrayList<>();
		for (final VCSFile file : getFilesPreOrder()) {
			final Optional<Size> size = file.computeSize();
			size.ifPresent(sizes::add);
		}
		return sizes.parallelStream().reduce((s1, s2) -> new Size() {
			@Override
			public int getLOC() {
				return s1.getLOC() + s2.getLOC();
			}

			@Override
			public int getSLOC() {
				return s1.getSLOC() + s2.getSLOC();
			}

			@Override
			public int getCLOC() {
				return s1.getCLOC() + s2.getCLOC();
			}

			@Override
			public int getNOT() {
				return s1.getNOT() + s2.getNOT();
			}

			@Override
			public int getSNOT() {
				return s1.getSNOT() + s2.getSNOT();
			}

			@Override
			public int getCNOT() {
				return s1.getCNOT() + s2.getCNOT();
			}
		}).orElse(new Size() {
			@Override
			public int getLOC() {
				return 0;
			}

			@Override
			public int getSLOC() {
				return 0;
			}

			@Override
			public int getCLOC() {
				return 0;
			}

			@Override
			public int getNOT() {
				return 0;
			}

			@Override
			public int getSNOT() {
				return 0;
			}

			@Override
			public int getCNOT() {
				return 0;
			}
		});
	}
}
