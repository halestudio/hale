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
package eu.esdihumboldt.hale.io.project.jaxb.writer;

import java.io.File;
import java.net.URI;

import javax.swing.GroupLayout.Alignment;

import eu.esdihumboldt.hale.io.project.jaxb.generated.HaleProject;
import jakarta.xml.bind.JAXBException;

/**
 * The {@link ProjectGenerator} serializes all project info to an xml file. The
 * {@link Alignment} is saved alongside in a goml file.
 * 
 * @author Thorsten Reitz
 */
public class ProjectGenerator {

//	private static final ALogger _log = ALoggerFactory.getLogger(ProjectGenerator.class);

//	private final ProjectService projectService;

	/**
	 * Constructor
	 */
	public ProjectGenerator(/* ProjectService projectService */) {
		super();
//		this.projectService = projectService;
	}

	/**
	 * Write the project to the given file
	 * 
	 * @param xmlPath the file name
	 * @param name the project name
	 * 
	 * @throws JAXBException if writing the project fails
	 */
	@SuppressWarnings({ "unused" })
	public void write(String xmlPath, String name) throws JAXBException {
//		
//		// add *.xml extension if is wasn't added before
//		if (!xmlPath.endsWith(".xml")) { //$NON-NLS-1$
//			xmlPath = xmlPath + ".xml"; //$NON-NLS-1$
//		}
//		
//		// create HaleProject object from various services.
//		HaleProject hproject = createHaleProject(xmlPath, name);
//		
//		// 2. marshall AlignmentType to xml
//		JAXBContext jc = JAXBContext.newInstance(
//				"eu.esdihumboldt.hale.ui.service.project.internal.generated", ProjectGenerator.class.getClassLoader()); //$NON-NLS-1$
//		Marshaller m = jc.createMarshaller();
//
//		try {
//			m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
//					new NamespacePrefixMapperImpl());
//		} catch (PropertyException e) {
//			_log.warn("JAXB provider doesn't recognize the prefix mapper:", e); //$NON-NLS-1$
//		}
//
//		// make the output indented.
//		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//		m.marshal(new JAXBElement(new QName(null, "HaleProject", "hale"), //$NON-NLS-1$ //$NON-NLS-2$
//				HaleProject.class, hproject), new File(xmlPath));
	}

	@SuppressWarnings("unused")
	private HaleProject createHaleProject(String xmlPath, String name) throws JAXBException {
//		final String basePath = FilenameUtils.getFullPath(xmlPath);
//		
//		// setup project and basic attributes
		HaleProject hproject = new HaleProject();
//		hproject.setHaleVersion(projectService.getHaleVersion());
//		hproject.setDateCreated(projectService.getProjectCreatedDate());
//		hproject.setDateModified(Calendar.getInstance().getTime().toString());
//		hproject.setName(name);
//		
//		// create InstanceData element
//		this.createInstanceData(hproject, basePath);
//		
//		// create MappedSchema elements
//		this.createMappedSchema(hproject, basePath);
//		
//		// transfer task status
//		this.createTaskStatus(hproject);
//		
//		// add configSections
//		this.createConfigSections(hproject);
//		
//		// serialize mapping and link it in HaleProject 
//		this.createAlignment(hproject, basePath, xmlPath);
//		
//		// save SLD and background
//		this.createStyle(hproject, basePath, xmlPath);
//		
		return hproject;
	}

	@SuppressWarnings("unused")
	private void createInstanceData(HaleProject hproject, String basePath) {
//		if (projectService.getInstanceDataPath() != null) {
//			InstanceData id = new InstanceData();
//			id.setPath(getRelativeLocation(projectService.getInstanceDataPath(), basePath));
//			try {
//				InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
//				CRSDefinition crsdef = is.getCRS();
//				if (crsdef instanceof CodeDefinition) {
//					id.setEpsgcode(((CodeDefinition) crsdef).getCode());
//				}
//				else if (crsdef instanceof WKTDefinition) {
//					id.setWkt(((WKTDefinition) crsdef).getWkt());
//				}
//				else {
//					id.setEpsgcode(crsdef.getCRS().getIdentifiers().iterator().next().toString());
//				}
//			} catch (Throwable e) {
//				// ignore errors when trying to set CRS
//			}
//			id.setType(projectService.getInstanceDataType().name());
//			hproject.setInstanceData(id);
//		}
	}

