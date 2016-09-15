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

import org.jdesktop.swingx.mapviewer.TileOverlayPainter;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;

/**
 * Collection of tile overlay factories
 * 
 * @author Simon Templer
 */
public interface TileOverlayFactoryCollection
		extends ExtensionObjectFactoryCollection<TileOverlayPainter, TileOverlayFactory> {

	/**
	 * Sets the painter priority
	 * 
	 * @param priority the priority
	 */
	public void setPriority(int priority);

}
