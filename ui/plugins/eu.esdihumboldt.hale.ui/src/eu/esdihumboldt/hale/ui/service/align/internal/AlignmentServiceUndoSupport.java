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

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Decorator that adds undo/redo support to an alignment service.
 * @author Simon Templer
 */
public class AlignmentServiceUndoSupport extends AlignmentServiceDecorator {
	
	/**
	 * Operation that replaces a cell in the alignment.
	 */
	public class ReplaceOperation extends AbstractOperation {

		private final MutableCell oldCell;
		private final MutableCell newCell;

		/**
		 * Create an operation that replaces a cell in the alignment.
		 * @param oldCell the cell to replace
		 * @param newCell the new cell to add
		 */
		public ReplaceOperation(MutableCell oldCell, MutableCell newCell) {
			super("Replace an alignment cell");
			
			this.oldCell = oldCell;
			this.newCell = newCell;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.replaceCell(oldCell, newCell);
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.replaceCell(newCell, oldCell);
			return Status.OK_STATUS;
		}

	}

	/**
	 * Operation that cleans the alignment. 
	 */
	public class CleanOperation extends AbstractOperation {

		private MutableAlignment alignment;
		
		/**
		 * Create an operation that cleans the alignment.
		 * @param currentAlignment the current alignment, that is to be restored
		 *   on undo 
		 */
		public CleanOperation(MutableAlignment currentAlignment) {
			super("Clean the alignment");
			
			this.alignment = currentAlignment;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.clean();
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.addOrUpdateAlignment(alignment);
			return Status.OK_STATUS;
		}

	}

	/**
	 * Operations that removes a cell from the alignment service.
	 */
	public class RemoveCellOperation extends AbstractOperation {

		private final MutableCell cell;
		
		/**
		 * Create an operation removing the given cell from the alignment service.
		 * @param cell the cell
		 */
		public RemoveCellOperation(MutableCell cell) {
			super("Remove alignment cell");
			
			this.cell = cell;
		}

		/**
		 * @see AbstractOperation#execute(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.removeCell(cell);
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.addCell(cell);
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
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.addCell(cell);
			return Status.OK_STATUS;
		}

		/**
		 * @see AbstractOperation#redo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see AbstractOperation#undo(IProgressMonitor, IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			alignmentService.removeCell(cell);
			return Status.OK_STATUS;
		}

	}
	
	private static final ALogger log = ALoggerFactory.getLogger(AlignmentServiceUndoSupport.class);

	/**
	 * Create undo/redo support for the given alignment service.
	 * @param alignmentService the alignment service
	 */
	public AlignmentServiceUndoSupport(AlignmentService alignmentService) {
		super(alignmentService);
	}
	
	/**
	 * Execute an operation.
	 * @param operation the operation to execute
	 */
	protected void executeOperation(IUndoableOperation operation) {
		IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench().getOperationSupport();
		// service is workbench wide, so the operation should also be workbench wide
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
		//TODO implement undo support?
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
	 * @see AlignmentServiceDecorator#removeCell(Cell)
	 */
	@Override
	public synchronized void removeCell(Cell cell) {
		if (cell == null) {
			return;
		}
		
		boolean contains = getAlignment().getCells().contains(cell);
		if (contains && cell instanceof MutableCell) {
			/*
			 * Cell must be contained in the current alignment, else the redo
			 * would do something unexpected (readding a cell that was not
			 * previously there). 
			 * 
			 * Also, as long as there is no copy constructor in DefaultCell, 
			 * undo only for removing MutableCells supported.
			 */
			IUndoableOperation operation = new RemoveCellOperation((MutableCell) cell);
			executeOperation(operation);
		}
		else {
			super.removeCell(cell);
		}
	}

	/**
	 * @see AlignmentServiceDecorator#clean()
	 */
	@Override
	public synchronized void clean() {
		//XXX problem: what about cleans that should not be undone? e.g. when the schemas have changed
		//XXX -> currently on project clean the workbench history is reset
		Alignment alignment = getAlignment();
		if (alignment.getCells().isEmpty()) {
			return;
		}
		
		if (alignment instanceof MutableAlignment) {
			/*
			 * As long as there is no copy constructor in DefaultAlignment, 
			 * undo only supported if the current alignment is a 
			 * MutableAlignment.
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
					 * Cell must be contained in the current alignment, else the redo
					 * would do something unexpected (readding a cell that was not
					 * previously there). 
					 */
					addCell(newCell);
				}
				else {
					if (oldCell instanceof MutableCell) {
						/*
						 * As long as there is no copy constructor in DefaultCell, 
						 * undo only supported for MutableCells to be replaced.
						 */
						IUndoableOperation operation = new ReplaceOperation(
								(MutableCell) oldCell, newCell);
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
			removeCell(oldCell);
		}
	}

}
