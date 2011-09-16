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

import java.util.List;

import org.geotools.gml3.bindings.MultiPolygonTypeBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Adds support for {@link Polygon} arrays as child values
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleMultiPolygonTypeBinding extends MultiPolygonTypeBinding {
	
	private final GeometryFactory gFactory;

	/**
	 * @see MultiPolygonTypeBinding#MultiPolygonTypeBinding(GeometryFactory)
	 */
	public HaleMultiPolygonTypeBinding(GeometryFactory gFactory) {
		super(gFactory);
		
		this.gFactory = gFactory;
	}

	/**
	 * @see MultiPolygonTypeBinding#parse(ElementInstance, Node, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		// get polygon child values
		List<Polygon> polys = node.getChildValues(Polygon.class);
		
		// get polygon array child values
		List<Polygon[]> arrys = node.getChildValues(Polygon[].class);
		for (Polygon[] ps : arrys) {
			for (Polygon p : ps) {
				polys.add(p);
			}
		}

        return gFactory.createMultiPolygon(polys.toArray(new Polygon[polys.size()]));
	}

}
