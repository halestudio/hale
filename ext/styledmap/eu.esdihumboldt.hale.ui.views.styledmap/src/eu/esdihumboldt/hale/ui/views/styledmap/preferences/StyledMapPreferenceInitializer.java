/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import eu.esdihumboldt.hale.ui.views.styledmap.internal.StyledMapBundle;

/**
 * Preference initializer for the styled map view.
 * 
 * @author Simon Templer
 */
public class StyledMapPreferenceInitializer extends AbstractPreferenceInitializer implements
		StyledMapPreferenceConstants {

	private static final String DEFAULT_MAP_LAYOUT = "eu.esdihumboldt.hale.ui.views.styledmap.default";

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = StyledMapBundle.getDefault().getPreferenceStore();

		store.setDefault(CURRENT_MAP_LAYOUT, DEFAULT_MAP_LAYOUT);
	}

}
