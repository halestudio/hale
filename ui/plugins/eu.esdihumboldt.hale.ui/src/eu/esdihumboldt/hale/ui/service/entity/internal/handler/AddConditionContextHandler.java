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

package eu.esdihumboldt.hale.ui.service.entity.internal.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.filter.TypeFilterDialog;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Adds a new condition context for a selected {@link EntityDefinition}.
 * @author Simon Templer
 */
public class AddConditionContextHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			
			if (element instanceof EntityDefinition) {
				EntityDefinitionService eds = (EntityDefinitionService) PlatformUI.getWorkbench().getService(EntityDefinitionService.class);
				EntityDefinition entityDef = (EntityDefinition) element;
				Filter filter = null;
				if (entityDef.getPropertyPath().isEmpty()) {
					// type filter
					TypeFilterDialog tfd = new TypeFilterDialog(
							HandlerUtil.getActiveShell(event), 
							entityDef.getType(),
							"Type condition",
							"Define the filter for the new context");
					if (tfd.open() == TypeFilterDialog.OK) {
						filter = tfd.getFilter();
					}
				}
				else {
					//TODO value filter
				}
				if (filter != null) {
					eds.addConditionContext((EntityDefinition) element, filter);
				}
			}
		}
		
		return null;
	}

}
