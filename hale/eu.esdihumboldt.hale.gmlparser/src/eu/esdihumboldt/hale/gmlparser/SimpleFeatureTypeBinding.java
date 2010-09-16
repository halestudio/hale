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

package eu.esdihumboldt.hale.gmlparser;

import javax.xml.namespace.QName;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.Converters;
import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SimpleFeatureTypeBinding extends AbstractComplexBinding {

	private final QName name;
	
	private final SimpleFeatureType type;
	
	/**
	 * @param name
	 * @param featureType
	 */
	public SimpleFeatureTypeBinding(QName name, SimpleFeatureType featureType) {
		super();
		
		this.name = name;
		this.type = featureType;
	}

	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		// get feature ID
        String fid = (String) node.getAttributeValue("fid");

        if (fid == null) {
            //look for id
            fid = (String) node.getAttributeValue("id");
        }

        // build feature
		SimpleFeatureBuilder b = new SimpleFeatureBuilder(type);

        for (int i = 0; i < type.getAttributeCount(); i++) {
            AttributeDescriptor att = type.getDescriptor(i);
            AttributeType attType = att.getType();
            Object attValue = node.getChildValue(att.getLocalName());

            if ((attValue != null) && !attType.getBinding().isAssignableFrom(attValue.getClass())) {
                //type mismatch, to try convert
                Object converted = Converters.convert(attValue, attType.getBinding());

                if (converted != null) {
                    attValue = converted;
                }
            }

            b.add(attValue);
        }
        
        //create the feature
        return b.buildFeature(fid);
	}

	/**
	 * @see org.geotools.xml.Binding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return name;
	}

	/**
	 * @see org.geotools.xml.Binding#getType()
	 */
	@Override
	public Class getType() {
		return Feature.class;
	}

}
