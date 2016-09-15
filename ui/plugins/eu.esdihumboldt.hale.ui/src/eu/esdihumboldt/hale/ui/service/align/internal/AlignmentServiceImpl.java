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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ModifiableCell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.BaseAlignmentLoader;
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
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				projectService.setChanged();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				projectService.setChanged();
			}

			@Override
			public void alignmentChanged() {
				projectService.setChanged();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				projectService.setChanged();
			}

			@Override
			public void customFunctionsChanged() {
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

	@Override
	public void addCustomPropertyFunction(CustomPropertyFunction function) {
		alignment.addCustomPropertyFunction(function);

		notifyCustomFunctionsChanged();
	}

	@Override
	public void removeCustomPropertyFunction(String id) {
		alignment.removeCustomPropertyFunction(id);

		notifyCustomFunctionsChanged();
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
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentService#replaceCells(java.util.Map)
	 */
	@Override
	public void replaceCells(Map<? extends Cell, MutableCell> cells) {
		synchronized (this) {
			for (Entry<? extends Cell, MutableCell> e : cells.entrySet()) {
				alignment.removeCell(e.getKey());
				alignment.addCell(e.getValue());
			}
		}
		notifyCellsReplaced(cells);
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

		/*
		 * XXX Updates for disables not supported
		 * 
		 * One problem is, that base alignments shouldn't be added twice, so if
		 * the given alignment contains a base alignment that is also contained
		 * in the current alignment it won't be added.
		 * 
		 * Now what about cells in the given alignment, that reference those
		 * base alignments (-> disabled for)? They need to be changed to the
		 * base alignment cells of the current alignment.
		 * 
		 * Those cells can be obtained by replacing the prefix part of the cell
		 * and using getCell(String).
		 * 
		 * XXX Addition of base alignments not supported
		 * 
		 * Here the problem is, that it is not enough to gather the base
		 * alignment cells and change their prefix to a new one, since the base
		 * cell could be another base alignment cell, which would need to
		 * change, too. They would have to be added in order. An option would be
		 * to load them with the base alignment loading mechanism, which would
		 * result in a loss of additional disables.
		 * 
		 * XXX Changes of conflicting base alignment cells not handled
		 */

//		if (!alignment.getBaseAlignments().isEmpty()) {
//			_log.warn("Adding alignments currently does not support merging of base alignments. Import base alignments independently.");
//		}

		boolean addedBaseCells = false;

		// add cells
		synchronized (this) {

			for (Cell cell : alignment.getCells()) {
				if (cell instanceof MutableCell) {
					this.alignment.addCell((MutableCell) cell);
					added.add(cell);
				}
				else if (!(cell instanceof BaseAlignmentCell)) {
					throw new IllegalStateException(
							"The given alignment contained a cell which is neither mutable nor from a base alignment.");
				}
				else {
					addedBaseCells = true;
				}
			}
		}

		synchronized (this) {
			// XXX this needs merging, see above
			boolean first = true;
			for (Entry<String, URI> baseAlignment : alignment.getBaseAlignments().entrySet()) {
				Collection<CustomPropertyFunction> baseFunctions;
				if (first) {
					// XXX hack - insert all base functions with the first base
					// alignment
					baseFunctions = alignment.getBasePropertyFunctions().values();
				}
				else {
					// base functions should only be added once
					baseFunctions = Collections.<CustomPropertyFunction> emptyList();
				}

				this.alignment.addBaseAlignment(baseAlignment.getKey(), baseAlignment.getValue(),
						alignment.getBaseAlignmentCells(baseAlignment.getValue()), //
						baseFunctions);
			}
		}

		// add custom functions
		boolean addedFunctions = false;
		synchronized (this) {
			for (CustomPropertyFunction cf : alignment.getCustomPropertyFunctions().values()) {
				this.alignment.addCustomPropertyFunction(cf);
				addedFunctions = true;
			}
		}

		if (addedBaseCells || (!added.isEmpty() && addedFunctions)) {
			// only emit one combined event (not to trigger multiple
			// transformations)
			notifyAlignmentChanged();
		}
		else {
			// emit individual events

			if (!added.isEmpty()) {
				notifyCellsAdded(added);
			}

			if (addedFunctions) {
				notifyCustomFunctionsChanged();
			}
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
		if (cell instanceof ModifiableCell && (Cell.PROPERTY_DISABLE_FOR.equals(propertyName)
				|| Cell.PROPERTY_ENABLE_FOR.equals(propertyName))) {
			boolean disable = Cell.PROPERTY_DISABLE_FOR.equals(propertyName);
			if (property instanceof Cell) {
				Cell other = (Cell) property;
				if (!AlignmentUtil.isTypeCell(other))
					throw new IllegalArgumentException();
				// This call may fail, if the cell was disabled in a base
				// alignment and someone tries to enable it again.
				((ModifiableCell) cell).setDisabledFor(other, disable);
			}
			else
				throw new IllegalArgumentException();

			notifyCellsPropertyChanged(Arrays.asList(cell), propertyName);
		}
		else if (Cell.PROPERTY_TRANSFORMATION_MODE.equals(propertyName)) {
			// set the transformation mode
			if (cell instanceof ModifiableCell && property instanceof TransformationMode) {
				ModifiableCell modCell = (ModifiableCell) cell;
				modCell.setTransformationMode((TransformationMode) property);
				notifyCellsPropertyChanged(Arrays.asList(cell), propertyName);
			}
		}
		else if (cell instanceof MutableCell) {
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
				notifyCellsPropertyChanged(Arrays.asList(cell), propertyName);
			}
		}
		else {
			throw new IllegalArgumentException("No mutable cell by the given id found: " + cellId);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentService#addBaseAlignment(eu.esdihumboldt.hale.ui.service.align.BaseAlignmentLoader)
	 */
	@Override
	public boolean addBaseAlignment(BaseAlignmentLoader loader) {
		boolean success;
		synchronized (this) {
			success = loader.load(alignment);
		}
		if (success)
			notifyAlignmentChanged();
		return success;
	}

}
