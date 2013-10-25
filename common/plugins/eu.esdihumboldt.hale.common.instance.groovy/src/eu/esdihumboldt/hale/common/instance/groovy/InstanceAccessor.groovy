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

package eu.esdihumboldt.hale.common.instance.groovy;

import javax.xml.namespace.QName

import com.google.common.collect.ImmutableList

import eu.esdihumboldt.hale.common.instance.model.Group
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.util.groovy.paths.AbstractAccessor
import eu.esdihumboldt.util.groovy.paths.Path
import eu.esdihumboldt.util.groovy.paths.PathImpl
import groovy.transform.CompileStatic

/**
 * Property accessor for {@link Instance}s.
 * 
 * It mutates, so it is only usable once.
 * 
 * @author Simon Templer
 */
@CompileStatic
class InstanceAccessor extends AbstractAccessor<Object> {

	/**
	 * Create an accessor for a given definition.
	 * 
	 * @param definition the definition
	 */
	public InstanceAccessor(def object) {
		super(ImmutableList.of(new PathImpl<Object>(object)));
	}

	@Override
	public InstanceAccessor findChildren(String name) {
		return (InstanceAccessor) super.findChildren(name);
	}

	@Override
	public InstanceAccessor findChildren(String name, List<?> args) {
		return (InstanceAccessor) super.findChildren(name, args);
	}

	@Override
	protected List<? extends Path<Object>> findChildPaths(
			List<? extends Path<Object>> parentPaths, final String name, List<?> args) {
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

		QName fullName
		if (namespace) {
			fullName = new QName(namespace, name)
		}
		else {
			fullName = new QName(name)
		}

		all().collectMany { Path<Object> parentPath ->
			// search for possible children and
			// create sub-paths for found properties
			List<Path<Object>> result = []

			def object = parentPath.elements.last()

			def values = null
			if (object instanceof Group) {
				// there may only be children if this is a group
				Group group = (Group) object

				if (group.definition != null) {
					// access based on definitions
				}
				else {
					// access only based on group properties

					// first try full name
					values = group.getProperty(fullName)
					if (!values) {
						// if no value present, match with local name only
						def valueList = []
						group.propertyNames.each { QName pname ->
							if (pname.localPart == fullName.localPart) {
								def pvalues = group.getProperty(pname)
								pvalues?.each { valueList << it }
							}
						}
						values = valueList
					}
				}
			}

			values?.each {
				result << parentPath.subPath(it)
			}

			//			DefinitionGroup group = DefinitionUtil.getDefinitionGroup(parentPath.elements.last())
			//
			//
			//			List<Path<Definition<?>>> paths = DefinitionResolver.findPropertyCached(group, name, namespace)
			//			paths?.each { Path<Definition<?>> propertyPath ->
			//				// create a sub-path for each
			//				result << parentPath.subPath(propertyPath)
			//			}

			result
		}
	}

	/**
	 * Iterator over the path last values.
	 * 
	 * @param instance <code>true</code> if instances should be preserved,
	 *   <code>false</code> if the instance values should be supplied instead		
	 * @param closure the closure to execute for each value
	 */
	public void each(boolean instance = true, Closure closure) {
		list(instance).each { closure(it) }
	}

	/**
	 * Get the first value.
	 * 
	 * @param instance <code>true</code> if an instance should be preserved,
	 *   <code>false</code> if the instance value should be returned instead
	 * @return the first value
	 */
	public Object first(boolean instance = true) {
		def value = eval(false)?.elements.last()
		if (value instanceof Instance && !instance) {
			value = ((Instance) value).value
		}
		value
	}

	/**
	 * Get the first value, for an instance its value is returned.
	 *
	 * @return the first value
	 */
	public Object value() {
		first(false)
	}

	/**
	 * Get all values as a list.
	 *
	 * @param instance <code>true</code> if instances should be preserved,
	 *   <code>false</code> if the instance values should be returned instead
	 * @return the list of values
	 */
	public List<Object> list(boolean instance = true) {
		all().collect { Path<Object> path ->
			def value = path.elements.last()
			if (value instanceof Instance && !instance) {
				value = ((Instance) value).value
			}
			value
		}
	}

	/**
	 * Get all values as a list, for instances their values are returned.
	 *
	 * @return the list of values
	 */
	public List<Object> values() {
		list(false)
	}

}
