package eu.esdihumboldt.hale.rcp.views.report.service;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

public class ServiceFactory extends AbstractServiceFactory {

	private ReportServiceImpl instance = null;
	
	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator, IServiceLocator)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object create(Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {
		
		if (serviceInterface.equals(ReportService.class)) {
			if (instance == null) {
				instance = new ReportServiceImpl();
			}
			return instance;
		}
		
		return null;
	}

}
