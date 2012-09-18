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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Represents an {@link Entity}
 * 
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
	 * 
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
	 * 
	 * @param index the type index
	 * @param schemaSpace the associated schema space
	 * @return the entity definition
	 */
	protected abstract T createEntityDefinition(TypeIndex index, SchemaSpaceID schemaSpace);

	/**
	 * Create the represented entity
	 * 
	 * @param types the type index
	 * @param schemaSpace the associated schema space
	 * @return the entity
	 */
	public abstract Entity createEntity(TypeIndex types, SchemaSpaceID schemaSpace);

	/**
	 * Get the type filter.
	 * 
	 * @return the type filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Set the type filter.
	 * 
	 * @param filter the type filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * Get the type name
	 * 
	 * @return the type name
	 */
	public QName getTypeName() {
		return typeName;
	}

	/**
	 * Set the type name
	 * 
	 * @param typeName the type name to set
	 */
	public void setTypeName(QName typeName) {
		this.typeName = typeName;
	}

}
