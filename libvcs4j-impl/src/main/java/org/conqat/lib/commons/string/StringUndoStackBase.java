/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.commons.string;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.algo.Diff;
import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Base class for an undo stack using a string as the underlying model.
 * 
 * Please refer to the test case for a demonstration and further explanation of
 * this class.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 47169 $
 * @ConQAT.Rating GREEN Hash: 0FE4E0EB4C467F47DB6AE8193C10532B
 */
public abstract class StringUndoStackBase {

	/** The stack of versions. */
	private final List<Delta<String>> deltas = new ArrayList<>();

	/** The current string. */
	private String currentVersion;

	/** The index of the currently used version/delta. */
	private int currentVersionIndex = -1;

	/** The last position used for saving. */
	private int savePosition = -1;

	/** Constructor. */
	protected StringUndoStackBase(String initialString) {
		currentVersion = initialString;
	}

	/** Returns whether undo is possible. */
	public boolean canUndo() {
		return currentVersionIndex >= 0;
	}

	/** Performs one undo step. */
	public void undo() {
		CCSMPre.isTrue(canUndo(), "Must be allowed to undo!");
		currentVersion = join(deltas.get(currentVersionIndex--).backwardPatch(
				split(currentVersion)));
		setModelFromString(currentVersion);
		fireStackChanged();
	}

	/** Returns whether redo is possible. */
	public boolean canRedo() {
		return currentVersionIndex + 1 < deltas.size();
	}

	/** Performs one redo step. */
	public void redo() {
		CCSMPre.isTrue(canRedo(), "Must be allowed to redo!");
		currentVersion = join(deltas.get(++currentVersionIndex).forwardPatch(
				split(currentVersion)));
		setModelFromString(currentVersion);
		fireStackChanged();
	}

	/** Returns whether something changed compared to the last safe. */
	public boolean isDirty() {
		return currentVersionIndex != savePosition;
	}

	/** Mark the current position as saved (affects dirty calculation). */
	public void doSave() {
		savePosition = currentVersionIndex;
	}

	/** Inserts a new version of the model (as a string) into this stack. */
	protected void insertNewVersion(String s) {
		++currentVersionIndex;
		if (savePosition >= currentVersionIndex) {
			savePosition = -1;
		}

		// discard later versions/deltas
		while (deltas.size() > currentVersionIndex) {
			deltas.remove(deltas.size() - 1);
		}

		deltas.add(Diff.computeDelta(split(currentVersion), split(s)));
		currentVersion = s;
		fireStackChanged();
	}

	/**
	 * Splits the given string (as reported from the implementing class) into
	 * suitable parts used for diffing (lines, words, tokens, etc.).
	 */
	protected abstract List<String> split(String s);

	/** Joins the parts created by {@link #split(String)}. */
	protected abstract String join(List<String> parts);

	/**
	 * This should write back the stack content to the model. This is called for
	 * every undo and redo operation.
	 */
	protected abstract void setModelFromString(String s);

	/** Something about this stack has changed. */
	protected abstract void fireStackChanged();

	/** Prints the amount of memory currently used by this stack. */
	protected int debugGetSize() {
		int size = 2 * currentVersion.length();
		for (Delta<String> delta : deltas) {
			for (int i = 0; i < delta.getSize(); ++i) {
				size += 4 + 2 * delta.getT(i).length();
			}
		}
		return 2 * size;
	}
}