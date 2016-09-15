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

package de.fhg.igd.mapviewer.server.tiles.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;

import de.fhg.igd.mapviewer.server.tiles.CustomTileMapServerConfiguration;

/**
 * Basic Wizard page for custom tile map server configuration
 * 
 * @author Arun
 */
public abstract class CustomTileWizardPage extends WizardPage {

	private final CustomTileMapServerConfiguration configuration;

	/**
	 * Default constructor
	 * 
	 * @param configuration {@link CustomTileMapServerConfiguration}
	 * @param name name of the server
	 */
	public CustomTileWizardPage(CustomTileMapServerConfiguration configuration, String name) {
		super(name);
		this.configuration = configuration;
	}

	/**
	 * To get configuration supplied
	 * 
	 * @return the configuration {@link CustomTileMapServerConfiguration}
	 */
	public CustomTileMapServerConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * To update configuration
	 * 
	 * @param configuration {@link CustomTileMapServerConfiguration}
	 * @return true or false based on validity of fields
	 */
	public abstract boolean updateConfiguration(CustomTileMapServerConfiguration configuration);

}
