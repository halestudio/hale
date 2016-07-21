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
import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.eclipse.ui.util.extension.selective.SelectiveExtensionContribution;
import de.fhg.igd.eclipse.util.extension.selective.SelectiveExtension;

/**
 * Contribution that represents and controls the active
 * {@link TileOverlayPainter}s
 * 
 * @author Simon Templer
 */
public class TileOverlayContribution
		extends SelectiveExtensionContribution<TileOverlayPainter, TileOverlayFactory> {

	/**
	 * @see SelectiveExtensionContribution#getExtension()
	 */
	@Override
	protected SelectiveExtension<TileOverlayPainter, TileOverlayFactory> initExtension() {
		return PlatformUI.getWorkbench().getService(ITileOverlayService.class);
	}

}
