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
		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		is.setTransformationEnabled(!is.isTransformationEnabled());

		return null;
	}

	/**
	 * @see IElementUpdater#updateElement(UIElement, Map)
	 */
	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
//		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
//				InstanceService.class);
//		boolean enabled = is.isTransformationEnabled();
//
//		if (enabled) {
//			element.setText("Disable transformation");
		// XXX for checked toolbar buttons always the hover icon is shown ,
		// for menus never a hover icon is shown
//			element.setHoverIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_STOP));
//			element.setIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_PLAY));
//		}
//		else {
//			element.setText("Enable transformation");
//			element.setHoverIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_PLAY));
//			element.setIcon(CommonSharedImages.getImageRegistry().getDescriptor(CommonSharedImages.IMG_STOP));
//		}
	}

}
