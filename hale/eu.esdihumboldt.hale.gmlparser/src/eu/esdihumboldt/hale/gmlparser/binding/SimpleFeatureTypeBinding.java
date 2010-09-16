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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.Converters;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.Binding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.geotools.xml.impl.InstanceBinding;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;

/**
 * Binding for {@link SimpleFeatureType}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimpleFeatureTypeBinding extends AbstractComplexBinding
	implements InstanceBinding {
	
	private static final Log log = LogFactory.getLog(SimpleFeatureTypeBinding.class);

	/**
	 * Feature user data property name for XML attributes
	 * @see eu.esdihumboldt.tools.AttributeProperty XXX add dependency to commons instead of defining it here again
	 */
	private static final String XML_ATTRIBUTES = "XmlAttributes";

	private final QName name;
	
	private final SimpleFeatureType type;
	
	/**
	 * Constructor
	 * 
	 * @param name the qualified type name
	 * @param featureType the feature type
	 */
	public SimpleFeatureTypeBinding(QName name, SimpleFeatureType featureType) {
		super();
		
		this.name = name;
		this.type = featureType;
	}

	/**
	 * @see AbstractComplexBinding#parse(ElementInstance, Node, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		// get feature ID
        String fid = (String) node.getAttributeValue("fid");

        if (fid == null) {
            //look for id
            fid = (String) node.getAttributeValue("id");
        }
        
        // attributes for property map
        Map<String, Map<String, String>> attributes = new HashMap<String, Map<String,String>>();

        // build feature
		SimpleFeatureBuilder b = new SimpleFeatureBuilder(type);

        for (int i = 0; i < type.getAttributeCount(); i++) {
            AttributeDescriptor att = type.getDescriptor(i);
            AttributeType attType = att.getType();
            Object attValue = node.getChildValue(att.getLocalName());
            
            if (attValue instanceof AttributesWrapper) {
            	AttributesWrapper wrapper = (AttributesWrapper) attValue;
            	attValue = wrapper.getValue();
            	attributes.put(att.getLocalName(), wrapper.getAttributes());
            }

            if ((attValue != null) && !attType.getBinding().isAssignableFrom(attValue.getClass())) {
                //type mismatch, to try convert
            	//TODO register converter for AttributeWrapper? Possible?
                Object converted = Converters.convert(attValue, attType.getBinding());

                if (converted != null) {
                    attValue = converted;
                }
            }

            b.add(attValue);
        }
        
        //create the feature
        Feature result = b.buildFeature(fid);
        
        // add missing attributes
        for (Entry<String, Map<String, String>> entry : attributes.entrySet()) {
        	Property p = result.getProperty(entry.getKey());
        	if (p != null) {
        		/*
        		 * @see eu.esdihumboldt.tools.AttributeProperty
        		 */
        		Map<String, String> attMap = (Map<String, String>) p.getUserData().get(XML_ATTRIBUTES);
        		if (attMap == null) {
        			attMap = new HashMap<String, String>();
        			p.getUserData().put(XML_ATTRIBUTES, attMap);
        		}
        		
        		for (Entry<String, String> attEntry : entry.getValue().entrySet()) {
        			attMap.put('<' + attEntry.getKey() + '>', attEntry.getValue());
        		}
        	}
        	else {
        		log.warn("Property for attribute not found");
        	}
        }
        
        return result;
	}

	/**
	 * @see Binding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return name;
	}

	/**
	 * @see Binding#getType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class getType() {
		return Feature.class;
	}

}
