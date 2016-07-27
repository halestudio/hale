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

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.ui.util.extension.selective.SelectiveExtensionContribution;
import de.fhg.igd.eclipse.util.extension.selective.SelectiveExtension;
import de.fhg.igd.mapviewer.MapPainter;

/**
 * Contribution that represents and controls the activated {@link MapPainter}s
 * 
 * @author Simon Templer
 */
public class MapPainterContribution
		extends SelectiveExtensionContribution<MapPainter, MapPainterFactory> {

	/**
	 * @see SelectiveExtensionContribution#getExtension()
	 */
	@Override
	protected SelectiveExtension<MapPainter, MapPainterFactory> initExtension() {
		return PlatformUI.getWorkbench().getService(IMapPainterService.class);
	}

}
