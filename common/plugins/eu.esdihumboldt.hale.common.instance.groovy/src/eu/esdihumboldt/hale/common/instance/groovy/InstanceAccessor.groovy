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

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists

import eu.esdihumboldt.hale.common.instance.model.Group
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.paths.DefinitionResolver
import eu.esdihumboldt.util.groovy.paths.AbstractAccessor
import eu.esdihumboldt.util.groovy.paths.Path
import eu.esdihumboldt.util.groovy.paths.PathImpl
import eu.esdihumboldt.util.groovy.paths.PathWithNulls
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

	private final boolean accessorNulls

	/**
	 * Create an accessor for a given instance/value.
	 * 
	 * @param object the object to access
	 * @param accessNulls if it should be possible to access <code>null</code> values 
	 */
	public InstanceAccessor(def object, boolean accessNulls = false) {
		super(ImmutableList.of((object == null || accessNulls)
		? new PathWithNulls<Object>(object) : new PathImpl<Object>(object)));
		this.accessorNulls = accessNulls
	}

	/**
	 * Creates an accessor for the given instances/values.
	 * 
	 * @param objects the initial objects
	 */
	public InstanceAccessor(List<?> objects, boolean accessNulls = false) {
		super(transform(objects, accessNulls));
		this.accessorNulls = accessNulls
	}

	private static List<Path<Object>> transform(List<?> objects, final boolean accessNulls) {
		List<Path<Object>> list = Lists.transform(objects, new Function<Object, Path<Object>>() {
					public Path<Object> apply(Object from) {
						if (from == null || accessNulls) {
							return new PathWithNulls<Object>(from);
						}
						else {
							return new PathImpl<Object>(from)
						}
					}
				});
		return Collections.unmodifiableList(list);
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

		List<Path<Object>> allPaths = (List<Path<Object>>) all() // Groovy CompileStatic can't deal properly with ? extends ...
		allPaths.collectMany { Path<Object> parentPath ->
			// search for possible children and
			// create sub-paths for found properties

			def object = parentPath.elements.last()
			def values = null

			if (object instanceof Group) {
				// there may only be children if this is a group
				Group group = (Group) object

				if (group.definition != null) {
					// access based on definitions

					// find possible paths to children
					def valueList = []
					List<Path<Definition<?>>> paths = DefinitionResolver.findPropertyCached(group.definition, name, namespace)
					paths?.each { Path<Definition<?>> propertyPath ->
						def pvalues = getPropertyValues(group, propertyPath)
						pvalues?.each { valueList << it }
					}
					values = valueList
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

			List<Path<Object>> result = []
			values?.each {
				if (it != null || accessorNulls) {
					// only add if not null or nulls should be listed
					result << parentPath.subPath(it)
				}
			}
			result
		}
	}

	private def getPropertyValues(Group object, Path<Definition<?>> path) {
		Queue queue = new LinkedList()
		queue << object

		path.elements.eachWithIndex { Definition definition, int index ->
			Queue newQueue = new LinkedList()
			while (!queue.empty) {
				Object element = queue.poll()

				if (element instanceof Group) {
					((Group) element).getProperty(definition.name)?.each { newQueue << it }
				}
			}
			queue = newQueue
		}

		queue
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
		def value = eval(false)?.elements?.last()
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
		List<Path<Object>> allPaths = (List<Path<Object>>) all() // Groovy CompileStatic can't deal properly with ? extends ...
		allPaths.collect { Path<Object> path ->
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
