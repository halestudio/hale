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

package eu.esdihumboldt.hale.ui.style.service.internal;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.ui.style.internal.InstanceStylePlugin;
import eu.esdihumboldt.hale.ui.util.swing.SwingRcpUtilities;

/**
 * Style perferences
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StylePreferences extends AbstractPreferenceInitializer implements
		StylePreferenceConstants {

	private static final RGB SOURCE_DEFAULT_COLOR = new RGB(57, 75, 95);

	private static final RGB TRANSFORMED_DEFAULT_COLOR = new RGB(90, 25, 90);

	private static final RGB DEFAULT_BACKGROUND = new RGB(126, 166, 210);

	private static final RGB DEFAULT_SELECTION_COLOR = new RGB(255, 0, 0);

	static final Set<String> ALL_KEYS = new HashSet<String>();
	static {
		ALL_KEYS.add(KEY_DEFAULT_BACKGROUND);
		ALL_KEYS.add(KEY_SOURCE_DEFAULT_COLOR);
		ALL_KEYS.add(KEY_TRANSFORMED_DEFAULT_COLOR);
		ALL_KEYS.add(KEY_DEFAULT_WIDTH);
		ALL_KEYS.add(KEY_SELECTION_COLOR);
		ALL_KEYS.add(KEY_SELECTION_WIDTH);
	}

	/**
	 * @see AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = InstanceStylePlugin.getDefault().getPreferenceStore();

		preferences.setDefault(KEY_SOURCE_DEFAULT_COLOR,
				StringConverter.asString(SOURCE_DEFAULT_COLOR));
		preferences.setDefault(KEY_TRANSFORMED_DEFAULT_COLOR,
				StringConverter.asString(TRANSFORMED_DEFAULT_COLOR));
		preferences.setDefault(KEY_DEFAULT_WIDTH, StringConverter.asString(1));
		preferences
				.setDefault(KEY_DEFAULT_BACKGROUND, StringConverter.asString(DEFAULT_BACKGROUND));

		preferences.setDefault(KEY_SELECTION_COLOR,
				StringConverter.asString(DEFAULT_SELECTION_COLOR));
		preferences.setDefault(KEY_SELECTION_WIDTH, StringConverter.asString(2));
	}

	/**
	 * Get the default color for the given data set.
	 * 
	 * @param dataSet the data set
	 * @return the default color
	 */
	public static Color getDefaultColor(DataSet dataSet) {
		switch (dataSet) {
		case TRANSFORMED:
			return getColor(KEY_TRANSFORMED_DEFAULT_COLOR);
		case SOURCE:
		default:
			return getColor(KEY_SOURCE_DEFAULT_COLOR);
		}
	}

	/**
	 * Get the default width
	 * 
	 * @return the default width
	 */
	public static int getDefaultWidth() {
		IPreferenceStore preferences = InstanceStylePlugin.getDefault().getPreferenceStore();
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
		IPreferenceStore preferences = InstanceStylePlugin.getDefault().getPreferenceStore();
		return preferences.getInt(KEY_SELECTION_WIDTH);
	}

	private static Color getColor(String colorKey) {
		IPreferenceStore preferences = InstanceStylePlugin.getDefault().getPreferenceStore();

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
		IPreferenceStore preferences = InstanceStylePlugin.getDefault().getPreferenceStore();

		String color = preferences.getString(KEY_DEFAULT_BACKGROUND);
		return StringConverter.asRGB(color);
	}

}
