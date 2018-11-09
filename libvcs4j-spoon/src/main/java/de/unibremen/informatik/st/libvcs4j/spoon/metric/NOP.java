package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;

import java.util.Optional;

/**
 * This visitor gathers the 'number of parameters' metric for {@link CtMethod}
 * and {@link CtConstructor} elements. Use {@link #NOPOf(CtExecutable)} to
 * get the metric for a given {@link CtExecutable}.
 */
public class NOP extends IntMetric {

	/**
	 * Returns the 'number of parameters' metric of {@code executable}. Returns
	 * an empty {@link Optional} if {@code executable} is {@code null}.
	 *
	 * @param executable
	 * 		The executable whose 'number of parameters' metric is requested.
	 * @return
	 * 		The 'number of parameters' metric of {@code executable}.
	 */
	public Optional<Integer> NOPOf(final CtExecutable executable) {
		return metricOf(executable);
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> method) {
		visitNode(method, Propagation.NONE, method.getParameters().size());
	}

	@Override
	public <T> void visitCtConstructor(final CtConstructor<T> constructor) {
		visitNode(constructor, Propagation.NONE,
				constructor.getParameters().size());
	}
}
