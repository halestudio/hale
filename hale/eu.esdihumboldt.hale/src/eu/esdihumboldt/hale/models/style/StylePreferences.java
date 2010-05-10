/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.models.style;

import java.awt.Color;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.swingrcpbridge.SwingRcpUtilities;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StylePreferences extends
		AbstractPreferenceInitializer implements StylePreferenceConstants {

	private static final RGB DEFAULT_COLOR = new RGB(57, 75, 95);
	
	private static final RGB DEFAULT_BACKGROUND = new RGB(126, 166, 210);
	
	private static final RGB DEFAULT_SELECTION_COLOR = new RGB(255, 0, 0);

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = HALEActivator.getDefault().getPreferenceStore();
		
		preferences.setDefault(KEY_DEFAULT_COLOR, StringConverter.asString(DEFAULT_COLOR));
		preferences.setDefault(KEY_DEFAULT_WIDTH, StringConverter.asString(1));
		preferences.setDefault(KEY_DEFAULT_BACKGROUND, StringConverter.asString(DEFAULT_BACKGROUND));
		
		preferences.setDefault(KEY_SELECTION_COLOR, StringConverter.asString(DEFAULT_SELECTION_COLOR));
		preferences.setDefault(KEY_SELECTION_WIDTH, StringConverter.asString(2));
	}
	
	/**
	 * Get the default color
	 * 
	 * @return the default color
	 */
	public static Color getDefaultColor() {
		return getColor(KEY_DEFAULT_COLOR);
	}
	
	/**
	 * Get the default width
	 * 
	 * @return the default width
	 */
	public static int getDefaultWidth() {
		IPreferenceStore preferences = HALEActivator.getDefault().getPreferenceStore();
		return preferences.getInt(KEY_DEFAULT_WIDTH);
	}
	
	/**
	 * Get the selection color
	 * 
	 * @return the selection color
	 */
	public static Color getSelectionColor() {
		return getColor(KEY_SELECTION_COLOR);
	}
	
	/**
	 * Get the selection width
	 * 
	 * @return the selection width
	 */
	public static int getSelectionWidth() {
		IPreferenceStore preferences = HALEActivator.getDefault().getPreferenceStore();
		return preferences.getInt(KEY_SELECTION_WIDTH);
	}

	private static Color getColor(String colorKey) {
		IPreferenceStore preferences = HALEActivator.getDefault().getPreferenceStore();
		
		String color = preferences.getString(colorKey);
		if (color == null) {
			return null;
		}
		else {
			RGB rgb = StringConverter.asRGB(color);
			return SwingRcpUtilities.convertToColor(rgb);
		}
	}

	/**
	 * Get the default background
	 * 
	 * @return the default background
	 */
	public static RGB getDefaultBackground() {
		IPreferenceStore preferences = HALEActivator.getDefault().getPreferenceStore();
		
		String color = preferences.getString(KEY_DEFAULT_BACKGROUND);
		return StringConverter.asRGB(color);
	}

}
