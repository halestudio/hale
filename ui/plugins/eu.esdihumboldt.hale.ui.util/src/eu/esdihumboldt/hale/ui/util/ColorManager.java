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

package eu.esdihumboldt.hale.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

/**
 * Default color manager implementation.
 * 
 * @author Simon Templer
 */
public class ColorManager implements IColorManager {

	private final Map<String, RGB> keyColors = new HashMap<>();

	private final Map<RGB, Color> rgbColors = new HashMap<>();

	private final IPreferenceStore prefStore;

	/**
	 * Create a color manager based on the given preference store.
	 * 
	 * @param prefStore the preference store
	 */
	public ColorManager(IPreferenceStore prefStore) {
		super();
		this.prefStore = prefStore;
	}

	/**
	 * @see org.eclipse.jface.text.source.ISharedTextColors#getColor(org.eclipse.swt.graphics.RGB)
	 */
	@Override
	public Color getColor(RGB rgb) {
		synchronized (rgbColors) {
			Color color = rgbColors.get(rgb);
			if (color == null) {
				color = new Color(PlatformUI.getWorkbench().getDisplay(), rgb);
				rgbColors.put(rgb, color);
			}
			return color;
		}
	}

	/**
	 * @see org.eclipse.jface.text.source.ISharedTextColors#dispose()
	 */
	@Override
	public void dispose() {
		synchronized (rgbColors) {
			for (Color color : rgbColors.values()) {
				color.dispose();
			}
			rgbColors.clear();
		}
	}

	/**
	 * Get the color for the given key. Uses black as a default color if no
	 * color for the key can be found.
	 * 
	 * @see eu.esdihumboldt.hale.ui.util.IColorManager#getColor(java.lang.String)
	 */
	@Override
	public Color getColor(String key) {
		RGB color = keyColors.get(key);
		if (color == null) {
			// try preferences
			String rgbStr = prefStore.getString(key);
			color = StringConverter.asRGB(rgbStr, new RGB(0, 0, 0));
		}
		return getColor(color);
	}

}
