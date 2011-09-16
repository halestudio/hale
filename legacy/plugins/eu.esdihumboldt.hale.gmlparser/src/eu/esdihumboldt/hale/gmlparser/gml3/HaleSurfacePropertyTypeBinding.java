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

package eu.esdihumboldt.hale.gmlparser.gml3;

import org.geotools.gml3.bindings.SurfacePropertyTypeBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Improved {@link SurfacePropertyTypeBinding}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleSurfacePropertyTypeBinding extends SurfacePropertyTypeBinding {

	/**
	 * @see org.geotools.gml3.bindings.SurfacePropertyTypeBinding#parse(org.geotools.xml.ElementInstance, org.geotools.xml.Node, java.lang.Object)
	 */
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		Object result = super.parse(instance, node, value);
		if (result == null) {
			// might be multipolygon instead
			// extract polygon
			MultiPolygon mp = (MultiPolygon) node.getChildValue(MultiPolygon.class);
			if (mp != null && !mp.isEmpty()) {
				result = mp.getGeometryN(0);
			}
		}
		return result;
	}

}
