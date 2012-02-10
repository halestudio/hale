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

package eu.esdihumboldt.hale.ui.service.schema.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.google.common.base.Preconditions;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

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
	private static final Map<SchemaSpaceID, DefaultSchemaSpace> spaces = new HashMap<SchemaSpaceID, DefaultSchemaSpace>();

	/**
	 * @see AbstractSchemaService#AbstractSchemaService(ProjectService)
	 */
	public SchemaServiceImpl(ProjectService projectService) {
		super(projectService);
	}

	/**
	 * @see SchemaService#getSchemas(SchemaSpaceID)
	 */
	@Override
	public SchemaSpace getSchemas(SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);

		synchronized (spaces) {
			DefaultSchemaSpace space = spaces.get(spaceID);
			if (space == null) {
				space = new DefaultSchemaSpace();
				spaces.put(spaceID, space);
			}
			return space;
		}
	}

	/**
	 * @see SchemaService#addSchema(Schema, SchemaSpaceID)
	 */
	@Override
	public void addSchema(Schema schema, SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);

		String paramName = "mappable" + (spaceID == SchemaSpaceID.SOURCE ? "Source" : "Target") + "Type";
		List<String> mappableConfig = getProjectService().getConfigurationService().getList(paramName);
		if (mappableConfig != null) {
			for (TypeDefinition type : schema.getTypes()) {
				// don't like warnings, and direct cast to
				// AbstractDefinition<TypeConstraint> gives warning...
				Definition<TypeConstraint> def = type;
				if (mappableConfig.contains(type.getName().toString()))
					((AbstractDefinition<TypeConstraint>) def).setConstraint(MappableFlag.ENABLED);
				else
					((AbstractDefinition<TypeConstraint>) def).setConstraint(MappableFlag.DISABLED);
			}
		}

		DefaultSchemaSpace space = (DefaultSchemaSpace) getSchemas(spaceID);
		space.addSchema(schema);

		notifySchemaAdded(spaceID, schema);
	}

	/**
	 * @see SchemaService#clearSchemas(SchemaSpaceID)
	 */
	@Override
	public void clearSchemas(SchemaSpaceID spaceID) {
		Preconditions.checkNotNull(spaceID);

		synchronized (spaces) {
			spaces.remove(spaceID);
		}

		notifySchemasCleared(spaceID);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaService#toggleMappable(eu.esdihumboldt.hale.common.schema.SchemaSpaceID,
	 *      java.util.Collection)
	 */
	@Override
	public void toggleMappable(SchemaSpaceID spaceID, Collection<? extends TypeDefinition> types) {
		ToggleMappableOperation operation = new ToggleMappableOperation(spaceID, types);
		
		IWorkbenchOperationSupport operationSupport = PlatformUI.getWorkbench().getOperationSupport();
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
		public ToggleMappableOperation(SchemaSpaceID spaceID, Collection<? extends TypeDefinition> types) {
			super("Change mappable types");

			this.spaceID = spaceID;
			this.types = types;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if (!types.isEmpty()) {
				// update schema space
				SchemaSpace schemaSpace = getSchemas(spaceID);
				schemaSpace.toggleMappable(types);

				// update config
				String paramName = "mappable" + (spaceID == SchemaSpaceID.SOURCE ? "Source" : "Target") + "Type";
				List<String> mappableConfig = getProjectService().getConfigurationService().getList(paramName);
				if (mappableConfig == null) {
					Collection<? extends TypeDefinition> mappableTypes = schemaSpace.getMappableTypes();
					mappableConfig = new ArrayList<String>(mappableTypes.size());
					for (TypeDefinition type : mappableTypes)
						mappableConfig.add(type.getName().toString());
					getProjectService().getConfigurationService().setList(paramName, mappableConfig);
				} else {
					for (TypeDefinition type : types)
						if (type.getConstraint(MappableFlag.class).isEnabled())
							mappableConfig.add(type.getName().toString());
						else
							mappableConfig.remove(type.getName().toString());
					getProjectService().getConfigurationService().setList(paramName, mappableConfig);
				}

				// fire event
				notifyMappableTypesChanged(spaceID, types);
			}
			return Status.OK_STATUS;
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		/**
		 * @see org.eclipse.core.commands.operations.AbstractOperation#undo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
		 */
		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info); // it is toggle, so undo and redo is the same
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
				EditMappableTypesWizard wizard = new EditMappableTypesWizard(spaceID, getSchemas(spaceID));
				Shell shell = Display.getCurrent().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}
}
