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

package eu.esdihumboldt.hale.common.align.groovy.accessor

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.align.groovy.accessor.internal.EntityAccessorUtil
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Condition
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import eu.esdihumboldt.hale.common.instance.model.Filter
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.paths.DefinitionResolver
import eu.esdihumboldt.util.groovy.paths.AbstractAccessor
import eu.esdihumboldt.util.groovy.paths.Path
import eu.esdihumboldt.util.groovy.paths.PathImpl
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode

/**
 * Entity definition accessor.
 * 
 * @author Simon Templer
 */
@CompileStatic
class EntityAccessor extends AbstractAccessor<PathElement> {

	/**
	 * Create an accessor for a given entity definition.
	 *
	 * @param root the entity definition
	 */
	public EntityAccessor(EntityDefinition root) {
		super(new PathImpl<PathElement>(new PathElement(root)))
	}

	@Override
	public EntityAccessor findChildren(String name) {
		return (EntityAccessor) super.findChildren(name);
	}

	/**
	 * Find the children with the qualified name.
	 *
	 * @param name the qualified name
	 * @return the child accessor
	 */
	public EntityAccessor findChildren(QName name) {
		if (!name.namespaceURI)
			return findChildren(name.localPart)
		else
			return findChildren(name.localPart, name.namespaceURI)
	}

	/**
	 * Find the children with the given local name and namespace.
	 *
	 * @param name the local name
	 * @param namespace the namespace or <code>null</code> to ignore the namespace
	 * @return the child accessor
	 */
	public EntityAccessor findChildren(String name, String namespace) {
		if (namespace == null)
			return findChildren(name)
		else
			return findChildren(name, [namespace])
	}

	@Override
	public EntityAccessor findChildren(String name, List<?> args) {
		return (EntityAccessor) super.findChildren(name, args);
	}

	@Override
	@CompileStatic(TypeCheckingMode.SKIP) // compile static breaks access to createChildContext
	@TypeChecked
	protected List<? extends Path<PathElement>> findChildPaths(
			List<? extends Path<PathElement>> parentPaths, String name, List<?> args) {
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

		all().collectMany { Path<PathElement> parentPath ->
			// search for possible property paths and
			// create sub-paths for found properties

			PathElement element = parentPath.elements.last()

			DefinitionGroup group = DefinitionUtil.getDefinitionGroup(element.definition)
			List<Path<PathElement>> result = []

			List<Path<Definition<?>>> paths = DefinitionResolver.findPropertyCached(group, name, namespace)
			paths?.each { Path<Definition<?>> propertyPath ->
				// create a sub-path for each

				// convert definitions to path elements
				List<Definition<?>> definitions = propertyPath.elements
				List<PathElement> pathElements = []

				if (definitions) {
					if (definitions.size() > 1) {
						// create default child contexts for groups
						definitions[0..definitions.size()-2].each { Definition<?> definition ->
							pathElements << new PathElement(new ChildContext((ChildDefinition<?>) definition))
						}
					}
					// last element of path
					// create child context based on parameters
					ChildContext context = createChildContext((ChildDefinition<?>) definitions.last(), list)
					pathElements << new PathElement(context)
				}

				Path<PathElement> entityPath = new PathImpl<>(pathElements)

				result << parentPath.subPath(entityPath)
			}

			result
		}
	}

	protected ChildContext createChildContext(ChildDefinition<?> child, List<?> args) {
		Integer contextName = null
		Integer index = null
		Condition condition = null

		if (args) {
			int startIndex = 0
			if (args[0] instanceof Map) {
				((Map) args[0]).each { key, value ->
					switch (key) {
						case 'filter':
							if (value instanceof Filter) {
								condition = new Condition((Filter) value)
							}
							else {
								Filter filter = createFilter(value as String)
								if (filter) {
									condition = new Condition(filter)
								}
							}
							break
						case 'index':
							index = value as Integer
							break
						case 'name':
							contextName = value as Integer
							break
					}
				}

				startIndex = 1;
			}

			// handle other parameters
			if (startIndex < args.size()) {
				args[startIndex..args.size() - 1].each {
					// detect automatically by type
					if (it instanceof Filter) {
						// must be a condition
						condition = new Condition(it)
					}
					else if (it instanceof Number) {
						// either index or name
						int value = it.intValue()

						// determine schema space
						SchemaSpaceID ss = all().first().elements.first().root.schemaSpace

						switch (ss) {
							case SchemaSpaceID.SOURCE:
							// source only supports index
								index = value
								break;
							case SchemaSpaceID.TARGET:
							// target only supports name
								contextName = value
								break;
						}
					}
					else if (it instanceof ChildContext) {
						// copy from ChildContext
						contextName = it.contextName
						condition = it.condition
						index = it.index
					}
					else if (it instanceof ChildContextType) {
						// copy from ChildContextType
						contextName = it.context as Integer
						index = it.index as Integer
						if (it.condition) {
							Filter filter = FilterDefinitionManager.instance.from(it.condition.lang, it.condition.value)
							if (filter) {
								condition = new Condition(filter)
							}
						}
					}
					else {
						String value = it as String

						Filter filter = createFilter(value)

						if (filter) {
							condition = new Condition(filter)
						}
					}
				}
			}
		}

		new ChildContext(contextName, index, condition, child)
	}

	/**
	 * Create a filter from the given test.
	 * 	
	 * @param text the filter text
	 * @return the filter or <code>null</code>
	 */
	protected Filter createFilter(String text) {
		Filter filter = FilterDefinitionManager.instance.parse(text)
		if (!filter) {
			/*
			 * If parsing failed, assume it is a CQL filter.
			 */
			filter = FilterDefinitionManager.instance.from('CQL', text)
		}

		filter
	}

	public EntityDefinition toEntityDefinition() {
		EntityAccessorUtil.createEntity(eval())
	}

	public Definition toDefinition() {
		PathElement element = eval().elements.last()
		if (element.root) {
			element.root.definition
		}
		else {
			element.child.child
		}
	}

	public Object asType(Class clazz) {
		if (clazz == EntityDefinition || clazz == TypeEntityDefinition || clazz == PropertyEntityDefinition) {
			toEntityDefinition()
		}
		else if (clazz == Definition || clazz == TypeDefinition || clazz == PropertyDefinition || clazz == ChildDefinition) {
			toDefinition()
		}
		else {
			super.asType(clazz)
		}
	}
}
