/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.inspire.apps.templates;

import java.util.regex.Matcher

import org.eclipse.core.runtime.content.IContentType
import org.osgi.framework.Version

import com.google.common.collect.Multimap

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.ImportProvider
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration
import eu.esdihumboldt.hale.common.core.io.project.model.Project
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.inspire.schemas.ApplicationSchemas
import eu.esdihumboldt.hale.common.inspire.schemas.SchemaInfo
import eu.esdihumboldt.hale.common.schema.io.SchemaIO
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Generate INSPIRE mapping templates.
 * 
 * @author Simon Templer
 */
@CompileStatic
public class GenerateTemplates {

	private static final ALogger log = ALoggerFactory.getLogger(GenerateTemplates)

	/**
	 * Identifier of the XML schema reader.
	 */
	private static final String XML_SCHEMA_READER_ID = 'eu.esdihumboldt.hale.io.xsd.reader'

	private final GenerateTemplatesContext context;

	public GenerateTemplates(GenerateTemplatesContext context) {
		this.context = context;
	}

	/**
	 * Generate projects for all application schemas.
	 */
	void generate() {
		Multimap<String, SchemaInfo> schemas = ApplicationSchemas.getSchemaInfos();
		for (SchemaInfo schema : schemas.values()) {
			generate(schema);
		}
	}

	/**
	 * Generate a project for a specific schema.
	 * 
	 * @param schema the application schema
	 */
	void generate(SchemaInfo schema) {
		println "Generate project for application schema $schema.name"

		// extract short identifier
		def shortId = extractShortId(schema)
		String filename = "inspire-${shortId}.halex"
		File projectFile = new File(context.targetDir, filename)

		// create project
		final Project project = new Project();

		// basic medadata
		project.author = 'HALE (generated)'
		project.name = "Map to INSPIRE ${schema.name} ${schema.version}"
		project.description = "Template project for mapping to INSPIRE ${schema.name}."
		project.haleVersion = Version.parseVersion('2.8.0')
		project.created = new Date()
		project.modified = project.created

		// schema reader
		IOConfiguration schemaConf = createSchemaConfiguration(schema);
		project.getResources().add(schemaConf);

		// write project
		IContentType projectType = HaleIO.findContentType(ProjectWriter.class, null, filename);
		IOProviderDescriptor factory = HaleIO.findIOProviderFactory(ProjectWriter.class, projectType, null);
		ProjectWriter projectWriter;
		try {
			projectWriter = (ProjectWriter) factory.createExtensionObject();
		} catch (Exception e1) {
			log.userError("Failed to create project wrtier", e1);
			return;
		}
		projectWriter.setProject(project);
		projectWriter.setProjectFiles(new HashMap<String, ProjectFile>());
		projectWriter.setTarget(new FileIOSupplier(projectFile));

		// store (incomplete) save configuration
		IOConfiguration saveConf = new IOConfiguration();
		projectWriter.storeConfiguration(saveConf.getProviderConfiguration());
		saveConf.setProviderId(factory.getIdentifier());
		project.setSaveConfiguration(saveConf);

		IOReport report = null;
		try {
			report = projectWriter.execute(null);
			//XXX instead through ThreadProgressMonitor?
		} catch (Exception e) {
			log.userError("Error writing project file.", e);
		}
	}

	private IOConfiguration createSchemaConfiguration(SchemaInfo schema) {
		IOConfiguration result = new IOConfiguration()

		result.actionId = SchemaIO.ACTION_LOAD_SOURCE_SCHEMA
		result.providerId = XML_SCHEMA_READER_ID
		result.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
				Value.of(schema.location.toString()))

		result
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private String extractShortId(SchemaInfo schema) {
		def inspireShortIdRegex = '^http://inspire.ec.europa.eu/applicationschema/([^/]*)$'
		Matcher matcher = ( schema.appSchemaId =~ inspireShortIdRegex )
		matcher[0][1]
	}
}
