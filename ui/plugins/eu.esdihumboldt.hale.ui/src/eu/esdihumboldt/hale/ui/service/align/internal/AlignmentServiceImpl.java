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

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

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
	 * @param projectService the project service 
	 */
	public AlignmentServiceImpl(final ProjectService projectService) {
		super();
		
		alignment = new DefaultAlignment();
		
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				clean();
			}
			
		});
		
		// inform project service on alignment changes
		addListener(new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				projectService.setChanged();
			}

			@Override
			public void cellRemoved(Cell cell) {
				projectService.setChanged();
			}

			@Override
			public void cellReplaced(Cell oldCell, Cell newCell) {
				projectService.setChanged();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				projectService.setChanged();
			}
		});
	}

	/**
	 * @see AlignmentService#addCell(MutableCell)
	 */
	@Override
	public void addCell(MutableCell cell) {
		synchronized (this) {
			alignment.addCell(cell);
		}
		notifyCellsAdded(Collections.singletonList((Cell) cell));
	}

	/**
	 * @see AlignmentService#replaceCell(Cell, MutableCell)
	 */
	@Override
	public void replaceCell(Cell oldCell, MutableCell newCell) {
		synchronized (this) {
			alignment.removeCell(oldCell);
			alignment.addCell(newCell);
		}
		notifyCellReplaced(oldCell, newCell);
	}

	/**
	 * @see AlignmentService#clean()
	 */
	@Override
	public void clean() {
		synchronized (this) {
			alignment = new DefaultAlignment();
		}
		notifyAlignmentCleared();
	}

	
	/**
	 * @see AlignmentService#addOrUpdateAlignment(MutableAlignment)
	 */
	@Override
	public void addOrUpdateAlignment(MutableAlignment alignment) {
		Collection<Cell> added = new ArrayList<Cell>();
		
		// add cells
		synchronized (this) {
			for (Cell cell : alignment.getCells()) {
				this.alignment.addCell(cell);
				added.add(cell);
			}
		}
		
		if (!added.isEmpty()) {
			notifyCellsAdded(added);
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
