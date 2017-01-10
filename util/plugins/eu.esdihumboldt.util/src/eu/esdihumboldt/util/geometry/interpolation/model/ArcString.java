/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.geometry.interpolation.model;

import java.util.List;

/**
 * An arc string composed of a number of arcs.
 * 
 * @author Simon Templer
 */
public interface ArcString extends ComplexGeometry {

	/**
	 * @return the arcs the arc string is composed of
	 */
	List<Arc> getArcs();

}
