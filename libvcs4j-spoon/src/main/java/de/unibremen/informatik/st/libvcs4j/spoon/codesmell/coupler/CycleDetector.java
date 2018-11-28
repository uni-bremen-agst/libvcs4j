package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.coupler;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CycleDetector extends CodeSmellDetector {

	private final Map<CtType, Set<CtType>> dependencies = new HashMap<>();

	public CycleDetector(@NonNull Revision revision) {
		super(revision);
	}

	@Override
	public void visitRoot(final CtElement element) {
		super.visitRoot(element);
		final TypeFactory typeFactory = element.getFactory().Type();
		final Set<CtType> types = new HashSet<>(dependencies.keySet());

		for (final CtType t1 : types) {
			final Set<CtType> t1deps = dependencies.get(t1);
			for (final CtType t2 : types) {
				if (!t1.equals(t2)) {
					final Set<CtType> t2deps = dependencies.get(t2);
					if (t1deps.contains(t2) && t2deps.contains(t1)) {
						t1deps.remove(t2);
						t2deps.remove(t1);
						addCodeSmellWithMultiplePositions(
								Arrays.asList(t1, t2),
								Collections.emptyList(),
								null);
					}
				}
			}
		}
	}

	@Override
	public <T> void visitCtField(final CtField<T> field) {
		final Optional<CtType> from = Optional
				.ofNullable(field.getDeclaringType());
		final Optional<CtType> to = Optional
				.ofNullable(field.getType())
				.map(CtTypeReference::getDeclaration);
		if (from.isPresent() && to.isPresent()) {
			dependencies.computeIfAbsent(from.get(), f -> {
				final Set<CtType> set = new HashSet<>();
				set.add(f);
				return set;
			}).add(to.get());
		}
		super.visitCtField(field);
	}

	@Override
	public CodeSmell.Definition getDefinition() {
		return new CodeSmell.Definition("Cycle", new Thresholds());
	}
}
