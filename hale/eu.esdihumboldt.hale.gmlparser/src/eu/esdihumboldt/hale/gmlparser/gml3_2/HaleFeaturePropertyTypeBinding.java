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

import javax.xml.namespace.QName;

import org.geotools.gml3.bindings.FeaturePropertyTypeBinding;
import org.geotools.gml3.v3_2.GML;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleFeaturePropertyTypeBinding extends FeaturePropertyTypeBinding {

	/**
	 * @see FeaturePropertyTypeBinding#getTarget()
	 */
	@Override
	public QName getTarget() {
		return GML.FeaturePropertyType;
	}

	/**
	 * @see FeaturePropertyTypeBinding#parse(ElementInstance, Node, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object parse(ElementInstance instance, Node node, Object value)
			throws Exception {
		// must be an array of SimpleFeatures because of AbstractFeatureCollectionTypeBinding
		List<SimpleFeature> features = node.getChildValues(SimpleFeature.class);
		if (features == null || features.isEmpty()) {
			return null;
		}
		else {
			return features.toArray(new SimpleFeature[features.size()]);
		}
	}

	/**
	 * @see FeaturePropertyTypeBinding#getType()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class getType() {
		return SimpleFeature[].class; //return List.class;
	}

}
