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

package eu.esdihumboldt.hale.ui.style.service.internal;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.style.service.StyleService;

/**
 * Service factory for the style service
 * 
 * @author Simon Templer
 */
public class StyleServiceFactory extends AbstractServiceFactory {

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator,
	 *      IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface,
			IServiceLocator parentLocator, IServiceLocator locator) {
		if (serviceInterface.equals(StyleService.class)) {
			return new StyleServiceImpl((ProjectService) locator.getService(ProjectService.class),
					(SchemaService) locator.getService(SchemaService.class));
		}

		return null;
	}

}
