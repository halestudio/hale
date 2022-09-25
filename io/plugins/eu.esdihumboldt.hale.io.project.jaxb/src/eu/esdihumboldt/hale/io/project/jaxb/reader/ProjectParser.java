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

package eu.esdihumboldt.hale.io.project.jaxb.reader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.content.IContentType;
import org.osgi.framework.Version;

import eu.esdihumboldt.hale.common.align.io.AlignmentIO;
import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.style.io.StyleReader;
import eu.esdihumboldt.hale.io.project.jaxb.generated.ConfigData;
import eu.esdihumboldt.hale.io.project.jaxb.generated.ConfigSection;
import eu.esdihumboldt.hale.io.project.jaxb.generated.HaleProject;
import eu.esdihumboldt.hale.io.project.jaxb.generated.ObjectFactory;
import eu.esdihumboldt.hale.io.project.jaxb.internal.Messages;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;

/**
 * The project parser reads a given project XML file and populates a
 * {@link Project} instance accordingly.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class ProjectParser extends AbstractImportProvider implements ProjectReader {

//	private static ALogger log = ALoggerFactory.getLogger(ProjectParser.class);

	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String PROJECT_CONTEXT = "eu.esdihumboldt.hale.io.project.jaxb.generated"; //$NON-NLS-1$

	private Map<String, ProjectFile> projectFiles;

	private Project project;

	/**
	 * The current reporter
	 */
	private IOReporter report;

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see ProjectReader#setProjectFiles(Map)
	 */
	@Override
	public void setProjectFiles(Map<String, ProjectFile> projectFiles) {
		this.projectFiles = projectFiles;
	}

	/**
	 * @see ProjectReader#getProjectFiles()
	 */
	@Override
	public Map<String, ProjectFile> getProjectFiles() {
		return projectFiles;
	}

	/**
	 * @see ProjectReader#getProject()
	 */
	@Override
	public Project getProject() {
		return project;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "HALE project (up to 2.1.x)";
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin(Messages.ProjectParser_0, ProgressIndicator.UNKNOWN);
		try {
			File file;
			try {
				file = new File(getSource().getLocation());
			} catch (IllegalArgumentException e) {
				file = null;
			}
			String basePath = (file == null) ? (new File(".").getAbsolutePath())
					: (FilenameUtils.getFullPath(file.getAbsolutePath()));

			// Unmarshal the project file
			JAXBContext jc;
			JAXBElement<HaleProject> root;
			try {
				jc = JAXBContext.newInstance(PROJECT_CONTEXT, ObjectFactory.class.getClassLoader());
				Unmarshaller u = jc.createUnmarshaller();
				u.setEventHandler(new DefaultValidationEventHandler());
				root = u.unmarshal(new StreamSource(getSource().getInput()), HaleProject.class);
			} catch (JAXBException e) {
				reporter.error(new IOMessageImpl(
						"Unmarshalling the HaleProject from the given resource failed: {0}", e, -1,
						-1, getSource().getLocation()));
				reporter.setSuccess(false);
				return reporter;
			}

			project = new Project();
			projectFiles = new HashMap<String, ProjectFile>();
			report = reporter;

			HaleProject haleProject = root.getValue();

			// populate project and project files
			loadProject(haleProject);
			loadSchemas(haleProject, basePath);
			loadAlignment(haleProject, basePath);
			loadStyle(haleProject, basePath);
			loadInstances(haleProject, basePath);
			loadTasks(haleProject, basePath);
			loadConfig(haleProject);

			report = null;

			reporter.setSuccess(true);
			return reporter;
		} finally {
			progress.end();
		}
	}

	private void loadProject(HaleProject haleProject) {
		// project name
		String name = haleProject.getName();
		project.setName(name);

		// author
		project.setAuthor("Unknown");

		// description
		project.setDescription("Imported from a version of HALE before 2.5");

		// version
		String version = haleProject.getHaleVersion();
		try {
			project.setHaleVersion(Version.parseVersion(version));
		} catch (IllegalArgumentException e) {
			// ignore
		}

		// dates TODO how to parse?
		// String created = haleProject.getDateCreated();
		// project.setCreated(created);
		// String modified = haleProject.getDateModified();
		// project.setModified(modified);
	}

	private void loadConfig(HaleProject haleProject) {
		List<ConfigSection> config = haleProject.getConfigSchema();

		// translate ConfigSections to project properties
		for (ConfigSection section : config) {
			String prefix = section.getName();

			for (ConfigData data : section.getData()) {
				// TODO check if this is compatible with
				// NamespaceConfigurationItem
				project.getProperties().put(prefix + "/" + data.getKey(),
						Value.of(data.getValue()));
			}
		}
	}

	private void loadSchemas(HaleProject haleProject, String basePath) {

		String msgCT = "Could not load schema at {0}, the content type could not be identified.";
		String msgIO = "Could not load schema at {0}, no matching I/O provider could be found.";

		if (haleProject.getSourceSchema() != null
				&& haleProject.getSourceSchema().getPath() != null) {
			URI source = getLocation(haleProject.getSourceSchema().getPath(), basePath);

			// configure for source schema
//			IOConfiguration conf = new IOConfiguration();
//			conf.setActionId(SchemaIO.ACTION_LOAD_SOURCE_SCHEMA);
//			loadSchema(conf, source);
			load(source, SchemaIO.ACTION_LOAD_SOURCE_SCHEMA, msgCT, msgIO, SchemaReader.class);
		}

		if (haleProject.getTargetSchema() != null
				&& haleProject.getTargetSchema().getPath() != null) {
			URI source = getLocation(haleProject.getTargetSchema().getPath(), basePath);

			// configure for target schema
//			IOConfiguration conf = new IOConfiguration();
//			conf.setActionId(SchemaIO.ACTION_LOAD_TARGET_SCHEMA);
//			loadSchema(conf, source);
			load(source, SchemaIO.ACTION_LOAD_TARGET_SCHEMA, msgCT, msgIO, SchemaReader.class);
		}
	}

	// XXX: can be removed ?
