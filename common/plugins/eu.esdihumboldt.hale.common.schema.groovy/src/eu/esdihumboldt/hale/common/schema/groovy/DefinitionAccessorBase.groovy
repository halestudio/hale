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

package eu.esdihumboldt.hale.common.schema.groovy

import org.codehaus.groovy.runtime.InvokerHelper

import eu.esdihumboldt.hale.common.schema.helper.DefinitionPath
import eu.esdihumboldt.hale.common.schema.helper.DefinitionResolver
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked



/**
 * Base class for property accessors for {@link Definition}s.
 * 
 * It mutates, so an instance is only usable once.
 * 
 * @author Simon Templer
 */
@CompileStatic
class DefinitionAccessorBase<T extends DefinitionPath> {

	/**
	 * The definition paths
	 */
	private List<? extends T> accessorPaths

	/**
	 * Creates a new accessor.
	 * 
	 * @param initialPaths the initial paths, usually containing only one path
	 *   with the parent definition
	 */
	DefinitionAccessorBase(List<? extends T> initialPaths) {
		this.accessorPaths = initialPaths
	}

	def propertyMissing(String name) {
		findProperties(name)
	}

	def methodMissing(String name, args) {
		List list = InvokerHelper.asList(args)

		String namespace = null
		if (list) {
			namespace = list[0] as String
		}

		findProperties(name, namespace)
	}

	/**
	 * Find properties with the given name and namespace.
	 *  
	 * @param name the property name
	 * @param namespace the namespace, if <code>null</code> the property
	 *   namespace is ignored
	 * @return this {@link DefinitionAccessor}
	 */
	DefinitionAccessorBase findProperties(String name, String namespace = null) {
		accessorPaths = accessorPaths.collectMany { DefinitionPath parentPath ->
			// search for possible property paths and
			// create sub-paths for found properties

			DefinitionGroup group = DefinitionUtil.getDefinitionGroup(parentPath.path.last())
			List<DefinitionPath> result = []

			List<DefinitionPath> paths = DefinitionResolver.findPropertyCached(group, name, namespace)
			paths?.each { DefinitionPath propertyPath ->
				// create a sub-path for each
				result << parentPath.subPath(propertyPath)
			}

			result
		}

		this
	}

	/**
	 * Get all found definition paths.
	 * 
	 * @return the list of definition paths
	 */
	List<? extends T> all() {
		accessorPaths
	}

	/**
	 * Get a single found property path.
	 * 
	 * @param unique if the path must be unique 
	 * @return a property path or <code>null</code> if none was found
	 * @throws IllegalStateException if there are multiple paths but a unique
	 *   path was requested 
	 */
	T eval(boolean unique = true) {
		if (!accessorPaths) {
			return null
		}
		else if (!unique || accessorPaths.size() == 1) {
			// return a single property
			return accessorPaths[0]
		}
		else {
			throw new IllegalStateException('Multiple possible property paths found')
		}
	}

	Definition toDefinition() {
		eval().path.last()
	}

	Object asType(Class clazz) {
		if (clazz == Definition) {
			toDefinition()
		}
		else {
			super.asType(clazz)
		}
	}
}
