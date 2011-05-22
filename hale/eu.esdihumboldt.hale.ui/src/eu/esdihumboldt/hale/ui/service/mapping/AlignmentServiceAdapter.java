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

package eu.esdihumboldt.hale.ui.service.mapping;

import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AlignmentServiceAdapter implements
		AlignmentServiceListener {

	/**
	 * @see AlignmentServiceListener#alignmentChanged()
	 */
	/*@Override
	public void alignmentChanged() {
		// override me
	}*/

	/**
	 * @see AlignmentServiceListener#alignmentCleared()
	 */
	@Override
	public void alignmentCleared() {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellRemoved(ICell)
	 */
	@Override
	public void cellRemoved(ICell cell) {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellsUpdated(Iterable)
	 */
	@Override
	public void cellsUpdated(Iterable<ICell> cells) {
		// override me
	}

	/**
	 * @see AlignmentServiceListener#cellsAdded(Iterable)
	 */
	@Override
	public void cellsAdded(Iterable<ICell> cells) {
		// override me
	}

	/**
	 * @see HaleServiceListener#update(UpdateMessage)
	 */
	@Override
	public void update(@SuppressWarnings("rawtypes") UpdateMessage message) {
		// override me if you are sure you need to be called on any event
	}

}
