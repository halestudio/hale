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

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.StyleService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.impl.AlignmentServiceImpl;
import eu.esdihumboldt.hale.models.impl.InstanceServiceImpl;
import eu.esdihumboldt.hale.models.impl.SchemaServiceImpl;
import eu.esdihumboldt.hale.models.impl.SchemaServiceImplApache;
import eu.esdihumboldt.hale.models.impl.StyleServiceImpl;
import eu.esdihumboldt.hale.models.impl.TaskServiceImpl;

/**
 * This implementation of the {@link AbstractServiceFactory} allows to use the
 * {@link InstanceService} as a eclipse service, thereby making direct 
 * references to the implementation unnecessary.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class HaleServiceFactory 
	extends AbstractServiceFactory {
	
	private InstanceService instance = InstanceServiceImpl.getInstance();
	private SchemaService schema = SchemaServiceImplApache.getInstance();
//	private SchemaService schema = SchemaServiceImpl.getInstance();
	private StyleService style = StyleServiceImpl.getInstance();
	private TaskService task = TaskServiceImpl.getInstance();
	private AlignmentService alignment = AlignmentServiceImpl.getInstance();

	public HaleServiceFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.ui.services.AbstractServiceFactory#create(java.lang.Class, org.eclipse.ui.services.IServiceLocator, org.eclipse.ui.services.IServiceLocator)
	 */
	@Override
	public Object create(Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		if (serviceInterface.equals(InstanceService.class)) {
			return this.instance;
		}
		if (serviceInterface.equals(SchemaService.class)) {
			return this.schema;
		}
		if (serviceInterface.equals(TaskService.class)) {
			return this.task;
		}
		if (serviceInterface.equals(StyleService.class)) {
			return this.style;
		}
		if (serviceInterface.equals(AlignmentService.class)) {
			return this.alignment;
		}
		else {
			throw new RuntimeException("For the given serviceInterface (" 
					+ serviceInterface.getCanonicalName() 
					+ "), no service implementation is known.");
		}
	}

}
