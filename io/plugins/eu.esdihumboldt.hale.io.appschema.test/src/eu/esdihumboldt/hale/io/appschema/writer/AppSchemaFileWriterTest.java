/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.writer;

import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.getFirstElementByTagName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.io.ByteStreams;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentReader;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFileInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters.Parameter;

@SuppressWarnings("javadoc")
public class AppSchemaFileWriterTest {

	private static final ALogger log = ALoggerFactory.getLogger(AppSchemaFileWriterTest.class);

	private static final String PROJECT_LOCATION = "/data/landcover.halez";
	private static final String MAPPING_SCHEMA = "/data/AppSchemaDataAccess.xsd";

	private static DefaultSchemaSpace sourceSchemaSpace;
	private static DefaultSchemaSpace targetSchemaSpace;

	private static Project project;
	private static Alignment alignment;
	private static File tempDir;

	@BeforeClass
	public static void loadTestProject() {

		try {
			URL archiveLocation = AppSchemaFileWriterTest.class.getResource(PROJECT_LOCATION);

			ArchiveProjectReader projectReader = new ArchiveProjectReader();
			projectReader.setSource(new DefaultInputSupplier(archiveLocation.toURI()));
			IOReport report = projectReader.execute(new LogProgressIndicator());
			if (!report.isSuccess()) {
				throw new RuntimeException("project reader execution failed");
			}
			tempDir = projectReader.getTemporaryFiles().iterator().next();

			project = projectReader.getProject();
			assertNotNull(project);

			sourceSchemaSpace = new DefaultSchemaSpace();
			targetSchemaSpace = new DefaultSchemaSpace();

			// load schemas
			List<IOConfiguration> resources = project.getResources();
			for (IOConfiguration resource : resources) {
				String actionId = resource.getActionId();
				String providerId = resource.getProviderId();

				// get provider
				IOProvider provider = null;
				IOProviderDescriptor descriptor = IOProviderExtension.getInstance().getFactory(
						providerId);
				if (descriptor == null) {
					throw new RuntimeException("Could not load I/O provider with ID: "
							+ resource.getProviderId());
				}

				provider = descriptor.createExtensionObject();
				provider.loadConfiguration(resource.getProviderConfiguration());
				prepareProvider(provider, project, tempDir.toURI());

				IOReport providerReport = provider.execute(new LogProgressIndicator());
				if (!providerReport.isSuccess()) {
					throw new RuntimeException("I/O provider execution failed");
				}

				// handle results
				// TODO: could (should?) be done by an advisor
				if (provider instanceof SchemaReader) {
					Schema schema = ((SchemaReader) provider).getSchema();
					if (actionId.equals(SchemaIO.ACTION_LOAD_SOURCE_SCHEMA)) {
						sourceSchemaSpace.addSchema(schema);
					}
					else if (actionId.equals(SchemaIO.ACTION_LOAD_TARGET_SCHEMA)) {
						targetSchemaSpace.addSchema(schema);
					}
				}
			}

			// load alignment
			List<ProjectFileInfo> projectFiles = project.getProjectFiles();
			for (ProjectFileInfo projectFile : projectFiles) {
				if (projectFile.getName().equals(AlignmentIO.PROJECT_FILE_ALIGNMENT)) {
					AlignmentReader alignReader = new JaxbAlignmentReader();
					alignReader.setSource(new DefaultInputSupplier(projectFile.getLocation()));
					alignReader.setSourceSchema(sourceSchemaSpace);
					alignReader.setTargetSchema(targetSchemaSpace);
					alignReader.setPathUpdater(new PathUpdate(null, null));
					IOReport alignReport = alignReader.execute(new LogProgressIndicator());
					if (!alignReport.isSuccess()) {
						throw new RuntimeException("alignment reader execution failed");
					}

					alignment = alignReader.getAlignment();
					assertNotNull(alignment);

					break;
				}
			}
		} catch (Exception e) {
			log.error("Exception occurred", e);
			fail("Test project could not be loaded: " + e.getMessage());
		}

	}

