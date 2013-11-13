/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.common.schema.groovy;

import com.google.common.collect.ImmutableList

import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.paths.DefinitionResolver
import eu.esdihumboldt.util.groovy.paths.AbstractAccessor
import eu.esdihumboldt.util.groovy.paths.Path
import eu.esdihumboldt.util.groovy.paths.PathImpl
import groovy.transform.CompileStatic

/**
 * Property accessor for {@link Definition}s.
 * 
 * It mutates, so it is only usable once.
 * 
 * @author Simon Templer
 */
@CompileStatic
class DefinitionAccessor extends AbstractAccessor<Definition<?>> {

	/**
	 * Create an accessor for a given definition.
	 * 
	 * @param definition the definition
	 */
	public DefinitionAccessor(Definition<?> definition) {
		super(ImmutableList.of(new PathImpl<Definition<?>>(definition)));
	}

	@Override
	public DefinitionAccessor findChildren(String name) {
		return (DefinitionAccessor) super.findChildren(name);
	}

	/**
	 * Find the children with the given local name and namespace.
	 * 
	 * @param name the local name
	 * @param namespace the namespace or <code>null</code> to ignore the namespace
	 * @return the child accessor
	 */
	public DefinitionAccessor findChildren(String name, String namespace) {
		if (namespace == null)
			return findChildren(name)
		else
			return findChildren(name, [namespace])
	}

	@Override
	public DefinitionAccessor findChildren(String name, List<?> args) {
		return (DefinitionAccessor) super.findChildren(name, args);
	}

	@Override
	protected List<? extends Path<Definition<?>>> findChildPaths(
			List<? extends Path<Definition<?>>> parentPaths, String name, List<?> args) {
		List<?> list = new ArrayList<>(args)

		String namespace = null
		if (list) {
			int index = 0
			if (list[0] instanceof Map) {
				// first argument may be a named parameter map
				index = 1
			}
			if (index < list.size()) {
				// namespace must be first argument (after map)
				namespace = list[index] as String
				list.remove(index)
			}
		}

		all().collectMany { Path<Definition<?>> parentPath ->
			// search for possible property paths and
			// create sub-paths for found properties

			DefinitionGroup group = DefinitionUtil.getDefinitionGroup(parentPath.elements.last())
			List<Path<Definition<?>>> result = []

			List<Path<Definition<?>>> paths = DefinitionResolver.findPropertyCached(group, name, namespace)
			paths?.each { Path<Definition<?>> propertyPath ->
				// create a sub-path for each
				result << parentPath.subPath(propertyPath)
			}

			result
		}
	}

	public Definition toDefinition() {
		eval()?.elements?.last()
	}

	public Object asType(Class clazz) {
		if (clazz == Definition) {
			toDefinition()
		}
		else if (clazz == TypeDefinition) {
			(TypeDefinition) toDefinition()
		}
		else if (clazz == PropertyDefinition) {
			(PropertyDefinition) toDefinition()
		}
		else if (clazz == GroupPropertyDefinition) {
			(GroupPropertyDefinition) toDefinition()
		}
		else {
			super.asType(clazz)
		}
	}
}
