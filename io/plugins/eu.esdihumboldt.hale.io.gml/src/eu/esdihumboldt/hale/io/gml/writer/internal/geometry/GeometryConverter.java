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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import org.locationtech.jts.geom.Geometry;

/**
 * Converts a geometry to another kind of geometry
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @param <S> the source geometry type
 * @param <T> the target geometry type
 */
public interface GeometryConverter<S extends Geometry, T extends Geometry> {

	/**
	 * Get the target geometry type
	 * 
	 * @return the target geometry type
	 */
	public Class<T> getTargetType();

	/**
	 * Get the source geometry type
	 * 
	 * @return the source geometry type
	 */
	public Class<S> getSourceType();

	/**
	 * Convert the given geometry
	 * 
	 * @param geometry the source geometry
	 * 
	 * @return the converted geometry
	 */
	public T convert(S geometry);

	/**
	 * Determines if there is a loss of information when converting the given
	 * geometry
	 * 
	 * @param geometry the source geometry
	 * 
	 * @return if there would be a loss of information
	 */
	public boolean lossOnConversion(S geometry);

}
