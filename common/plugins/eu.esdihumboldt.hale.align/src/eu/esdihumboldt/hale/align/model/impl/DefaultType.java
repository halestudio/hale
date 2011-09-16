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

package eu.esdihumboldt.hale.align.model.impl;

import eu.esdihumboldt.hale.align.model.Entity;
import eu.esdihumboldt.hale.align.model.Type;

/**
 * Represents a type in a mapping cell
 * @author Simon Templer
 */
public class DefaultType implements Type {

	private final TypeEntityDefinition definition;
	
	/**
	 * Create a type entity
	 * @param definition the type entity definition
	 */
	public DefaultType(TypeEntityDefinition definition) {
		super();
		this.definition = definition;
	}

	/**
	 * @see Entity#getDefinition()
	 */
	@Override
	public TypeEntityDefinition getDefinition() {
		return definition;
	}
	
	//TODO type filter/restriction stuff

}
