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

package eu.esdihumboldt.hale.common.instance.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.junit.Test;


import eu.esdihumboldt.hale.common.core.io.ContentType;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceCollection;
import eu.esdihumboldt.hale.io.gml.reader.internal.StreamGmlReader;
import eu.esdihumboldt.hale.io.gml.reader.internal.XmlInstanceReaderFactory;

import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeSchemaReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;


/**
 * Tests for {@link GmlInstanceCollection}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("restriction")
public class PropertyResolverTest {
	
	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadShiporder() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/shiporder/shiporder.xsd").toURI(),
				getClass().getResource("/data/shiporder/shiporder.xml").toURI());
		
//		String ns = "http://www.example.com";
		int size = instances. size();
		ResourceIterator<Instance> it = instances.iterator();
		assertTrue(it.hasNext());
		
		Instance instance = it.next();
		assertNotNull(instance);
			
	//	Filter filter = CQL.toFilter("orderperson = John Smith");	
	//	assertTrue(filter.evaluate(instance));
		
		TypeDefinition test = instance.getDefinition().getChildren().iterator().next().asProperty().getParentType();
		
		//assertTrue(PropertyResolver.hasProperty(instance, "{http://www.example.com}orderperson"));
	//	assertTrue(PropertyResolver.hasProperty(instance, "{http://www.example.com}shipto.{http://www.example.com}city"));
	//	assertTrue(PropertyResolver.hasProperty(instance, "orderperson"));
		//assertTrue(PropertyResolver.hasProperty(instance, "shipto.city"));
		assertTrue(PropertyResolver.hasProperty(instance, "shipto.{http://www.example.com}city"));
		//TODO
		
		it.close();
	}
	
	
	@Test
	public void testComplexInstances() throws Exception {
		
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier((getClass().getResource("/data/erm/inspire3/HydroPhysicalWaters.xsd").toURI())));
		IOReport report = reader.execute(null);
		assertTrue(report.isSuccess());
		Schema schema = reader.getSchema();
		
		StreamGmlReader instanceReader = new StreamGmlReader(ContentType.getContentType("GML"), true);;
		instanceReader.setSource(new DefaultInputSupplier(getClass().getResource("/data/out/transformWrite_ERM_HPW.gml").toURI()));
		instanceReader.setSourceSchema(schema);
		
		instanceReader.validate();
		report = instanceReader.execute(null);
		assertTrue(report.isSuccess());
		
		InstanceCollection instances = instanceReader.getInstances();
		assertFalse(instances.isEmpty());
		
		
		
		
		ResourceIterator<Instance> ri = instances.iterator();
		Instance instance = ri.next();
	
		
	    assertTrue(PropertyResolver.hasProperty(instance, "description"));
	    assertTrue(PropertyResolver.hasProperty(instance, "{http://www.opengis.net/gml/3.2}description"));
        assertTrue(PropertyResolver.hasProperty(instance, "boundedBy.Envelope.coordinates"));
        assertTrue(PropertyResolver.hasProperty(instance, "boundedBy.Envelope.{http://www.opengis.net/gml/3.2}coordinates"));
        assertTrue(PropertyResolver.hasProperty(instance, "{http://www.opengis.net/gml/3.2}boundedBy.{http://www.opengis.net/gml/3.2}Envelope.{http://www.opengis.net/gml/3.2}coordinates"));
        assertFalse(PropertyResolver.hasProperty(instance, "boundedBy.Envelope.{http://www.opengis.net/gml/3.2}coordinates.description"));
		assertTrue(PropertyResolver.hasProperty(instance, "location.AbstractSolid.id"));
		assertTrue(PropertyResolver.hasProperty(instance, "location.CompositeCurve.curveMember.type"));
		
        
		//TODO
        //Aufzeichnen der Pfade funktioniert noch nicht, nur bis letztes und vorletztes item
        //LOOP Prevention auch noch nicht richtig	
        //nur gruppen und instanzen haben kinder
	
	}	
	
	private InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation) throws IOException, IOProviderConfigurationException {
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(schemaLocation));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		Schema sourceSchema = reader.getSchema();
		
		
		//
		
		
		XmlInstanceReaderFactory f = new XmlInstanceReaderFactory();
		InstanceReader instanceReader = f.createProvider();
		
		instanceReader.setSource(new DefaultInputSupplier(xmlLocation));
		instanceReader.setSourceSchema(sourceSchema);
		
		IOReport instanceReport = instanceReader.execute(null);
		assertTrue(instanceReport.isSuccess());
		
//		 Collection<? extends TypeDefinition> coll = sourceSchema.getTypes();
//		int size = coll.size();
		 
		return instanceReader.getInstances();	
	}

}


//problem? set *spring*osgi and convert to true in runconfigs->plugins
