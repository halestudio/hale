/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.paths.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.paths.DefinitionResolver;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.groovy.paths.Path;
import eu.esdihumboldt.util.groovy.paths.PathImpl;

/**
 * Constraint that caches paths found by the {@link DefinitionResolver}.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = true)
public class CachedResolver implements TypeConstraint, GroupPropertyConstraint {

	private final Map<Pair<String, String>, List<Path<Definition<?>>>> resolvedPaths = new HashMap<>();

	private final Definition<?> def;

	/**
	 * Default constructor.
	 * 
	 * @param def the definition the constraint is associated to
	 */
	public CachedResolver(Definition<?> def) {
		super();

		Preconditions.checkArgument(def instanceof TypeDefinition
				|| def instanceof GroupPropertyDefinition);

		this.def = def;
	}

	/**
	 * Get the resolved paths for the given property name and namespace.
	 * 
	 * @param name the property name
	 * @param namespace the property namespace or <code>null</code> if the
	 *            namespace should be ignored
	 * @return the resolved paths
	 */
	public List<Path<Definition<?>>> getResolvedPaths(String name, String namespace) {
		synchronized (resolvedPaths) {
			Pair<String, String> key = new Pair<>(namespace, name);
			List<Path<Definition<?>>> paths = resolvedPaths.get(key);
			if (paths == null) {
				paths = ImmutableList.copyOf(DefinitionResolver.findProperty((DefinitionGroup) def,
						name, namespace, new PathImpl<Definition<?>>(), true));
				resolvedPaths.put(key, paths);
			}
			return paths;
		}
	}

	@Override
	public boolean isInheritable() {
		return false;
	}

}
