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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.reader.internal.XmlInstanceReaderFactory;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
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
		
		ResourceIterator<Instance> it = instances.iterator();
		assertTrue(it.hasNext());
		
		Instance instance = it.next();
		assertNotNull(instance);
		
		//TODO
		
		it.close();
	}
	
	private InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation) throws IOException, IOProviderConfigurationException {
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(schemaLocation));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		Schema sourceSchema = reader.getSchema();
		
		XmlInstanceReaderFactory f = new XmlInstanceReaderFactory();
		InstanceReader instanceReader = f.createProvider();
		
		instanceReader.setSource(new DefaultInputSupplier(xmlLocation));
		instanceReader.setSourceSchema(sourceSchema);
		
		IOReport instanceReport = instanceReader.execute(null);
		assertTrue(instanceReport.isSuccess());
		
		return instanceReader.getInstances();
	}

}
