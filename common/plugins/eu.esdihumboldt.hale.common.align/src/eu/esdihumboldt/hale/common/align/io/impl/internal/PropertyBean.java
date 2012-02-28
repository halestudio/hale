/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Represents a {@link Property}.
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
				throw new IllegalStateException("Could not resolve property entity definition: child not present");
			}
			
			ChildDefinition<?> child = parent.getChild(childContext.getChildName());
			if (child == null) {
				throw new IllegalStateException("Could not resolve property entity definition: child not found");
			}
			
			path.add(new ChildContext(
					childContext.getContextName(), 
					childContext.getContextIndex(), 
					createCondition(childContext.getConditionFilter()), 
					child));
			
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
		
		return new PropertyEntityDefinition(typeDef, path, schemaSpace, 
				FilterDefinitionManager.getInstance().parse(getFilter()));
	}

	/**
	 * Create a condition.
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
	 * @return the property names
	 */
	public List<ChildContextBean> getProperties() {
		return properties;
	}

	/**
	 * Set the property names
	 * @param properties the property names to set
	 */
	public void setProperties(List<ChildContextBean> properties) {
		this.properties = properties;
	}

}
