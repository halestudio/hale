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

package de.fhg.igd.geom.indices;

import java.util.Comparator;

import de.fhg.igd.geom.Localizable;

/**
 * This is a comparator for determining the distance that lies between two
 * Localizables. It is used to determine which Localizable is the nearest
 * neighbor to a given Localizable. <br/>
 * Implementations can be quite different, like comparing center point distance
 * or actual closest point pair distance.
 * 
 * @author Thorsten Reitz
 */
public interface NNComparator extends Comparator<Localizable> {

	/**
	 * This method will return the distance between two Localizables.
	 * 
	 * @param first the first localizable
	 * @param second the second localizable
	 * @return the distance
	 */
	public double getDistance(Localizable first, Localizable second);

	/**
	 * @return Returns the Localizable to which the distance will be calculated.
	 */
	public Localizable getLoc();

	/**
	 * @param loc The Localizable to set.
	 */
	public void setLoc(Localizable loc);
}
