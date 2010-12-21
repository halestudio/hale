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

import org.geotools.gml3.bindings.AbstractGeometryTypeBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleAbstractGeometryTypeBinding extends
		AbstractGeometryTypeBinding {

	/**
	 * @see org.geotools.gml3.bindings.AbstractGeometryTypeBinding#parse(org.geotools.xml.ElementInstance, org.geotools.xml.Node, java.lang.Object)
	 */
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		Object v;
		if (value == null || !(value instanceof Geometry)) {
			v = node.getChildValue(Geometry.class); 
		}
		else {
			v = value;
		}
		return super.parse(instance, node, v);
	}

}
