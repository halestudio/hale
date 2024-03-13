/*
 * Copyright (c) 2018 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.filter.internal;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.opengis.filter.expression.PropertyName;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.EntityMatch;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Filter visitor that replaces entities in filters with their
 * {@link AlignmentMigration} replacement.
 * 
 * @author Simon Templer
 */
public class EntityReplacementVisitor extends DuplicatingFilterVisitor {

	private final AlignmentMigration migration;
	private final Function<PropertyName, Optional<EntityDefinition>> resolveProperty;
	private final TypeDefinition preferRoot;
	private final SimpleLog log;

	// counter for total attempted replacements (where the original could be
	// resolved)
	private int total = 0;
	// counter for successful replacements
	private int matched = 0;

	private final Set<EntityDefinition> mismatches = new LinkedHashSet<>();
	private final Set<EntityDefinition> replacements = new LinkedHashSet<>();

	/**
	 * Create an entity replacement visitor.
	 * 
	 * @param migration the alignment migration
	 * @param resolveProperty the function resolving a property to an entity
	 * @param preferRoot hint on which entity to prefer if there are multiple
	 *            matches
	 * @param log the operation log
	 */
	public EntityReplacementVisitor(AlignmentMigration migration,
			Function<PropertyName, Optional<EntityDefinition>> resolveProperty,
			TypeDefinition preferRoot, SimpleLog log) {
		this.migration = migration;
		this.resolveProperty = resolveProperty;
		this.preferRoot = preferRoot;
		this.log = log;
	}

	@Override
	public Object visit(PropertyName expression, Object extraData) {
		Optional<EntityDefinition> resolved = resolveProperty.apply(expression)
				.map(p -> AlignmentUtil.getAllDefaultEntity(p));
		if (resolved.isPresent()) {
			total++;

			Optional<EntityDefinition> replace = migration
					.entityReplacement(resolved.get(), preferRoot, log).map(EntityMatch::getMatch);
			if (replace.isPresent()) {
				matched++;
				replacements.add(replace.get());

				if (!resolved.get().equals(replace.get())) {
					return getFactory(extraData).property(toPropertyName(replace.get()),
							expression.getNamespaceContext());
				}
			}
			else {
				mismatches.add(resolved.get());
			}
		}
		else {
			log.warn("Could not resolve property {0} for replacement", expression);
		}

		return super.visit(expression, extraData);
	}

	private String toPropertyName(EntityDefinition entityDefinition) {
		// similar to PropertyResolver paths
		String name = entityDefinition.getPropertyPath().stream()
				.filter(c -> c.getChild().asProperty() != null).map(c -> {
					return c.getChild().getName().getLocalPart();
					// TODO use version w/ namespace in ambiguous cases?
				}).collect(Collectors.joining("."));
		return name;
	}

	/**
	 * @return if none the attempted replacements matched.
	 */
	public boolean isAllMismatches() {
		return total > 0 && matched == 0;
	}

	/**
	 * @return if there were any failed attempted replacements
	 */
	public boolean hasMismatches() {
		return !mismatches.isEmpty();
	}

	/**
	 * @return the set of entities where no replacement was found
	 */
	public Set<EntityDefinition> getMismatches() {
		return Collections.unmodifiableSet(mismatches);
	}

	/**
	 * @return the set of entities that served as replacement
	 */
	public Set<EntityDefinition> getReplacements() {
		return Collections.unmodifiableSet(replacements);
	}

	/**
	 * Determine if related to an expected parent type all replacement attempts
	 * were mismatches or matching entities with a different parent type.
	 * 
	 * @param expectedParent the expected parent type
	 * @return <code>true</code> if related to the given expected parent type
	 *         all replacement attempts were mismatches or matching entities
	 *         with a different parent type, <code>false</code> if there were
	 *         successful matches with the given parent type
	 */
	public boolean isAllMismatches(TypeDefinition expectedParent) {
		if (expectedParent == null) {
			return isAllMismatches();
		}

		if (total > 0) {
			return !replacements.stream()
					.anyMatch(entity -> expectedParent.equals(entity.getType()));
		}
		else {
			return false;
		}
	}

}
