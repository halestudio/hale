/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.commons.goml.align.Formalism;
import eu.esdihumboldt.commons.goml.align.Schema;
import eu.esdihumboldt.commons.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.cache.Request;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.internal.Messages;
import eu.esdihumboldt.hale.ui.io.legacy.InstanceDataImportWizard;
import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaService;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.crs.CodeDefinition;
import eu.esdihumboldt.hale.ui.service.instance.crs.WKTDefinition;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.internal.generated.HaleProject;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.util.ExceptionHelper;
import eu.esdihumboldt.specification.cst.align.ISchema;

/**
 * The {@link ProjectParser} reads a given project xml file and directly pushes
 * the values read into the respective services, such as {@link ProjectService}, 
 * {@link InstanceService} and {@link AlignmentService}.
 * 
 * @author Thorsten Reitz
 */
public class ProjectParser {
	
	private static ALogger _log = ALoggerFactory.getLogger(ProjectParser.class);
	
	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String PROJECT_CONTEXT = "eu.esdihumboldt.hale.ui.service.project.internal.generated"; //$NON-NLS-1$
	
	private final ProjectService projectService;

	/**
	 * Constructor
	 * 
	 * @param projectService the project service
	 */
	public ProjectParser(ProjectService projectService) {
		super();
		this.projectService = projectService;
	}

