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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Converts {@link MultiPolygon}s to {@link Polygon}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class MultiLineStringToLineString extends
		AbstractGeometryCollectionConverter<MultiLineString, LineString> {

	/**
	 * Default constructor
	 */
	public MultiLineStringToLineString() {
		super(MultiLineString.class, LineString.class);
	}

	/**
	 * @see AbstractGeometryCollectionConverter#createEmptyGeometry()
	 */
	@Override
	protected LineString createEmptyGeometry() {
		return geomFactory.createLineString((Coordinate[]) null);
	}

}
