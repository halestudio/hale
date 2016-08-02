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

import org.eclipse.jface.preference.StringFieldEditor;

import de.fhg.igd.mapviewer.server.tiles.CustomTileMapServerConfiguration;
import de.fhg.igd.mapviewer.server.tiles.wizard.Messages;

/**
 * Custom tile map server Name editor
 * 
 * @author Arun
 */
public class ConfigurationNameFieldEditor extends StringFieldEditor {

	private final CustomTileMapServerConfiguration configuration;

	/**
	 * Default constructor
	 * 
	 * @param configuration the WMS configuration
	 */
	public ConfigurationNameFieldEditor(CustomTileMapServerConfiguration configuration) {
		super();

		setEmptyStringAllowed(false);

		this.configuration = configuration;
	}

	/**
	 * @see StringFieldEditor#doCheckState()
	 */
	@Override
	protected boolean doCheckState() {
		String text = getStringValue();

		if (text.indexOf('/') >= 0) { // invalid characters
			setErrorMessage(Messages.ServerNameFieldEditor_0);
			return false;
		}

		try {
			// don't allow already existing names
			boolean exists = configuration.nameExists(text);
			if (exists) {
				setErrorMessage(Messages.ServerNameFieldEditor_1);
			}
			return !exists;
		} catch (Exception e) {
			setErrorMessage(e.getLocalizedMessage());
			return false;
		}
	}
}
