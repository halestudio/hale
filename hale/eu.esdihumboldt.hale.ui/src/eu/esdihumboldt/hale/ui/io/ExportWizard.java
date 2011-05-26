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

import eu.esdihumboldt.hale.core.io.ExportProvider;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

/**
 * Abstract export wizard
 * @param <P> the {@link IOProvider} type used in the wizard
 * @param <T> the {@link IOProviderFactory} type used in the wizard
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class ExportWizard<P extends ExportProvider, T extends IOProviderFactory<P>> extends IOWizard<P, T> {

	private ExportSelectProviderPage<P, T, ? extends ExportWizard<P, T>> selectProviderPage;
	
	private ExportSelectTargetPage<P, T, ? extends ExportWizard<P, T>> selectTargetPage;

	/**
	 * @see IOWizard#IOWizard(Class)
	 */
	public ExportWizard(Class<T> factoryClass) {
		super(factoryClass);
		
		setDefaultPageImageDescriptor(HALEUIPlugin.imageDescriptorFromPlugin(
				HALEUIPlugin.PLUGIN_ID, "/icons/banner/export_wiz.png"));
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		addPage(selectProviderPage = createSelectProviderPage());
		addPage(selectTargetPage = createSelectTargetPage());
	}

	/**
	 * Create the page where the provider is selected
	 * 
	 * @return the created page
	 */
	protected ExportSelectProviderPage<P, T, ? extends ExportWizard<P,T>> createSelectProviderPage() {
		return new ExportSelectProviderPage<P, T, ExportWizard<P,T>>();
	}
	
	/**
	 * Create the page where the provider is selected
	 * 
	 * @return the created page
	 */
	protected ExportSelectTargetPage<P, T, ? extends ExportWizard<P,T>> createSelectTargetPage() {
		return new ExportSelectTargetPage<P, T, ExportWizard<P,T>>();
	}

	/**
	 * @return the selectProviderPage
	 */
	protected ExportSelectProviderPage<P, T, ? extends ExportWizard<P, T>> getSelectProviderPage() {
		return selectProviderPage;
	}

	/**
	 * @return the selectTargetPage
	 */
	protected ExportSelectTargetPage<P, T, ? extends ExportWizard<P, T>> getSelectTargetPage() {
		return selectTargetPage;
	}

}
