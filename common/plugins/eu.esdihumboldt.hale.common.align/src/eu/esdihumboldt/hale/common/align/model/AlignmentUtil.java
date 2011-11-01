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

/**
 * Alignment model utility methods.
 * @author Simon Templer
 */
public abstract class AlignmentUtil {

	/**
	 * Determines if the given cell is a type cell.
	 * @param cell the cell
	 * @return if the cell is a type cell
	 */
	public static boolean isTypeCell(Cell cell) {
		// check if cell is a type cell
		return cell.getTarget().values().iterator().next() instanceof Type;
	}
	
	/**
	 * Determines if the given cell is an augmentation.
	 * @param cell the cell
	 * @return if the cell is an augmentation
	 */
	public static boolean isAugmentation(Cell cell) {
		// check if cell is an augmentation cell
		return cell.getSource().isEmpty();
	}

}
