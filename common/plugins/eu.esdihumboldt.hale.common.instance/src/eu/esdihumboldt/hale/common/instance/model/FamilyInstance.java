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

package eu.esdihumboldt.hale.common.instance.model;

import java.util.Collection;

/**
 * FamilyInstance is an Instance with functionality to add child instance links.
 * 
 * @author Kai Schwierczek
 */
public interface FamilyInstance extends Instance {

	/**
	 * Returns the child instances.
	 * 
	 * @return the child instances
	 */
	public Collection<FamilyInstance> getChildren();

	/**
	 * Adds the given instance as child to this instance.
	 * 
	 * @param child the child instance to add
	 */
	public void addChild(FamilyInstance child);
}
