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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.google.common.base.Objects;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;

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

	private Set<IOWizardListener<P, T, ? extends IOWizard<P, T>>> listeners = new HashSet<IOWizardListener<P,T,? extends IOWizard<P,T>>>();
	
	private final Class<T> factoryClass;
	
	private P provider;
	
	private T factory;
	
	private ContentType contentType;

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
	 * @param factory the provider factory to set
	 */
	public void setProviderFactory(T factory) {
		if (Objects.equal(factory, this.factory)) return;
		
		this.factory = factory;
		
		// reset provider
		provider = null;
		
		fireProviderFactoryChanged(factory);
	}
	
	/**
	 * Get the content type assigned to the wizard
	 * 
	 * @return the content type, may be <code>null</code>
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * Assign a content type to the wizard
	 * 
	 * @param contentType the content type to set
	 */
	public void setContentType(ContentType contentType) {
		if (Objects.equal(contentType, this.contentType)) return;
		
		this.contentType = contentType;
		
		fireContentTypeChanged(contentType);
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
		
		// validate and execute provider
		try {
			return validateAndExecute(provider);
		} catch (IOProviderConfigurationException e) {
			//TODO user feedback? details of configuration error as wizard message? how?
			log.error("Validation of the provider configuration failed", e);
			return false;
		}
	}

	/**
	 * Validate and execute the given provider
	 * 
	 * @param provider the I/O provider
	 * @return if validation and execution were successful
	 * @throws IOProviderConfigurationException if the provider validation failed
	 */
	protected boolean validateAndExecute(final IOProvider provider) throws IOProviderConfigurationException {
		// validate configuration
		provider.validate();
		
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

	/**
	 * Adds an {@link IOWizardListener}
	 * 
	 * @param listener the listener to add
	 */
	public void addIOWizardListener(IOWizardListener<P, T, ? extends IOWizard<P, T>> listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Removes an {@link IOWizardListener}
	 * 
	 * @param listener the listener to remove
	 */
	public void removeIOWizardListener(IOWizardListener<P, T, ? extends IOWizard<P, T>> listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private void fireProviderFactoryChanged(T providerFactory) {
		synchronized (listeners) {
			for (IOWizardListener<P, T, ? extends IOWizard<P, T>> listener : listeners) {
				listener.providerFactoryChanged(providerFactory);
			}
		}
	}
	
	private void fireContentTypeChanged(ContentType contentType) {
		synchronized (listeners) {
			for (IOWizardListener<P, T, ? extends IOWizard<P, T>> listener : listeners) {
				listener.contentTypeChanged(contentType);
			}
		}
	}
	
}
