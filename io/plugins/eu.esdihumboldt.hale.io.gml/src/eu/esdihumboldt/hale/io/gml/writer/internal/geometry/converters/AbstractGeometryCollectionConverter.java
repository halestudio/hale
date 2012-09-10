/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

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
