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

package eu.esdihumboldt.hale.ui.templates.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.ui.templates.internal.TemplatesUIPlugin;

/**
 * Web templates default preferences.
 * 
 * @author Simon Templer
 */
public class WebTemplatesPreferencesInitializer extends AbstractPreferenceInitializer implements
		WebTemplatesPreferences {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = TemplatesUIPlugin.getDefault().getPreferenceStore();
		prefs.setDefault(PREF_WEB_TEMPLATES_URL, "http://hale.igd.fraunhofer.de/templates/");
	}

}
