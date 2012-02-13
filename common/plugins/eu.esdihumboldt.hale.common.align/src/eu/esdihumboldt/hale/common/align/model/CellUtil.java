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

package eu.esdihumboldt.hale.common.align.model;

import java.util.List;

import com.google.common.collect.ListMultimap;

/**
 * Cell related utility methods.
 * @author Simon Templer
 */
public abstract class CellUtil {

	/**
	 * Get the first entity from the given entities map (as contained e.g. as
	 * source or target in a cell).
	 * @param entities the entities map
	 * @return first entity or <code>null</code> if there is none
	 */
	public static Entity getFirstEntity(ListMultimap<String, ? extends Entity> entities) {
		if (entities == null || entities.isEmpty()) {
			return null;
		}
		return entities.entries().iterator().next().getValue();
	}

	/**
	 * Get the first parameter with the given name in the given cell.
	 * @param cell the cell
	 * @param parameterName the parameter name 
	 * @return the parameter value or <code>null</code>
	 */
	public static String getFirstParameter(Cell cell, String parameterName) {
		ListMultimap<String, String> params = cell.getTransformationParameters();
		if (params != null) {
			List<String> values =  params.get(parameterName);
			if (values != null && !values.isEmpty()) {
				return values.get(0);
			}
		}
		
		return null;
	}

}
