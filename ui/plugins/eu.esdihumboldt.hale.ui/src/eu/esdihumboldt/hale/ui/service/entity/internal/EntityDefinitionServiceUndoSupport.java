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

package eu.esdihumboldt.hale.ui.service.entity.internal;

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
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Decorator that adds undo/redo support to an entity definition service.
 * 
 * @author Kai Schwierczek
 */
public class EntityDefinitionServiceUndoSupport extends EntityDefinitionServiceDecorator {

	private static final ALogger log = ALoggerFactory
			.getLogger(EntityDefinitionServiceUndoSupport.class);

	/**
	 * Create undo/redo support for the given entity definition service
	 * 
	 * @param entityDefinitionService the entity definition service
	 */
	public EntityDefinitionServiceUndoSupport(EntityDefinitionService entityDefinitionService) {
		super(entityDefinitionService);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.internal.EntityDefinitionServiceDecorator#addConditionContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      eu.esdihumboldt.hale.common.instance.model.Filter)
	 */
	@Override
	public EntityDefinition addConditionContext(EntityDefinition sibling, Filter filter) {
		AddConditionContextOperation operation = new AddConditionContextOperation(sibling, filter);
		executeOperation(operation);

		return operation.getResult();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.internal.EntityDefinitionServiceDecorator#addIndexContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      java.lang.Integer)
	 */
	@Override
	public EntityDefinition addIndexContext(EntityDefinition sibling, Integer index) {
		AddIndexContextOperation operation = new AddIndexContextOperation(sibling, index);
		executeOperation(operation);

		return operation.getResult();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.internal.EntityDefinitionServiceDecorator#addNamedContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public EntityDefinition addNamedContext(EntityDefinition sibling) {
		AddNamedContextOperation operation = new AddNamedContextOperation(sibling);
		executeOperation(operation);

		return operation.getResult();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.internal.EntityDefinitionServiceDecorator#removeContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public void removeContext(EntityDefinition entity) {
		RemoveContextOperation operation = new RemoveContextOperation(entity);
		executeOperation(operation);
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
		try {
			operationSupport.getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			log.error("Error executing operation on entity definition service", e);
		}
	}

	/**
	 * Abstract operation that supports an entity definition result and maps
	 * redo to execute.
	 */
	private static abstract class AbstractResultOperation extends AbstractOperation {

		private EntityDefinition result;

		/**
		 * Construct an operation that has the specified label.
		 * 
		 * @param label the label to be used for the operation. Should never be
		 *            <code>null</code>.
		 */
		public AbstractResultOperation(String label) {
			super(label);
		}

		/**
		 * Sets the resulting entity definition.
		 * 
		 * @param result the result
		 */
		protected void setResult(EntityDefinition result) {
			this.result = result;
		}

		/**
		 * Returns the resulting entity definition. Operation must be executed
		 * once, otherwise null is returned.
		 * 
		 * @return the resulting entity definition or <code>null</code> if the
		 *         operation wasn't executed yet.
		 */
		public EntityDefinition getResult() {
			return result;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}
	}

	/**
	 * Operation that adds a condition context to an entity definition.
	 */
	private class AddConditionContextOperation extends AbstractResultOperation {

		private final EntityDefinition sibling;
		private final Filter filter;

		/**
		 * Create an operation that adds a condition context to an entity
		 * definition.
		 * 
		 * @param sibling the entity definition which is a sibling of the entity
		 *            definition to create
		 * @param filter the condition filter
		 */
		private AddConditionContextOperation(EntityDefinition sibling, Filter filter) {
			super("Add condition context");
			this.sibling = sibling;
			this.filter = filter;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			setResult(entityDefinitionService.addConditionContext(sibling, filter));
			return Status.OK_STATUS;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			entityDefinitionService.removeContext(getResult());
			return Status.OK_STATUS;
		}
	}

	/**
	 * Operation that adds an index context to an entity definition.
	 */
	private class AddIndexContextOperation extends AbstractResultOperation {

		private final EntityDefinition sibling;
		private final Integer index;

		/**
		 * Create an operation that adds an index context to an entity
		 * definition.
		 * 
		 * @param sibling the entity definition which is a sibling of the entity
		 *            definition to create
		 * @param index the condition filter
		 */
		private AddIndexContextOperation(EntityDefinition sibling, Integer index) {
			super("Add index context");
			this.sibling = sibling;
			this.index = index;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			setResult(entityDefinitionService.addIndexContext(sibling, index));
			return Status.OK_STATUS;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			entityDefinitionService.removeContext(getResult());
			return Status.OK_STATUS;
		}
	}

	/**
	 * Operation that adds a named context to an entity definition.
	 */
	private class AddNamedContextOperation extends AbstractResultOperation {

		private final EntityDefinition sibling;

		/**
		 * Create an operation that adds a named context to an entity
		 * definition.
		 * 
		 * @param sibling the entity definition which is a sibling of the entity
		 *            definition to create
		 */
		private AddNamedContextOperation(EntityDefinition sibling) {
			super("Add named context");
			this.sibling = sibling;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			setResult(entityDefinitionService.addNamedContext(sibling));
			return Status.OK_STATUS;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			entityDefinitionService.removeContext(getResult());
			return Status.OK_STATUS;
		}
	}

	/**
	 * Operation that removes a context from an entity definition.
	 */
	private class RemoveContextOperation extends AbstractOperation {

		private final Filter filter;
		private final Integer index;
		private final Integer name;
		// entity with context
		private final EntityDefinition entity;
		// entity without context
		private final EntityDefinition baseEntity;

		/**
		 * Create an operation that removes a context from an entity definition.
		 * 
		 * @param entity the entity definition
		 */
		public RemoveContextOperation(EntityDefinition entity) {
			super("Remove " + getTypeText(entity) + " context");
			Condition condition = AlignmentUtil.getContextCondition(entity);
			if (condition != null)
				filter = condition.getFilter();
			else
				filter = null;

			index = AlignmentUtil.getContextIndex(entity);
			name = AlignmentUtil.getContextName(entity);
			this.entity = entity;
			this.baseEntity = AlignmentUtil.getDefaultEntity(entity);
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			entityDefinitionService.removeContext(entity);
			return Status.OK_STATUS;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			// resulting entity is equal to original entity
			if (filter != null)
				entityDefinitionService.addConditionContext(baseEntity, filter);
			else if (index != null)
				entityDefinitionService.addIndexContext(baseEntity, index);
			else if (name != null)
				entityDefinitionService.addNamedContext(baseEntity);

			return Status.OK_STATUS;
		}
	}

	private static String getTypeText(EntityDefinition entity) {
		if (AlignmentUtil.getContextCondition(entity) != null)
			return "condition";
		else if (AlignmentUtil.getContextIndex(entity) != null)
			return "index";
		else if (AlignmentUtil.getContextName(entity) != null)
			return "named";
		throw new IllegalArgumentException("Cannot remove context from entity without context.");
	}
}
