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
package de.fhg.igd.mapviewer.view.server;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.ui.util.extension.exclusive.ExclusiveExtensionContribution;
import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension;
import de.fhg.igd.mapviewer.server.MapServer;
import de.fhg.igd.mapviewer.server.MapServerFactory;

/**
 * Contribution that represents and controls the current {@link MapServer}
 * 
 * @author Simon Templer
 */
public class MapServerContribution
		extends ExclusiveExtensionContribution<MapServer, MapServerFactory> {

	/**
	 * @see ExclusiveExtensionContribution#getExtension()
	 */
	@Override
	protected ExclusiveExtension<MapServer, MapServerFactory> initExtension() {
		return PlatformUI.getWorkbench().getService(IMapServerService.class);
	}

}
