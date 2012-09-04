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
 * Mutable {@link Alignment} which is used where changes to the alignment are
 * allowed.
 * 
 * @author Simon Templer
 */
public interface MutableAlignment extends Alignment {

	/**
	 * Add a cell to the alignment
	 * 
	 * @param cell the cell to add. It should be already configured, especially
	 *            with the cell target
	 */
	public void addCell(Cell cell);

	/**
	 * Remove a cell
	 * 
	 * @param cell the cell to remove
	 * @return if the cell was present and removed
	 */
	public boolean removeCell(Cell cell);

}
