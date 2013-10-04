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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.jcip.annotations.Immutable;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.BaseAlignmentLoader;

/**
 * Decorator that adds undo/redo support to an alignment service.
 * 
 * @author Simon Templer
 */
public class AlignmentServiceUndoSupport extends AlignmentServiceDecorator {

	/**
	 * Operation that replaces a cell in the alignment.
	 */
	public class ReplaceOperation extends AbstractOperation {

		private final BiMap<MutableCell, MutableCell> cells;

		/**
		 * Create an operation that replaces a cell in the alignment.
		 * 
		 * @param oldCell the cell to replace
		 * @param newCell the new cell to add
		 */
		public ReplaceOperation(MutableCell oldCell, MutableCell newCell) {
			super("Replace an alignment cell");

			cells = HashBiMap.create(1);
			cells.put(oldCell, newCell);
		}

		/**
		 * Create an operation that replaces a cell in the alignment.
		 * 
		 * @param cells mapping from replaced cells to new cells
		 */
		public ReplaceOperation(BiMap<MutableCell, MutableCell> cells) {
			super("Replace alignment cells");

			this.cells = cells;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.replaceCells(cells);
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.replaceCells(cells.inverse());
			return Status.OK_STATUS;
		}

	}

	/**
	 * Operation that sets the priority of a cell.
	 */
	public class SetCellPropertyOperation extends AbstractOperation {

		private final Object oldProperty;
		private final Object newProperty;
		private final String mutableCellId;
		private final String propertyName;

		/**
		 * Create an operation that sets the priority of a cell.
		 * 
		 * @param mutableCellId the cell to set the property.
		 * @param propertyName the name of the property to set.
		 * @param oldProperty the old property value.
		 * @param newProperty the new property value.
		 * 
		 */
		public SetCellPropertyOperation(String mutableCellId, String propertyName,
				Object oldProperty, Object newProperty) {
			super("Set a cell property.");
			this.mutableCellId = mutableCellId;
			this.propertyName = propertyName;
			this.oldProperty = oldProperty;
			this.newProperty = newProperty;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.setCellProperty(mutableCellId, propertyName, newProperty);
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.setCellProperty(mutableCellId, propertyName, oldProperty);
			return Status.OK_STATUS;
		}

	}

	/**
	 * Operation that enables / disables a cell for a specific cell.
	 */
	public class DisableCellOperation extends AbstractOperation {

		private final boolean disable;
		private final String mutableCellId;
		private final Cell typeCell;

		/**
		 * Create an operation that disables a cell.
		 * 
		 * @param disable whether to disable or enable the cell
		 * @param mutableCellId the cell to set the priority
		 * @param typeCell the type cell for which to disable/enable the given
		 *            cell
		 */
		public DisableCellOperation(boolean disable, String mutableCellId, Cell typeCell) {
			super((disable ? "Disable" : "Enable") + " a cell");
			this.disable = disable;
			this.mutableCellId = mutableCellId;
			this.typeCell = typeCell;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.setCellProperty(mutableCellId, disable ? Cell.PROPERTY_DISABLE_FOR
					: Cell.PROPERTY_ENABLE_FOR, typeCell);
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.setCellProperty(mutableCellId, disable ? Cell.PROPERTY_ENABLE_FOR
					: Cell.PROPERTY_DISABLE_FOR, typeCell);
			return Status.OK_STATUS;
		}

	}

	/**
	 * Operation that cleans the alignment.
	 */
	public class CleanOperation extends AbstractOperation {

		private final MutableAlignment alignment;

		/**
		 * Create an operation that cleans the alignment.
		 * 
		 * @param currentAlignment the current alignment, that is to be restored
		 *            on undo
		 */
		public CleanOperation(MutableAlignment currentAlignment) {
			super("Clean the alignment");

			this.alignment = currentAlignment;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.clean();
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.addOrUpdateAlignment(alignment);
			return Status.OK_STATUS;
		}

	}

