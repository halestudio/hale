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

package eu.esdihumboldt.hale.ui.service.instance.internal.handler;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Handler that toggles the transformation of the {@link InstanceService}.
 * 
 * @author Simon Templer
 */
public class ToggleTransformationHandler extends AbstractHandler implements IElementUpdater {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		is.setTransformationEnabled(!is.isTransformationEnabled());

		return null;
	}

	/**
	 * @see IElementUpdater#updateElement(UIElement, Map)
	 */
	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		boolean enabled = is.isTransformationEnabled();

		if (enabled) {
			element.setText("Disable transformation");
			// XXX for checked toolbar buttons always the hover icon is shown ,
			// for menus never a hover icon is shown
//			element.setHoverIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_STOP));
//			element.setIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_PLAY));
		}
		else {
			element.setText("Enable transformation");
//			element.setHoverIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_PLAY));
//			element.setIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_STOP));
		}
	}

}
