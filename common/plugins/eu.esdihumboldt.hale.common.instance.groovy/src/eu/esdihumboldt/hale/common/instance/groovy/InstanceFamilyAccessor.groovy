/*
 * Copyright (c) 2015 Simon Templer
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

import eu.esdihumboldt.hale.common.instance.model.FamilyInstance
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.util.groovy.paths.AbstractAccessor
import eu.esdihumboldt.util.groovy.paths.Path
import eu.esdihumboldt.util.groovy.paths.PathImpl
import groovy.transform.CompileStatic

/**
 * Accessor linked instances of a {@link FamilyInstance}s.
 * 
 * It mutates, so it is only usable once.
 * 
 * @author Simon Templer
 */
@CompileStatic
class InstanceFamilyAccessor extends AbstractAccessor<FamilyInstance> {

	/**
	 * Create an accessor for a given family instance.
	 * 
	 * @param definition the definition
	 */
	public InstanceFamilyAccessor(FamilyInstance instance) {
		super(ImmutableList.of(new PathImpl<FamilyInstance>(instance)));
	}

	/**
	 * Creates an accessor for the given family instances.
	 * 
	 * @param objects the initial objects
	 */
	public InstanceFamilyAccessor(List<? extends FamilyInstance> instances) {
		super(transform(instances));
	}

	private static List<Path<FamilyInstance>> transform(List<? extends FamilyInstance> instances) {
		List<Path<FamilyInstance>> list = Lists.transform(instances, new Function<FamilyInstance, Path<FamilyInstance>>() {
					public Path<FamilyInstance> apply(FamilyInstance from) {
						return new PathImpl<FamilyInstance>(from);
					}
				});
		return Collections.unmodifiableList(list);
	}

	@Override
	public InstanceFamilyAccessor findChildren(String name) {
		return (InstanceFamilyAccessor) super.findChildren(name);
	}

	@Override
	public InstanceFamilyAccessor findChildren(String name, List<?> args) {
		return (InstanceFamilyAccessor) super.findChildren(name, args);
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

		List<Path<FamilyInstance>> allPaths = (List<Path<FamilyInstance>>) all() // Groovy CompileStatic can't deal properly with ? extends ...
		allPaths.collectMany { Path<FamilyInstance> parentPath ->
			// search for possible children and
			// create sub-paths for found instances

			FamilyInstance parent = parentPath.elements.last()
			def candidates = null

			if (parent) {
				candidates = parent.children.findAll { FamilyInstance child ->
					// check if child matches the condition

					// match name against type
					TypeDefinition type = child.getDefinition()
					if (type) {
						QName typeName = type.name
						if (typeName == fullName) {
							// perfect match
							true
						}
						else if (!namespace) {
							// no namespace provided

							if (name == typeName.localPart) {
								// local part matched
								true
							}
							else if (name == type.displayName) {
								// display name matched
								true
							}
							else {
								// no match
								false
							}
						}
						else {
							// namespace provided

							if (namespace == typeName.namespaceURI && name == type.displayName) {
								// allow namespace / displayName combination
								true
							}
							else {
								// no match
								false
							}
						}
					}
					else {
						// cannot check for match w/o type
						false
					}
				}
			}

			List<Path<FamilyInstance>> result = []
			candidates?.each { FamilyInstance candidate ->
				result << parentPath.subPath(candidate)
			}
			result
		}
	}

	/**
	 * Iterator over the path last values.
	 * 
	 * @param closure the closure to execute for each value
	 */
	public void each(Closure closure) {
		list().each { closure(it) }
	}

	/**
	 * Get the first value.
	 * 
	 * @return the first value
	 */
	public Object first() {
		eval(false)?.elements?.last()
	}

	/**
	 * Get all instances as a list.
	 *
	 * @return the list of instances
	 */
	public List<FamilyInstance> list() {
		List<Path<FamilyInstance>> allPaths = (List<Path<FamilyInstance>>) all() // Groovy CompileStatic can't deal properly with ? extends ...
		allPaths.collect { Path<FamilyInstance> path ->
			path.elements.last()
		}
	}

	// create associated InstanceAccessor

	public InstanceAccessor accessor() {
		new InstanceAccessor(list())
	}

	public InstanceAccessor getP() {
		accessor()
	}

	public InstanceAccessor getProperties() {
		accessor()
	}

}
