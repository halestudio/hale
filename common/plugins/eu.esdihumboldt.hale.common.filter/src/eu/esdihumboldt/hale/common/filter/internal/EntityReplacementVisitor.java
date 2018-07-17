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

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.opengis.filter.expression.PropertyName;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Filter visitor that replaces entities in filters with their
 * {@link AlignmentMigration} replacement.
 * 
 * @author Simon Templer
 */
public class EntityReplacementVisitor extends DuplicatingFilterVisitor {

	private final AlignmentMigration migration;
	private final Function<PropertyName, Optional<EntityDefinition>> resolveProperty;
	private final SimpleLog log;

	/**
	 * Create an entity replacement visitor.
	 * 
	 * @param migration the alignment migration
	 * @param resolveProperty the function resolving a property to an entity
	 * @param log the operation log
	 */
	public EntityReplacementVisitor(AlignmentMigration migration,
			Function<PropertyName, Optional<EntityDefinition>> resolveProperty, SimpleLog log) {
		this.migration = migration;
		this.resolveProperty = resolveProperty;
		this.log = log;
	}

	@Override
	public Object visit(PropertyName expression, Object extraData) {
		Optional<EntityDefinition> resolved = resolveProperty.apply(expression)
				.map(p -> AlignmentUtil.getAllDefaultEntity(p));
		if (resolved.isPresent()) {
			Optional<EntityDefinition> replace = migration.entityReplacement(resolved.get(), log);
			if (replace.isPresent() && !resolved.get().equals(replace.get())) {
				return getFactory(extraData).property(toPropertyName(replace.get()),
						expression.getNamespaceContext());
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
		// put in quotes so it is possible to parse the resulting filterTerm
		return '"' + name + '"';
	}

}
