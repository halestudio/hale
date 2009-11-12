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
package eu.esdihumboldt.hale.rcp.utils;

import eu.esdihumboldt.cst.align.IEntity;

/**
 * Entity utility methods
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class EntityHelper {
	
	/**
	 * Get a short name for the given entity
	 * 
	 * @param entity the entity
	 * @return the short name
	 */
	public static String getShortName(IEntity entity) {
		if (entity.getAbout() != null && entity.getAbout().getAbout() != null ) {
			String label = entity.getAbout().getAbout();
			String[] nameparts = label.split("\\/");
			return nameparts[nameparts.length -1];
		}
		String label = entity.getAbout().getAbout();
		String[] nameparts = label.split("\\/");
		return "unnamed";
	}

}
