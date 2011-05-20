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

package eu.esdihumboldt.hale.models.alignment;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Listener for alignment service events
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface AlignmentServiceListener extends HaleServiceListener {
	
	/**
	 * Called when the alignment has been cleared
	 */
	public void alignmentCleared();
	
	/**
	 * Called when the whole alignment has changed and calling lesser events
	 *   is not feasible
	 */
	//public void alignmentChanged();
	
	/**
	 * Called when cells have been added
	 * 
	 * @param cells the cells that have been added
	 */
	public void cellsAdded(Iterable<ICell> cells);
	
	/**
	 * Called when existing cells have been updated
	 * 
	 * @param cells the cells that have been updated
	 */
	public void cellsUpdated(Iterable<ICell> cells);
	
	/**
	 * Called when an existing cell has been removed
	 * 
	 * @param cell the cell that has been removed
	 */
	public void cellRemoved(ICell cell);

}
