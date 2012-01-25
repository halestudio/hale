/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.gml.geometry.handler.internal;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.vividsolutions.jts.geom.GeometryFactory;

import de.fhg.igd.osgi.util.OsgiUtilsActivator;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Base class for handler tests.
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class AbstractHandlerTest {
	
	/**
	 * Test namespace
	 */
	public static final String NS_TEST = "eu:esdihumboldt:hale:test";
	
	/**
	 * The geometry factory instance
	 */
	protected GeometryFactory geomFactory;
	
	/**
	 * Prepare the conversion service
	 */
	@BeforeClass
	public static void initAll() {
		List<String> bundlesToStart = new ArrayList<String>();
		bundlesToStart.add("org.springframework.osgi.core"); // for osgi extensions in application context
		bundlesToStart.add("org.springframework.osgi.extender"); // activate the extender
		bundlesToStart.add("eu.esdihumboldt.hale.common.convert"); // activate the conversion service
		
		Map<String, Bundle> bundles = new HashMap<String, Bundle>();
		BundleContext context = OsgiUtilsActivator.getInstance().getContext();
		for (Bundle bundle : context.getBundles()) {
			bundles.put(bundle.getSymbolicName(), bundle);
		}
		
		for (String bundleName : bundlesToStart) {
			Bundle bundle = bundles.get(bundleName);
			if (bundle != null) {
				try {
					bundle.start();
					System.out.println("Attempting to start bundle " + bundle.getSymbolicName());
					Thread.sleep(2000); //XXX wait for start to have finished FIXME improve
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.err.println("Bundle to start not found: " + bundleName);
			}
		}
	}
	
	/**
	 * Initialize the test class.
	 */
	@Before
	public void init() {
		geomFactory = new GeometryFactory();
	}
	
	/**
	 * Load an instance collection from a GML file.
	 * @param schemaLocation the GML application schema location
	 * @param xmlLocation the GML file location
	 * @return the instance collection
	 * @throws IOException if reading schema or instances failed
	 * @throws IOProviderConfigurationException if the I/O providers were 
	 *   not configured correctly
	 */
	public static InstanceCollection loadXMLInstances(URI schemaLocation,
			URI xmlLocation) throws IOException,
			IOProviderConfigurationException {
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(schemaLocation));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		Schema sourceSchema = reader.getSchema();
		
		InstanceReader instanceReader = new GmlInstanceReader();
		
		instanceReader.setSource(new DefaultInputSupplier(xmlLocation));
		instanceReader.setSourceSchema(sourceSchema);
		
		IOReport instanceReport = instanceReader.execute(null);
		assertTrue(instanceReport.isSuccess());
		
		return instanceReader.getInstances();	
	}

}
