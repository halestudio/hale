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

package eu.esdihumboldt.hale.models.project;

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
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.ui.PlatformUI;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.cst.align.ISchema;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.project.generated.HaleProject;
import eu.esdihumboldt.hale.models.project.generated.Task;
import eu.esdihumboldt.hale.models.project.generated.TaskStatus;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.ExceptionHelper;
import eu.esdihumboldt.hale.rcp.views.map.SelectCRSDialog;
import eu.esdihumboldt.hale.rcp.wizards.io.InstanceDataImportWizard;
import eu.esdihumboldt.hale.schemaprovider.ProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.task.TaskUserData;
import eu.esdihumboldt.hale.task.impl.BaseTask;
import eu.esdihumboldt.hale.task.impl.TaskUserDataImpl;

/**
 * The {@link ProjectParser} reads a given project xml file and directly pushes
 * the values read into the respective services, such as {@link ProjectService}, 
 * {@link InstanceService} and {@link AlignmentService}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ProjectParser {
	
	private static ALogger _log = ALoggerFactory.getLogger(ProjectParser.class);
	
	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String PROJECT_CONTEXT = "eu.esdihumboldt.hale.models.project.generated";
	
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
		monitor.beginTask("Loading alignment project", IProgressMonitor.UNKNOWN);
		
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
			_log.error("Unmarshalling the selected HaleProject failed: ", e);
		}
		
		if (root != null) {
			List<String> errors = load(root.getValue(), basePath, monitor);
			monitor.done();
			
			if (errors != null && !errors.isEmpty()) {
				// show error dialog
				StringBuffer message = new StringBuffer("The project was loaded but " +
						"the following operations failed:\n\n");
				for (int i = 0; i < errors.size(); i++) {
					if (i != 0) {
						message.append('\n');
					}
					message.append("- ");
					message.append(errors.get(i));
				}
				
				ExceptionHelper.handleException(message.toString(), 
						HALEActivator.PLUGIN_ID, null);
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
		
		// get service references as required.
		InstanceService instanceService = 
			(InstanceService) PlatformUI.getWorkbench().getService(
					InstanceService.class);
		
		AlignmentService alignmentService = 
			(AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
		
		SchemaService schemaService = 
			(SchemaService) PlatformUI.getWorkbench().getService(
					SchemaService.class);
		
		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
		
		StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		// first, load schemas.
		monitor.subTask("Schemas");
		try {
			ProgressIndicator progress = new ProgressIndicator() {
				
				@Override
				public void setProgress(int percent) {
					// ignore
				}
				
				@Override
				public void setCurrentTask(String taskName) {
					monitor.subTask(taskName);
				}
			};
			
			if (project.getSourceSchema() != null 
					&& project.getSourceSchema().getPath() != null) {
				URI sourceSchemaPath = getLocation(project.getSourceSchema().getPath(), basePath); 
				
				schemaService.loadSchema(
						sourceSchemaPath, null,
						SchemaType.SOURCE, progress);
				projectService.setSourceSchemaPath(sourceSchemaPath.toString());
			}
			if (project.getTargetSchema() != null 
					&& project.getTargetSchema().getPath() != null) {
				URI targetSchemaPath = getLocation(project.getTargetSchema().getPath(), basePath);
				
				schemaService.loadSchema(
						targetSchemaPath, null,
						SchemaType.TARGET, progress);
				projectService.setTargetSchemaPath(targetSchemaPath.toString());
			}
		} catch (Exception e) {
			// fail
			throw new RuntimeException("Schema could not be loaded: ", e);
		}
		
		List<String> errors = new ArrayList<String>();
		
		// second, load alignment.
		monitor.subTask("Mapping");
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
				newSchema = new Schema(projectService.getTargetSchemaPath(), (Formalism) orgSchema.getFormalism());
				newSchema.setAbout(orgSchema.getAbout());
				alignment.setSchema2(newSchema);
				
				alignmentService.addOrUpdateAlignment(alignment);
				_log.info("Number of loaded cells: " + alignmentService.getAlignment().getMap().size());
			} catch (Exception e) {
				// continue
				String message = "Mapping could not be loaded"; 
				_log.error(message, e);
				errors.add(message + ": " + e.getMessage());
				alignmentService.cleanModel();
			}
		}
		
		// second and a half, load styles
		monitor.subTask("Styles");
		if (project.getStyles() != null) {
			String path = project.getStyles().getPath();
			URI stylesLoc = getLocation(path, basePath);
			
			//styleService.clearStyles();
			if (path != null) {
				try {
					styleService.addStyles(stylesLoc.toURL());
				} catch (Exception e) {
					// continue
					String message = "Error loading SLD from " + path; 
					_log.error(message, e);
					errors.add(message);
					styleService.clearStyles();
				}
			}
			// background
			final String color = project.getStyles().getBackground();
			styleService.setBackground((color == null)?(null):(StringConverter.asRGB(color)));
		}
		
		// third, load instances.
		monitor.subTask("Instances");
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
					SelectCRSDialog.setEpsgcode(project.getInstanceData().getEpsgcode());
				}
				else if (project.getInstanceData() != null) {
					SelectCRSDialog.setWkt(project.getInstanceData().getWkt());
				}
				instanceService.addInstances(DatasetType.reference, 
						InstanceDataImportWizard.loadInstances(file, conf, null));
				projectService.setInstanceDataPath(file.toString()); //project.getInstanceData().getPath());
				projectService.setInstanceDataType(conf);
				
			} catch (Exception e) {
				// continue
				String message = "Instances could not be loaded"; 
				_log.error(message, e);
				errors.add(message + ": " + e.getMessage());
				instanceService.cleanInstances();
			}
		}
		
		// fourth, it's time for loading the tasks.
		monitor.subTask("Tasks");
		ATransaction taskTrans = _log.begin("Loading tasks");
		try {
			taskService.clearUserTasks();
			TaskStatus status = project.getTaskStatus();
			if (status != null) {
				for (Task task : status.getTask()) {
					try {
						// get identifiers
						List<Definition> definitions = new ArrayList<Definition>();
						for (String identifier : task.getContextIdentifier()) {
							Definition definition = schemaService.getDefinition(identifier);
							if (definition == null) {
								throw new IllegalStateException("Unknown identifier " + 
										identifier + ", failed to load task.");
							}
							else {
								definitions.add(definition);
							}
						}
						eu.esdihumboldt.hale.task.Task newTask = new BaseTask(task.getTaskType(), definitions);
//						if (newTask != null) {
							TaskUserData userData = new TaskUserDataImpl();
							userData.setUserComment(task.getComment());
							userData.setTaskStatus(eu.esdihumboldt.hale.task.TaskUserData.TaskStatus.valueOf(task.getTaskStatus()));
							
							taskService.setUserData(newTask, userData);
//						}
//						else {
//							_log.error("Task creation of type " + task.getTaskType() + " failed");
//						}
					} catch (IllegalStateException e) {
						_log.error(e.getMessage());
					}
				}
			}
		}
		finally {
			taskTrans.end();
		}
		
		// Finally, initialize other ProjectService values.
		projectService.setProjectCreatedDate(project.getDateCreated());
		
		return errors;
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
