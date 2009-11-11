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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.ui.PlatformUI;
import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.goml.oml.io.OmlRdfReader;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.InstanceService.DatasetType;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.models.instance.HaleGMLParser;
import eu.esdihumboldt.hale.models.project.generated.HaleProject;
import eu.esdihumboldt.hale.rcp.views.map.SelectCRSDialog;

/**
 * The {@link ProjectParser} reads a given project xml file and directly pushes
 * the values read into the respective services, such as {@link ProjectService}, 
 * {@link InstanceService} and {@link AlignmentService}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ProjectParser {
	
	private static Logger _log = Logger.getLogger(ProjectParser.class);
	
	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String PROJECT_CONTEXT = "eu.esdihumboldt.hale.models.project.generated";

	/**
	 * @param result
	 */
	public static void read(String result) {
		ProjectParser._log.setLevel(Level.INFO);
		// 1. unmarshal rdf
		JAXBContext jc;
		JAXBElement<HaleProject> root = null;
		try {
			jc = JAXBContext.newInstance(PROJECT_CONTEXT);
            Unmarshaller u = jc.createUnmarshaller();

            u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            root = u.unmarshal(new StreamSource(new File(result)),
					HaleProject.class);
		} catch (JAXBException e) {
			_log.error("Unmarshalling the selected HaleProject failed: ", e);
		}
		
		ProjectParser.load(root.getValue());
	}

	/**
	 * @param value
	 */
	private static void load(HaleProject project) {
		// get service references as required.
		ProjectService projectService = 
			(ProjectService) PlatformUI.getWorkbench().getService(
					ProjectService.class);
		
		InstanceService instanceService = 
			(InstanceService) PlatformUI.getWorkbench().getService(
					InstanceService.class);
		
		AlignmentService alignmentService = 
			(AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
		
		SchemaService schemaService = 
			(SchemaService) PlatformUI.getWorkbench().getService(
					SchemaService.class);
		
		// first, load schemas.
		try {
			schemaService.loadSchema(
					new URI(project.getSourceSchema().getPath()), 
					SchemaType.SOURCE);
			schemaService.loadSchema(
					new URI(project.getTargetSchema().getPath()), 
					SchemaType.TARGET);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Schema could not be loaded: ", e);
		}
		
		// second, load alignment.
		try {
			OmlRdfReader reader = new OmlRdfReader();
			alignmentService.addOrUpdateAlignment(
					reader.read(project.getOmlPath()));
			_log.info("Number of loaded cells: " + alignmentService.getAlignment().getMap().size());
		} catch (Exception e) {
			throw new RuntimeException("Alignment could not be loaded: ", e);
		}
		
		// third, load instances.
		if (project.getInstanceData() != null) {
			try {
				URI file = new URI(URLDecoder.decode(project.getInstanceData().getPath(), "UTF-8"));
				InputStream xml = new FileInputStream(new File(file));
				Configuration configuration = new GMLConfiguration();
				HaleGMLParser parser = new HaleGMLParser(configuration);
				instanceService.addInstances(DatasetType.reference, 
						(FeatureCollection<FeatureType, Feature>) parser.parse(xml));
				if (project.getInstanceData().getEpsgcode() != null) {
					SelectCRSDialog.setEpsgcode(project.getInstanceData().getEpsgcode());
				}
				else if (project.getInstanceData() != null) {
					SelectCRSDialog.setWkt(project.getInstanceData().getWkt());
				}
			} catch (Exception e) {
				throw new RuntimeException("Instances could not be loaded: ", e);
			}
		}
		
		// fourth, it's time for loading the tasks.
		// TODO load tasks from project
		
		// Finally, initialize ProjectService values.
		projectService.setInstanceDataPath(project.getInstanceData().getPath());
		projectService.setSourceSchemaPath(project.getSourceSchema().getPath());
		projectService.setTargetSchemaPath(project.getTargetSchema().getPath());
		projectService.setProjectCreatedDate(project.getDateCreated());
	}

}