	/**
	 * Load a project from a file
	 * 
	 * @param filename the file name
	 * @param monitor the progress monitor
	 * 
	 * @return the loaded project 
	 */
	public HaleProject read(String filename, IProgressMonitor monitor) {
		monitor.beginTask(Messages.ProjectParser_0, IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		
		String basePath = FilenameUtils.getFullPath(filename);
		
//		ProjectParser._log.setLevel(Level.INFO);
		// 1. unmarshal rdf
		JAXBContext jc;
		JAXBElement<HaleProject> root = null;
		try {
			jc = JAXBContext.newInstance(PROJECT_CONTEXT);
            Unmarshaller u = jc.createUnmarshaller();

            u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            root = u.unmarshal(new StreamSource(new File(filename)),
					HaleProject.class);
		} catch (JAXBException e) {
			_log.error("Unmarshalling the selected HaleProject failed: ", e); //$NON-NLS-1$
		}
		
		if (root != null) {
			List<String> errors = load(root.getValue(), basePath, monitor);
			monitor.done();
			
			if (errors != null && !errors.isEmpty()) {
				// show error dialog
				StringBuffer message = new StringBuffer(Messages.ProjectParser_1); //$NON-NLS-1$
				for (int i = 0; i < errors.size(); i++) {
					if (i != 0) {
						message.append('\n');
					}
					message.append("- "); //$NON-NLS-1$
					message.append(errors.get(i));
				}
				
				ExceptionHelper.handleException(message.toString(), 
						HALEUIPlugin.PLUGIN_ID, null);
			}
			
			return root.getValue();
		}
		else {
			monitor.done();
			return null;
		}
	}

	private List<String> load(HaleProject project, String basePath, 
			final IProgressMonitor monitor) {
		projectService.clean();
		
		// last but not least load different config stuff
		this.loadConfig(project);
		
		// first, load schemas.
		this.loadSchemas(project, basePath, monitor);
		
		List<String> errors = new ArrayList<String>();
		
		// second, load alignment.
		this.loadAlignment(project, basePath, monitor, errors);
		
		// second and a half, load styles
		this.loadStyle(project, basePath, monitor, errors);
		
		// third, load instances.
		this.loadInstances(project, basePath, monitor, errors);
		
		// fourth, it's time for loading the tasks.
		this.loadTasks(project, basePath, monitor);
		
		// Finally, initialize other ProjectService values.
		projectService.setProjectCreatedDate(project.getDateCreated());
		
		// 
		Request.getInstance().flush();
		
		return errors;
	}
	
	private void loadSchemas(HaleProject project, String basePath, final IProgressMonitor monitor) {
		SchemaService schemaService = 
			(SchemaService) PlatformUI.getWorkbench().getService(
					SchemaService.class);
		
		monitor.subTask(Messages.ProjectParser_6); //$NON-NLS-1$
		ProgressIndicator progress = new ProgressIndicator() {
			
			@Override
			public void begin(String taskName, int totalWork) {
				// ignore
			}

			@Override
			public void end() {
				// ignore
			}

			@Override
			public boolean isCanceled() {
				return false;
			}

			@Override
			public void advance(int workUnits) {
				// ignore
			}
			
			@Override
			public void setCurrentTask(String taskName) {
				monitor.subTask(taskName);
			}
		};
		
		try {
			if (project.getSourceSchema() != null 
					&& project.getSourceSchema().getPath() != null) {
				URI sourceSchemaPath = getLocation(project.getSourceSchema().getPath(), basePath); 
				
				schemaService.loadSchema(
						sourceSchemaPath, null,
						SchemaSpaceID.SOURCE, progress);
				projectService.setSourceSchemaPath(sourceSchemaPath.toString());
			}
		} catch (Exception e) {
			// fail
			throw new RuntimeException("Schema could not be loaded: ", e); //$NON-NLS-1$
		}
		try{
			if (project.getTargetSchema() != null 
					&& project.getTargetSchema().getPath() != null) {
				URI targetSchemaPath = getLocation(project.getTargetSchema().getPath(), basePath);
				
				schemaService.loadSchema(
						targetSchemaPath, null,
						SchemaSpaceID.TARGET, progress);
				projectService.setTargetSchemaPath(targetSchemaPath.toString());
			}
		} catch (Exception e) {
			// fail
			throw new RuntimeException("Schema could not be loaded: ", e); //$NON-NLS-1$
		}
	}
	
	private void loadAlignment(HaleProject project, String basePath, final IProgressMonitor monitor, List<String> errors) {
		AlignmentService alignmentService = 
			(AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
		
		monitor.subTask(Messages.ProjectParser_8); //$NON-NLS-1$
		if (project.getOmlPath() != null && !project.getOmlPath().isEmpty()) {
			try {
				OmlRdfReader reader = new OmlRdfReader();
				URI omlPath = getLocation(project.getOmlPath(), basePath);
				Alignment alignment = reader.read(omlPath.toURL());
				
				// update alignment
				// source schema location
				ISchema orgSchema = alignment.getSchema1();
				Schema newSchema = new Schema(projectService.getSourceSchemaPath(), (Formalism) orgSchema.getFormalism());
				newSchema.setAbout(orgSchema.getAbout());
				alignment.setSchema1(newSchema);
				
				// target schema location
				orgSchema = alignment.getSchema2();
				if (orgSchema != null) {
					newSchema = new Schema(projectService.getTargetSchemaPath(), (Formalism) orgSchema.getFormalism());
					newSchema.setAbout(orgSchema.getAbout());
					alignment.setSchema2(newSchema);
				}
				
				alignmentService.addOrUpdateAlignment(alignment);
				_log.info("Number of loaded cells: " + alignmentService.getAlignment().getMap().size()); //$NON-NLS-1$
			} catch (Exception e) {
				// continue
				String message = "Mapping could not be loaded";  //$NON-NLS-1$
				_log.error(message, e);
				errors.add(message + ": " + e.getMessage()); //$NON-NLS-1$
				alignmentService.cleanModel();
			}
		}
	}
	
	private void loadStyle(HaleProject project, String basePath, final IProgressMonitor monitor, List<String> errors) {
		//XXX styles in project deactivated for now
//		StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//		
//		monitor.subTask(Messages.ProjectParser_12); //$NON-NLS-1$
//		if (project.getStyles() != null) {
//			String path = project.getStyles().getPath();
//			URI stylesLoc = getLocation(path, basePath);
//			
//			//styleService.clearStyles();
//			if (path != null) {
//				try {
//					styleService.addStyles(stylesLoc.toURL());
//				} catch (Exception e) {
//					// continue
//					String message = "Error loading SLD from " + path;  //$NON-NLS-1$
//					_log.error(message, e);
//					errors.add(message);
//					styleService.clearStyles();
//				}
//			}
//			// background
//			final String color = project.getStyles().getBackground();
//			styleService.setBackground((color == null)?(null):(StringConverter.asRGB(color)));
//		}
	}
	
	private void loadInstances(HaleProject project, String basePath, final IProgressMonitor monitor, List<String> errors) {
		InstanceService instanceService = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		monitor.subTask(Messages.ProjectParser_14); //$NON-NLS-1$
		if (project.getInstanceData() != null) {
			try {
//				URI file = new URI(URLDecoder.decode(project.getInstanceData().getPath(), "UTF-8"));
				URI file = getLocation(project.getInstanceData().getPath(), basePath);
				ConfigurationType conf;
				try {
					conf = ConfigurationType.valueOf(project.getInstanceData().getType());
				} catch (Exception e) {
					// fall back to default
					conf = ConfigurationType.GML3;
				}
				
				if (project.getInstanceData().getEpsgcode() != null) {
					instanceService.setCRS(new CodeDefinition(project.getInstanceData().getEpsgcode(), null));
				}
				else if (project.getInstanceData() != null && project.getInstanceData().getWkt() != null) {
					instanceService.setCRS(new WKTDefinition(project.getInstanceData().getWkt(), null));
				}
				instanceService.addInstances(DataSet.SOURCE, 
						InstanceDataImportWizard.loadInstances(file, conf, null));
				projectService.setInstanceDataPath(file.toString()); //project.getInstanceData().getPath());
				projectService.setInstanceDataType(conf);
			} catch (Exception e) {
				// continue
				String message = "Instances could not be loaded";  //$NON-NLS-1$
				_log.error(message, e);
				errors.add(message + ": " + e.getMessage()); //$NON-NLS-1$
				instanceService.cleanInstances();
			}
		}
	}
	
	private void loadTasks(HaleProject project, String basePath, final IProgressMonitor monitor) {
		//XXX tasks in project deactivated for now
//		SchemaService schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
//		
//		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
//		
//		monitor.subTask(Messages.ProjectParser_17); //$NON-NLS-1$
//		ATransaction taskTrans = _log.begin("Loading tasks"); //$NON-NLS-1$
//		try {
//			taskService.clearUserTasks();
//			TaskStatus status = project.getTaskStatus();
//			if (status != null) {
//				for (Task task : status.getTask()) {
//					try {
//						// get identifiers
//						List<Definition> definitions = new ArrayList<Definition>();
//						for (String identifier : task.getContextIdentifier()) {
//							Definition definition = schemaService.getDefinition(identifier);
//							if (definition == null) {
//								throw new IllegalStateException("Unknown identifier " +  //$NON-NLS-1$
//										identifier + ", failed to load task."); //$NON-NLS-1$
//							}
//							else {
//								definitions.add(definition);
//							}
//						}
//						eu.esdihumboldt.hale.ui.service.project.internal.generated.task.Task newTask = new BaseTask(task.getTaskType(), definitions);
//							TaskUserData userData = new TaskUserDataImpl();
//							userData.setUserComment(task.getComment());
//							userData.setTaskStatus(eu.esdihumboldt.hale.task.TaskUserData.TaskStatus.valueOf(task.getTaskStatus()));
//							
//							taskService.setUserData(newTask, userData);
//					} catch (IllegalStateException e) {
//						_log.error(e.getMessage());
//					}
//				}
//			}
//		}
//		finally {
//			taskTrans.end();
//		}
	}

	private void loadConfig(HaleProject project) {
		ConfigSchemaService config = (ConfigSchemaService) PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
		
		config.parseConfig(project.getConfigSchema());
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

}
