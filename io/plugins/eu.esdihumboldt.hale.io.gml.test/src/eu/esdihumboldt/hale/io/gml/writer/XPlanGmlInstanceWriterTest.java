/*
 * Copyright (c) 2026 wetransform GmbH
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.esdihumboldt.hale.io.gml.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Tests for {@link XPlanGmlInstanceWriter}, in particular for the
 * {@code gml:boundedBy}/{@code gml:Envelope} that defines the mandatory default
 * CRS of the XPlanung model (conformance rule 2.1.3.1).
 *
 * @author Emanuela Epure
 */
@SuppressWarnings("restriction")
public class XPlanGmlInstanceWriterTest {

	private static final String XPLAN_NS = "http://www.xplanung.de/xplangml/test"; //$NON-NLS-1$

	private static final String GML_NS = "http://www.opengis.net/gml/3.2"; //$NON-NLS-1$

	private static final String SRS_NAME = "EPSG:25832"; //$NON-NLS-1$

	private static final GeometryFactory geomFactory = new GeometryFactory();

	/**
	 * Prepare the conversion service
	 */
	@BeforeClass
	public static void initAll() {
		TestUtil.startConversionService();
	}

	/**
	 * Writing with geometries that carry a CRS must produce a {@code gml:boundedBy}
	 * with a {@code gml:Envelope} whose {@code srsName} defines the default CRS and
	 * whose extent covers all exported geometries. The envelope must use the GML
	 * 3.2 {@code lowerCorner}/{@code upperCorner} form and must not use
	 * {@code gml:pos}.
	 *
	 * @throws Exception if any error occurs
	 */
	@Test
	public void testBoundedByWithDefaultCrs() throws Exception {
		final XmlIndex schema = loadSchema();
		final TypeDefinition featureType = getFeatureType(schema);
		final CRSDefinition crs = new CodeDefinition(SRS_NAME);

		final InstanceCollection instances = new DefaultInstanceCollection(
				Arrays.asList(createFeature(featureType, crs, 10.0, 20.0),
						createFeature(featureType, crs, 110.0, 220.0)));

		final File output = File.createTempFile("xplanAuszug", ".gml"); //$NON-NLS-1$ //$NON-NLS-2$
		output.deleteOnExit();
		final IOReport report = write(schema, instances, output);
		assertTrue("Writing the XPlanGML output was not successful", report.isSuccess()); //$NON-NLS-1$

		final Element envelope = getSingleEnvelope(output);

		assertEquals("Envelope should carry the default CRS as srsName", SRS_NAME, //$NON-NLS-1$
				envelope.getAttribute("srsName")); //$NON-NLS-1$

		// GML 3.2 form: lowerCorner/upperCorner, never gml:pos (gap "gml:pos").
		final NodeList lower = envelope.getElementsByTagNameNS(GML_NS, "lowerCorner"); //$NON-NLS-1$
		final NodeList upper = envelope.getElementsByTagNameNS(GML_NS, "upperCorner"); //$NON-NLS-1$
		assertEquals("Exactly one gml:lowerCorner expected", 1, lower.getLength()); //$NON-NLS-1$
		assertEquals("Exactly one gml:upperCorner expected", 1, upper.getLength()); //$NON-NLS-1$
		assertEquals("A GML 3.2 Envelope must not use gml:pos", 0, //$NON-NLS-1$
				envelope.getElementsByTagNameNS(GML_NS, "pos").getLength()); //$NON-NLS-1$

		// The envelope is written directly from the JTS envelope, so its
		// corners are (minX minY) / (maxX maxY) independent of CRS axis order.
		final double[] low = parseCoordinates(lower.item(0).getTextContent());
		final double[] up = parseCoordinates(upper.item(0).getTextContent());
		assertEquals(10.0, low[0], 1e-6);
		assertEquals(20.0, low[1], 1e-6);
		assertEquals(110.0, up[0], 1e-6);
		assertEquals(220.0, up[1], 1e-6);
	}

