/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.util.Pair;

/**
 * Represents a {@link Property}.
 * 
 * @author Simon Templer
 */
public class PropertyBean extends EntityBean<PropertyEntityDefinition> {

	private List<ChildContextBean> properties = new ArrayList<ChildContextBean>();

	/**
	 * Default constructor
	 */
	public PropertyBean() {
		super();
	}

	/**
	 * Create a property entity bean based on the given property entity
	 * 
	 * @param property the property entity
	 */
	public PropertyBean(Property property) {
		super();

		setTypeName(property.getDefinition().getType().getName());
		setFilter(FilterDefinitionManager.getInstance().asString(
				property.getDefinition().getFilter()));

		for (ChildContext child : property.getDefinition().getPropertyPath()) {
			properties.add(new ChildContextBean(child));
		}
	}

	/**
	 * @see EntityBean#createEntity(TypeIndex, SchemaSpaceID)
	 */
	@Override
	public Entity createEntity(TypeIndex types, SchemaSpaceID schemaSpace) {
		return new DefaultProperty(createEntityDefinition(types, schemaSpace));
	}

	/**
	 * @see EntityBean#createEntityDefinition(TypeIndex, SchemaSpaceID)
	 */
	@Override
	protected PropertyEntityDefinition createEntityDefinition(TypeIndex index,
			SchemaSpaceID schemaSpace) {
		TypeDefinition typeDef = index.getType(getTypeName());
		if (typeDef == null) {
			throw new IllegalStateException(MessageFormat.format(
					"TypeDefinition for type {0} not found", getTypeName()));
		}

		List<ChildContext> path = new ArrayList<ChildContext>();

		DefinitionGroup parent = typeDef;
		for (ChildContextBean childContext : properties) {
			if (parent == null) {
				throw new IllegalStateException(
						"Could not resolve property entity definition: child not present");
			}

			Pair<ChildDefinition<?>, List<ChildDefinition<?>>> childs = findChild(parent,
					childContext.getChildName());

			ChildDefinition<?> child = childs.getFirst();

			// if the child is still null throw an exception
			if (child == null) {
				throw new IllegalStateException(
						"Could not resolve property entity definition: child not found");
			}

			if (childs.getSecond() != null) {
				for (ChildDefinition<?> pathElems : childs.getSecond()) {
					path.add(new ChildContext(childContext.getContextName(), childContext
							.getContextIndex(), createCondition(childContext.getConditionFilter()),
							pathElems));
				}
			}

			path.add(new ChildContext(childContext.getContextName(),
					childContext.getContextIndex(), createCondition(childContext
							.getConditionFilter()), child));

			if (child instanceof DefinitionGroup) {
				parent = (DefinitionGroup) child;
			}
			else if (child.asProperty() != null) {
				parent = child.asProperty().getPropertyType();
			}
			else {
				parent = null;
			}
		}

		return new PropertyEntityDefinition(typeDef, path, schemaSpace, FilterDefinitionManager
				.getInstance().parse(getFilter()));
	}

	/**
	 * The function to look for a child as ChildDefinition or as Group
	 * 
	 * @param parent the starting point to traverse from
	 * @param childName the name of the parent's child
	 * @return a pair of child and a list with the full path from parent to the
	 *         child or <code>null</code> if no such child was found
	 */
	public static Pair<ChildDefinition<?>, List<ChildDefinition<?>>> findChild(
			DefinitionGroup parent, QName childName) {

		ChildDefinition<?> child = parent.getChild(childName);
		if (child == null) {
			// if the child is null there can be still a childname

			// if the namespace is not null
			if (childName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
				// get all children and iterate over them
				Collection<? extends ChildDefinition<?>> children = DefinitionUtil
						.getAllChildren(parent);
				for (ChildDefinition<?> _child : children) {
					// try to find another child with the same local part,
					// if we find a child with the same local part but
					// different namespace we overwrite child
					if (_child.getName().getLocalPart().equals(childName.getLocalPart())) {
						child = _child;
						break;
					}
				}
			}

		}

		if (child != null) {
			return new Pair<ChildDefinition<?>, List<ChildDefinition<?>>>(child, null);
		}

		Collection<? extends ChildDefinition<?>> children = DefinitionUtil.getAllChildren(parent);

		for (ChildDefinition<?> groupChild : children) {

			if (groupChild.asGroup() != null) {
				GroupPropertyDefinition temp = groupChild.asGroup();

				if (findChild(temp, childName) != null) {
					Pair<ChildDefinition<?>, List<ChildDefinition<?>>> recTemp = findChild(temp,
							childName);

					if (recTemp.getSecond() == null) {
						List<ChildDefinition<?>> second = new ArrayList<ChildDefinition<?>>();
						second.add(temp);
						ChildDefinition<?> first = recTemp.getFirst();
						return new Pair<ChildDefinition<?>, List<ChildDefinition<?>>>(first, second);
					}
					else {
						recTemp.getSecond().add(0, temp);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Create a condition.
	 * 
	 * @param conditionFilter the condition filter
	 * @return the condition or <code>null</code>
	 */
	private Condition createCondition(String conditionFilter) {
		Filter filter = FilterDefinitionManager.getInstance().parse(conditionFilter);
		if (filter != null) {
			return new Condition(filter);
		}
		return null;
	}

	/**
	 * Get the property names
	 * 
	 * @return the property names
	 */
	public List<ChildContextBean> getProperties() {
		return properties;
	}

	/**
	 * Set the property names
	 * 
	 * @param properties the property names to set
	 */
	public void setProperties(List<ChildContextBean> properties) {
		this.properties = properties;
	}

}
