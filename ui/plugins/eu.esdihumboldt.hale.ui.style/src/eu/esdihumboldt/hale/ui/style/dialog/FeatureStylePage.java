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
package eu.esdihumboldt.hale.ui.style.dialog;

import org.eclipse.jface.dialogs.DialogPage;
import org.geotools.styling.Style;

/**
 * Dialog page for the {@link FeatureStyleDialog}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class FeatureStylePage extends DialogPage {

	private final FeatureStyleDialog parent;

	/**
	 * Creates a new dialog page
	 * 
	 * @param parent the parent dialog
	 * @param title the page title
	 */
	public FeatureStylePage(FeatureStyleDialog parent, String title) {
		super(title);

		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public FeatureStyleDialog getParent() {
		return parent;
	}

	/**
	 * Get the edited style
	 * 
	 * @param force if a style shall be returned even if there were no changes
	 * 
	 * @return the style (or null if the parent style shall be used)
	 * @throws Exception if the style could not be retrieved
	 */
	public abstract Style getStyle(boolean force) throws Exception;

}
