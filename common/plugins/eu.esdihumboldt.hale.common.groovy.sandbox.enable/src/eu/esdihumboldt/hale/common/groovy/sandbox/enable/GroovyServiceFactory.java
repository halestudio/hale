package eu.esdihumboldt.hale.common.groovy.sandbox.enable;

import eu.esdihumboldt.hale.common.core.service.ServiceFactory;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.util.groovy.sandbox.DefaultGroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;

/**
 * Service factory for GroovyService.
 * 
 * @author Kai Schwierczek
 */
public class GroovyServiceFactory implements ServiceFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createService(Class<T> serviceInterface, ServiceProvider serviceLocator) {
		if (serviceInterface.equals(GroovyService.class)) {
			return (T) new DefaultGroovyService();
		}

		return null;
	}

}
