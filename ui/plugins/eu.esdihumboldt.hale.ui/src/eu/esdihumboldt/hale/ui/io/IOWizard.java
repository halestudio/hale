/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactoryCollection;
import de.fhg.igd.eclipse.util.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FilesIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.action.ActionUI;
import eu.esdihumboldt.hale.ui.io.action.ActionUIExtension;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.config.ConfigurationPageExtension;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Abstract I/O wizard based on {@link IOProvider} descriptors
 * 
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class IOWizard<P extends IOProvider> extends Wizard
		implements IPageChangingListener {

	private static final ALogger log = ALoggerFactory.getLogger(IOWizard.class);

	private final Set<IOWizardListener<P, ? extends IOWizard<P>>> listeners = new HashSet<IOWizardListener<P, ? extends IOWizard<P>>>();

	private final Class<P> providerType;

	private P provider;

	private IOProviderDescriptor descriptor;

	private IOAdvisor<P> advisor;

	private String actionId;

	private IContentType contentType;

	private Multimap<String, AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> configPages;

	private final List<IWizardPage> mainPages = new ArrayList<IWizardPage>();

	private Queue<URI> usedLocations = null;

	/**
	 * Create an I/O wizard
	 * 
	 * @param providerType the I/O provider type
	 */
	public IOWizard(Class<P> providerType) {
		super();
		this.providerType = providerType;

		// create possible configuration pages
		configPages = createConfigurationPages(getFactories());

		setNeedsProgressMonitor(true);
	}

	/**
	 * Get the I/O advisor
	 * 
	 * @return the advisor
	 */
	protected IOAdvisor<P> getAdvisor() {
		return advisor;
	}

	/**
	 * Get the action identifier
	 * 
	 * @return the action ID
	 */
	public String getActionId() {
		return actionId;
	}

	/**
	 * Set the I/O advisor
	 * 
	 * @param advisor the advisor to set
	 * @param actionId the action identifier, <code>null</code> if it has none
	 */
	public void setAdvisor(IOAdvisor<P> advisor, String actionId) {
		this.advisor = advisor;
		this.actionId = actionId;

		// recreate possible configuration pages now that advisor is set
		configPages = createConfigurationPages(getFactories());
	}

	/**
	 * Get the provider IDs mapped to configuration pages.
	 * 
	 * @param factories the provider descriptors
	 * @return provider IDs mapped to configuration pages
	 */
	protected ListMultimap<String, AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> createConfigurationPages(
			Collection<IOProviderDescriptor> factories) {
		return ConfigurationPageExtension.getInstance().getConfigurationPages(getFactories());
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		// add configuration pages
		for (AbstractConfigurationPage<? extends P, ? extends IOWizard<P>> page : new HashSet<>(
				configPages.values())) {
			addPage(page);
		}

		if (getContainer() instanceof WizardDialog) {
			((WizardDialog) getContainer()).addPageChangingListener(this);
		}
		else {
			throw new RuntimeException("Only WizardDialog as container supported");
		}
	}

	/**
	 * @see IPageChangingListener#handlePageChanging(PageChangingEvent)
	 */
	@Override
	public void handlePageChanging(PageChangingEvent event) {
		if (getProvider() == null) {
			return;
		}

		if (event.getCurrentPage() instanceof IWizardPage
				&& event.getTargetPage() == getNextPage((IWizardPage) event.getCurrentPage())) {
			// only do automatic configuration when proceeding to next page
			if (event.getCurrentPage() instanceof IOWizardPage<?, ?>) {
				@SuppressWarnings("unchecked")
				IOWizardPage<P, ?> page = (IOWizardPage<P, ?>) event.getCurrentPage();
				event.doit = validatePage(page);
				// TODO error message?!
			}
		}
	}

	/**
	 * @see Wizard#dispose()
	 */
	@Override
	public void dispose() {
		if (getContainer() instanceof WizardDialog) {
			((WizardDialog) getContainer()).removePageChangingListener(this);
		}

		super.dispose();
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
	protected List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> getConfigurationPages() {
		if (descriptor == null) {
			return null;
		}

		// get the provider id
		String id = descriptor.getIdentifier();

		List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> result = new ArrayList<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>>(
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
		List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> confPages = getConfigurationPages();
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
				List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> confPages = getConfigurationPages();
				if (confPages != null && confPages.size() > 0) {
					return confPages.get(0);
				}
			}
		}
		else {
			// current page is a configuration page
			List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> confPages = getConfigurationPages();
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
		List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> confPages = getConfigurationPages();
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
			List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> confPages = getConfigurationPages();
			if (confPages != null) {
				if (confPages.size() > 0 && confPages.get(0) == page) {
					if (mainPages.isEmpty()) {
						return null;
					}
					else {
						// return last main page
						return mainPages.get(mainPages.size() - 1);
					}
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
		if (!mainPages.isEmpty()) {
			return mainPages.get(0);
		}
		else {
			List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> cps = getConfigurationPages();
			if (cps != null && !cps.isEmpty()) {
				return cps.get(0);
			}
			else {
				// TODO provide an empty completed page instead?
				throw new IllegalStateException("No starting page to display for wizard");
			}
		}
	}

	/**
	 * Get the available provider descriptors. To filter or sort them you can
	 * override this method.
	 * 
	 * @return the available provider descriptors
	 */
	public List<IOProviderDescriptor> getFactories() {
		// FIXME rename method
		return IOProviderExtension.getInstance()
				.getFactories(new FactoryFilter<IOProvider, IOProviderDescriptor>() {

					@Override
					public boolean acceptFactory(IOProviderDescriptor factory) {
						// accept all factories that provide a compatible I/O
						// provider
						return providerType.isAssignableFrom(factory.getProviderType());
					}

					@Override
					public boolean acceptCollection(
							ExtensionObjectFactoryCollection<IOProvider, IOProviderDescriptor> collection) {
						return true;
					}
				});
	}

	/**
	 * Get the provider assigned to the wizard. It will be <code>null</code> if
	 * no page assigned a provider factory to the wizard yet.
	 * 
	 * @return the I/O provider
	 */
	@SuppressWarnings("unchecked")
	public P getProvider() {
		if (provider == null && descriptor != null) {
			try {
				provider = (P) descriptor.createExtensionObject();
			} catch (Exception e) {
				throw new IllegalStateException("Could not instantiate I/O provider", e);
			}
			advisor.prepareProvider(provider);
		}

		return provider;
	}

	/**
	 * Assign an I/O provider factory to the wizard
	 * 
	 * @param descriptor the provider factory to set
	 */
	public void setProviderFactory(IOProviderDescriptor descriptor) {
		/*
		 * The following must be done even if the descriptor seems the same, as
		 * the descriptor might be a preset and thus influence the configuration
		 * pages.
		 */

		// disable old configuration pages
		List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> pages = getConfigurationPages();
		if (pages != null) {
			for (AbstractConfigurationPage<? extends P, ? extends IOWizard<P>> page : pages) {
				page.disable();
			}
		}

		this.descriptor = descriptor;

		// reset provider
		provider = null;

		// enable new configuration pages
		pages = getConfigurationPages();
		if (pages != null) {
			for (AbstractConfigurationPage<? extends P, ? extends IOWizard<P>> page : pages) {
				page.enable();
			}
		}

		// force button update
		try {
			getContainer().updateButtons();
		} catch (NullPointerException e) {
			// ignore - buttons may not have been initialized yet
		}

		fireProviderFactoryChanged(descriptor);
	}

	/**
	 * Get the content type assigned to the wizard
	 * 
	 * @return the content type, may be <code>null</code>
	 */
	public IContentType getContentType() {
		return contentType;
	}

	/**
	 * Assign a content type to the wizard
	 * 
	 * @param contentType the content type to set
	 */
	public void setContentType(IContentType contentType) {
		if (Objects.equal(contentType, this.contentType))
			return;

		this.contentType = contentType;

		fireContentTypeChanged(contentType);
	}

	/**
	 * Get the provider descriptor assigned to the wizard. It will be
	 * <code>null</code> if no page assigned a provider factory to the wizard
	 * yet.
	 * 
	 * @return the I/O provider factory
	 */
	public IOProviderDescriptor getProviderFactory() {
		return descriptor;
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

		if (!applyConfiguration()) {
			return false;
		}

		if (initializeUsedLocations() == null) {
			// could not find any URIs in the provider.
			return false;
		}

		URI uriLoc = usedLocations.poll();
		while (uriLoc != null) {

			// If multiple files were selected then for every file
			// initialize/reset FileIOSupplier in the provider. For the first
			// loop it will be FilesIOSupplier (or FileIOSupplier when single
			// file is selected) and from the second loop onwards
			// FileIOSupplier
			if (provider instanceof ImportProvider
					&& (((ImportProvider) provider).getSource() instanceof FilesIOSupplier
							|| ((ImportProvider) provider).getSource() instanceof FileIOSupplier)) {
				// always set FileIOSupplier to avoid huge impacts.
				((ImportProvider) provider).setSource(new FileIOSupplier(new File(uriLoc)));
			}
			// else it is a non-file source and proceed with the code without
			// updating the provider.

			IOReporter defReport = provider.createReporter();

			// validate and execute provider
			try {
				// validate configuration
				provider.validate();

				ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
				URI projectLoc = ps.getLoadLocation() == null ? null : ps.getLoadLocation();
				boolean isProjectResource = false;
				if (actionId != null) {
					// XXX instead move project resource to action?
					ActionUI factory = ActionUIExtension.getInstance().findActionUI(actionId);
					isProjectResource = factory.isProjectResource();
				}

				// prevent loading of duplicate resources
				if (isProjectResource && provider instanceof ImportProvider
						&& !getProviderFactory().allowDuplicateResource()) {

					String currentResource = ((ImportProvider) provider).getSource().getLocation()
							.toString();
					URI currentAbsolute = URI.create(currentResource);

					if (projectLoc != null && !currentAbsolute.isAbsolute()) {
						currentAbsolute = projectLoc.resolve(currentAbsolute);
					}

					for (IOConfiguration conf : ((Project) ps.getProjectInfo()).getResources()) {
						Value otherResourceValue = conf.getProviderConfiguration()
								.get(ImportProvider.PARAM_SOURCE);
						if (otherResourceValue == null) {
							continue;
						}

						String otherResource = otherResourceValue.as(String.class);
						URI otherAbsolute = URI.create(otherResource);
						if (projectLoc != null && !otherAbsolute.isAbsolute()) {
							otherAbsolute = projectLoc.resolve(otherAbsolute);
						}
						String action = conf.getActionId();
						// resource is already loaded into the project
						if (currentAbsolute.equals(otherAbsolute)
								&& Objects.equal(actionId, action)) {
							// check if the resource is loaded with a provider
							// that allows duplicates
							boolean allowDuplicate = false;
							IOProviderDescriptor providerFactory = IOProviderExtension.getInstance()
									.getFactory(conf.getProviderId());
							if (providerFactory != null) {
								allowDuplicate = providerFactory.allowDuplicateResource();
							}

							if (!allowDuplicate) {
								log.userError(
										"Resource is already loaded. Loading duplicate resources is aborted!");
								// should not proceed with the loop else
								// duplicate resource will be uploaded.
								return false;
							}
						}
					}
				}

				// enable provider internal caching
				if (isProjectResource && provider instanceof CachingImportProvider) {
					((CachingImportProvider) provider).setProvideCache();
				}
				IOReport report = execute(provider, defReport);

				if (report != null) {
					// add report to report server
					ReportService repService = PlatformUI.getWorkbench()
							.getService(ReportService.class);
					repService.addReport(report);

					// show message to user
					if (report.isSuccess()) {
						// no message, we rely on the report being
						// shown/processed

						// let advisor handle results
						try {
							getContainer().run(true, false, new IRunnableWithProgress() {

								@Override
								public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
									monitor.beginTask("Completing operation...",
											IProgressMonitor.UNKNOWN);
									try {
										advisor.handleResults(getProvider());
									} finally {
										monitor.done();
									}
								}

							});
						} catch (InvocationTargetException e) {
							log.userError("Error processing results:\n"
									+ e.getCause().getLocalizedMessage(), e.getCause());
							return false;
						} catch (Exception e) {
							log.userError("Error processing results:\n" + e.getLocalizedMessage(),
									e);
							return false;
						}

						// add to project service if necessary
						if (isProjectResource)
							ps.rememberIO(actionId, getProviderFactory().getIdentifier(), provider);

					}
					else {
						// error message
						log.userError(report.getSummary() + "\nPlease see the report for details.");
						return false;
					}
				}
			} catch (IOProviderConfigurationException e) {
				// user feedback
				log.userError("Validation of the provider configuration failed:\n"
						+ e.getLocalizedMessage(), e);
				return false;
			}
			uriLoc = usedLocations.poll();
		}
		return true;
	}

	/**
	 * Apply configuration of main pages, configuration pages and the wizard.
	 * 
	 * @return <code>true</code> if validation was successful,
	 *         <code>false</code> otherwise
	 */
	protected boolean applyConfiguration() {
		// process main pages
		for (int i = 0; i < mainPages.size(); i++) {
			// validating is still necessary as it is not guaranteed to be up to
			// date by handlePageChanging
			boolean valid = validatePage(mainPages.get(i));
			if (!valid) {
				// TODO error message?!
				return false;
			}
		}

		// check if configuration pages are complete
		List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> confPages = getConfigurationPages();
		if (confPages != null) {
			for (int i = 0; i < confPages.size(); i++) {
				// validating is still necessary as it is not guaranteed to be
				// up to date by handlePageChanging
				boolean valid = validatePage(confPages.get(i));
				if (!valid) {
					// TODO error message?!
					return false;
				}
			}
		}

		// process wizard
		updateConfiguration(provider);

		return true;
	}

	/**
	 * Execute the given provider
	 * 
	 * @param provider the I/O provider
	 * @param defaultReporter the default reporter that is used if the provider
	 *            doesn't supply a report
	 * @return the execution report, if null it will not give feedback to the
	 *         user and the advisor's handleResult method won't be called either
	 */
	protected IOReport execute(final IOProvider provider, final IOReporter defaultReporter) {
		// execute provider
		final AtomicReference<IOReport> report = new AtomicReference<IOReport>(defaultReporter);
		defaultReporter.setSuccess(false);
		try {
			getContainer().run(true, provider.isCancelable(), new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					ATransaction trans = log.begin(defaultReporter.getTaskName());
					try {

						IOReport result = null;
						if (((ImportProvider) provider).getResourceIdentifier() != null) {
							result = ((ImportProvider) provider)
									.execute(new ProgressMonitorIndicator(monitor), null);
						}
						else {

							result = provider.execute(new ProgressMonitorIndicator(monitor));
						}
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
		if (page instanceof IOWizardPage<?, ?>) {
			return ((IOWizardPage<P, ?>) page).updateConfiguration(getProvider());
		}
		else {
			return true;
		}
	}

	/**
	 * Get the supported I/O provider type, usually an interface.
	 * 
	 * @return the supported I/O provider type
	 */
	public Class<P> getProviderType() {
		return providerType;
	}

	/**
	 * Adds an {@link IOWizardListener}
	 * 
	 * @param listener the listener to add
	 */
	public void addIOWizardListener(IOWizardListener<P, ? extends IOWizard<P>> listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes an {@link IOWizardListener}
	 * 
	 * @param listener the listener to remove
	 */
	public void removeIOWizardListener(IOWizardListener<P, ? extends IOWizard<P>> listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	private void fireProviderFactoryChanged(IOProviderDescriptor providerFactory) {
		synchronized (listeners) {
			for (IOWizardListener<P, ? extends IOWizard<P>> listener : listeners) {
				listener.providerDescriptorChanged(providerFactory);
			}
		}
	}

	private void fireContentTypeChanged(IContentType contentType) {
		synchronized (listeners) {
			for (IOWizardListener<P, ? extends IOWizard<P>> listener : listeners) {
				listener.contentTypeChanged(contentType);
			}
		}
	}

	/**
	 * Method to initialize used locations. It initializes usedLocations as a
	 * list in case multiple files were selected otherwise as a single resource
	 * in case of non-file source like HTTP/HTTPS or JDBC URIs.
	 * 
	 * @return the usedLocations
	 */
	private Queue<URI> initializeUsedLocations() {
		List<URI> uris = new ArrayList<>();
		if (provider instanceof ImportProvider) {
			LocatableInputSupplier<? extends InputStream> source = ((ImportProvider) getProvider())
					.getSource();
			if (source instanceof FilesIOSupplier) {
				uris = ((FilesIOSupplier) source).getUsedLocations();
			}
			else {
				// non-file locations like HTTP/HTTPS or JDBC URIs or when a
				// single file is selected.
				URI location = ((ImportProvider) getProvider()).getSource().getLocation();
				uris = Arrays.asList(location);
			}

			if (usedLocations == null) {
				usedLocations = new LinkedList<>(uris);
			}
		}
		return usedLocations;
	}

}
