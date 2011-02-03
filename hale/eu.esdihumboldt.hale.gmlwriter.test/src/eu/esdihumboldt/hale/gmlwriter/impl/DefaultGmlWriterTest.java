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

package eu.esdihumboldt.hale.gmlwriter.impl;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.geotools.feature.AttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureImpl;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.identity.Identifier;

import eu.esdihumboldt.hale.gmlparser.CstFeatureCollection;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;
import eu.esdihumboldt.tools.FeatureInspector;

/**
 * Tests for {@link DefaultGmlWriter}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefaultGmlWriterTest {

	/**
	 * Test writing a simple feature from a simple schema
	 * 
	 * @throws Exception if any error occurs 
	 */
	@Test
	public void testSimpleWrite() throws Exception {
		SchemaProvider sp = new ApacheSchemaProvider();
		
		// load the sample schema
		Schema schema = sp.loadSchema(getClass().getResource("/data/sample_wva/wfs_va.xsd").toURI(), null);
		
		FeatureCollection<FeatureType, Feature> fc = new CstFeatureCollection();
		
		// create feature
		Feature feature = createFeature(schema.getElements().values().iterator().next().getType());
		
		// set some values
		FeatureInspector.setPropertyValue(feature, Arrays.asList("LENGTH"), 10.2);
		FeatureInspector.setPropertyValue(feature, Arrays.asList("NAME"), "Test");
		
		fc.add(feature );
		
		// write to file (XXX for now console)
		DefaultGmlWriter writer = new DefaultGmlWriter();
		OutputStream out = System.err;
		try {
			writer.writeFeatures(fc, schema, out );
		} finally {
			out.flush();
			out.close();
		}
	}

	private Feature createFeature(TypeDefinition type) {
		Collection<Property> properties = new HashSet<Property>();
		SimpleFeatureType targetType = ((SimpleFeatureType) type.getFeatureType());
		for (AttributeDescriptor ad : targetType.getAttributeDescriptors()) {
			Identifier id = new FeatureIdImpl(ad.getLocalName());
			// create normal AttributeImpls
			if (ad instanceof GeometryDescriptorImpl) {
				properties.add(new GeometryAttributeImpl(
						null, (GeometryDescriptor)ad, id));
			}
			else if (ad instanceof AttributeDescriptorImpl) {
				properties.add(new AttributeImpl(null, ad, id));
			}
		}
		return new FeatureImpl(properties, targetType, 
					new FeatureIdImpl(UUID.randomUUID().toString()));
	}
	
}
