/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.service.align.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.align.model.Alignment;
import eu.esdihumboldt.hale.align.model.Cell;
import eu.esdihumboldt.hale.align.model.MutableAlignment;
import eu.esdihumboldt.hale.align.model.MutableCell;
import eu.esdihumboldt.hale.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Default {@link AlignmentService} implementation
 * 
 * @author Thorsten Reitz
 * @author Simon Templer 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class AlignmentServiceImpl extends AbstractAlignmentService {

//	private static ALogger _log = ALoggerFactory.getLogger(AlignmentServiceImpl.class);

	private MutableAlignment alignment;

	/**
	 * Default constructor
	 */
	public AlignmentServiceImpl() {
		super();
		
		alignment = new DefaultAlignment();
	}

	/**
	 * @see AlignmentService#addOrUpdateCell(MutableCell)
	 */
	@Override
	public void addOrUpdateCell(MutableCell cell) {
		boolean replaced;
		synchronized (this) {
			replaced = alignment.addCell(cell);
		}
		if (!replaced) {
			notifyCellsAdded(Collections.singletonList((Cell) cell));
		}
		else {
			notifyCellsUpdated(Collections.singletonList((Cell) cell));
		}
	}

	/**
	 * @see AlignmentService#clean()
	 */
	@Override
	public void clean() {
		synchronized (this) {
			alignment = new DefaultAlignment();
		}
	}

	
	/**
	 * @see AlignmentService#addOrUpdateAlignment(MutableAlignment)
	 */
	@Override
	public void addOrUpdateAlignment(MutableAlignment alignment) {
		Collection<Cell> added = new ArrayList<Cell>();
		Collection<Cell> updated = new ArrayList<Cell>();
		
		// add cells
		synchronized (this) {
			for (MutableCell cell : alignment.getCells()) {
				boolean replaced = alignment.addCell(cell);
				if (!replaced) {
					added.add(cell);
				}
				else {
					updated.add(cell);
				}
			}
		}
		
		if (!added.isEmpty()) {
			notifyCellsAdded(added);
		}
		if (!updated.isEmpty()) {
			notifyCellsUpdated(updated);
		}
	}

	/**
	 * @see AlignmentService#getAlignment()
	 */
	@Override
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @see AlignmentService#removeCell(Cell)
	 */
	@Override
	public void removeCell(Cell cell) {
		if (alignment.removeCell(cell)) {
			notifyCellRemoved(cell);
		}
	}

}
