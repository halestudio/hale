/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.view.overlay;

import de.fhg.igd.eclipse.ui.util.extension.selective.PreferencesSelectiveExtension;
import de.fhg.igd.mapviewer.MapPainter;
import de.fhg.igd.mapviewer.view.MapviewerPlugin;
import de.fhg.igd.mapviewer.view.preferences.MapPreferenceConstants;

/**
 * Service managing the active {@link MapPainter}s
 * 
 * @author Simon Templer
 */
public class MapPainterService extends PreferencesSelectiveExtension<MapPainter, MapPainterFactory>
		implements IMapPainterService {

	/**
	 * Default constructor
	 */
	public MapPainterService() {
		super(new MapPainterExtension(), MapviewerPlugin.getDefault().getPreferenceStore(),
				MapPreferenceConstants.ACTIVE_MAP_PAINTERS);
	}

}
