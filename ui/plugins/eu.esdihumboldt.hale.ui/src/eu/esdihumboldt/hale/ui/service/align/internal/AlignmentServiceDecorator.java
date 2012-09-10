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

package eu.esdihumboldt.hale.ui.service.align.internal;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * Alignment service decorator
 * 
 * @author Simon Templer
 */
public abstract class AlignmentServiceDecorator implements AlignmentService {

	/**
	 * The decorated alignment service
	 */
	protected final AlignmentService alignmentService;

	/**
	 * Create a decorator for the given alignment service
	 * 
	 * @param alignmentService the alignment service
	 */
	public AlignmentServiceDecorator(AlignmentService alignmentService) {
		super();
		this.alignmentService = alignmentService;
	}

	/**
	 * @see AlignmentService#getAlignment()
	 */
	@Override
	public Alignment getAlignment() {
		return alignmentService.getAlignment();
	}

	/**
	 * @see AlignmentService#addOrUpdateAlignment(MutableAlignment)
	 */
	@Override
	public void addOrUpdateAlignment(MutableAlignment alignment) {
		alignmentService.addOrUpdateAlignment(alignment);
	}

	/**
	 * @see AlignmentService#addCell(MutableCell)
	 */
	@Override
	public void addCell(MutableCell cell) {
		alignmentService.addCell(cell);
	}

	/**
	 * @see AlignmentService#removeCell(Cell)
	 */
	@Override
	public void removeCell(Cell cell) {
		alignmentService.removeCell(cell);
	}

	/**
	 * @see AlignmentService#clean()
	 */
	@Override
	public void clean() {
		alignmentService.clean();
	}

	/**
	 * @see AlignmentService#addListener(AlignmentServiceListener)
	 */
	@Override
	public void addListener(AlignmentServiceListener listener) {
		alignmentService.addListener(listener);
	}

	/**
	 * @see AlignmentService#removeListener(AlignmentServiceListener)
	 */
	@Override
	public void removeListener(AlignmentServiceListener listener) {
		alignmentService.removeListener(listener);
	}

	/**
	 * @see AlignmentService#replaceCell(Cell, MutableCell)
	 */
	@Override
	public void replaceCell(Cell oldCell, MutableCell newCell) {
		alignmentService.replaceCell(oldCell, newCell);
	}

}
