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

package eu.esdihumboldt.hale.ui.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;

/**
 * Abstract I/O wizard based on {@link IOProvider} factories
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class IOWizard<P extends IOProvider, T extends IOProviderFactory<P>> extends Wizard {
	
	private static final ALogger log = ALoggerFactory.getLogger(IOWizard.class);

	private final Class<T> factoryClass;
	
	private P provider;
	
	private T factory;

	/**
	 * Create an I/O wizard
	 * 
	 * @param factoryClass the class of the I/O provider factory
	 */
	public IOWizard(Class<T> factoryClass) {
		super();
		this.factoryClass = factoryClass;
		
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Get the available factories. 
	 * To filter or sort them you can override this method.
	 * 
	 * @return the available factories
	 */
	public Collection<T> getFactories() {
		return OsgiUtils.getServices(factoryClass);
	}

	/**
	 * Get the provider assigned to the wizard. It will be <code>null</code> if
	 * no page assigned a provider factory to the wizard yet.
	 * 
	 * @return the I/O provider
	 */
	public P getProvider() {
		if (provider == null && factory != null) {
			provider = factory.createProvider();
		}
		
		return provider;
	}

	/**
	 * Assign an I/O provider factory to the wizard
	 * 
	 * @param factory the provider to set
	 */
	public void setProviderFactory(T factory) {
		this.factory = factory;
		
		// reset provider
		provider = null;
	}
	
	/**
	 * Get the provider factory assigned to the wizard. It will be
	 * <code>null</code> if no page assigned a provider factory to the wizard 
	 * yet.
	 * 
	 * @return the I/O provider factory
	 */
	public T getProviderFactory() {
		return factory;
	}
	
	/**
	 * @see Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		if (getProvider() == null) {
			return false;
		}
		
		// process pages
		for (IWizardPage page : getPages()) {
			boolean valid = validatePage(page);
			if (!valid) {
				return false;
			}
		}
		
		// process wizard
		updateConfiguration(provider);
		
		// validate configuration
		try {
			provider.validate();
		} catch (IOProviderConfigurationException e) {
			//TODO user feedback? details of configuration error as wizard message? how?
			log.error("Validation of the provider configuration failed", e);
			return false;
		}
		
		// execute provider
		final AtomicBoolean success = new AtomicBoolean(false);
		try {
			getContainer().run(true, provider.isCancelable(), new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					try {
						provider.execute(new ProgressMonitorIndicator(monitor));
						success.set(true);
					} catch (IOProviderConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return success.get();
	}

	/**
	 * Update the provider configuration
	 * 
	 * @param provider the I/O provider 
	 */
	protected void updateConfiguration(P provider) {
		// do nothing
	}

	/**
	 * Validate the given page and update the I/O provider
	 * 
	 * @param page the wizard page to validate
	 * @return if the page is valid and updating the I/O provider was successful
	 */
	@SuppressWarnings("unchecked")
	protected boolean validatePage(IWizardPage page) {
		if (page instanceof IOWizardPage<?, ?, ?>) {
			return ((IOWizardPage<P, ? extends IOProviderFactory<P>, ?>) page).updateConfiguration(provider);
		}
		else {
			return true;
		}
	}

}
