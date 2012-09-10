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

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;

/**
 * A name associated with an entity bean
 * 
 * @author Simon Templer
 */
public class NamedEntityBean {

	private String name;

	private EntityBean<?> entity;

	/**
	 * Default constructor
	 */
	public NamedEntityBean() {
		super();
	}

	/**
	 * Create a named entity bean and initialize it using the given entity
	 * 
	 * @param name the entity name
	 * @param entity the entity
	 */
	public NamedEntityBean(String name, Entity entity) {
		this.name = name;

		if (entity instanceof Type) {
			this.entity = new TypeBean((Type) entity);
		}
		else if (entity instanceof Property) {
			this.entity = new PropertyBean((Property) entity);
		}
		else {
			throw new IllegalArgumentException("Unsupported entity type: "
					+ entity.getClass().getSimpleName());
		}
	}

	/**
	 * Get the entity name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the entity name
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the entity bean
	 * 
	 * @return the entity
	 */
	public EntityBean<?> getEntity() {
		return entity;
	}

	/**
	 * Set the entity bean
	 * 
	 * @param entity the entity to set
	 */
	public void setEntity(EntityBean<?> entity) {
		this.entity = entity;
	}

}
