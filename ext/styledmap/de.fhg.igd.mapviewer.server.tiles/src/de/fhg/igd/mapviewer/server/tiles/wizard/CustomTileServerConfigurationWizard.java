/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package de.fhg.igd.mapviewer.server.tiles.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.fhg.igd.mapviewer.server.tiles.CustomTileMapServerConfiguration;
import de.fhg.igd.mapviewer.server.tiles.wizard.pages.CustomTileServerConfigPage;
import de.fhg.igd.mapviewer.server.tiles.wizard.pages.CustomTileServerExtensionConfigPage;
import de.fhg.igd.mapviewer.server.tiles.wizard.pages.CustomTileWizardPage;

/**
 * Wizard to configure custom tile map server
 * 
 * @author Arun
 */
public class CustomTileServerConfigurationWizard extends Wizard {

	CustomTileMapServerConfiguration configuration;

	/**
	 * Constructor
	 * 
	 * @param configuration {@link CustomTileMapServerConfiguration}
	 */
	public CustomTileServerConfigurationWizard(CustomTileMapServerConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(new CustomTileServerConfigPage(this.configuration));
		addPage(new CustomTileServerExtensionConfigPage(this.configuration));
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		for (IWizardPage page : getPages()) {
			boolean valid = ((CustomTileWizardPage) page).updateConfiguration(configuration);
			if (!valid) {
				return false;
			}
		}
		return true;
	}

}
