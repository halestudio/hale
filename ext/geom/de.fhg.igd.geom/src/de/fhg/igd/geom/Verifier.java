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

package de.fhg.igd.geom;

/**
 * Verifies if a localizable in a spatial index has a certain relation to
 * another localizable.
 * 
 * @author Michel Kraemer
 * @param <T> the type of the localizables in the spatial index
 * @param <L> the type of the Localizable to compare to
 */
public interface Verifier<T extends Localizable, L extends Localizable> {

	/**
	 * Verifies if a localizable in a spatial index has a certain relation to
	 * another localizable.
	 * 
	 * @param first the first localizable
	 * @param second the other localizable
	 * @return true if the first localizable has a spatial relation to the
	 *         second one
	 */
	public boolean verify(T first, L second);
}