	/**
	 * When no CRS can be determined for the geometries, the {@code srsName} is
	 * omitted (the output cannot satisfy rule 2.1.3.1) and a warning must be
	 * reported so the situation is not silent.
	 *
	 * @throws Exception if any error occurs
	 */
	@Test
	public void testBoundedByWithoutCrsWarns() throws Exception {
		final XmlIndex schema = loadSchema();
		final TypeDefinition featureType = getFeatureType(schema);

		final InstanceCollection instances = new DefaultInstanceCollection(
				Arrays.asList(createFeature(featureType, null, 10.0, 20.0)));

		final File output = File.createTempFile("xplanAuszug", ".gml"); //$NON-NLS-1$ //$NON-NLS-2$
		output.deleteOnExit();
		final IOReport report = write(schema, instances, output);
		assertTrue("Writing the XPlanGML output was not successful", report.isSuccess()); //$NON-NLS-1$

		final Element envelope = getSingleEnvelope(output);
		assertFalse("srsName must be omitted when no CRS is known", //$NON-NLS-1$
				envelope.hasAttribute("srsName")); //$NON-NLS-1$

		boolean warned = false;
		for (Message message : report.getWarnings()) {
			if (message.getMessage() != null && message.getMessage().contains("2.1.3.1")) { //$NON-NLS-1$
				warned = true;
				break;
			}
		}
		assertTrue("Expected a warning about the missing default CRS", warned); //$NON-NLS-1$
	}

	/**
	 * Uses the geometries of a real-world XPlanAuszug export (as produced by
	 * XPlanManager) as input, instead of the synthetic points used by
	 * {@link #testBoundedByWithDefaultCrs()}, to verify the {@code gml:boundedBy}
	 * extent and {@code srsName} are correctly derived from actual XPlanGML
	 * geometries and their GML-3.2-style {@code srsName} (a full URL, not a simple
	 * {@code EPSG:<code>} code).
	 *
	 * @throws Exception if any error occurs
	 */
	@Test
	public void testBoundedByWithRealExport() throws Exception {
		final XmlIndex schema = loadSchema();
		final TypeDefinition featureType = getFeatureType(schema);

		final List<RealGeometry> realGeometries = loadRealExportGeometries();
		assertFalse("Expected at least one geometry in the real XPlanAuszug export", //$NON-NLS-1$
				realGeometries.isEmpty());

		final Envelope expectedEnvelope = new Envelope();
		String expectedSrsName = null;
		final List<MutableInstance> features = new ArrayList<>();
		for (RealGeometry realGeometry : realGeometries) {
			expectedEnvelope.expandToInclude(realGeometry.geometry.getEnvelopeInternal());
			if (expectedSrsName == null) {
				expectedSrsName = realGeometry.srsName;
			}
			features.add(createFeature(featureType, new CodeDefinition(realGeometry.srsName),
					realGeometry.geometry));
		}

		final InstanceCollection instances = new DefaultInstanceCollection(features);

		final File output = File.createTempFile("xplanAuszugReal", ".gml"); //$NON-NLS-1$ //$NON-NLS-2$
		output.deleteOnExit();
		final IOReport report = write(schema, instances, output);
		assertTrue("Writing the XPlanGML output was not successful", report.isSuccess()); //$NON-NLS-1$

		final Element envelope = getSingleEnvelope(output);
		assertEquals("Envelope should carry the CRS found in the real export as srsName", //$NON-NLS-1$
				expectedSrsName, envelope.getAttribute("srsName")); //$NON-NLS-1$

		final NodeList lower = envelope.getElementsByTagNameNS(GML_NS, "lowerCorner"); //$NON-NLS-1$
		final NodeList upper = envelope.getElementsByTagNameNS(GML_NS, "upperCorner"); //$NON-NLS-1$
		final double[] low = parseCoordinates(lower.item(0).getTextContent());
		final double[] up = parseCoordinates(upper.item(0).getTextContent());
		assertEquals(expectedEnvelope.getMinX(), low[0], 1e-6);
		assertEquals(expectedEnvelope.getMinY(), low[1], 1e-6);
		assertEquals(expectedEnvelope.getMaxX(), up[0], 1e-6);
		assertEquals(expectedEnvelope.getMaxY(), up[1], 1e-6);
	}

	/**
	 * Parses the {@code gml:Polygon} geometries and their {@code srsName} out of
	 * the real-world {@code Export_XPlanGMLAuszug.gml} test resource.
	 *
	 * @return the geometries found, in document order
	 * @throws Exception if reading or parsing the resource fails
	 */
	private List<RealGeometry> loadRealExportGeometries() throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		final DocumentBuilder builder = factory.newDocumentBuilder();

