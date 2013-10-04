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

package eu.esdihumboldt.hale.common.schema.paths;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.paths.internal.CachedResolver;
import eu.esdihumboldt.util.groovy.paths.Path;
import eu.esdihumboldt.util.groovy.paths.PathImpl;

/**
 * Resolves property names on {@link DefinitionGroup}s.
 * 
 * @author Simon Templer
 */
public class DefinitionResolver {

	/**
	 * Find all possible property paths for the given property name, also
	 * descending into sub-groups. The results will be cached in a special
	 * definition constraint.
	 * 
	 * @param parent the type or group in which to look for the property
	 * @param name the property local name
	 * @param namespace the property namespace or <code>null</code> if the
	 *            namespace should be ignored
	 * @return the list of found definition paths
	 */
	public static List<Path<Definition<?>>> findPropertyCached(DefinitionGroup parent, String name,
			String namespace) {
		if (parent instanceof TypeDefinition) {
			return ((TypeDefinition) parent).getConstraint(CachedResolver.class).getResolvedPaths(
					name, namespace);
		}
		else if (parent instanceof GroupPropertyDefinition) {
			return ((GroupPropertyDefinition) parent).getConstraint(CachedResolver.class)
					.getResolvedPaths(name, namespace);
		}

		throw new IllegalArgumentException(
				"Parent must be either a type or group property definition");
	}

	/**
	 * Find all possible property paths for the given property name, also
	 * descending into sub-groups. Does a complete calculation based on the
	 * definition structure.
	 * 
	 * @see #findPropertyCached(DefinitionGroup, String, String)
	 * 
	 * @param parent the type or group in which to look for the property
	 * @param name the property local name
	 * @param namespace the property namespace or <code>null</code> if the
	 *            namespace should be ignored
	 * @return the list of found definition paths
	 */
	public static List<Path<Definition<?>>> findProperty(DefinitionGroup parent, String name,
			String namespace) {
		return findProperty(parent, name, namespace, new PathImpl<Definition<?>>(), false);
	}

	/**
	 * Find all possible property paths for the given property name, also
	 * descending into sub-groups.
	 * 
	 * Used for the internal computation. In most cases
	 * {@link #findProperty(DefinitionGroup, String, String)} or
	 * {@link #findPropertyCached(DefinitionGroup, String, String)} should be
	 * called instead.
	 * 
	 * @param parent the type or group in which to look for the property
	 * @param name the property local name
	 * @param namespace the property namespace or <code>null</code> if the
	 *            namespace should be ignored
	 * @param basePath the definition base path
	 * @param useCachedResolver if for sub-groups a cached resolver should be
	 *            used
	 * @return the list of found definition paths
	 */
	public static List<Path<Definition<?>>> findProperty(DefinitionGroup parent, String name,
			String namespace, Path<Definition<?>> basePath, boolean useCachedResolver) {
		List<Path<Definition<?>>> results = new ArrayList<>();
		for (ChildDefinition<?> child : DefinitionUtil.getAllChildren(parent)) {
			if (child.asProperty() != null) {
				// properties may only be direct matches
				PropertyDefinition property = child.asProperty();
				if (accept(property.getName(), name, namespace)) {
					results.add(basePath.subPath(property));
				}
			}
			else if (child.asGroup() != null) {
				GroupPropertyDefinition group = child.asGroup();
				Path<Definition<?>> groupPath = basePath.subPath(group);

				/*
				 * If the name is a match, we take the reference to the group as
				 * result as well.
				 */
				if (accept(group.getName(), name, namespace)) {
					results.add(groupPath);
				}

				// FIXME what about group cycles?

				// check the group children
				List<Path<Definition<?>>> childResults;
				if (useCachedResolver) {
					childResults = findPropertyCached(group, name, namespace);
				}
				else {
					childResults = findProperty(group, name, namespace);
				}
				for (Path<Definition<?>> path : childResults) {
					results.add(groupPath.subPath(path));
				}
			}
		}
		return results;
	}

	/**
	 * Determines if the given qualified name is accepted as match for the given
	 * local name and namespace.
	 * 
	 * @param name the qualified name
	 * @param localName the local name
	 * @param namespace the namespace, may be <code>null</code> if any namespace
	 *            is acceptable
	 * @return if the name is accepted
	 */
	private static boolean accept(QName name, String localName, String namespace) {
		if (namespace == null) {
			return localName.equals(name.getLocalPart());
		}
		return localName.equals(name.getLocalPart()) && namespace.equals(name.getNamespaceURI());
	}

}
