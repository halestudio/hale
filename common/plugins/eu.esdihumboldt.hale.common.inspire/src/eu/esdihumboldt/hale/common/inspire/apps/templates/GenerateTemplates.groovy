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

import javax.xml.XMLConstants

import org.eclipse.core.runtime.content.IContentType
import org.osgi.framework.Version

import com.google.common.collect.Multimap

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.align.groovy.accessor.EntityAccessor
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.codelist.config.CodeListAssociations
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.ImportProvider
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor
import eu.esdihumboldt.hale.common.core.io.project.ComplexConfigurationService
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration
import eu.esdihumboldt.hale.common.core.io.project.model.Project
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier
import eu.esdihumboldt.hale.common.inspire.codelists.CodeListRef
import eu.esdihumboldt.hale.common.inspire.codelists.RegistryCodeLists
import eu.esdihumboldt.hale.common.inspire.schemas.ApplicationSchemas
import eu.esdihumboldt.hale.common.inspire.schemas.SchemaInfo
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.io.SchemaIO
import eu.esdihumboldt.hale.common.schema.io.SchemaReader
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag
import eu.esdihumboldt.hale.io.codelist.inspire.reader.INSPIRECodeListReader
import eu.esdihumboldt.hale.io.gml.geometry.GMLConstants
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

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
		// read configuration
		def config = GenerateTemplatesConfig.readConfig()

		Multimap<String, SchemaInfo> schemas = ApplicationSchemas.getSchemaInfos();
		Map<String, SchemaInfo> schemasByShortId = [:]

		for (SchemaInfo schema : schemas.values()) {
			// generate single schema templates
			generate([schema]);

			// associate with short-id
			//XXX what about different versions of a schema?
			schemasByShortId.put(extractShortId(schema), schema);
		}

		// generate for combinations of schemas
		config.combinations.each { combination ->
			def combinationSchemas = combination.schemaIds.collect { id ->
				schemasByShortId.get(id)
			}
			generate(combinationSchemas)
		}
	}

	/**
	 * Generate a project for a specific schema.
	 * 
	 * @param schema the application schema
	 */
	void generate(List<SchemaInfo> schemas) {
		assert schemas != null && !schemas.empty, 'Schemas may not be empty'

		println "Generate project for application schema $schemas[0].name (and ${schemas.size() - 1} additionally included schemas)"

		// extract short identifier
		List<String> shortIds = schemas.collect { extractShortId(it) }
		def shortId = shortIds.join('_')
		String filename = schemas.size() != 1 ? "inspire-${shortId}.halez" : "inspire-${shortId}.halex"
		File projectFile = new File(context.targetDir, filename)

		// create project
		final Project project = new Project();

		String schemaName = schemas[0].name
		String schemaNameVersion = schemas[0].name + ' ' + schemas[0].version
		String otherSchemas = schemas.size() > 1 ? schemas[1..-1].collect{ it.name }.join(' and ') : null
		if (otherSchemas) {
			schemaName = schemaName + ' with ' + otherSchemas
			schemaNameVersion = schemaNameVersion + ' with ' + otherSchemas
		}

		// basic medadata
		project.author = 'HALE (generated)'
		project.name = "Map to INSPIRE ${schemaNameVersion}"
		project.description = "Template project for mapping to INSPIRE ${schemaName}."
		if (context.explicit) {
			project.haleVersion = Version.parseVersion('2.8.0')
		}
		else {
			//TODO instead determine version from bundle?
			project.haleVersion = Version.parseVersion('2.9.0')
		}
		project.created = new Date()
		project.modified = project.created

		// schema reader
		IOConfiguration schemaConf = createSchemaConfiguration(schemas)
		project.resources << schemaConf

		// load the schema
		Collection<Schema> loadedSchemas = (schemas.collect {
			DefaultInputSupplier schemaInput = new DefaultInputSupplier(it.location)
			SchemaReader reader = HaleIO.findIOProvider(SchemaReader, schemaInput, it.location.path)
			reader.source = schemaInput
			IOReport schemaReport = reader.execute(null)
			Schema loadedSchema
			if (schemaReport.isSuccess()) {
				loadedSchema = reader.schema
			}
			else {
				log.error("Failed to load schema $it.name", (Throwable)null)
			}
			loadedSchema
		}).findAll()

		// project configuration service
		ComplexConfigurationService config = ProjectIO.createProjectConfigService(project)

		// determine relevant types
		Set<TypeDefinition> relevantTypes = null
		if (!loadedSchemas.empty) {
			Set<String> relevantNamespaces = new HashSet<>()

			// which namespaces are relevant? TODO make configurable?
			if (schemas.size() == 1) {
				// currently only include direct imports for single schema projects
				schemas.each {
					relevantNamespaces.addAll(it.directImports as Set)
				}
			}

			Set<String> mainNamespaces = new HashSet<>(loadedSchemas.collect { it.namespace })

			// determine relevant types
			relevantTypes = new LinkedHashSet<>()
			Set<TypeDefinition> mainTypes = new LinkedHashSet<>()

			loadedSchemas.each { loadedSchema ->
				for (def t : loadedSchema.types) {
					TypeDefinition type = (TypeDefinition) t
					if (typeIsMappingRelevant(type)) {
						if (mainNamespaces.contains(type.name.namespaceURI)) {
							mainTypes << type
						}
						else if (relevantNamespaces.contains(type.name.namespaceURI)) {
							relevantTypes << type
						}
					}
				}
			}

			// make sure main types are at the end
			relevantTypes.addAll(mainTypes)
		}

		// set relevant types in configuration
		if (relevantTypes != null) {
			config.setList(SchemaIO.getMappingRelevantTypesParameterName(SchemaSpaceID.TARGET), relevantTypes.collect{ TypeDefinition type ->
				type.name.toString()
			})
		}

		// determine auxiliary schema infos
		Multimap<String, SchemaInfo> schemaMap = ApplicationSchemas.getSchemaInfos()
		List<SchemaInfo> auxSchemas = []
		schemas.each { schema ->
			for (String ns : schema.directImports) {
				auxSchemas.addAll(schemaMap.get(ns))
			}
		}

		// code list readers and associations
		CodeListAssociations associations = new CodeListAssociations()
		// collect code lists
		Set<CodeListRef> codeLists = new LinkedHashSet<>()
		for (SchemaInfo auxSchema : auxSchemas) {
			codeLists.addAll(RegistryCodeLists.getCodeLists(auxSchema.appSchemaId))
		}
		schemas.each { schema ->
			codeLists.addAll(RegistryCodeLists.getCodeLists(schema.appSchemaId))
		}
		println "Found ${codeLists.size()} code lists for schema $schemaName"
		for (CodeListRef cl in codeLists) {
			IOConfiguration clConf = createCodeListConfiguration(cl)
			project.resources << clConf
			if (relevantTypes != null) {
				addAssociations(associations, cl, relevantTypes)
			}
		}
		config.setProperty("codelists", Value.complex(associations))

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

	private boolean typeIsMappingRelevant(TypeDefinition type) {
		type.getConstraint(MappableFlag).enabled && 'SpatialDataSetType' != type.name.localPart &&
				!type.getConstraint(AbstractFlag).enabled && isFeatureType(type)
	}

	/**
	 * Determine if a given type is a feature type.
	 *
	 * @param type the type definition
	 * @return if the type represents a feature type
	 */
	private static boolean isFeatureType(TypeDefinition type) {
		if ("AbstractFeatureType".equals(type.getName().getLocalPart())
				&& type.getName().getNamespaceURI().startsWith(GMLConstants.GML_NAMESPACE_CORE)) {
			return true;
		}

		if (type.getSuperType() != null) {
			return isFeatureType(type.getSuperType());
		}

		return false;
	}

	private void addAssociations(CodeListAssociations associations, CodeListRef cl,
			Collection<TypeDefinition> types) {
		// set namespace and identifier to match INSPIRECodeListReader
		String namespace = cl.id
		String identifier
		if (context.explicit) {
			identifier = cl.name
		}
		else {
			identifier = cl.id
			int idxSlash = identifier.indexOf('/');
			if (idxSlash >= 0 && idxSlash + 1 < identifier.length()) {
				identifier = identifier.substring(idxSlash);
			}
		}

		/*
		 * How to automagically determine to which properties the code list should be associated?
		 */

		// search by name convention
		associateByName(cl.id, types, associations, namespace, identifier)
		if (cl.parentId != null) {
			associateByName(cl.parentId, types, associations, namespace, identifier)
		}
	}

	private void associateByName(String codeListId, Collection<TypeDefinition> types, CodeListAssociations associations,
			String codeListNamespace, String codeListIdentifier) {
		def regex = '/([^/]+)Value$'
		def matcher = ( codeListId =~ regex )
		if (matcher.find()) {
			def name = matcher.group(1)
			name = name[0].toLowerCase() + name[1..-1]

			// search for property with given name
			for (TypeDefinition type : types) {
				associateProperties(type, name, associations, codeListNamespace, codeListIdentifier)
			}
		}
	}

	private void associateProperties(TypeDefinition type, String propertyName,
			CodeListAssociations associations, String codeListNamespace, String codeListIdentifier) {
		EntityDefinition typeEntity = new TypeEntityDefinition(type, null, null)
		EntityDefinition property = new EntityAccessor(typeEntity).findChildren(propertyName).findChildren('href').toEntityDefinition()
		if (property != null) {
			associations.assignCodeList(property, codeListNamespace, codeListIdentifier)
		}
	}

	private IOConfiguration createSchemaConfiguration(List<SchemaInfo> schemas) {
		IOConfiguration result = new IOConfiguration()
		result.actionId = SchemaIO.ACTION_LOAD_TARGET_SCHEMA
		result.providerId = XML_SCHEMA_READER_ID

		if (schemas.size() == 1) {
			result.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
					Value.of(schemas[0].location.toString()))
		}
		else {
			// create combined schema

			List<String> shortIds = schemas.collect { extractShortId(it) }
			def shortId = shortIds.join('_')
			String filename = "inspire-${shortId}.xsd"
			File schemaFile = new File(context.targetDir, filename)

			createCombinedSchema(schemaFile, "http://esdi-humboldt.eu/hale/inspire-combined-$shortId", schemas)

			result.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
					Value.of(schemaFile.toURI()))
		}

		result
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void createCombinedSchema(File file, targetNamespace, List<SchemaInfo> schemas) {
		def xmlBuilder = new StreamingMarkupBuilder()
		def xml = xmlBuilder.bind {
			mkp.declareNamespace( xsd: XMLConstants.W3C_XML_SCHEMA_NS_URI )
			schemas.each { schema ->
				mkp.declareNamespace( (extractShortId(schema)): schema.namespace )
			}
			'xsd:schema'(elementFormDefault: 'qualified', targetNamespace: targetNamespace) {
				schemas.each { schema ->
					'xsd:import'(namespace: schema.namespace, schemaLocation: schema.location)
				}
			}
		}

		file.withOutputStream {
			XmlUtil.serialize(xml, it)
		}
	}

	private IOConfiguration createCodeListConfiguration(CodeListRef cl) {
		IOConfiguration result = new IOConfiguration()

		String loc = cl.location.toString()
		if (context.explicit) {
			// append format and language for old versions (<= 2.8)
			int index = loc.lastIndexOf('/')
			String name = loc.substring(index + 1)
			loc += "/${name}.en.xml"
		}

		result.actionId = CodeListReader.ACTION_ID
		result.providerId = INSPIRECodeListReader.PROVIDER_ID
		result.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
				Value.of(loc))

		result
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private String extractShortId(SchemaInfo schema) {
		def inspireShortIdRegex = '^http://inspire.ec.europa.eu/applicationschema/([^/]*)$'
		Matcher matcher = ( schema.appSchemaId =~ inspireShortIdRegex )
		matcher[0][1]
	}
}