		try (InputStream in = getClass()
				.getResourceAsStream("/data/xplan/Export_XPlanGMLAuszug.gml")) { //$NON-NLS-1$
			assertNotNull("Test resource Export_XPlanGMLAuszug.gml not found", in); //$NON-NLS-1$
			final Document doc = builder.parse(in);

			final List<RealGeometry> result = new ArrayList<>();
			final NodeList polygons = doc.getElementsByTagNameNS(GML_NS, "Polygon"); //$NON-NLS-1$
			for (int i = 0; i < polygons.getLength(); i++) {
				final Element polygon = (Element) polygons.item(i);
				final String srsName = polygon.getAttribute("srsName"); //$NON-NLS-1$

				final NodeList posLists = polygon.getElementsByTagNameNS(GML_NS, "posList"); //$NON-NLS-1$
				assertEquals("Expected exactly one gml:posList per polygon", 1, //$NON-NLS-1$
						posLists.getLength());

				result.add(
						new RealGeometry(parsePolygon(posLists.item(0).getTextContent()), srsName));
			}
			return result;
		}
	}

	private static Geometry parsePolygon(String posList) {
		final String[] tokens = posList.trim().split("\\s+"); //$NON-NLS-1$
		assertEquals("Expected an even number of ordinates in gml:posList", 0, //$NON-NLS-1$
				tokens.length % 2);

		final Coordinate[] coordinates = new Coordinate[tokens.length / 2];
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = new Coordinate(Double.parseDouble(tokens[i * 2]),
					Double.parseDouble(tokens[i * 2 + 1]));
		}
		return geomFactory.createPolygon(coordinates);
	}

	/**
	 * A geometry together with the {@code srsName} it was found with.
	 */
	private static final class RealGeometry {

		private final Geometry geometry;
		private final String srsName;

		RealGeometry(Geometry geometry, String srsName) {
			this.geometry = geometry;
			this.srsName = srsName;
		}
	}

	private XmlIndex loadSchema() throws Exception {
		final XmlSchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(
				getClass().getResource("/data/xplan/xplan-auszug.xsd").toURI())); //$NON-NLS-1$
		final IOReport report = reader.execute(null);
		assertTrue("Loading the test schema failed", report.isSuccess()); //$NON-NLS-1$
		return reader.getSchema();
	}

	private TypeDefinition getFeatureType(XmlIndex schema) {
		for (XmlElement element : schema.getElements().values()) {
			if (element.getName().getLocalPart().equals("TestFeature")) { //$NON-NLS-1$
				return element.getType();
			}
		}
		throw new IllegalStateException("TestFeature element not found in schema"); //$NON-NLS-1$
	}

	private MutableInstance createFeature(TypeDefinition featureType, CRSDefinition crs, double x,
			double y) {
		final Point point = geomFactory.createPoint(new Coordinate(x, y));
		return createFeature(featureType, crs, point);
	}

	private MutableInstance createFeature(TypeDefinition featureType, CRSDefinition crs,
			Geometry geometry) {
		final MutableInstance feature = new DefaultInstance(featureType, null);
		feature.addProperty(new QName(XPLAN_NS, "geometry"), //$NON-NLS-1$
				new DefaultGeometryProperty<>(crs, geometry));
		return feature;
	}

	private IOReport write(Schema schema, InstanceCollection instances, File output)
			throws Exception {
		final XPlanGmlInstanceWriter writer = new XPlanGmlInstanceWriter();
		writer.setInstances(instances);
		final DefaultSchemaSpace schemaSpace = new DefaultSchemaSpace();
		schemaSpace.addSchema(schema);
		writer.setTargetSchema(schemaSpace);
		writer.setTarget(new FileIOSupplier(output));

		return writer.execute(null);
	}

	private Element getSingleEnvelope(File output) throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document doc = builder.parse(output);

		final NodeList boundedBy = doc.getElementsByTagNameNS(GML_NS, "boundedBy"); //$NON-NLS-1$
		assertEquals("Exactly one gml:boundedBy expected", 1, boundedBy.getLength()); //$NON-NLS-1$

		final NodeList envelopes = doc.getElementsByTagNameNS(GML_NS, "Envelope"); //$NON-NLS-1$
		assertEquals("Exactly one gml:Envelope expected", 1, envelopes.getLength()); //$NON-NLS-1$

		final Element envelope = (Element) envelopes.item(0);
		assertNotNull(envelope);
		return envelope;
	}

	private static double[] parseCoordinates(String text) {
		final String[] parts = text.trim().split("\\s+"); //$NON-NLS-1$
		assertEquals("Expected two coordinate values", 2, parts.length); //$NON-NLS-1$
		return new double[] { Double.parseDouble(parts[0]), Double.parseDouble(parts[1]) };
	}
}
