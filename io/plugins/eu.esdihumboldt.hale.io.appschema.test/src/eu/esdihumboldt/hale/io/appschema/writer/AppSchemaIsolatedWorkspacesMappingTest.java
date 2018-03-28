/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     GeoSolutions <https://www.geo-solutions.it>
 */

package eu.esdihumboldt.hale.io.appschema.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentReader;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.common.schema.persist.hsd.HaleSchemaReader;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;
import eu.esdihumboldt.hale.io.appschema.model.WorkspaceConfiguration;
import eu.esdihumboldt.hale.io.geoserver.Namespace;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Tests the correct handling of workspace configuration when the mapping is
 * generated.
 * 
 * @author Stefano Costa, GeoSolutions
 */
@SuppressWarnings("javadoc")
public class AppSchemaIsolatedWorkspacesMappingTest {

	private static final String SOURCE_SCHEMA_PATH = "/data/isolated_workspaces_source.haleschema";
	private static final String TARGET_SCHEMA_STATIONS_PATH = "/data/stations_gml32.xsd";
	private static final String ALIGNMENT_PATH = "/data/isolated_workspaces_test.halex.alignment.xml";

	private static final String STATIONS_NS_URI = "http://www.stations_gml32.org/1.0";
	private static final String STATIONS_WS_DEFAULT = "st";
	private static final String STATIONS_WS_RENAMED = "stations";

	private static final String MEASUREMENTS_NS_URI = "http://www.measurements_gml32.org/1.0";
	private static final String MEASUREMENTS_WS_DEFAULT = "nns__1";
	private static final String MEASUREMENTS_WS_RENAMED = "measurements";
	private static final String MEASUREMENTS_TYPENAME = "MeasurementType";
	private static final QName MEASUREMENTS_QNAME = new QName(MEASUREMENTS_NS_URI,
			MEASUREMENTS_TYPENAME);

	private static final String FEATURE_CHAINING_CONF = "/data/feature-chaining-isolated-workspaces.xml";
	private static final String WORKSPACES_CONF = "/data/workspace-conf-isolated-workspaces.xml";

	private static DefaultSchemaSpace sourceSchemaSpace;
	private static DefaultSchemaSpace targetSchemaSpace;
	private static Alignment alignment;

	private FeatureChaining featureChainingConf;
	private WorkspaceConfiguration workspaceConf;

	@BeforeClass
	public static void init() throws Exception {
		TestUtil.startConversionService();

		sourceSchemaSpace = new DefaultSchemaSpace();
		targetSchemaSpace = new DefaultSchemaSpace();

		Schema source = loadSchema(new HaleSchemaReader(), SOURCE_SCHEMA_PATH);
		assertNotNull(source);
		sourceSchemaSpace.addSchema(source);

		Schema target = loadSchema(new XmlSchemaReader(), TARGET_SCHEMA_STATIONS_PATH);
		assertNotNull(target);
		targetSchemaSpace.addSchema(target);

		// make sure MeasurementsType is included in the mapping relevant types
		List<TypeDefinition> mappingRelevantTypes = new ArrayList<>();
		mappingRelevantTypes.add(targetSchemaSpace.getType(MEASUREMENTS_QNAME));
		targetSchemaSpace.toggleMappingRelevant(mappingRelevantTypes);
		assertEquals(3, targetSchemaSpace.getMappingRelevantTypes().size());

		alignment = loadAlignment(new JaxbAlignmentReader(), ALIGNMENT_PATH);
		assertNotNull(alignment);
	}

	@Before
	public void loadConfiguration() throws Exception {
		featureChainingConf = loadFeatureChainingConf(FEATURE_CHAINING_CONF);
		workspaceConf = loadWorkspaceConf(WORKSPACES_CONF);
	}

