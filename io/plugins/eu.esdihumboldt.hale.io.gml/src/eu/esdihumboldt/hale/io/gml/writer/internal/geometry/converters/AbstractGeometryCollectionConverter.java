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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryConverter;

/**
 * Converts a {@link GeometryCollection} to a single {@link Geometry}.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @param <C> the geometry collection type
 * @param <T> the geometry type contained in the collection
 */
public abstract class AbstractGeometryCollectionConverter<C extends GeometryCollection, T extends Geometry>
		extends AbstractGeometryConverter<C, T> {

	/**
	 * Constructor
	 * 
	 * @param sourceType the geometry collection type
	 * @param targetType the geometry type contained in the collection
	 */
	public AbstractGeometryCollectionConverter(Class<C> sourceType, Class<T> targetType) {
		super(sourceType, targetType);
	}

	/**
	 * @see GeometryConverter#convert(Geometry)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T convert(C geometryCollection) {
		if (geometryCollection.getNumGeometries() >= 1) {
			return (T) geometryCollection.getGeometryN(0);
		}
		else {
			return createEmptyGeometry();
		}
	}

	/**
	 * Create an empty target geometry
	 * 
	 * @return the empty geometry
	 */
	protected abstract T createEmptyGeometry();

	/**
	 * @see GeometryConverter#lossOnConversion(Geometry)
	 */
	@Override
	public boolean lossOnConversion(C geometryCollection) {
		// loss if geometries are lost
		return geometryCollection.getNumGeometries() > 1;
	}

}
