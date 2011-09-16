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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.geotools.gml3.bindings.CurvePropertyTypeBinding;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.Binding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Curve property type that supports also {@link MultiLineString}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleCurvePropertyTypeBinding extends CurvePropertyTypeBinding {
	
	private static final GeometryFactory gf = new GeometryFactory();

	/**
	 * @see Binding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return new QName("http://www.opengis.net/gml/3.2","CurvePropertyType"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see Binding#getType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class getType() {
		return LineString.class;
	}

	/**
	 * @see AbstractComplexBinding#parse(ElementInstance, Node, Object)
	 */
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		Object result = super.parse(instance, node, value);
		
		if (result == null) {
			MultiLineString multiLines = (MultiLineString) node.getChildValue(MultiLineString.class);
			if (multiLines != null) {
				// try to create a LineString
				Coordinate[] coordinates = multiLines.getCoordinates();
				List<Coordinate> collect = new ArrayList<Coordinate>();
				for (int i = 0; i < coordinates.length; i++) {
					Coordinate c = coordinates[i];
					
					if (!collect.isEmpty()) {
						// compare with previous
						if (!c.equals(collect.get(collect.size() - 1))) {
							// only add if not equal to previous
							collect.add(c);
						}
					}
					else {
						collect.add(c);
					}
				}
				
				result = gf.createLineString(collect.toArray(new Coordinate[collect.size()]));
			}
		}
		
		return result; 
	}

}
