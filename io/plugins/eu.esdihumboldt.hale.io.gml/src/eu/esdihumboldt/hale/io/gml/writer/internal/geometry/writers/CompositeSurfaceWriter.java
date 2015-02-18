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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers;

import javax.xml.namespace.QName;

import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Writes {@link MultiPolygon}s as CompositeSurfaces.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CompositeSurfaceWriter extends AbstractMultiPolygonWriter {

	/**
	 * Default constructor
	 */
	public CompositeSurfaceWriter() {
		// compatible types to serve as entry point
		addCompatibleType(new QName(Pattern.GML_NAMESPACE_PLACEHOLDER, "CompositeSurfaceType")); //$NON-NLS-1$
	}

	@Override
	public boolean checkValid(MultiPolygon geometry) {
		// FIXME check if individual surfaces are properly aligned to form a
		// composite surface
		return super.checkValid(geometry);
	}

}
