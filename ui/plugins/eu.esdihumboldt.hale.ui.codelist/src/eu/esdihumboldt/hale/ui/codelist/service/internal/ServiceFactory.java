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

package eu.esdihumboldt.hale.ui.codelist.service.internal;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Service factory for code list service.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ServiceFactory extends AbstractServiceFactory {

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator,
	 *      IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface,
			IServiceLocator parentLocator, IServiceLocator locator) {
		if (serviceInterface.equals(CodeListService.class)) {
			return new CodeListServiceImpl(
					(ProjectService) locator.getService(ProjectService.class));
		}
		return null;
	}
}