	// adapted from DefaultIOAdvisor and subclasses
	private static void prepareProvider(IOProvider provider, ProjectInfo projectInfo,
			URI projectLocation) {
		if (provider instanceof ProjectInfoAware) {
			ProjectInfoAware pia = (ProjectInfoAware) provider;
			pia.setProjectInfo(projectInfo);
			pia.setProjectLocation(projectLocation);
		}
		if (provider instanceof InstanceReader) {
			InstanceReader ir = (InstanceReader) provider;
			ir.setSourceSchema(sourceSchemaSpace);
		}
	}

	@AfterClass
	public static void cleanUp() throws IOException {
		if (tempDir != null && tempDir.exists()) {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void testProject() {
		assertNotNull(project);
		assertNotNull(alignment);
		assertEquals(2, alignment.getTypeCells().size());
	}

	@Test
	public void testWriteMappingFile() throws Exception {

		final File targetFile = File.createTempFile(Long.toString(System.currentTimeMillis()),
				".xml");

		try {
			writeAlignment(targetFile, AppSchemaIO.CONTENT_TYPE_MAPPING);

			assertTrue(targetFile.exists());
			assertTrue(targetFile.length() > 0);
			assertTrue(isMappingValid(targetFile));
		} finally {
			if (targetFile != null)
				targetFile.delete();
		}

	}

	@Test
	public void testWriteArchive() throws Exception {

		final File targetFile = File.createTempFile(Long.toString(System.currentTimeMillis()),
				".zip");

		try {
			writeAlignment(targetFile, AppSchemaIO.CONTENT_TYPE_ARCHIVE);

			assertTrue(targetFile.exists());
			assertTrue(targetFile.length() > 0);

			checkArchive(targetFile);
		} finally {
			if (targetFile != null)
				targetFile.delete();
		}

	}

	private void writeAlignment(File targetFile, String contentType) throws IOException,
			IOProviderConfigurationException {
		AbstractAppSchemaConfigurator alignWriter = new AppSchemaMappingFileWriter();
		prepareProvider(alignWriter, project, tempDir.toURI());
		alignWriter.setAlignment(alignment);
		alignWriter.setSourceSchema(sourceSchemaSpace);
		alignWriter.setTargetSchema(targetSchemaSpace);
		alignWriter.setTarget(new FileIOSupplier(targetFile));
		DataStore dataStoreParam = createDataStoreParam();
		alignWriter.setParameter(AppSchemaIO.PARAM_DATASTORE, new ComplexValue(dataStoreParam));
		alignWriter
				.setContentType(HalePlatform.getContentTypeManager().getContentType(contentType));

		IOReport report = alignWriter.execute(new LogProgressIndicator());
		assertNotNull(report);
		assertTrue(report.isSuccess());
	}

	private DataStore createDataStoreParam() {
		DataStore dataStoreParam = new DataStore();
		dataStoreParam.setParameters(new Parameters());

		Parameter hostParam = new Parameter();
		hostParam.setName("host");
		hostParam.setValue("localhost");
		Parameter dbParam = new Parameter();
		dbParam.setName("database");
		dbParam.setValue("postgres");
		Parameter userParam = new Parameter();
		userParam.setName("user");
		userParam.setValue("postgres");
		Parameter passwdParam = new Parameter();
		passwdParam.setName("passwd");
		passwdParam.setValue("postgres");
		Parameter dbtypeParam = new Parameter();
		dbtypeParam.setName("dbtype");
		dbtypeParam.setValue("postgis");

		dataStoreParam.getParameters().getParameter()
				.addAll(Arrays.asList(hostParam, dbParam, dbtypeParam, userParam, passwdParam));

		return dataStoreParam;
	}

	private boolean isMappingValid(File mappingFile) throws IOException {
		URL mappingSchema = getClass().getResource(MAPPING_SCHEMA);
		Source xmlFile = new StreamSource(mappingFile);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		javax.xml.validation.Schema schema = null;
		try {
			schema = schemaFactory.newSchema(mappingSchema);
		} catch (SAXException e) {
			fail("Exception parsing mapping schema: " + e.getMessage());
		}

		@SuppressWarnings("null")
		Validator validator = schema.newValidator();
		try {
			validator.validate(xmlFile);
			return true;
		} catch (SAXException e) {
			log.error("Mapping file validation failed", e);
			return false;
		}
	}

	private void checkArchive(File archive) throws IOException {
		final File tempFile = File
				.createTempFile(Long.toString(System.currentTimeMillis()), ".xml");
		ZipInputStream zis = null;
		Document doc = null;
		try {
			zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(archive)));

			ZipEntry lcvWsDir = zis.getNextEntry();
			checkDirEntry(lcvWsDir, "lcv/");
			zis.closeEntry();

			ZipEntry lcvWsFile = zis.getNextEntry();
			checkFileEntry(lcvWsFile, "lcv/" + AppSchemaIO.WORKSPACE_FILE);
			doc = readDocument(zis);
			checkWorkspaceDocument(doc, "lcv_workspace", "lcv");
			zis.closeEntry();

			ZipEntry lcvNsFile = zis.getNextEntry();
			checkFileEntry(lcvNsFile, "lcv/" + AppSchemaIO.NAMESPACE_FILE);
			doc = readDocument(zis);
			checkNamespaceDocument(doc, "lcv_namespace", "lcv",
					"http://inspire.ec.europa.eu/schemas/lcv/3.0");
			zis.closeEntry();

			ZipEntry lcvDataStoreDir = zis.getNextEntry();
			checkDirEntry(lcvDataStoreDir, "lcv/LandCoverVector/");
			zis.closeEntry();

			ZipEntry lcvDataStoreFile = zis.getNextEntry();
			checkFileEntry(lcvDataStoreFile, "lcv/LandCoverVector/" + AppSchemaIO.DATASTORE_FILE);
			doc = readDocument(zis);
			assertNotNull(doc);
			assertEquals("LandCoverVector_datastore",
					getFirstElementByTagName(doc.getDocumentElement(), "id").getTextContent());
			assertEquals("LandCoverVector",
					getFirstElementByTagName(doc.getDocumentElement(), "name").getTextContent());
			Element wsEl = getFirstElementByTagName(doc.getDocumentElement(), "workspace");
			assertNotNull(wsEl);
			assertEquals("lcv_workspace", getFirstElementByTagName(wsEl, "id").getTextContent());
			NodeList paramEntries = doc.getDocumentElement().getElementsByTagName("entry");
			assertNotNull(paramEntries);
			assertEquals(3, paramEntries.getLength());
			for (int i = 0; i < paramEntries.getLength(); i++) {
				Node param = paramEntries.item(i);
				Node key = param.getAttributes().getNamedItem("key");
				if ("dbtype".equals(key.getTextContent())) {
					assertEquals("app-schema", param.getTextContent());
				}
				else if ("namespace".equals(key.getTextContent())) {
					assertEquals("http://inspire.ec.europa.eu/schemas/lcv/3.0",
							param.getTextContent());
				}
				else if ("url".equals(key.getTextContent())) {
					assertEquals("file:./workspaces/lcv/LandCoverVector/LandCoverVector.xml",
							param.getTextContent());
				}
				else {
					fail("Unknown connection parameter found: " + key.getTextContent());
				}
			}
			zis.closeEntry();

			ZipEntry mappingFile = zis.getNextEntry();
			checkFileEntry(mappingFile, "lcv/LandCoverVector/LandCoverVector.xml");
			ByteStreams.copy(zis, new FileOutputStream(tempFile));
			assertTrue(isMappingValid(tempFile));
			zis.closeEntry();

			ZipEntry unitFtDir = zis.getNextEntry();
			checkDirEntry(unitFtDir, "lcv/LandCoverVector/LandCoverUnit/");
			zis.closeEntry();

			ZipEntry unitFtFile = zis.getNextEntry();
			checkFileEntry(unitFtFile, "lcv/LandCoverVector/LandCoverUnit/"
					+ AppSchemaIO.FEATURETYPE_FILE);
			doc = readDocument(zis);
			checkFeatureTypeDocument(doc, "LandCoverUnit");
			zis.closeEntry();

			ZipEntry unitLayerFile = zis.getNextEntry();
			checkFileEntry(unitLayerFile, "lcv/LandCoverVector/LandCoverUnit/"
					+ AppSchemaIO.LAYER_FILE);
			doc = readDocument(zis);
			checkLayerDocument(doc, "LandCoverUnit");
			zis.closeEntry();

			ZipEntry datasetFtDir = zis.getNextEntry();
			checkDirEntry(datasetFtDir, "lcv/LandCoverVector/LandCoverDataset/");
			zis.closeEntry();

			ZipEntry datasetFtFile = zis.getNextEntry();
			checkFileEntry(datasetFtFile, "lcv/LandCoverVector/LandCoverDataset/"
					+ AppSchemaIO.FEATURETYPE_FILE);
			doc = readDocument(zis);
			checkFeatureTypeDocument(doc, "LandCoverDataset");
			zis.closeEntry();

			ZipEntry datasetLayerFile = zis.getNextEntry();
			checkFileEntry(datasetLayerFile, "lcv/LandCoverVector/LandCoverDataset/"
					+ AppSchemaIO.LAYER_FILE);
			doc = readDocument(zis);
			checkLayerDocument(doc, "LandCoverDataset");
			zis.closeEntry();

			ZipEntry baseWsDir = zis.getNextEntry();
			checkDirEntry(baseWsDir, "base/");
			zis.closeEntry();

			ZipEntry baseWsFile = zis.getNextEntry();
			checkFileEntry(baseWsFile, "base/" + AppSchemaIO.WORKSPACE_FILE);
			doc = readDocument(zis);
			checkWorkspaceDocument(doc, "base_workspace", "base");
			zis.closeEntry();

			ZipEntry baseNsFile = zis.getNextEntry();
			checkFileEntry(baseNsFile, "base/" + AppSchemaIO.NAMESPACE_FILE);
			doc = readDocument(zis);
			checkNamespaceDocument(doc, "base_namespace", "base",
					"http://inspire.ec.europa.eu/schemas/base/3.3");
			zis.closeEntry();

			ZipEntry gmlWsDir = zis.getNextEntry();
			checkDirEntry(gmlWsDir, "gml/");
			zis.closeEntry();

			ZipEntry gmlWsFile = zis.getNextEntry();
			checkFileEntry(gmlWsFile, "gml/" + AppSchemaIO.WORKSPACE_FILE);
			doc = readDocument(zis);
			checkWorkspaceDocument(doc, "gml_workspace", "gml");
			zis.closeEntry();

			ZipEntry gmlNsFile = zis.getNextEntry();
			checkFileEntry(gmlNsFile, "gml/" + AppSchemaIO.NAMESPACE_FILE);
			doc = readDocument(zis);
			checkNamespaceDocument(doc, "gml_namespace", "gml", "http://www.opengis.net/gml/3.2");
			zis.closeEntry();

			ZipEntry xlinkWsDir = zis.getNextEntry();
			checkDirEntry(xlinkWsDir, "xlink/");
			zis.closeEntry();

			ZipEntry xlinkWsFile = zis.getNextEntry();
			checkFileEntry(xlinkWsFile, "xlink/" + AppSchemaIO.WORKSPACE_FILE);
			doc = readDocument(zis);
			checkWorkspaceDocument(doc, "xlink_workspace", "xlink");
			zis.closeEntry();

			ZipEntry xlinkNsFile = zis.getNextEntry();
			checkFileEntry(xlinkNsFile, "xlink/" + AppSchemaIO.NAMESPACE_FILE);
			doc = readDocument(zis);
			checkNamespaceDocument(doc, "xlink_namespace", "xlink", "http://www.w3.org/1999/xlink");
			zis.closeEntry();

			ZipEntry xsiWsDir = zis.getNextEntry();
			checkDirEntry(xsiWsDir, "xsi/");
			zis.closeEntry();

			ZipEntry xsiWsFile = zis.getNextEntry();
			checkFileEntry(xsiWsFile, "xsi/" + AppSchemaIO.WORKSPACE_FILE);
			doc = readDocument(zis);
			checkWorkspaceDocument(doc, "xsi_workspace", "xsi");
			zis.closeEntry();

			ZipEntry xsiNsFile = zis.getNextEntry();
			checkFileEntry(xsiNsFile, "xsi/" + AppSchemaIO.NAMESPACE_FILE);
			doc = readDocument(zis);
			checkNamespaceDocument(doc, "xsi_namespace", "xsi", AppSchemaMappingUtils.XSI_URI);
			zis.closeEntry();

			assertNull(zis.getNextEntry());
		} catch (ZipException e) {
			fail("Exception reading generated ZIP archive: " + e.getMessage());
		} finally {
			if (tempFile != null)
				tempFile.delete();
			if (zis != null)
				zis.close();
		}
	}

