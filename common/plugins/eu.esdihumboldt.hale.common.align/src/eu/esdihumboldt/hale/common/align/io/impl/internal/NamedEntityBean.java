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
