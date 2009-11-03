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
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.oml.io.OmlRdfGenerator;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.project.generated.HaleProject;
import eu.esdihumboldt.hale.models.project.generated.InstanceData;
import eu.esdihumboldt.hale.models.project.generated.MappedSchema;
import eu.esdihumboldt.hale.models.project.generated.Task;
import eu.esdihumboldt.hale.models.project.generated.TaskStatus;
import eu.esdihumboldt.hale.rcp.views.map.SelectCRSDialog;
import eu.esdihumboldt.mediator.util.NamespacePrefixMapperImpl;

/**
 * The {@link ProjectGenerator} serializes all project info to an xml file.
 * The {@link Alignment} is saved alongside in a goml file.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ProjectGenerator {
	
	private static Logger _log = Logger.getLogger(ProjectGenerator.class);

	public static void write(String xmlPath, String name) throws JAXBException {
		
		// 1. create HaleProject object from various services.
		HaleProject hproject = ProjectGenerator.createHaleProject(xmlPath, name);
		
		// 2. marshall AlignmentType to xml
		JAXBContext jc = JAXBContext.newInstance(
				"eu.esdihumboldt.hale.models.project.generated");
		Marshaller m = jc.createMarshaller();

		try {
			m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new NamespacePrefixMapperImpl());
		} catch (PropertyException e) {
			_log.warn("JAXB provider doesn't recognize the prefix mapper.");
		}

		// make the output indented.
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(new JAXBElement(new QName(null, "HaleProject", "hale"),
				HaleProject.class, hproject), new File(xmlPath));
	}
	
	private static HaleProject createHaleProject(String xmlPath, String name) 
			throws JAXBException {
		// get service references as required.
		ProjectService projectService = 
			(ProjectService) PlatformUI.getWorkbench().getService(
					ProjectService.class);
		
		TaskService taskService = 
			(TaskService) PlatformUI.getWorkbench().getService(
					TaskService.class);
		
		AlignmentService alignmentService = 
			(AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
		
		// setup project and basic attributes
		HaleProject hproject = new HaleProject();
		hproject.setHaleVersion(projectService.getHaleVersion());
		hproject.setDateCreated(projectService.getProjectCreatedDate());
		hproject.setDateModified(Calendar.getInstance().getTime().toString());
		hproject.setName(name);
		
		// create InstanceData element
		InstanceData id = new InstanceData();
		id.setPath(projectService.getInstanceDataPath());
		if (SelectCRSDialog.lastWasCode()) {
			id.setEpsgcode(
					SelectCRSDialog.getValue().getCoordinateSystem().getName().getCode());
		}
		else if (SelectCRSDialog.getValueWKT() != null) {
			id.setWkt(SelectCRSDialog.getValueWKT());
		}
		hproject.setInstanceData(id);
		
		// create MappedSchema elements
		MappedSchema sourceschema = new MappedSchema();
		sourceschema.setPath(projectService.getSourceSchemaPath());
		hproject.setSourceSchema(sourceschema);
		
		MappedSchema targetschema = new MappedSchema();
		targetschema.setPath(projectService.getTargetSchemaPath());
		hproject.setTargetSchema(targetschema);
		
		// transfer task status
		TaskStatus taskStatus = new TaskStatus();
		List<Task> tasks = taskStatus.getTask();
		for (eu.esdihumboldt.hale.task.Task t : taskService.getOpenTasks()) {
			Task newTask = new Task();
			newTask.setSeverityLevel(t.getSeverityLevel().toString());
			newTask.setTaskStatus(t.getTaskStatus().toString());
			newTask.setTaskType(t.getTaskType());
			newTask.setTitle(t.getTaskTitle());
			newTask.setValue(t.getValue());
			tasks.add(newTask);
		}
		
		// serialize mapping and link it in HaleProject 
		OmlRdfGenerator org = new OmlRdfGenerator();
		org.write(alignmentService.getAlignment(), xmlPath + ".goml");
		hproject.setOmlPath(xmlPath + ".goml");
		return hproject;
	}
}
