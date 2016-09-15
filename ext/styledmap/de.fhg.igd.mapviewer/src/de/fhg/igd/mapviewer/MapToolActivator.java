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

import javax.swing.AbstractButton;

import de.fhg.igd.mapviewer.tools.Activator;

/**
 * MapToolActivator
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class MapToolActivator implements Activator {

	private final AbstractButton button;

	/**
	 * Constructor
	 * 
	 * @param button the internal button
	 */
	public MapToolActivator(AbstractButton button) {
		super();

		this.button = button;
	}

	/**
	 * @see Activator#activate()
	 */
	@Override
	public void activate() {
		button.setSelected(true);
	}

}
