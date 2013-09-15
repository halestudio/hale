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
import groovy.transform.TypeCheckingMode



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
		List list = new ArrayList(InvokerHelper.asList(args))

		String namespace = null
		if (list) {
			int index = 0;
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

		findProperties(name, namespace, list)
	}

	/**
	 * Find properties with the given name and namespace.
	 *  
	 * @param name the property name
	 * @param namespace the namespace, if <code>null</code> the property
	 *   namespace is ignored
	 * @param moreArgs the list of additional arguments apart from the
	 *   namespace
	 * @return this {@link DefinitionAccessor}
	 */
	@CompileStatic(TypeCheckingMode.SKIP) // doesn't recognize createSubPath
	@TypeChecked
	DefinitionAccessorBase findProperties(String name, String namespace = null, List<?> moreArgs = []) {
		accessorPaths = accessorPaths.collectMany { T parentPath ->
			// search for possible property paths and
			// create sub-paths for found properties

			DefinitionGroup group = DefinitionUtil.getDefinitionGroup(parentPath.path.last())
			List<T> result = []

			List<T> paths = DefinitionResolver.findPropertyCached(group, name, namespace)
			paths?.each { T propertyPath ->
				// create a sub-path for each
				result << createSubPath(parentPath, propertyPath, moreArgs)
			}

			result
		}

		this
	}

	/**
	 * Create a sub path for the given parent path.
	 * 
	 * @param parentPath the parent path
	 * @param propertyPath the property path to add in the sub path
	 * @param moreArgs list of additional arguments
	 * @return the sub path
	 */
	protected T createSubPath(T parentPath, T propertyPath, List moreArgs) {
		parentPath.subPath(propertyPath)
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
