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

package eu.esdihumboldt.hale.io.gml.geometry.handler.internal;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;

import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.geometry.curve.InterpolationConstant;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Base class for handler tests.
 * 
 * @author Simon Templer, Arun Varma
 */
@SuppressWarnings("restriction")
public abstract class AbstractHandlerTest {

	/**
	 * Test namespace
	 */
	public static final String NS_TEST = "eu:esdihumboldt:hale:test";

	/**
	 * Maximum positional error for curve geometry
	 */
	private static final double MAX_POSITION_ERROR = 0.1;

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
		return loadXMLInstances(schemaLocation, xmlLocation, true);
	}

	/**
	 * Load an instance collection from a GML file.
	 * 
	 * @param schemaLocation the GML application schema location
	 * @param xmlLocation the GML file location
	 * @param keepOriginal true to keep original coordinates unchanged else
	 *            false
	 * @return the instance collection
	 * @throws IOException if reading schema or instances failed
	 * @throws IOProviderConfigurationException if the I/O providers were not
	 *             configured correctly
	 */
	public static InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation,
			boolean keepOriginal) throws IOException, IOProviderConfigurationException {
		return loadXMLInstances(schemaLocation, xmlLocation, keepOriginal, MAX_POSITION_ERROR);
	}

	/**
	 * Load an instance collection from a GML file.
	 * 
	 * @param schemaLocation the GML application schema location
	 * @param xmlLocation the GML file location
	 * @param keepOriginal true to keep original coordinates unchanged else
	 *            false
	 * @param maxPoisitionError maximum positional error parameter value
	 * @return the instance collection
	 * @throws IOException if reading schema or instances failed
	 * @throws IOProviderConfigurationException if the I/O providers were not
	 *             configured correctly
	 */
	public static InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation,
			boolean keepOriginal, double maxPoisitionError)
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
		instanceReader.setParameter(InterpolationConstant.INTERPOL_GEOMETRY_KEEP_ORIGINAL,
				Value.of(keepOriginal));
		instanceReader.setParameter(InterpolationConstant.INTERPOL_MAX_POSITION_ERROR,
				Value.of(maxPoisitionError));

		IOReport instanceReport = instanceReader.execute(null);
		assertTrue(instanceReport.isSuccess());

		return instanceReader.getInstances();
	}

	/**
	 * Retrieve geometries from an instances holding them as value.
	 * 
	 * @param geomInstance the instance with a geometry value
	 * @return the list of geometry properties
	 */
	protected List<? extends GeometryProperty<?>> getGeometries(Instance geomInstance) {
		List<GeometryProperty<?>> result = new ArrayList<>();
		if (geomInstance.getValue() instanceof Collection<?>) {
			for (Object instance : ((Collection<?>) geomInstance.getValue())) {
				assertTrue(instance instanceof GeometryProperty<?>);
				result.add((GeometryProperty<?>) instance);
			}
		}
		else if (geomInstance.getValue() instanceof GeometryProperty<?>) {
			result.add((GeometryProperty<?>) geomInstance.getValue());
		}
		else {
			throw new IllegalStateException("No geometries encountered in instance");
		}
		return result;
	}

}
