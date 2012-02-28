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
import org.geotools.filter.text.cql2.CQLException;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.filter.FilterGeoCqlImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
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
				//TODO different kinds of filters?
				//TODO filter configuration dialog
				//FIXME this is a dummy filter!
				Filter filter;
				try {
					filter = new FilterGeoCqlImpl("id = '0'");
				} catch (CQLException e) {
					throw new ExecutionException("Could not create filter", e);
				}
				eds.addConditionContext((EntityDefinition) element, filter);
			}
		}
		
		return null;
	}

}
