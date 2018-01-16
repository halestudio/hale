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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentReader;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
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

@SuppressWarnings("javadoc")
public class XtraServerMappingFileWriterTest {

	private static final ALogger log = ALoggerFactory
			.getLogger(XtraServerMappingFileWriterTest.class);

	private static final String PROJECT_LOCATION = "/data/simpledemo.halez";
	private static final String MAPPING_SCHEMA = "/data/Cities.xsd";

	private static DefaultSchemaSpace sourceSchemaSpace;
	private static DefaultSchemaSpace targetSchemaSpace;

	private static Project project;
	private static Alignment alignment;
	private static File tempDir;

	@BeforeClass
	public static void loadTestProject() {

		try {
			URL archiveLocation = XtraServerMappingFileWriterTest.class
					.getResource(PROJECT_LOCATION);
			assertNotNull(archiveLocation);

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
				IOProviderDescriptor descriptor = IOProviderExtension.getInstance()
						.getFactory(providerId);
				if (descriptor == null) {
					throw new RuntimeException(
							"Could not load I/O provider with ID: " + resource.getProviderId());
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
		} finally {
			if (targetFile != null)
				targetFile.delete();
		}

	}

	private void writeAlignment(File targetFile, String contentType)
			throws IOException, IOProviderConfigurationException {
		AbstractAlignmentWriter alignWriter = new XtraServerMappingFileWriter();
		prepareProvider(alignWriter, project, tempDir.toURI());
		alignWriter.setAlignment(alignment);
		alignWriter.setSourceSchema(sourceSchemaSpace);
		alignWriter.setTargetSchema(targetSchemaSpace);
		alignWriter.setTarget(new FileIOSupplier(targetFile));
		alignWriter
				.setContentType(HalePlatform.getContentTypeManager().getContentType(contentType));

		IOReport report = alignWriter.execute(new LogProgressIndicator());
		assertNotNull(report);
		assertTrue(report.isSuccess());
	}

}
