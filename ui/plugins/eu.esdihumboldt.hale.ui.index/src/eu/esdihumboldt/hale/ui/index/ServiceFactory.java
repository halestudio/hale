package eu.esdihumboldt.hale.ui.index;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.index.internal.InstanceIndexUpdateServiceImpl;

/**
 * Service factory for the instance index update service.
 * 
 * @author Florian Esser
 */
public class ServiceFactory extends AbstractServiceFactory {

	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		if (InstanceIndexUpdateService.class.equals(serviceInterface)) {
			return new InstanceIndexUpdateServiceImpl(HaleUI.getServiceProvider());
		}

		return null;
	}

}