	/**
	 * Isolated attribute must be false, names must match the default ones,
	 * unique mapping names must not be generated.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testNullWorkspaceConfiguration() throws IOException {
		AppSchemaMappingGenerator generator = new AppSchemaMappingGenerator(alignment,
				targetSchemaSpace, null, featureChainingConf, null);

		IOReporter reporter = new DefaultIOReporter(
				targetSchemaSpace.getSchemas().iterator().next(), "Generate App-Schema Mapping",
				AppSchemaIO.CONTENT_TYPE_MAPPING, false);
		generator.generateMapping(reporter);

		assertEquals(STATIONS_WS_DEFAULT, generator.getMainNamespace().name());
		assertFalse((boolean) generator.getMainNamespace().getAttribute(Namespace.ISOLATED));
		assertEquals(STATIONS_WS_DEFAULT, generator.getMainWorkspace().name());
		assertFalse((boolean) generator.getMainWorkspace().getAttribute(Namespace.ISOLATED));

		boolean measurementsNsFound = false;
		for (Namespace ns : generator.getSecondaryNamespaces()) {
			if (MEASUREMENTS_NS_URI.equals(ns.getAttribute(Namespace.URI))) {
				measurementsNsFound = true;
				assertEquals(MEASUREMENTS_WS_DEFAULT, ns.name());
				assertFalse((boolean) ns.getAttribute(Namespace.ISOLATED));
				assertEquals(MEASUREMENTS_WS_DEFAULT, generator.getWorkspace(ns).name());
				assertFalse((boolean) generator.getWorkspace(ns).getAttribute(Namespace.ISOLATED));
			}
		}
		assertTrue(measurementsNsFound);

		List<FeatureTypeMapping> typeMappings = generator.getGeneratedMapping()
				.getAppSchemaMapping().getTypeMappings().getFeatureTypeMapping();
		assertEquals(2, typeMappings.size());
		for (FeatureTypeMapping typeMapping : typeMappings) {
			assertTrue(Strings.isNullOrEmpty(typeMapping.getMappingName()));
		}
	}

	/**
	 * Isolated attribute must be false, names must match those specified in the
	 * workspace configuration, unique mapping names must not be generated.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testNonIsolatedRenamed() throws IOException {
		AppSchemaMappingGenerator generator = new AppSchemaMappingGenerator(alignment,
				targetSchemaSpace, null, featureChainingConf, workspaceConf);

		IOReporter reporter = new DefaultIOReporter(
				targetSchemaSpace.getSchemas().iterator().next(), "Generate App-Schema Mapping",
				AppSchemaIO.CONTENT_TYPE_MAPPING, false);
		generator.generateMapping(reporter);

		assertEquals(STATIONS_WS_RENAMED, generator.getMainNamespace().name());
		assertFalse((boolean) generator.getMainNamespace().getAttribute(Namespace.ISOLATED));
		assertEquals(STATIONS_WS_RENAMED, generator.getMainWorkspace().name());
		assertFalse((boolean) generator.getMainWorkspace().getAttribute(Namespace.ISOLATED));

		boolean measurementsNsFound = false;
		for (Namespace ns : generator.getSecondaryNamespaces()) {
			if (MEASUREMENTS_NS_URI.equals(ns.getAttribute(Namespace.URI))) {
				measurementsNsFound = true;
				assertEquals(MEASUREMENTS_WS_RENAMED, ns.name());
				assertFalse((boolean) ns.getAttribute(Namespace.ISOLATED));
				assertEquals(MEASUREMENTS_WS_RENAMED, generator.getWorkspace(ns).name());
				assertFalse((boolean) generator.getWorkspace(ns).getAttribute(Namespace.ISOLATED));
			}
		}
		assertTrue(measurementsNsFound);

		List<FeatureTypeMapping> typeMappings = generator.getGeneratedMapping()
				.getAppSchemaMapping().getTypeMappings().getFeatureTypeMapping();
		assertEquals(2, typeMappings.size());
		for (FeatureTypeMapping typeMapping : typeMappings) {
			assertTrue(Strings.isNullOrEmpty(typeMapping.getMappingName()));
		}
	}

	/**
	 * Isolated attribute must be true, names must match those specified in the
	 * workspace configuration, unique mapping names must be generated.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testBothIsolated() throws IOException {
		workspaceConf.getWorkspaces().forEach(ws -> ws.setIsolated(true));

		AppSchemaMappingGenerator generator = new AppSchemaMappingGenerator(alignment,
				targetSchemaSpace, null, featureChainingConf, workspaceConf);

		IOReporter reporter = new DefaultIOReporter(
				targetSchemaSpace.getSchemas().iterator().next(), "Generate App-Schema Mapping",
				AppSchemaIO.CONTENT_TYPE_MAPPING, false);
		generator.generateMapping(reporter);

		assertEquals(STATIONS_WS_RENAMED, generator.getMainNamespace().name());
		assertTrue((boolean) generator.getMainNamespace().getAttribute(Namespace.ISOLATED));
		assertEquals(STATIONS_WS_RENAMED, generator.getMainWorkspace().name());
		assertTrue((boolean) generator.getMainWorkspace().getAttribute(Namespace.ISOLATED));

		boolean measurementsNsFound = false;
		for (Namespace ns : generator.getSecondaryNamespaces()) {
			if (MEASUREMENTS_NS_URI.equals(ns.getAttribute(Namespace.URI))) {
				measurementsNsFound = true;
				assertEquals(MEASUREMENTS_WS_RENAMED, ns.name());
				assertTrue((boolean) ns.getAttribute(Namespace.ISOLATED));
				assertEquals(MEASUREMENTS_WS_RENAMED, generator.getWorkspace(ns).name());
				assertTrue((boolean) generator.getWorkspace(ns).getAttribute(Namespace.ISOLATED));
			}
		}
		assertTrue(measurementsNsFound);

		List<FeatureTypeMapping> typeMappings = generator.getGeneratedMapping()
				.getAppSchemaMapping().getTypeMappings().getFeatureTypeMapping();
		assertEquals(2, typeMappings.size());

		Set<String> mappingNames = new HashSet<String>();
		for (FeatureTypeMapping typeMapping : typeMappings) {
			assertFalse(Strings.isNullOrEmpty(typeMapping.getMappingName()));
			mappingNames.add(typeMapping.getMappingName());
		}
		assertEquals(2, mappingNames.size());
	}

	/**
	 * Isolated attribute must be true for the stations ws and false for the
	 * measurements ws, names must match those specified in the workspace
	 * configuration, unique mapping names must be generated only for the
	 * stations ws.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testStationsIsolated() throws IOException {
		workspaceConf.getWorkspace(STATIONS_NS_URI).setIsolated(true);

		AppSchemaMappingGenerator generator = new AppSchemaMappingGenerator(alignment,
				targetSchemaSpace, null, featureChainingConf, workspaceConf);

		IOReporter reporter = new DefaultIOReporter(
				targetSchemaSpace.getSchemas().iterator().next(), "Generate App-Schema Mapping",
				AppSchemaIO.CONTENT_TYPE_MAPPING, false);
		generator.generateMapping(reporter);

		assertEquals(STATIONS_WS_RENAMED, generator.getMainNamespace().name());
		assertTrue((boolean) generator.getMainNamespace().getAttribute(Namespace.ISOLATED));
		assertEquals(STATIONS_WS_RENAMED, generator.getMainWorkspace().name());
		assertTrue((boolean) generator.getMainWorkspace().getAttribute(Namespace.ISOLATED));

		boolean measurementsNsFound = false;
		for (Namespace ns : generator.getSecondaryNamespaces()) {
			if (MEASUREMENTS_NS_URI.equals(ns.getAttribute(Namespace.URI))) {
				measurementsNsFound = true;
				assertEquals(MEASUREMENTS_WS_RENAMED, ns.name());
				assertFalse((boolean) ns.getAttribute(Namespace.ISOLATED));
				assertEquals(MEASUREMENTS_WS_RENAMED, generator.getWorkspace(ns).name());
				assertFalse((boolean) generator.getWorkspace(ns).getAttribute(Namespace.ISOLATED));
			}
		}
		assertTrue(measurementsNsFound);

		List<FeatureTypeMapping> typeMappings = generator.getGeneratedMapping()
				.getAppSchemaMapping().getTypeMappings().getFeatureTypeMapping();
		assertEquals(2, typeMappings.size());

		boolean stationsFtFound = false, measurementsFtFound = false;
		Set<String> mappingNames = new HashSet<String>();
		for (FeatureTypeMapping typeMapping : typeMappings) {
			if ((STATIONS_WS_RENAMED + ":Station_gml32").equals(typeMapping.getTargetElement())) {
				stationsFtFound = true;
				assertFalse(Strings.isNullOrEmpty(typeMapping.getMappingName()));
			}
			if ((MEASUREMENTS_WS_RENAMED + ":Measurement_gml32")
					.equals(typeMapping.getTargetElement())) {
				measurementsFtFound = true;
				assertTrue(Strings.isNullOrEmpty(typeMapping.getMappingName()));
			}
			if (!Strings.isNullOrEmpty(typeMapping.getMappingName())) {
				mappingNames.add(typeMapping.getMappingName());
			}
		}
		assertEquals(1, mappingNames.size());
		assertTrue(stationsFtFound);
		assertTrue(measurementsFtFound);
	}

	private FeatureChaining loadFeatureChainingConf(String confResource) throws Exception {
		Element root = loadXmlResource(confResource);
		// read value object from XML
		FeatureChaining chainingConf = HaleIO.getComplexValue(root, FeatureChaining.class, null);
		assertNotNull(chainingConf);
		AppSchemaMappingUtils.resolvePropertyTypes(chainingConf, targetSchemaSpace,
				SchemaSpaceID.TARGET);
		return chainingConf;
	}

	private WorkspaceConfiguration loadWorkspaceConf(String confResource) throws Exception {
		Element root = loadXmlResource(confResource);
		// read value object from XML
		WorkspaceConfiguration workspaceConf = HaleIO.getComplexValue(root,
				WorkspaceConfiguration.class, null);
		assertNotNull(workspaceConf);
		return workspaceConf;
	}

	private Element loadXmlResource(String resource)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(getClass().getResourceAsStream(resource)).getDocumentElement();
	}

	private static Schema loadSchema(SchemaReader schemaReader, String resource) throws Exception {
		DefaultInputSupplier input = new DefaultInputSupplier(
				AppSchemaIsolatedWorkspacesMappingTest.class.getResource(resource).toURI());
		schemaReader.setSharedTypes(new DefaultTypeIndex());
		schemaReader.setSource(input);

		schemaReader.validate();
		IOReport report = schemaReader.execute(null);

		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return schemaReader.getSchema();
	}

	private static Alignment loadAlignment(AlignmentReader alignReader, String resource)
			throws Exception {
		alignReader.setSource(new DefaultInputSupplier(
				AppSchemaIsolatedWorkspacesMappingTest.class.getResource(resource).toURI()));
		alignReader.setSourceSchema(sourceSchemaSpace);
		alignReader.setTargetSchema(targetSchemaSpace);
		alignReader.setPathUpdater(new PathUpdate(null, null));

		IOReport report = alignReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

		return alignReader.getAlignment();
	}
}