//	private void loadSchema(IOConfiguration conf, URI source) {
//		// populate IOConfiguration
//		// advisor ID must be already set
//
//		// find provider
//		File file;
//		try {
//			file = new File(source);
//		} catch (IllegalArgumentException e) {
//			file = null;
//		}
//		IContentType ct = HaleIO.findContentType(SchemaReader.class, new DefaultInputSupplier(
//				source), (file == null) ? (null) : (file.getAbsolutePath()));
//		if (ct == null) {
//			report.error(new IOMessageImpl(
//					"Could not load schema at {0}, the content type could not be identified.",
//					null, -1, -1, source));
//			return;
//		}
//		IOProviderDescriptor srf = HaleIO.findIOProviderFactory(SchemaReader.class, ct, null);
//		if (srf == null) {
//			report.error(new IOMessageImpl(
//					"Could not load schema at {0}, no matching I/O provider could be found.", null,
//					-1, -1, source));
//			return;
//		}
//		conf.setProviderId(srf.getIdentifier());
//
//		// provider configuration
//		// source
//		conf.getProviderConfiguration().put(AbstractImportProvider.PARAM_SOURCE, source.toString());
//		// content type
//		conf.getProviderConfiguration().put(AbstractImportProvider.PARAM_CONTENT_TYPE, ct.getId());
//
//		// no dependencies needed
//
//		// add configuration to project
//		project.getResources().add(conf);
//	}

	private void loadInstances(HaleProject haleProject, String basePath) {
		if (haleProject.getInstanceData() != null) {
			// URI source = new
			// URI(URLDecoder.decode(project.getInstanceData().getPath(),
			// "UTF-8"));
			URI source = getLocation(haleProject.getInstanceData().getPath(), basePath);

			String msgCT = "Could not load instance data at {0}, the content type could not be identified.";
			String msgIO = "Could not load instance data at {0}, no matching I/O provider could be found.";

			load(source, InstanceIO.ACTION_LOAD_SOURCE_DATA, msgCT, msgIO, InstanceReader.class);

			// not needed as the new XML/GML parser doesn't differentiate
			// between GML versions
			// String configurationType =
			// haleProject.getInstanceData().getType(); // GML2, GML3 or GML3_2

			// TODO default crs
			// if (haleProject.getInstanceData().getEpsgcode() != null) {
			// instanceService.setCRS(new
			// CodeDefinition(haleProject.getInstanceData().getEpsgcode(),
			// null));
			// }
			// else if (haleProject.getInstanceData() != null &&
			// haleProject.getInstanceData().getWkt() != null) {
			// instanceService.setCRS(new
			// WKTDefinition(haleProject.getInstanceData().getWkt(), null));
			// }

			// create IOConfiguration for source data
			// IOConfiguration conf = new IOConfiguration();

			// populate IOConfiguration
			// set action ID
			// conf.setActionId(InstanceIO.ACTION_LOAD_SOURCE_DATA);

			// find provider
			// File file;
			// try {
			// file = new File(source);
			// } catch (IllegalArgumentException e) {
			// file = null;
			// }
			// IContentType ct = HaleIO.findContentType(InstanceReader.class,
			// new DefaultInputSupplier(source), (file == null) ? (null)
			// : (file.getAbsolutePath()));
			// if (ct == null) {
			// report.error(new IOMessageImpl(
			// "Could not load instance data at {0}, the content type could not
			// be identified.",
			// null, source));
			// return;
			// }
			// IOProviderDescriptor irf = HaleIO.findIOProviderFactory(
			// InstanceReader.class, ct, null);
			// if (irf == null) {
			// report.error(new IOMessageImpl(
			// "Could not load instance data at {0}, no matching I/O provider
			// could be found.",
			// null, source));
			// return;
			// }
			// conf.setProviderId(irf.getIdentifier());

			// provider configuration
			// source
			// conf.getProviderConfiguration().put(
			// AbstractImportProvider.PARAM_SOURCE, source.toString());
			// content type
			// conf.getProviderConfiguration().put(
			// AbstractImportProvider.PARAM_CONTENT_TYPE, ct.getId());
			// TODO default crs?

			// add configuration to project
			// project.getResources().add(conf);
		}
	}

	private void loadAlignment(HaleProject haleProject, String basePath) {
		if (haleProject.getOmlPath() != null && !haleProject.getOmlPath().isEmpty()) {
			URI omlPath = getLocation(haleProject.getOmlPath(), basePath);
			// XXX how to handle project files?
			// TODO create OML Alignment ProjectFile

			String msgCT = "Could not load mapping data at {0}, the content type could not be identified.";
			String msgIO = "Could not load mapping data at {0}, no matching I/O provider could be found.";

			load(omlPath, AlignmentIO.ACTION_LOAD_ALIGNMENT, msgCT, msgIO, AlignmentReader.class);

			// try {
			// OmlRdfReader reader = new OmlRdfReader();
			//
			// Alignment alignment = reader.read(omlPath.toURL());
			//
			// // update alignment
			// // source schema location
			// ISchema orgSchema = alignment.getSchema1();
			// Schema newSchema = new
			// Schema(projectService.getSourceSchemaPath(), (Formalism)
			// orgSchema.getFormalism());
			// newSchema.setAbout(orgSchema.getAbout());
			// alignment.setSchema1(newSchema);
			//
			// // target schema location
			// orgSchema = alignment.getSchema2();
			// if (orgSchema != null) {
			// newSchema = new Schema(projectService.getTargetSchemaPath(),
			// (Formalism) orgSchema.getFormalism());
			// newSchema.setAbout(orgSchema.getAbout());
			// alignment.setSchema2(newSchema);
			// }
			//
			// alignmentService.addOrUpdateAlignment(alignment);
			// log.info("Number of loaded cells: " +
			// alignmentService.getAlignment().getMap().size()); //$NON-NLS-1$
			// } catch (Exception e) {
			// // continue
			// String message = "Mapping could not be loaded"; //$NON-NLS-1$
			// log.error(message, e);
			// errors.add(message + ": " + e.getMessage()); //$NON-NLS-1$
			// alignmentService.cleanModel();
			// }
		}
	}

	private void loadStyle(HaleProject haleProject, String basePath) {
		if (haleProject.getStyles() != null) {
			String path = haleProject.getStyles().getPath();
			URI stylesLoc = getLocation(path, basePath);

			String msgCT = "Could not load style data at {0}, the content type could not be identified.";
			String msgIO = "Could not load style data at {0}, no matching I/O provider could be found.";

			// TODO: change to static final string in the right style class
			String ACTION_LOAD_STYLE = "eu.esdihumboldt.hale.ui.style.load";

			// TODO create SLD Style ProjectFile

			load(stylesLoc, ACTION_LOAD_STYLE, msgCT, msgIO, StyleReader.class);

			// //styleService.clearStyles();
			// if (path != null) {
			// try {
			// styleService.addStyles(stylesLoc.toURL());
			// } catch (Exception e) {
			// // continue
			// String message = "Error loading SLD from " + path; //$NON-NLS-1$
			// _log.error(message, e);
			// errors.add(message);
			// styleService.clearStyles();
			// }
			// }
			// // background
			// final String color = project.getStyles().getBackground();
			// styleService.setBackground((color ==
			// null)?(null):(StringConverter.asRGB(color)));
		}
	}

	/**
	 * Load tasks from the given project.
	 * 
	 * @param haleProject the project
	 * @param basePath the base path
	 */
	private void loadTasks(HaleProject haleProject, String basePath) {
		// XXX tasks in project deactivated for now
		// SchemaService schemaService = (SchemaService)
		// PlatformUI.getWorkbench().getService(SchemaService.class);
		//
		// TaskService taskService = (TaskService)
		// PlatformUI.getWorkbench().getService(TaskService.class);
		//
		// monitor.subTask(Messages.ProjectParser_17); //$NON-NLS-1$
		// ATransaction taskTrans = _log.begin("Loading tasks"); //$NON-NLS-1$
		// try {
		// taskService.clearUserTasks();
		// TaskStatus status = project.getTaskStatus();
		// if (status != null) {
		// for (Task task : status.getTask()) {
		// try {
		// // get identifiers
		// List<Definition> definitions = new ArrayList<Definition>();
		// for (String identifier : task.getContextIdentifier()) {
		// Definition definition = schemaService.getDefinition(identifier);
		// if (definition == null) {
		// throw new IllegalStateException("Unknown identifier " + //$NON-NLS-1$
		// identifier + ", failed to load task."); //$NON-NLS-1$
		// }
		// else {
		// definitions.add(definition);
		// }
		// }
		// eu.esdihumboldt.hale.ui.service.project.internal.generated.task.Task
		// newTask = new BaseTask(task.getTaskType(), definitions);
		// TaskUserData userData = new TaskUserDataImpl();
		// userData.setUserComment(task.getComment());
		// userData.setTaskStatus(eu.esdihumboldt.hale.task.TaskUserData.TaskStatus.valueOf(task.getTaskStatus()));
		//
		// taskService.setUserData(newTask, userData);
		// } catch (IllegalStateException e) {
		// _log.error(e.getMessage());
		// }
		// }
		// }
		// }
		// finally {
		// taskTrans.end();
		// }
	}

	/**
	 * Get the location
	 * 
	 * @param file the file path/URI
	 * @param basePath the possible base path
	 * 
	 * @return the file location
	 */
	private URI getLocation(String file, String basePath) {
		try {
			URI fileUri = new URI(file);
			String scheme = fileUri.getScheme();

			if (scheme == null) {
				// no scheme specified
				return getFileLocation(file, basePath);
			}
			else {
				return fileUri;
			}
		} catch (Exception e) {
			// assume file path w/o scheme
			return getFileLocation(file, basePath);
		}
	}

	/**
	 * Get the file location
	 * 
	 * @param file the file path (absolute or relative)
	 * @param basePath the possible base path
	 * 
	 * @return the file location
	 */
	private URI getFileLocation(String file, String basePath) {
		File f = new File(file);
		if (f.exists()) {
			return f.toURI();
		}
		else {
			File bf = new File(FilenameUtils.concat(basePath, file));
			return bf.toURI();
		}
	}

	private void load(URI source, String actionId, String msgCT, String msgIO,
			@SuppressWarnings("rawtypes") Class clazz) {
		// create IOConfiguration for source data
		IOConfiguration conf = new IOConfiguration();
		// populate IOConfiguration
		// set action ID
		conf.setActionId(actionId);

		// find provider
		File file;
		try {
			file = new File(source);
		} catch (IllegalArgumentException e) {
			file = null;
		}
		@SuppressWarnings("unchecked")
		IContentType ct = HaleIO.findContentType(clazz, new DefaultInputSupplier(source),
				(file == null) ? (null) : (file.getAbsolutePath()));
		if (ct == null) {
			report.error(new IOMessageImpl(msgCT, null, -1, -1, source));
			return;
		}
		@SuppressWarnings("unchecked")
		IOProviderDescriptor srf = HaleIO.findIOProviderFactory(clazz, ct, null);
		if (srf == null) {
			report.error(new IOMessageImpl(msgIO, null, -1, -1, source));
			return;
		}
		conf.setProviderId(srf.getIdentifier());

		// provider configuration
		// source
		conf.getProviderConfiguration().put(AbstractImportProvider.PARAM_SOURCE,
				Value.of(source.toString()));
		// content type
		conf.getProviderConfiguration().put(AbstractImportProvider.PARAM_CONTENT_TYPE,
				Value.of(ct.getId()));

		// no dependencies needed

		// add configuration to project
		project.getResources().add(conf);
	}

}
