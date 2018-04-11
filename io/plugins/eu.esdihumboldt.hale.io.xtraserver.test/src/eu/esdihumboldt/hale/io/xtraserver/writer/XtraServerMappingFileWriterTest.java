/*
 * Copyright (c) 2017 interactive instruments GmbH
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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoAware;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;
import eu.esdihumboldt.hale.common.headless.test.AbstractProjectTest;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

@SuppressWarnings("javadoc")
public class XtraServerMappingFileWriterTest extends AbstractProjectTest {

	private static final ALogger log = ALoggerFactory
			.getLogger(XtraServerMappingFileWriterTest.class);

	private static final String PROJECT_LOCATION = "/data/simpledemo.halez";

	private SchemaSpace sourceSchemaSpace;
	private SchemaSpace targetSchemaSpace;

	private Project project;
	private Alignment alignment;
	private File tempDir;

	@Before
	public void loadTestProject() {
		try {
			URL archiveLocation = XtraServerMappingFileWriterTest.class
					.getResource(PROJECT_LOCATION);
			assertNotNull(archiveLocation);
			ProjectTransformationEnvironment projectTransformationEnvironment = this
					.getProject(archiveLocation);

			this.project = projectTransformationEnvironment.getProject();
			this.alignment = projectTransformationEnvironment.getAlignment();
			this.sourceSchemaSpace = projectTransformationEnvironment.getSourceSchema();
			this.targetSchemaSpace = projectTransformationEnvironment.getTargetSchema();
			this.tempDir = Files.createTempDir();

		} catch (Exception e) {
			log.error("Exception occurred", e);
			fail("Test project could not be loaded: " + e.getMessage());
		}
	}

	@After
	public void cleanUp() throws IOException {
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
				".xml", tempDir);

		try {
			writeAlignment(targetFile, XtraServerMappingFileWriter.CONTENT_TYPE_MAPPING);

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
				".zip", tempDir);

		try {
			writeAlignment(targetFile, XtraServerMappingFileWriter.CONTENT_TYPE_ARCHIVE);

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

	// adapted from DefaultIOAdvisor and subclasses
	private void prepareProvider(IOProvider provider, ProjectInfo projectInfo,
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

}
