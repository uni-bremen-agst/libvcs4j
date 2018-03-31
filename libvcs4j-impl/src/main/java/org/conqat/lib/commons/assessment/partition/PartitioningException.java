/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.lib.commons.assessment.partition;

/**
 * Exception thrown in case of partitioning problems. The message of this
 * exception must be suitable to be shown to the user.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: 0684F03FDFA84BFEF185CCC13CCAE3FE
 */
public class PartitioningException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor. */
	public PartitioningException(String message) {
		super(message);
	}

	/** Constructor. */
	public PartitioningException(String message, Throwable t) {
		super(message, t);
	}
}