	@SuppressWarnings("unused")
	private void createMappedSchema(HaleProject hproject, String basePath) {
//		MappedSchema sourceschema = new MappedSchema();
//		sourceschema.setPath(getRelativeLocation(projectService.getSourceSchemaPath(), basePath));
//		hproject.setSourceSchema(sourceschema);
//		
//		MappedSchema targetschema = new MappedSchema();
//		if (projectService.getTargetSchemaPath() != null) {
//			targetschema.setPath(getRelativeLocation(projectService.getTargetSchemaPath(), basePath));
//			hproject.setTargetSchema(targetschema);
//		}
	}

	@SuppressWarnings("unused")
	private void createTaskStatus(HaleProject hproject) {
		// XXX tasks in project deactivated for now
//		TaskService taskService = 
//			(TaskService) PlatformUI.getWorkbench().getService(
//					TaskService.class);
//		
//		TaskStatus taskStatus = new TaskStatus();
//		List<Task> tasks = taskStatus.getTask();
//		for (Entry<eu.esdihumboldt.hale.ui.service.project.internal.generated.task.Task, TaskUserData> entry : taskService.getUserTasks().entrySet()) {
//			Task newTask = new Task();
//			newTask.setTaskStatus(entry.getValue().getTaskStatus().name());
//			newTask.setTaskType(entry.getKey().getTypeName());
//			newTask.setComment(entry.getValue().getUserComment());
//			List<String> identifiers = newTask.getContextIdentifier();
//			for (Definition definition : entry.getKey().getContext()) {
//				identifiers.add(definition.getIdentifier());
//			}
//			tasks.add(newTask);
//		}
//		hproject.setTaskStatus(taskStatus);
	}

	@SuppressWarnings("unused")
	private void createAlignment(HaleProject hproject, String basePath, String xmlPath)
			throws JAXBException {
//		AlignmentService alignmentService = 
//			(AlignmentService) PlatformUI.getWorkbench().getService(
//					AlignmentService.class);
//		
//		OmlRdfGenerator org = new HaleOmlRdfGenerator();
//		org.write(alignmentService.getAlignment(), xmlPath + ".goml"); //$NON-NLS-1$
//		hproject.setOmlPath(getRelativeLocation(xmlPath + ".goml", basePath)); //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	private void createConfigSections(HaleProject hproject) {
//		ConfigSchemaService config = (ConfigSchemaService) PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
//		
//		ArrayList<ConfigSection> projectConfigList = (ArrayList<ConfigSection>) hproject.getConfigSchema();
//		ArrayList<ConfigSection> list = (ArrayList<ConfigSection>) config.generateConfig();
//		
//		for(ConfigSection s : list) {
//			if (!projectConfigList.contains(s)) {
//				projectConfigList.add(s);
//			}
//		}
	}

	@SuppressWarnings("unused")
	private void createStyle(HaleProject hproject, String basePath, String xmlPath) {
		// XXX style in project deactivated for now
//		StyleService styleService = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//		
//		Style style = styleService.getStyle();
//		if (style != null) {
//			String stylePath = xmlPath + ".sld"; //$NON-NLS-1$
//			SLDTransformer trans = new SLDTransformer();
//			trans.setIndentation(2);
//			try {
//				FileWriter writer = new FileWriter(new File(stylePath));
//				trans.transform(styleService.getStyle(), writer);
//				writer.close();
//			} catch (Exception e) {
//				_log.error("Error saving SLD file", e); //$NON-NLS-1$
//			}
//			
//			Styles styles = new Styles();
//			styles.setPath(getRelativeLocation(stylePath, basePath));
//			
//			// background
//			RGB back = styleService.getBackground();
//			if (back.equals(StylePreferences.getDefaultBackground())) {
//				styles.setBackground(null);
//			}
//			else {
//				styles.setBackground(StringConverter.asString(back));
//			}
//			
//			hproject.setStyles(styles);
//		}
	}

	/**
	 * Get the relative location to a file
	 * 
	 * @param file the file path or URI
	 * @param basePath the possible base path
	 * 
	 * @return the relative file path if possible, otherwise an URI
	 */
	@SuppressWarnings("unused")
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
