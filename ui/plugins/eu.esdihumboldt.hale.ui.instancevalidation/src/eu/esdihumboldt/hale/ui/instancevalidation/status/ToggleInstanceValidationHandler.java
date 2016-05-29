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
		InstanceValidationService ivs = PlatformUI.getWorkbench()
				.getService(InstanceValidationService.class);
		ivs.setValidationEnabled(!ivs.isValidationEnabled());
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		InstanceValidationService ivs = PlatformUI.getWorkbench()
				.getService(InstanceValidationService.class);
		element.setChecked(ivs.isValidationEnabled());
	}
}
