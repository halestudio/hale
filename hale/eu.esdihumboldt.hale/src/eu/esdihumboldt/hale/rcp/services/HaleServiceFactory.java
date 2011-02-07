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
package eu.esdihumboldt.hale.rcp.services;

import org.apache.log4j.Logger;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.gmlwriter.GmlWriter;
import eu.esdihumboldt.hale.gmlwriter.impl.DefaultGmlWriter;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.alignment.AlignmentServiceImpl;
import eu.esdihumboldt.hale.models.instance.InstanceServiceImpl;
import eu.esdihumboldt.hale.models.project.ProjectServiceImpl;
import eu.esdihumboldt.hale.models.project.RecentFilesService;
import eu.esdihumboldt.hale.models.project.RecentFilesServiceImpl;
import eu.esdihumboldt.hale.models.schema.SchemaProviderService;
import eu.esdihumboldt.hale.models.style.StyleServiceImpl;
import eu.esdihumboldt.hale.models.task.TaskServiceImpl;
import eu.esdihumboldt.hale.models.utils.SchemaItemService;
import eu.esdihumboldt.hale.models.utils.SchemaItemServiceImpl;

/**
 * This implementation of the {@link AbstractServiceFactory} allows to use the
 * {@link InstanceService}, {@link SchemaService}, {@link StyleService}, 
 * {@link TaskService} and {@link AlignmentService} 
 * as eclipse service, thereby making direct references to the implementation 
 * unnecessary.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class HaleServiceFactory 
	extends AbstractServiceFactory {
	
	private static final Logger log = Logger.getLogger(HaleServiceFactory.class);
	
	private InstanceService instance;
	private SchemaService schema;
	private StyleService style;
	private TaskService task;
	private AlignmentService alignment;
	private ProjectService project;
	private SchemaItemService schemaItem;
	private RecentFilesService recentFiles;
	private GmlWriter gmlWriter;

	/**
	 * Default constructor
	 */
	public HaleServiceFactory() {
		super();
	}

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator, IServiceLocator)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object create(Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		if (serviceInterface.equals(InstanceService.class)) {
			if (instance == null) {
				instance = InstanceServiceImpl.getInstance();
			}
			return instance;
		}
		else if (serviceInterface.equals(SchemaService.class)) {
			return getSchemaService();
		}
		else if (serviceInterface.equals(TaskService.class)) {
			if (task == null) {
				task = TaskServiceImpl.getInstance();
			}
			return task;
		}
		else if (serviceInterface.equals(StyleService.class)) {
			return getStyleService();
		}
		else if (serviceInterface.equals(AlignmentService.class)) {
			if (alignment == null) {
				alignment = AlignmentServiceImpl.getInstance();
			}
			return alignment;
		}
		else if (serviceInterface.equals(ProjectService.class)) {
			if (project == null) {
				project = ProjectServiceImpl.getInstance();
			}
			return project;
		}
		else if (serviceInterface.equals(SchemaItemService.class)) {
			if (schemaItem == null) {
				schemaItem = new SchemaItemServiceImpl(getSchemaService());
			}
			return schemaItem;
		}
		else if (serviceInterface.equals(RecentFilesService.class)) {
			if (recentFiles == null) {
				recentFiles = new RecentFilesServiceImpl();
			}
			return recentFiles;
		}
		else if (serviceInterface.equals(GmlWriter.class)) {
			if (gmlWriter == null) {
				gmlWriter = new DefaultGmlWriter();
			}
			return gmlWriter;
		}
		else {
			throw new RuntimeException("For the given serviceInterface (" 
					+ serviceInterface.getCanonicalName() 
					+ "), no service implementation is known.");
		}
	}

	/**
	 * Get the schema service
	 * 
	 * @return the schema service
	 */
	private SchemaService getSchemaService() {
		if (schema == null) {
			try {
				schema = new SchemaProviderService(); //SchemaProviderService.getInstance(ApacheSchemaProvider.class);
			} catch (Exception e) {
				log.error("Error instantiating schema service", e);
			}
		}
		return schema;
	}
	
	/**
	 * Get the style service
	 * 
	 * @return the style service
	 */
	private StyleService getStyleService() {
		if (style == null) {
			style = StyleServiceImpl.getInstance(getSchemaService());
		}
		return style;
	}

}
