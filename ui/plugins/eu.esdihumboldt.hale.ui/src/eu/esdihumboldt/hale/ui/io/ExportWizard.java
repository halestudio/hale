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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptorDecorator;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Abstract export wizard
 * 
 * @param
 * 			<P>
 *            the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class ExportWizard<P extends ExportProvider> extends IOWizard<P> {

	/**
	 * I/O provider descriptor marked as preset.
	 */
	private static class PresetDescriptor extends IOProviderDescriptorDecorator {

		private final String name;

		/**
		 * @param descriptor the decoratee
		 * @param name the preset name
		 */
		public PresetDescriptor(IOProviderDescriptor descriptor, String name) {
			super(descriptor);
			this.name = name;
		}

		/**
		 * @return the preset name
		 */
		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

	}

	private ExportSelectTargetPage<P, ? extends ExportWizard<P>> selectTargetPage;

	private final ProjectService projectService;

	/**
	 * @see IOWizard#IOWizard(Class)
	 */
	public ExportWizard(Class<P> providerType) {
		super(providerType);

		setWindowTitle("Export wizard");

		setDefaultPageImageDescriptor(HALEUIPlugin.imageDescriptorFromPlugin(HALEUIPlugin.PLUGIN_ID,
				"/icons/banner/export_wiz.png"));

		projectService = PlatformUI.getWorkbench().getService(ProjectService.class);
	}

	/**
	 * @return the names of presets applicable for the provider type
	 */
	public Collection<String> getPresets() {
		return projectService.getExportConfigurationNames(getProviderType());
	}

	/**
	 * Set the given preset.
	 * 
	 * @param preset the preset name
	 * @return if the preset was successfully set
	 */
	public boolean setPreset(String preset) {
		IOConfiguration config = projectService.getExportConfiguration(preset);
		if (config != null) {
			IOProviderDescriptor descriptor = IOProviderExtension.getInstance()
					.getFactory(config.getProviderId());
			if (descriptor != null) {
				descriptor = new PresetDescriptor(descriptor, preset);
				setProviderFactory(descriptor);
				getProvider().loadConfiguration(config.getProviderConfiguration());
				setContentType(getProvider().getContentType());
				return true;
			}
		}

		setProviderFactory(null);
		return false;
	}

	@Override
	protected List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> getConfigurationPages() {
		List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> pages = super.getConfigurationPages();
		if (pages == null) {
			return null;
		}

		if (getProviderFactory() instanceof PresetDescriptor) {
			// only accept the target page as configuration page
			// because the other configuration is already provided
			for (AbstractConfigurationPage<? extends P, ? extends IOWizard<P>> page : pages) {
				if (page == getSelectTargetPage()) {
					List<AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> result = new ArrayList<>();
					result.add(page);
					return result;
				}
			}
			return null;
		}
		else {
			// leave pages untouched
			return pages;
		}
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		if (getFactories().size() == 1 && getPresets().isEmpty()) {
			// only one possibility, directly set the provider
			setProviderFactory(getFactories().iterator().next());
		}
		else {
			addPage(createSelectProviderPage());
		}
	}

	@Override
	protected ListMultimap<String, AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> createConfigurationPages(
			Collection<IOProviderDescriptor> factories) {
		ListMultimap<String, AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> configPages = super.createConfigurationPages(
				factories);

		ListMultimap<String, AbstractConfigurationPage<? extends P, ? extends IOWizard<P>>> result = ArrayListMultimap
				.create();

		// append target selection page if applicable
		for (IOProviderDescriptor descr : factories) {
			if (!suppressTargetPage() && descr.getSupportedTypes() != null
					&& !descr.getSupportedTypes().isEmpty()) {
				result.put(descr.getIdentifier(), getSelectTargetPage());
			}
		}

		// append all other configuration pages
		result.putAll(configPages);

		return result;
	}

	/**
	 * @return if the target page should be suppressed
	 */
	protected boolean suppressTargetPage() {
		return false;
	}

	/**
	 * Create the page where the provider is selected
	 * 
	 * @return the created page
	 */
	protected ExportSelectProviderPage<P, ? extends ExportWizard<P>> createSelectProviderPage() {
		return new ExportSelectProviderPage<P, ExportWizard<P>>();
	}

	/**
	 * Create the page where the target (e.g. a file) is selected
	 * 
	 * @return the created page
	 */
	protected ExportSelectTargetPage<P, ? extends ExportWizard<P>> createSelectTargetPage() {
		return new ExportSelectTargetPage<P, ExportWizard<P>>();
	}

	/**
	 * @return the selectTargetPage
	 */
	protected ExportSelectTargetPage<P, ? extends ExportWizard<P>> getSelectTargetPage() {
		if (selectTargetPage == null) {
			selectTargetPage = createSelectTargetPage();
		}
		return selectTargetPage;
	}

	/**
	 * @return the project service
	 */
	protected ProjectService getProjectService() {
		return projectService;
	}

}
