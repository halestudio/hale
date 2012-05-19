/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.schemaprovider.model;

import eu.esdihumboldt.commons.goml.align.Entity;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@Deprecated
public interface Definition {
	
	/**
	 * Get the definitions identifier
	 * 
	 * @return the unique name of the definition
	 */
	public String getIdentifier();
	
	/**
	 * Get the definition's display name
	 * 
	 * @return the display name
	 */
	public String getDisplayName();
	
	/**
	 * Get the description
	 *  
	 * @return the description string or <code>null</code>
	 */
	public abstract String getDescription();

	/**
	 * Create an entity for the definition.
	 * 
	 * @return the entity
	 */
	public Entity getEntity();
	
	/**
	 * Get the definition location
	 * 
	 * @return the location
	 */
	public String getLocation();

}
