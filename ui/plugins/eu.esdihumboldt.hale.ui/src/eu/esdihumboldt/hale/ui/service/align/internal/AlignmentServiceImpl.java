/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.ui.service.align.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Priority;
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
	 * 
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
			public void cellsRemoved(Iterable<Cell> cell) {
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
	 * @see AlignmentService#removeCells(Cell[])
	 */
	@Override
	public void removeCells(Cell... cells) {
		if (cells == null || cells.length == 0) {
			return;
		}

		List<Cell> removed = new ArrayList<Cell>();
		synchronized (this) {
			for (Cell cell : cells) {
				if (alignment.removeCell(cell)) {
					removed.add(cell);
				}
			}
		}
		notifyCellsRemoved(removed);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentService#setCellProperty(java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void setCellProperty(String cellId, String propertyName, Object property) {
		if (propertyName == null || property == null) {
			throw new IllegalArgumentException("Mandatory parameter is null");
		}
		Cell cell = getAlignment().getCell(cellId);
		if (cell instanceof MutableCell) {
			MutableCell mutableCell = (MutableCell) cell;
			if (Cell.PROPERTY_PRIORITY.equals(propertyName)) {
				if (property instanceof Priority) {
					Priority priority = (Priority) property;
					mutableCell.setPriority(priority);
				}
				if (property instanceof String) {
					String priorityStr = (String) property;
					Priority priority = Priority.valueOf(priorityStr);
					if (priority != null) {
						mutableCell.setPriority(priority);
					}
					else {
						throw new IllegalArgumentException();
					}
				}
				notifyCellsUpdated(Arrays.asList(cell));
			}
		}
		else {
			throw new IllegalArgumentException("No mutable cell by the given id found: " + cellId);
		}
	}

}
