/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.service.internal;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.internal.AlignmentServiceImpl;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.internal.orient.OrientInstanceService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentFilesService;
import eu.esdihumboldt.hale.ui.service.project.internal.ProjectServiceImpl;
import eu.esdihumboldt.hale.ui.service.project.internal.RecentFilesServiceImpl;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.service.report.internal.ReportServiceImpl;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.internal.SchemaServiceImpl;

/**
 * Factory for HALE services
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class HaleServiceFactory extends AbstractServiceFactory {

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator, IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		if (ReportService.class.equals(serviceInterface)) {
			return new ReportServiceImpl();
		}
		
		if (InstanceService.class.equals(serviceInterface)) {
			return OrientInstanceService.getInstance(
					(SchemaService) locator.getService(SchemaService.class),
					(ProjectService) locator.getService(ProjectService.class),
					(AlignmentService) locator.getService(AlignmentService.class));
		}
		
		if (AlignmentService.class.equals(serviceInterface)) {
			return new AlignmentServiceImpl();
		}
		
		if (ProjectService.class.equals(serviceInterface)) {
			return new ProjectServiceImpl();
		}
		
		if (RecentFilesService.class.equals(serviceInterface)) {
			return new RecentFilesServiceImpl();
		}
		
		if (SchemaService.class.equals(serviceInterface)) {
			return new SchemaServiceImpl(
					(ProjectService) locator.getService(ProjectService.class));
		}
		
		return null;
	}

}
