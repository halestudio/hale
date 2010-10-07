/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
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
import java.io.FileWriter;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.ui.PlatformUI;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Style;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.project.generated.HaleProject;
import eu.esdihumboldt.hale.models.project.generated.InstanceData;
import eu.esdihumboldt.hale.models.project.generated.MappedSchema;
import eu.esdihumboldt.hale.models.project.generated.Styles;
import eu.esdihumboldt.hale.models.project.generated.Task;
import eu.esdihumboldt.hale.models.project.generated.TaskStatus;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import eu.esdihumboldt.hale.rcp.views.map.MapView;
import eu.esdihumboldt.hale.rcp.views.map.SelectCRSDialog;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.task.TaskUserData;

/**
 * The {@link ProjectGenerator} serializes all project info to an xml file.
 * The {@link Alignment} is saved alongside in a goml file.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ProjectGenerator {
	
	private static Logger _log = Logger.getLogger(ProjectGenerator.class);

	private final ProjectService projectService;
	
	/**
	 * Constructor
	 * 
	 * @param projectService the project service
	 */
	public ProjectGenerator(ProjectService projectService) {
		super();
		this.projectService = projectService;
	}

	/**
	 * Write the project to the given file
	 * 
	 * @param xmlPath the file name
	 * @param name the project name
	 * 
	 * @throws JAXBException if writing the project fails
	 */
	@SuppressWarnings("unchecked")
	public void write(String xmlPath, String name) throws JAXBException {
		
		// add *.xml extension if is wasn't added before
		if (!xmlPath.endsWith(".xml")) {
			xmlPath = xmlPath + ".xml";
		}
		
		// create HaleProject object from various services.
		HaleProject hproject = createHaleProject(xmlPath, name);
		
		// 2. marshall AlignmentType to xml
		JAXBContext jc = JAXBContext.newInstance(
				"eu.esdihumboldt.hale.models.project.generated", ProjectGenerator.class.getClassLoader());
		Marshaller m = jc.createMarshaller();

		try {
			m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",
					new NamespacePrefixMapperImpl());
		} catch (PropertyException e) {
			_log.warn("JAXB provider doesn't recognize the prefix mapper:", e);
		}

		// make the output indented.
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(new JAXBElement(new QName(null, "HaleProject", "hale"),
				HaleProject.class, hproject), new File(xmlPath));
	}
	
	private HaleProject createHaleProject(String xmlPath, String name) 
			throws JAXBException {
		final String basePath = FilenameUtils.getFullPath(xmlPath);
		
		// get service references as required.
		TaskService taskService = 
			(TaskService) PlatformUI.getWorkbench().getService(
					TaskService.class);
		
		AlignmentService alignmentService = 
			(AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
		
		StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		
		// setup project and basic attributes
		HaleProject hproject = new HaleProject();
		hproject.setHaleVersion(projectService.getHaleVersion());
		hproject.setDateCreated(projectService.getProjectCreatedDate());
		hproject.setDateModified(Calendar.getInstance().getTime().toString());
		hproject.setName(name);
		
		// create InstanceData element
		if (projectService.getInstanceDataPath() != null) {
			InstanceData id = new InstanceData();
			id.setPath(getRelativeLocation(projectService.getInstanceDataPath(), basePath));
			if (SelectCRSDialog.lastWasCode()) {
				id.setEpsgcode(
						SelectCRSDialog.getValue().getIdentifiers().iterator().next().toString());
			}
			else if (SelectCRSDialog.getValueWKT() != null) {
				id.setWkt(SelectCRSDialog.getValueWKT());
			}
			id.setType(projectService.getInstanceDataType().name());
			hproject.setInstanceData(id);
		}
		
		// create MappedSchema elements
		MappedSchema sourceschema = new MappedSchema();
		sourceschema.setPath(getRelativeLocation(projectService.getSourceSchemaPath(), basePath));
		hproject.setSourceSchema(sourceschema);
		
		MappedSchema targetschema = new MappedSchema();
		targetschema.setPath(getRelativeLocation(projectService.getTargetSchemaPath(), basePath));
		hproject.setTargetSchema(targetschema);
		
		// transfer task status
		TaskStatus taskStatus = new TaskStatus();
		List<Task> tasks = taskStatus.getTask();
		for (Entry<eu.esdihumboldt.hale.task.Task, TaskUserData> entry : taskService.getUserTasks().entrySet()) {
			Task newTask = new Task();
			newTask.setTaskStatus(entry.getValue().getTaskStatus().name());
			newTask.setTaskType(entry.getKey().getTypeName());
			newTask.setComment(entry.getValue().getUserComment());
			List<String> identifiers = newTask.getContextIdentifier();
			for (Definition definition : entry.getKey().getContext()) {
				identifiers.add(definition.getIdentifier());
			}
			tasks.add(newTask);
		}
		hproject.setTaskStatus(taskStatus);
		
		// serialize mapping and link it in HaleProject 
		OmlRdfGenerator org = new HaleOmlRdfGenerator();
		org.write(alignmentService.getAlignment(), xmlPath + ".goml");
		hproject.setOmlPath(getRelativeLocation(xmlPath + ".goml", basePath));
		
		// save SLD and background
		Style style = styleService.getStyle();
		if (style != null) {
			String stylePath = xmlPath + ".sld";
			SLDTransformer trans = new SLDTransformer();
			trans.setIndentation(2);
			try {
				FileWriter writer = new FileWriter(new File(stylePath));
				trans.transform(styleService.getStyle(), writer);
				writer.close();
			} catch (Exception e) {
				_log.error("Error saving SLD file", e);
			}
			
			Styles styles = new Styles();
			styles.setPath(getRelativeLocation(stylePath, basePath));
			
			// background
			MapView map = (MapView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MapView.ID);
			if (map != null) {
				if (!map.getPainter().isDefaultBackground()) {
					styles.setBackground(StringConverter.asString(map.getPainter().getBackground()));
				}
				else {
					styles.setBackground(null);
				}
			}
			
			hproject.setStyles(styles);
		}
		
		return hproject;
	}

	/**
	 * Get the relative location to a file
	 * 
	 * @param file the file path or URI
	 * @param basePath the possible base path
	 * 
	 * @return the relative file path if possible, otherwise an URI
	 */
	private String getRelativeLocation(String file, String basePath) {
		try {
			URI fileUri = new URI(file);
			String scheme = fileUri.getScheme();
			
			if (scheme == null) {
				// no scheme specified
				return getRelativePath(file, basePath);
			}
			else {
				try {
					File f = new File(fileUri);
					return getRelativePath(f.getAbsolutePath(), basePath);
				} catch (IllegalArgumentException e) {
					return fileUri.toString();
				}
			}
		} catch (Exception e) {
			// assume file path w/o scheme
			return getRelativePath(file, basePath);
		}
	}

	/**
	 * Get the relative file path
	 * 
	 * @param file the file path
	 * @param basePath the possible base path
	 * 
	 * @return the relative file path if possible, otherwise an URI
	 */
	private String getRelativePath(String file, String basePath) {
		if (file.startsWith(basePath)) {
			// can use relative path
			return file.substring(basePath.length());
		}
		else if (new File(basePath, file).exists()) {
			// already a relative path
			return file;
		}
		else {
			// return the path as URI
			return new File(file).toURI().toString();
		}
	}
	
}
