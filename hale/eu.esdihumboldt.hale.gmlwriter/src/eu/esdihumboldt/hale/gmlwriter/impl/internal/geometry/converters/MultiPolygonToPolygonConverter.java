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

package eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.converters;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.GeometryConverter;

/**
 * Converts {@link MultiPolygon}s to {@link Polygon}s 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MultiPolygonToPolygonConverter extends
		AbstractGeometryConverter<MultiPolygon, Polygon> {

	/**
	 * @see GeometryConverter#convert(Geometry)
	 */
	@Override
	public Polygon convert(MultiPolygon geometry) {
		if (geometry.getNumGeometries() >= 1) {
			return (Polygon) geometry.getGeometryN(0);
		}
		else {
			return geomFactory.createPolygon(null, null);
		}
	}

	/**
	 * @see GeometryConverter#getSourceType()
	 */
	@Override
	public Class<MultiPolygon> getSourceType() {
		return MultiPolygon.class;
	}

	/**
	 * @see GeometryConverter#getTargetType()
	 */
	@Override
	public Class<Polygon> getTargetType() {
		return Polygon.class;
	}

	/**
	 * @see GeometryConverter#lossOnConversion(Geometry)
	 */
	@Override
	public boolean lossOnConversion(MultiPolygon geometry) {
		// loss if polygons are lost
		return geometry.getNumGeometries() > 1;
	}

}
