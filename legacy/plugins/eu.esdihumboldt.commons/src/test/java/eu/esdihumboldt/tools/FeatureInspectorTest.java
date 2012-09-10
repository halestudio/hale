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

package eu.esdihumboldt.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import eu.esdihumboldt.commons.tools.FeatureInspector;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class FeatureInspectorTest {

	private final static String ns = "http://www.esdi-humboldt.eu/schema/test";

	/**
	 * Test with non-nested property
	 */
	@Test
	public void testSimple() {
		Feature source = this.buildSimpleFeature("SourceType");

		// test getting non-nested value
		Object value = FeatureInspector.getPropertyValue(source,
				Collections.singletonList("int"), null);
		Assert.assertEquals(new Integer(1234), value);

		// test setting non-nested value
		FeatureInspector.setPropertyValue(source,
				Collections.singletonList("int"), Integer.valueOf(2));
		value = FeatureInspector.getPropertyValue(source,
				Collections.singletonList("int"), null);
		Assert.assertEquals(Integer.valueOf(2), value);
	}

	/**
	 * Test with nested property
	 */
	@Test
	public void testNested() {
		Feature source = this.buildSimpleFeature("SourceType");

		List<String> nestedProperty = new ArrayList<String>();
		nestedProperty.add("complex");
		nestedProperty.add("int");

		// test getting nested value
		Object value = FeatureInspector.getPropertyValue(source,
				nestedProperty, null);
		// property not set, value must be default (null)
		Assert.assertNull(value);

		// test setting nested value
		FeatureInspector.setPropertyValue(source, nestedProperty,
				Integer.valueOf(8));
		value = FeatureInspector.getPropertyValue(source, nestedProperty, null);
		Assert.assertEquals(Integer.valueOf(8), value);
	}

	private Feature buildSimpleFeature(String featureTypeName) {
		SimpleFeatureType sft = this.getType(ns, featureTypeName, false);

		SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
		SimpleFeature f = sfb.buildFeature(
				String.valueOf(featureTypeName.hashCode()), new Object[] {
						"12.56", // string
						new Double(12.345678), // double
						new Long(1234567890), // long
						new Integer(1234), // int
						new Float(12.34), // float
						null // complex
				});

		return f;
	}

	private SimpleFeatureType getType(String featureTypeNamespace,
			String featureTypeName, boolean simple) {

		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.add("string", String.class);
			ftbuilder.add("double", Double.class);
			ftbuilder.add("long", Long.class);
			ftbuilder.add("int", Integer.class);
			ftbuilder.add("float", Float.class);
			if (!simple) {
				ftbuilder.add(getComplexAttributeDescriptor());
			}
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

	private AttributeDescriptor getComplexAttributeDescriptor() {
		return new AttributeDescriptorImpl(getType(ns, "AttType", true),
				new NameImpl("complex"), 0, 1, true, null);
	}

}
