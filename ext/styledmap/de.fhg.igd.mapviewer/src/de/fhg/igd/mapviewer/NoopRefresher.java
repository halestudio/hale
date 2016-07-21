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

package de.fhg.igd.mapviewer;

import java.awt.image.BufferedImageOp;

import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * A refresher that does simply nothing
 * 
 * @author Michel Kraemer
 */
public class NoopRefresher implements Refresher {

	/**
	 * A singleton instance of this stateless refresher
	 */
	public static final NoopRefresher INSTANCE = new NoopRefresher();

	private NoopRefresher() {
		// nothing to do here
	}

	@Override
	public void setImageOp(BufferedImageOp imageOp) {
		// nothing to do here
	}

	@Override
	public void addPosition(GeoPosition pos) {
		// nothing to do here
	}

	@Override
	public void addArea(GeoPosition topLeft, GeoPosition bottomRight) {
		// nothing to do here
	}

	@Override
	public void execute() {
		// nothing to do here
	}
}
