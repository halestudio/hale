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

package eu.esdihumboldt.hale.ui.instancevalidation.status;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;

/**
 * Handler for toggling the live instance validation.
 * 
 * @author Kai Schwierczek
 */
public class ToggleInstanceValidationHandler extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InstanceValidationService ivs = (InstanceValidationService) PlatformUI.getWorkbench()
				.getService(InstanceValidationService.class);
		ivs.setValidationEnabled(!ivs.isValidationEnabled());
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		InstanceValidationService ivs = (InstanceValidationService) PlatformUI.getWorkbench()
				.getService(InstanceValidationService.class);
		element.setChecked(ivs.isValidationEnabled());
	}
}
