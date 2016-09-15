/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.geometric.test.reprojectgeometry;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.BeforeClass;

import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Base class for handler tests.
 * 
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
		TestUtil.startConversionService();
	}

	/**
	 * Initialize the test class.
	 */
	@Before
	public void init() {
		geomFactory = new GeometryFactory();

		PropertyResolver.clearCache();
	}

	/**
	 * Load an instance collection from a GML file.
	 * 
	 * @param schemaLocation the GML application schema location
	 * @param xmlLocation the GML file location
	 * @return the instance collection
	 * @throws IOException if reading schema or instances failed
	 * @throws IOProviderConfigurationException if the I/O providers were not
	 *             configured correctly
	 */
	public static InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation)
			throws IOException, IOProviderConfigurationException {
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
