/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.launchaction.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Simple launch action displaying a message.
 * 
 * @author Simon Templer
 */
public class MessageAction extends AbstractLaunchAction<AtomicReference<String>> {

	@Override
	public void onOpenWorkbenchWindow() {
		if (getLaunchContext().get() != null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
					"Information", getLaunchContext().get());
		}
	}

	@Override
	protected AtomicReference<String> createLaunchContext() {
		return new AtomicReference<String>();
	}

	@Override
	protected void processParameter(String param, String value,
			AtomicReference<String> launchContext) {
		switch (param) {
		case "-msg":
			launchContext.set(value);
			break;
		}
	}

}
