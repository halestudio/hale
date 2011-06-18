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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.ui.io.advisor.IOAdvisorExtension;
import eu.esdihumboldt.hale.ui.io.advisor.IOAdvisorFactory;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.config.ConfigurationPageExtension;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
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
	
	private IOAdvisor<P> advisor;
	
	private String advisorId;
	
	private ContentType contentType;

	private final Multimap<String, AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> configPages;
	
	private final List<IWizardPage> mainPages = new ArrayList<IWizardPage>();

	/**
	 * Create an I/O wizard
	 * 
	 * @param factoryClass the class of the I/O provider factory
	 */
	public IOWizard(Class<T> factoryClass) {
		super();
		this.factoryClass = factoryClass;
		
		// create possible configuration pages
		configPages = ConfigurationPageExtension.getConfigurationPages(getFactories());
		
		setNeedsProgressMonitor(true);
	}

	/**
	 * Get the I/O advisor
	 * @return the advisor
	 */
	protected IOAdvisor<P> getAdvisor() {
		return advisor;
	}

	/**
	 * @return the advisor identifier
	 */
	protected String getAdvisorId() {
		return advisorId;
	}

	/**
	 * Set the I/O advisor
	 * @param advisor the advisor to set
	 * @param advisorId the advisor identifier, <code>null</code> if it has none
	 */
	public void setAdvisor(IOAdvisor<P> advisor, String advisorId) {
		this.advisor = advisor;
		this.advisorId = advisorId;
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		// add configuration pages
		for (AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>> page : configPages.values()) {
			addPage(page);
		}
	}

	/**
	 * @see Wizard#addPage(IWizardPage)
	 */
	@Override
	public void addPage(IWizardPage page) {
		// collect main pages
		if (!configPages.containsValue(page)) {
			mainPages.add(page);
		}
		
		super.addPage(page);
	}
	
	/**
	 * Get the list of configuration pages for the currently selected provider
	 * factory <code>null</code> if there are none.
	 * 
	 * @return the configuration pages for the current provider
	 */
	protected List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> getConfigurationPages() {
		if (factory == null) {
			return null;
		}

		// get the provider id
		String id = factory.getIdentifier();
		
		List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> result = 
			new ArrayList<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>>(
					configPages.get(id));
		
		return (result.size() > 0 ? result : null);
	}

	/**
	 * @see Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		// check if main pages are complete
		for (int i = 0; i < mainPages.size(); i++) {
            if (!(mainPages.get(i)).isPageComplete()) {
				return false;
			}
        }
		
		// check if configuration pages are complete
		List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> confPages = getConfigurationPages();
		if (confPages != null) {
			for (int i = 0; i < confPages.size(); i++) {
	            if (!(confPages.get(i)).isPageComplete()) {
					return false;
				}
	        }
		}
		
        return true;
	}

	/**
	 * @see Wizard#getNextPage(IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		// get main index
		int mainIndex = mainPages.indexOf(page);
		
		if (mainIndex >= 0) {
			// current page is one of the main pages
			if (mainIndex < mainPages.size() - 1) {
				// next main page
				return mainPages.get(mainIndex + 1); 
			}
			else {
				// first configuration page
				List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> confPages = getConfigurationPages();
				if (confPages != null && confPages.size() > 0) {
					return confPages.get(0);
				}
			}
		}
		else {
			// current page is a configuration page
			List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> confPages = getConfigurationPages();
			// return the next configuration page
			if (confPages != null) {
				for (int i = 0; i < confPages.size() - 1; ++i) {
					if (confPages.get(i) == page) {
						return confPages.get(i + 1);
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * @see Wizard#getPageCount()
	 */
	@Override
	public int getPageCount() {
		int count = mainPages.size();
		List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> confPages = getConfigurationPages();
		if (confPages != null) {
			count += confPages.size();
		}
		
		return count;
	}

	/**
	 * @see Wizard#getPreviousPage(IWizardPage)
	 */
	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		// get main index
		int mainIndex = mainPages.indexOf(page);
		
		if (mainIndex >= 0) {
			// current page is one of the main pages
			if (mainIndex > 0) {
				// previous main page
				return mainPages.get(mainIndex - 1); 
			}
		}
		else {
			// current page is a configuration page
			List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> confPages = getConfigurationPages();
			if (confPages != null) {
				if (confPages.size() > 0 && confPages.get(0) == page) {
					// return last main page
					return mainPages.get(mainPages.size() - 1);
				}
				// return the previous configuration page
				for (int i = 1; i < confPages.size(); ++i) {
					if (confPages.get(i) == page) {
						return confPages.get(i - 1);
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * @see Wizard#getStartingPage()
	 */
	@Override
	public IWizardPage getStartingPage() {
		return mainPages.get(0);
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
			advisor.prepareProvider(provider);
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
		
		// disable old configuration pages
		List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> pages = getConfigurationPages();
		if (pages != null) {
			for (AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>> page : pages) {
				page.disable();
			}
		}
		
		this.factory = factory;
		
		// reset provider
		provider = null;
		
		// enable new configuration pages
		pages = getConfigurationPages();
		if (pages != null) {
			for (AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>> page : pages) {
				page.enable();
			}
		}
		
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
		
		// process main pages
		for (int i = 0; i < mainPages.size(); i++) {
			boolean valid = validatePage(mainPages.get(i));
			if (!valid) {
				//TODO error message?!
				return false;
			}
        }
		
		// check if configuration pages are complete
		List<AbstractConfigurationPage<? extends P, ? extends T, ? extends IOWizard<P, T>>> confPages = getConfigurationPages();
		if (confPages != null) {
			for (int i = 0; i < confPages.size(); i++) {
				boolean valid = validatePage(confPages.get(i));
				if (!valid) {
					//TODO error message?!
					return false;
				}
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
				
				// let advisor handle results
				advisor.handleResults(getProvider());
				
				// add to project service if necessary
				if (advisorId != null) {
					IOAdvisorFactory factory = IOAdvisorExtension.getInstance().getFactory(advisorId);
					
					if (factory.isRemember()) {
						ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
						ps.rememberIO(factory, factoryClass, 
								getProviderFactory().getIdentifier(), provider);
					}
				}
				
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
					ATransaction trans = log.begin(defaultReporter.getTaskName());
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
					} finally {
						trans.end();
					}
				}
			});
		} catch (Throwable e) {
			defaultReporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
		}
		
		return report.get();
	}

	/**
	 * Update the provider configuration. This will be called just before the
	 * I/O provider is executed.
	 * 
	 * @param provider the I/O provider 
	 */
	protected void updateConfiguration(P provider) {
		// set the content type
		provider.setContentType(getContentType());
		
		// let advisor update configuration
		advisor.updateConfiguration(provider);
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
	 * @return the I/O provider factory class
	 */
	public Class<T> getFactoryClass() {
		return factoryClass;
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
