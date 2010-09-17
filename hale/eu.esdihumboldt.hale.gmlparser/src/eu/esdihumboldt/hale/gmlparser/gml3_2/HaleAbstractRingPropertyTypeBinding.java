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

package eu.esdihumboldt.hale.gmlparser.gml3_2;

import javax.xml.namespace.QName;

import org.geotools.gml3.bindings.AbstractRingPropertyTypeBinding;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.Binding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * Adds support for converting {@link LineString}s to {@link LinearRing}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleAbstractRingPropertyTypeBinding extends AbstractRingPropertyTypeBinding {
	
	private static final GeometryFactory gf = new GeometryFactory();

	/**
	 * @see Binding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return new QName("http://www.opengis.net/gml/3.2","AbstractRingPropertyType");
	}

	/**
	 * @see AbstractComplexBinding#parse(ElementInstance, Node, Object)
	 */
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		Object result = super.parse(instance, node, value);
		
		if (result == null) {
			LineString line = (LineString) node.getChildValue(LineString.class);
			if (line != null) {
				// try to create a LinearRing
				Coordinate[] coordinates = line.getCoordinates(); 
				
				if (coordinates != null && coordinates.length >= 2) {
					if (coordinates[0].equals(coordinates[coordinates.length - 1])) {
						// is closed, create ring
						result = gf.createLinearRing(coordinates);
					}
					else {
						Coordinate[] tmp = new Coordinate[coordinates.length + 1];
						System.arraycopy(coordinates, 0, tmp, 0, coordinates.length);
						tmp[coordinates.length] = coordinates[0];
						
						result = gf.createLinearRing(tmp);
					}
				}
			}
		}
		
		return result;
	}

}
