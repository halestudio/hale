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
 * Provides a human readable explanation for a cell.
 * 
 * @author Simon Templer
 */
public interface CellExplanation {

	/**
	 * Get the explanation for the given cell.
	 * 
	 * @param cell the cell
	 * @return the cell explanation, <code>null</code> if none is available
	 */
	public String getExplanation(Cell cell);

	/**
	 * Get the explanation in html format for the given cell
	 * 
	 * @param cell the cell
	 * @return the cell explanation in html format, <code>null</code> if none is
	 *         available
	 */
	public String getExplanationAsHtml(Cell cell);

}
