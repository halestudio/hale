/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/**
 * Converts a {@link MultiPoint} to a {@link Point}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class MultiPointToPoint extends AbstractGeometryCollectionConverter<MultiPoint, Point> {

	/**
	 * Default constructor
	 */
	public MultiPointToPoint() {
		super(MultiPoint.class, Point.class);
	}

	/**
	 * @see AbstractGeometryCollectionConverter#createEmptyGeometry()
	 */
	@Override
	protected Point createEmptyGeometry() {
		return geomFactory.createPoint((Coordinate) null);
	}

}
