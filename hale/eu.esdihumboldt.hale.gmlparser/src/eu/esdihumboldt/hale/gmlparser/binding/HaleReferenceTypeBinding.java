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

package eu.esdihumboldt.hale.gmlparser.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.ComplexAttributeImpl;
import org.geotools.gml3.GMLSchema;
import org.geotools.gml3.bindings.ReferenceTypeBinding;
import org.geotools.xml.AttributeInstance;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;

/**
 * Supports parsing of attributes
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleReferenceTypeBinding extends ReferenceTypeBinding {
	
	/**
	 * @see ReferenceTypeBinding#parse(ElementInstance, Node, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		ComplexAttribute result = new ComplexAttributeImpl(new ArrayList<Property>(), GMLSchema.REFERENCETYPE_TYPE, null);
		
		AttributeInstance[] attributes = instance.getAttributes();
		if (attributes != null && attributes.length > 0) {
			/*
    		 * @see eu.esdihumboldt.tools.AttributeProperty
    		 */
    		Map<String, String> attMap = (Map<String, String>) result.getUserData().get(SimpleFeatureTypeBinding.XML_ATTRIBUTES);
    		if (attMap == null) {
    			attMap = new HashMap<String, String>();
    			result.getUserData().put(SimpleFeatureTypeBinding.XML_ATTRIBUTES, attMap);
    		}
    		
    		for (AttributeInstance a : attributes) {
    			attMap.put('<' + a.getName() + '>', a.getText());
    		}
		}
		
		return result;
	}

}
