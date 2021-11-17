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

package eu.esdihumboldt.hale.ui.service.schema.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.google.common.base.Preconditions;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.ResourceSchemaSpace;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.internal.AbstractRemoveResourcesOperation;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Default {@link SchemaService} implementation
 * 
 * @author Simon Templer
 */
public class SchemaServiceImpl extends AbstractSchemaService {

	private static final ALogger log = ALoggerFactory.getLogger(SchemaServiceImpl.class);

	/**
	 * Maps schema space IDs to schema spaces
	 */
	private static final Map<SchemaSpaceID, ResourceSchemaSpace> spaces = new HashMap<SchemaSpaceID, ResourceSchemaSpace>();

	/**
	 * @see AbstractSchemaService#AbstractSchemaService(ProjectService)
	 */
	public SchemaServiceImpl(ProjectService projectService) {
		super(projectService);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaService#getSchema(java.lang.String,
	 *      eu.esdihumboldt.hale.common.schema.SchemaSpaceID)
	 */
	@Override
	public Schema getSchema(String resourceID, SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(resourceID);

		synchronized (spaces) {
			ResourceSchemaSpace space = spaces.get(spaceID);
			if (space != null) {
				return space.getSchemas(resourceID);
			}
			return null;
		}
	}

	/**
	 * @see SchemaService#getSchemas(SchemaSpaceID)
	 */
	@Override
	public SchemaSpace getSchemas(SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);

		synchronized (spaces) {
			ResourceSchemaSpace space = spaces.get(spaceID);
			if (space == null) {
				space = new ResourceSchemaSpace();
				spaces.put(spaceID, space);
			}
			return space;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaService#addSchema(java.lang.String,
	 *      eu.esdihumboldt.hale.common.schema.model.Schema,
	 *      eu.esdihumboldt.hale.common.schema.SchemaSpaceID)
	 */
	@Override
	public void addSchema(String resourceID, Schema schema, SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);

		SchemaIO.loadMappingRelevantTypesConfig(schema, spaceID, getProjectService()
				.getConfigurationService());

		ResourceSchemaSpace space = (ResourceSchemaSpace) getSchemas(spaceID);
		space.addSchema(resourceID, schema);

		notifySchemaAdded(spaceID, schema);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaService#removeSchema(java.lang.String,
	 *      eu.esdihumboldt.hale.common.schema.SchemaSpaceID)
	 */
	@Override
	public boolean removeSchema(String resourceID, SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);

		ResourceSchemaSpace space = (ResourceSchemaSpace) getSchemas(spaceID);
		Schema removedSchema = space.removeSchema(resourceID);
		notifySchemaRemoved(spaceID);
		notifyMappableTypesChanged(spaceID, space.getTypes());
		return removedSchema != null ? true : false;
	}

	/**
	 * @see SchemaService#clearSchemas(SchemaSpaceID)
	 */
	@Override
	public void clearSchemas(final SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);

		IUndoableOperation operation = new AbstractRemoveResourcesOperation("Clear "
				+ (spaceID == SchemaSpaceID.SOURCE ? "source" : "target") + " schema",
				spaceID == SchemaSpaceID.SOURCE ? ACTION_READ_SOURCE : ACTION_READ_TARGET) {

			/**
			 * @see eu.esdihumboldt.hale.ui.service.project.internal.AbstractRemoveResourcesOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
			 *      org.eclipse.core.runtime.IAdaptable)
			 */
			@Override
			public IStatus execute(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				synchronized (spaces) {
					spaces.remove(spaceID);
				}
				notifySchemasCleared(spaceID);

				return super.execute(monitor, info);
			}
		};
		IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench()
				.getOperationSupport();
		operation.addContext(operationSupport.getUndoContext());
		try {
			operationSupport.getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			log.error("Error executing operation on schema service", e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaService#toggleMappable(eu.esdihumboldt.hale.common.schema.SchemaSpaceID,
	 *      java.util.Collection)
	 */
	@Override
	public void toggleMappable(SchemaSpaceID spaceID, Collection<? extends TypeDefinition> types) {
		ToggleMappableOperation operation = new ToggleMappableOperation(spaceID, types);

		IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench()
				.getOperationSupport();
		operation.addContext(operationSupport.getUndoContext());
		try {
			operationSupport.getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			log.error("Error executing operation on schema service", e);
		}
	}

	/**
	 * Operation that toggles mappable flags.
	 */
	public class ToggleMappableOperation extends AbstractOperation {

		private final SchemaSpaceID spaceID;
		private final Collection<? extends TypeDefinition> types;

		/**
		 * Creates an operation that toggles some mappable flags.
		 * 
		 * @param spaceID the space id
		 * @param types the types to change
		 */
		public ToggleMappableOperation(SchemaSpaceID spaceID,
				Collection<? extends TypeDefinition> types) {
			super("Change mappable types");

			this.spaceID = spaceID;
			this.types = types;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
		 *      org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (!types.isEmpty()) {
				// update schema space
				SchemaSpace schemaSpace = getSchemas(spaceID);
				schemaSpace.toggleMappingRelevant(types);

				// update config
				String paramName = "mappable"
						+ (spaceID == SchemaSpaceID.SOURCE ? "Source" : "Target") + "Type";
				List<String> mappableConfig = getProjectService().getConfigurationService()
						.getList(paramName);
				if (mappableConfig == null) {
					Collection<? extends TypeDefinition> mappableTypes = schemaSpace
							.getMappingRelevantTypes();
					mappableConfig = new ArrayList<String>(mappableTypes.size());
					for (TypeDefinition type : mappableTypes)
						mappableConfig.add(type.getName().toString());
					getProjectService().getConfigurationService()
							.setList(paramName, mappableConfig);
				}
				else {
					for (TypeDefinition type : types)
						if (type.getConstraint(MappingRelevantFlag.class).isEnabled())
							mappableConfig.add(type.getName().toString());
						else
							mappableConfig.remove(type.getName().toString());
					getProjectService().getConfigurationService()
							.setList(paramName, mappableConfig);
				}

				// fire event
				notifyMappableTypesChanged(spaceID, types);
			}
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
			return execute(monitor, info); // it is toggle, so undo and redo is
											// the same
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaService#editMappableTypes(eu.esdihumboldt.hale.common.schema.SchemaSpaceID)
	 */
	@Override
	public void editMappableTypes(final SchemaSpaceID spaceID) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				EditMappableTypesWizard wizard = new EditMappableTypesWizard(spaceID,
						getSchemas(spaceID));
				Shell shell = Display.getCurrent().getActiveShell();
				HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}
}