	private void checkDirEntry(ZipEntry entry, final String dirName) {
		assertNotNull(entry);
		assertEquals(dirName, entry.getName());
		assertTrue(entry.isDirectory());
	}

	private void checkFileEntry(ZipEntry entry, String fileName) {
		assertNotNull(entry);
		assertEquals(fileName, entry.getName());
		assertFalse(entry.isDirectory());
	}

	private Document readDocument(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteStreams.copy(is, bos);

		DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dbFac.newDocumentBuilder();

			return docBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	private void checkWorkspaceDocument(Document doc, final String id, final String name) {
		assertNotNull(doc);
		assertEquals("workspace", doc.getDocumentElement().getNodeName());
		Element idEl = getFirstElementByTagName(doc.getDocumentElement(), "id");
		assertNotNull(idEl);
		assertEquals(id, idEl.getTextContent());
		Element nameEl = getFirstElementByTagName(doc.getDocumentElement(), "name");
		assertNotNull(nameEl);
		assertEquals(name, nameEl.getTextContent());
	}

	private void checkNamespaceDocument(Document doc, final String id, final String prefix,
			final String uri) {
		assertNotNull(doc);
		assertEquals("namespace", doc.getDocumentElement().getNodeName());
		Element idEl = getFirstElementByTagName(doc.getDocumentElement(), "id");
		assertNotNull(idEl);
		assertEquals(id, idEl.getTextContent());
		Element prefixEl = getFirstElementByTagName(doc.getDocumentElement(), "prefix");
		assertNotNull(prefixEl);
		assertEquals(prefix, prefixEl.getTextContent());
		Element uriEl = getFirstElementByTagName(doc.getDocumentElement(), "uri");
		assertNotNull(uriEl);
		assertEquals(uri, uriEl.getTextContent());
	}

	private void checkFeatureTypeDocument(Document doc, final String featureTypeName) {
		assertNotNull(doc);
		assertEquals(featureTypeName + "_featureType",
				getFirstElementByTagName(doc.getDocumentElement(), "id").getTextContent());
		assertEquals(featureTypeName, getFirstElementByTagName(doc.getDocumentElement(), "name")
				.getTextContent());
		assertEquals(featureTypeName,
				getFirstElementByTagName(doc.getDocumentElement(), "nativeName").getTextContent());
		assertEquals(featureTypeName, getFirstElementByTagName(doc.getDocumentElement(), "title")
				.getTextContent());
		assertEquals(featureTypeName,
				getFirstElementByTagName(doc.getDocumentElement(), "abstract").getTextContent());
		Element nsEl = getFirstElementByTagName(doc.getDocumentElement(), "namespace");
		assertNotNull(nsEl);
		assertEquals("lcv_namespace", getFirstElementByTagName(nsEl, "id").getTextContent());
		Element storeEl = getFirstElementByTagName(doc.getDocumentElement(), "store");
		assertNotNull(storeEl);
		assertEquals("LandCoverVector_datastore", getFirstElementByTagName(storeEl, "id")
				.getTextContent());
	}

	private void checkLayerDocument(Document doc, final String featureTypeName) {
		assertNotNull(doc);
		assertEquals(featureTypeName + "_layer",
				getFirstElementByTagName(doc.getDocumentElement(), "id").getTextContent());
		assertEquals(featureTypeName, getFirstElementByTagName(doc.getDocumentElement(), "name")
				.getTextContent());
		Element resourceEl = getFirstElementByTagName(doc.getDocumentElement(), "resource");
		assertNotNull(resourceEl);
		assertEquals(featureTypeName + "_featureType", getFirstElementByTagName(resourceEl, "id")
				.getTextContent());
	}
}
