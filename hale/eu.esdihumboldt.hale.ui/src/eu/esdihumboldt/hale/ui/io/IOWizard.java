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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

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
	 * 
	 * @return <code>true</code> if executing the I/O provider was successful
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
		
		// create default report
		IOReporter defReport = provider.createReporter();
		
		// validate and execute provider
		try {
			IOReport report = validateAndExecute(provider, defReport);
			// add report to report server
			ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
			repService.addReport(report);
			// show message to user
			if (report.isSuccess()) {
				// no message, we rely on the report being shown/processed
				return true;
			}
			else {
				// error message
				log.userError(report.getSummary());
				return false;
			}
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
	 * @param defaultReporter the default reporter that is used if the provider
	 *   doesn't supply a report
	 * @return the execution report
	 * @throws IOProviderConfigurationException if the provider validation failed
	 */
	protected IOReport validateAndExecute(final IOProvider provider,
			final IOReporter defaultReporter) throws IOProviderConfigurationException {
		// validate configuration
		provider.validate();
		
		// execute provider
		final AtomicReference<IOReport> report = new AtomicReference<IOReport>(defaultReporter);
		defaultReporter.setSuccess(false);
		try {
			getContainer().run(true, provider.isCancelable(), new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					try {
						IOReport result = provider.execute(new ProgressMonitorIndicator(monitor));
						if (result != null) {
							report.set(result);
						}
						else {
							defaultReporter.setSuccess(true);
						}
					} catch (Throwable e) {
						defaultReporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
					}
				}
			});
		} catch (Throwable e) {
			defaultReporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
		}
		
		return report.get();
	}

	/**
	 * Update the provider configuration
	 * 
	 * @param provider the I/O provider 
	 */
	protected void updateConfiguration(P provider) {
		// set the content type
		provider.setContentType(getContentType());
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
