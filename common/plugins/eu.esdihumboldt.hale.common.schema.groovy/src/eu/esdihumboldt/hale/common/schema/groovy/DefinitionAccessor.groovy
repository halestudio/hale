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
import eu.esdihumboldt.hale.common.schema.helper.internal.DefinitionPathImpl
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked



/**
 * Property accessor for {@link Definition}s.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
@CompileStatic
class DefinitionAccessor {

	enum Mode {
		/**
		 * All possible properties will be returned, there is always a list
		 * returned.
		 */
		ALL,
		/**
		 * Only one property is returned, if there are multiple the first is
		 * returned.
		 */
		SINGLE_LAX,
		/**
		 * Only one property is returned, if there are multiple an exception is
		 * thrown.
		 */
		SINGLE_STRICT
	}

	/**
	 * The definition path in relation to the parent.
	 */
	DefinitionPath accessorPath

	/**
	 * The parent accessor, may be <code>null</code>.
	 */
	DefinitionAccessor parentAccessor

	/**
	 * The accessor mode.
	 */
	Mode accessorMode = Mode.SINGLE_STRICT

	/**
	 * Default constructor. 
	 */
	DefinitionAccessor() {
		super();
	}

	/**
	 * Create an accessor for a given definition.
	 * 
	 * @param definition the definition
	 */
	DefinitionAccessor(Definition<?> definition) {
		super();

		accessorPath = new DefinitionPathImpl([definition])
		parentAccessor = null
	}

	def propertyMissing(String name) {
		def result = findProperties(name)
		if (result) {
			return result
		}
		else {
			throw new MissingPropertyException(name, getClass())
		}
	}

	def methodMissing(String name, args) {
		List list = InvokerHelper.asList(args)

		String namespace = null
		if (list) {
			namespace = list[0] as String
		}

		def result = findProperties(name, namespace)
		if (result) {
			return result
		}
		else {
			throw new MissingMethodException(name, getClass(), args)
		}
	}

	/**
	 * Find the property with the given name and namespace.
	 * 
	 * Convenience method for Java access, only usable with
	 * {@link Mode#SINGLE_LAX} or {@link Mode#SINGLE_STRICT}. 
	 *
	 * @param name the property name
	 * @param namespace the namespace, if <code>null</code> the property
	 *   namespace is ignored
	 * @return a {@link DefinitionAccessor}
	 */
	DefinitionAccessor findProperty(String name, String namespace = null) {
		(DefinitionAccessor) findProperties(name, namespace)
	}

	/**
	 * Find properties with the given name and namespace.
	 *  
	 * @param name the property name
	 * @param namespace the namespace, if <code>null</code> the property
	 *   namespace is ignored
	 * @return a {@link DefinitionAccessor} or a list of them, depending on
	 *   the accessor mode
	 */
	def findProperties(String name, String namespace = null) {
		DefinitionGroup group = DefinitionUtil.getDefinitionGroup(accessorPath.path.last())

		List<DefinitionPath> paths = DefinitionResolver.findPropertyCached(group, name, namespace)
		if (!paths) {
			// missing property/method
			return null;
		}

		switch (accessorMode) {
			case Mode.ALL:
			// return all properties
				def result = []
				paths.each { DefinitionPath path ->
					DefinitionAccessor child = createChild(this, path)
					child.accessorMode = accessorMode
					result << child
				}
				return result
			case Mode.SINGLE_LAX: // fall through
			case Mode.SINGLE_STRICT: // fall through
			default:
				if (accessorMode == Mode.SINGLE_LAX || paths.size() == 1) {
					// return a single property
					DefinitionPath path = paths[0]
					DefinitionAccessor child = createChild(this, path)
					child.accessorMode = accessorMode
					return child
				}
				else {
					throw new IllegalStateException("Multiple properties '$name' found")
				}
		}
	}

	protected DefinitionAccessor createChild(DefinitionAccessor parent, DefinitionPath childPath) {
		new DefinitionAccessor(parentAccessor: parent, accessorPath: childPath)
	}

	Definition toDefinition() {
		accessorPath.path.last()
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