	/**
	 * Operations that removes a cell from the alignment service.
	 */
	public class RemoveCellOperation extends AbstractOperation {

		private final Collection<MutableCell> cells;

		/**
		 * Create an operation removing the given cell from the alignment
		 * service.
		 * 
		 * @param cells the cells
		 */
		public RemoveCellOperation(Collection<MutableCell> cells) {
			super("Remove alignment cell");

			this.cells = cells;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.removeCells(cells.toArray(new Cell[cells.size()]));
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			MutableAlignment alignment = new DefaultAlignment();
			for (MutableCell cell : cells) {
				alignment.addCell(cell);
			}
			alignmentService.addOrUpdateAlignment(alignment);
			return Status.OK_STATUS;
		}

	}

	/**
	 * Operation that adds a cell to the alignment service.
	 */
	@Immutable
	public class AddCellOperation extends AbstractOperation {

		private final MutableCell cell;

		/**
		 * Create an operation adding the given cell to the alignment service.
		 * 
		 * @param cell the cell
		 */
		public AddCellOperation(MutableCell cell) {
			super("Add alignment cell");

			this.cell = cell;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.addCell(cell);
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			alignmentService.removeCells(cell);
			return Status.OK_STATUS;
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(AlignmentServiceUndoSupport.class);

	/**
	 * Create undo/redo support for the given alignment service.
	 * 
	 * @param alignmentService the alignment service
	 */
	public AlignmentServiceUndoSupport(AlignmentService alignmentService) {
		super(alignmentService);
	}

	/**
	 * Execute an operation.
	 * 
	 * @param operation the operation to execute
	 */
	protected void executeOperation(IUndoableOperation operation) {
		IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench()
				.getOperationSupport();
		// service is workbench wide, so the operation should also be workbench
		// wide
		operation.addContext(operationSupport.getUndoContext());
//		operation.addContext(new ObjectUndoContext(alignmentService, "Alignment service"));
		try {
//			OperationHistoryFactory.getOperationHistory().execute(operation, null, null);
			operationSupport.getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			log.error("Error executing operation on alignment service", e);
		}
	}

	/**
	 * @see AlignmentServiceDecorator#addOrUpdateAlignment(MutableAlignment)
	 */
	@Override
	public synchronized void addOrUpdateAlignment(MutableAlignment alignment) {
		// TODO implement undo support?
		super.addOrUpdateAlignment(alignment);
	}

	/**
	 * @see AlignmentServiceDecorator#addCell(MutableCell)
	 */
	@Override
	public synchronized void addCell(MutableCell cell) {
		if (cell == null) {
			return;
		}

		IUndoableOperation operation = new AddCellOperation(cell);
		executeOperation(operation);
	}

	/**
	 * @see AlignmentServiceDecorator#removeCells(Cell[])
	 */
	@Override
	public synchronized void removeCells(Cell... cells) {
		if (cells == null || cells.length == 0) {
			return;
		}

		List<MutableCell> contained = new ArrayList<MutableCell>();
		for (Cell cell : cells) {
			if (cell instanceof MutableCell && getAlignment().getCells().contains(cell)) {
				contained.add((MutableCell) cell);
			}
		}

		if (!contained.isEmpty()) {
			/*
			 * Cells must be contained in the current alignment, else the redo
			 * would do something unexpected (readding a cell that was not
			 * previously there).
			 * 
			 * Also, as long as there is no copy constructor in DefaultCell,
			 * undo only for removing MutableCells supported.
			 */
			IUndoableOperation operation = new RemoveCellOperation(contained);
			executeOperation(operation);
		}
		else {
			super.removeCells(cells);
		}
	}

	/**
	 * @see AlignmentServiceDecorator#clean()
	 */
	@Override
	public synchronized void clean() {
		// XXX problem: what about cleans that should not be undone? e.g. when
		// the schemas have changed
		// XXX -> currently on project clean the workbench history is reset
		Alignment alignment = getAlignment();
		if (alignment.getCells().isEmpty()) {
			return;
		}

		if (alignment instanceof MutableAlignment) {
			/*
			 * As long as there is no copy constructor in DefaultAlignment, undo
			 * only supported if the current alignment is a MutableAlignment.
			 */
			IUndoableOperation operation = new CleanOperation((MutableAlignment) alignment);
			executeOperation(operation);
		}
		else {
			super.clean();
		}
	}

	/**
	 * @see AlignmentServiceDecorator#replaceCell(Cell, MutableCell)
	 */
	@Override
	public synchronized void replaceCell(Cell oldCell, MutableCell newCell) {
		if (oldCell != null && newCell != null) {
			if (oldCell != newCell) {
				boolean contains = getAlignment().getCells().contains(oldCell);
				if (!contains) {
					/*
					 * Cell must be contained in the current alignment, else the
					 * redo would do something unexpected (reading a cell that
					 * was not previously there).
					 */
					addCell(newCell);
				}
				else {
					if (oldCell instanceof MutableCell) {
						/*
						 * As long as there is no copy constructor in
						 * DefaultCell, undo only supported for MutableCells to
						 * be replaced.
						 */
						IUndoableOperation operation = new ReplaceOperation((MutableCell) oldCell,
								newCell);
						executeOperation(operation);
					}
					else {
						super.replaceCell(oldCell, newCell);
					}
				}
			}
		}
		else if (newCell != null) {
			addCell(newCell);
		}
		else if (oldCell != null) {
			removeCells(oldCell);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentService#replaceCells(java.util.Map)
	 */
	@Override
	public void replaceCells(Map<? extends Cell, MutableCell> cells) {
		BiMap<MutableCell, MutableCell> map = HashBiMap.create(cells.size());
		for (Entry<? extends Cell, MutableCell> e : cells.entrySet()) {
			if (e.getKey() instanceof MutableCell && e.getValue() != null
					&& getAlignment().getCells().contains(e.getKey()))
				map.put((MutableCell) e.getKey(), e.getValue());
			else {
				log.warn("Replaced cells contains at least one cell which "
						+ "is either not mutable or not in the alignment. "
						+ "No undo/redo possible.");

				super.replaceCells(cells);
				return;
			}
		}

		IUndoableOperation operation = new ReplaceOperation(map);
		executeOperation(operation);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentService#setCellProperty(java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void setCellProperty(String cellId, String propertyName, Object property) {
		if (Cell.PROPERTY_DISABLE_FOR.equals(propertyName)
				|| Cell.PROPERTY_ENABLE_FOR.equals(propertyName)) {
			IUndoableOperation operation = new DisableCellOperation(
					Cell.PROPERTY_DISABLE_FOR.equals(propertyName), cellId, (Cell) property);
			executeOperation(operation);
		}
		else if (Cell.PROPERTY_PRIORITY.equals(propertyName)) {
			if (property instanceof Priority) {
				Priority newPriority = (Priority) property;
				Cell cell = getAlignment().getCell(cellId);
				Priority oldPriority = cell.getPriority();
				IUndoableOperation operation = new SetCellPropertyOperation(cellId, propertyName,
						oldPriority, newPriority);
				executeOperation(operation);
			}
		}
		else if (Cell.PROPERTY_TRANSFORMATION_MODE.equals(propertyName)) {
			Cell cell = getAlignment().getCell(cellId);
			Object oldValue = cell.getTransformationMode();
			IUndoableOperation operation = new SetCellPropertyOperation(cellId, propertyName,
					oldValue, property);
			executeOperation(operation);
		}
		else {
			log.warn("An unknown cell property is set. No undo support.");
			alignmentService.setCellProperty(cellId, propertyName, property);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.internal.AlignmentServiceDecorator#addBaseAlignment(eu.esdihumboldt.hale.ui.service.align.BaseAlignmentLoader)
	 */
	@Override
	public boolean addBaseAlignment(BaseAlignmentLoader loader) {
		// TODO implement undo support?
		return alignmentService.addBaseAlignment(loader);
	}

}
