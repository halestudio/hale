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
package de.fhg.igd.mapviewer.server;

import de.fhg.igd.mapviewer.MapPainter;

/**
 * Abstract {@link MapServer} without a configuration dialog
 * 
 * @author Simon Templer
 */
public abstract class AbstractMapServer implements MapServer {

	private String name;

	/**
	 * @see MapServer#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see MapServer#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The default implementation returns <code>null</code>. Please override if
	 * applicable.
	 * 
	 * @see MapServer#getMapOverlay()
	 */
	@Override
	public MapPainter getMapOverlay() {
		return null;
	}

}
