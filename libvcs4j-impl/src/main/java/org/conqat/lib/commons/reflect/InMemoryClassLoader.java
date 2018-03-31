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
package org.conqat.lib.commons.reflect;

/**
 * Class loader used to directly load a raw array of byte code.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 57702EB5700285DC615FB957489D8B33
 */
public class InMemoryClassLoader extends ClassLoader {

	/** Constructor. */
	public InMemoryClassLoader() {
		super();
	}

	/** Constructor. */
	public InMemoryClassLoader(ClassLoader parent) {
		super(parent);
	}

	/** Constructor. */
	public InMemoryClassLoader(String className, byte[] byteCode) {
		super();
		insertClass(className, byteCode);
	}

	/** Constructor. */
	public InMemoryClassLoader(ClassLoader parent, String className,
			byte[] byteCode) {
		super(parent);
		insertClass(className, byteCode);
	}

	/**
	 * Inserts a class into this class loader (i.e. makes it available for later
	 * calls).
	 */
	public void insertClass(String className, byte[] byteCode) {
		defineClass(className, byteCode, 0, byteCode.length);
	}
}