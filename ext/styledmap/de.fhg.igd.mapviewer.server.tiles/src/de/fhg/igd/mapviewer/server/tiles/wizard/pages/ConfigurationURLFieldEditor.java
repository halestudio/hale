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

import de.fhg.igd.mapviewer.server.tiles.wizard.Messages;

/**
 * Custom tile map server URL pattern editor
 * 
 * @author Arun
 */
public class ConfigurationURLFieldEditor extends StringFieldEditor {

	/**
	 * Default constructor
	 * 
	 */
	public ConfigurationURLFieldEditor() {
		super();
		setEmptyStringAllowed(false);
	}

	/**
	 * @see StringFieldEditor#doCheckState()
	 */
	@Override
	protected boolean doCheckState() {
		String text = getStringValue();

		// string should contain {x}, {y} and {z} literals
		if (text.contains("{x}") && text.contains("{y}") && text.contains("{z}")) {
			return true;
		}
		// else show message and return false
		setErrorMessage(Messages.URLFieldEditor_0);
		return false;

	}

}
