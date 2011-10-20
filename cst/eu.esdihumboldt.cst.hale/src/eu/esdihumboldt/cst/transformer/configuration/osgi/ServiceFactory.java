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
package eu.esdihumboldt.cst.transformer.configuration.osgi;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.cst.transformer.CstService;
import eu.esdihumboldt.cst.transformer.service.CstServiceFactory;

/**
 * This implementation of the {@link AbstractServiceFactory} allows to use the
 * {@link CstService} as eclipse service, thereby making direct references to
 * the implementation unnecessary.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ServiceFactory 
	extends AbstractServiceFactory {
	
	private CstService transform = new CstServiceWrapper(CstServiceFactory.getInstance());
	
	/**
	 * Default constructor
	 */
	public ServiceFactory() {
		super();
	}

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator, IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		if (serviceInterface.equals(CstService.class)) {
			return this.transform;
		}
		else {
			throw new RuntimeException("For the given serviceInterface ("  //$NON-NLS-1$
					+ serviceInterface.getCanonicalName() 
					+ "), no service implementation is known."); //$NON-NLS-1$
		}
	}

}
