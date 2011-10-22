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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Represents a {@link Property}
 * @author Simon Templer
 */
public class PropertyBean extends EntityBean<PropertyEntityDefinition> {

	private List<QName> properties = new ArrayList<QName>();
	
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
		
		boolean first = true;
		for (Definition<?> definition : property.getDefinition().getPath()) {
			if (first) {
				setTypeName(definition.getName());
				
				first = false;
			}
			else {
				properties.add(definition.getName());
			}
		}
	}

	/**
	 * @see EntityBean#createEntity(TypeIndex)
	 */
	@Override
	public Entity createEntity(TypeIndex types) {
		return new DefaultProperty(createEntityDefinition(types));
	}

	/**
	 * @see EntityBean#createEntityDefinition(TypeIndex)
	 */
	@Override
	protected PropertyEntityDefinition createEntityDefinition(TypeIndex index) {
		TypeDefinition typeDef = index.getType(getTypeName());
		if (typeDef == null) {
			throw new IllegalStateException(MessageFormat.format(
					"TypeDefinition for type {0} not found", getTypeName()));
		}
		
		List<Definition<?>> path = new ArrayList<Definition<?>>();
		path.add(typeDef);
		
		DefinitionGroup parent = typeDef;
		for (QName propertyName : properties) {
			if (parent == null) {
				throw new IllegalStateException("Could not resolve property entity definition: child not present");
			}
			
			ChildDefinition<?> child = parent.getChild(propertyName);
			if (child == null) {
				throw new IllegalStateException("Could not resolve property entity definition: child not found");
			}
			
			path.add(child);
			
			if (child instanceof DefinitionGroup) {
				parent = (DefinitionGroup) child;
			}
			else {
				parent = null;
			}
		}
		
		return new PropertyEntityDefinition(path);
	}

	/**
	 * Get the property names
	 * @return the property names
	 */
	public List<QName> getProperties() {
		return properties;
	}

	/**
	 * Set the property names
	 * @param properties the property names to set
	 */
	public void setProperties(List<QName> properties) {
		this.properties = properties;
	}

}
