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

import java.util.List;

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
		List<String> label = entity.getLabel();
		if (label.size() == 2) {
			return label.get(1);
		}
		else if (label.size() > 2) {
			return label.get(label.size() - 2) + "." + label.get(label.size() - 1);
		}
		else if (label.size() == 1) {
			return label.get(0);
		}
		
		return "unnamed";
	}
	
	/**
	 * Get the last label entry for the given entity
	 * 
	 * @param entity the entity
	 * @return the last label entry
	 */
	public static String getLastName(IEntity entity) {
		List<String> label = entity.getLabel();
		if (label.size() > 0) {
			return label.get(label.size() - 1);
		}
		
		return "unnamed";
	}

}
