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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Represents an {@link Entity}
 * @param <T> the entity definition type
 * @author Simon Templer
 */
public abstract class EntityBean<T extends EntityDefinition> {
	
	private QName typeName;
	
	private String filter;
	
	/**
	 * Default constructor 
	 */
	public EntityBean() {
		super();
	}

	/**
	 * Create an entity bean with the given type name
	 * @param typeName the type name
	 * @param filter the filter to the type entity
	 */
	public EntityBean(QName typeName, String filter) {
		super();
		this.typeName = typeName;
		this.filter = filter;
	}

	/**
	 * Get the entity definition
	 * @param index the type index
	 * @param schemaSpace the associated schema space
	 * @return the entity definition
	 */
	protected abstract T createEntityDefinition(TypeIndex index, 
			SchemaSpaceID schemaSpace);
	
	/**
	 * Create the represented entity
	 * @param types the type index
	 * @param schemaSpace the associated schema space
	 * @return the entity
	 */
	public abstract Entity createEntity(TypeIndex types, 
			SchemaSpaceID schemaSpace);

	/**
	 * Get the type filter.
	 * @return the type filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Set the type filter.
	 * @param filter the type filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * Get the type name
	 * @return the type name
	 */
	public QName getTypeName() {
		return typeName;
	}

	/**
	 * Set the type name
	 * @param typeName the type name to set
	 */
	public void setTypeName(QName typeName) {
		this.typeName = typeName;
	}

}
