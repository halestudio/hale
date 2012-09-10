package eu.esdihumboldt.hale.ui.service.schema.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.internal.EditMappableTypesWizard;

/**
 * Shows a {@link EditMappableTypesWizard} for the target schema space.
 * 
 * @author Kai Schwierczek
 */
public class EditMappableTargetTypesHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(
				SchemaService.class);
		schemaService.editMappableTypes(SchemaSpaceID.TARGET);
		return null;
	}
}