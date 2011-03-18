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

package eu.esdihumboldt.cst.transformer;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.goml.align.Cell;

/**
 * {@link ICell}/{@link Cell} utility methods
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class CellUtils {
	
	/**
	 * Get a string representation of a cell
	 * 
	 * @param cell the cell
	 * @return its string representation
	 */
	public static String asString(ICell cell) {
		ITransformation trans = cell.getEntity1().getTransformation();
		String entityString = null;
		if (trans != null) {
			entityString = EntityUtils.asString(cell.getEntity1()) + " -> " //$NON-NLS-1$
				+ EntityUtils.asString(cell.getEntity2());
		}
		else {
			trans = cell.getEntity2().getTransformation();
			
			entityString = EntityUtils.asString(cell.getEntity2());
		}
		
		if (trans != null) {
			return trans.getService().toString() + " : " + entityString; //$NON-NLS-1$
		}
		else {
			return entityString;
		}
	}

}
