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

import org.eclipse.jface.wizard.Wizard;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

/**
 * Abstract export wizard
 * 
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class ExportWizard<P extends ExportProvider> extends IOWizard<P> {

	private ExportSelectTargetPage<P, ? extends ExportWizard<P>> selectTargetPage;

	/**
	 * @see IOWizard#IOWizard(Class)
	 */
	public ExportWizard(Class<P> providerType) {
		super(providerType);

		setWindowTitle("Export wizard");

		setDefaultPageImageDescriptor(HALEUIPlugin.imageDescriptorFromPlugin(
				HALEUIPlugin.PLUGIN_ID, "/icons/banner/export_wiz.png"));
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		if (getFactories().size() == 1) {
			// only one possibility, directly set the provider
			setProviderFactory(getFactories().iterator().next());
		}
		else {
			addPage(new ExportSelectProviderPage<P, ExportWizard<P>>());
		}
		addPage(selectTargetPage = createSelectTargetPage());
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
	 * Create the page where the provider is selected
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
		return selectTargetPage;
	}

}
