/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.groovy.internal.handler;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;

/**
 * Handler that toggles the groovy script restriction.
 * 
 * @author Kai
 */
public class ToggleRestrictionHandler extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		GroovyService gs = HaleUI.getServiceProvider().getService(GroovyService.class);
		boolean restrictionActive = gs.isRestrictionActive();
		String warning;
		if (restrictionActive) {
			warning = "Are your sure, that you want to lift restrictions for all Groovy scripts of the current project?";
			warning += "\n\nWARNING: The Groovy scripts can then do \"anything\", so be sure to trust your source!";
		}
		else {
			warning = "Are you sure, that you want to restrict all Groovy scripts again?\nScripts relying on additional rights will stop working and overall script performance may go down.";
		}
		boolean confirmation = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
				"Groovy script restriction", warning);
		if (confirmation) {
			gs.setRestrictionActive(!gs.isRestrictionActive());
		}
		else {
			// make sure toggle button reverts
			ICommandService cs = PlatformUI.getWorkbench().getService(ICommandService.class);
			cs.refreshElements(event.getCommand().getId(), null);
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		GroovyService gs = HaleUI.getServiceProvider().getService(GroovyService.class);
		element.setChecked(gs.isRestrictionActive());
	}
}
