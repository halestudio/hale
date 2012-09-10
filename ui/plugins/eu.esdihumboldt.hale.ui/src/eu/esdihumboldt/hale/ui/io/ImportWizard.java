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

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;

/**
 * Abstract import wizard
 * 
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class ImportWizard<P extends ImportProvider> extends IOWizard<P> {

	private ImportSelectSourcePage<P, ? extends ImportWizard<P>> selectSourcePage;

	/**
	 * @see IOWizard#IOWizard(Class)
	 */
	public ImportWizard(Class<P> providerType) {
		super(providerType);

		setWindowTitle("Import wizard");

		setDefaultPageImageDescriptor(HALEUIPlugin.imageDescriptorFromPlugin(
				HALEUIPlugin.PLUGIN_ID, "/icons/banner/import_wiz.png"));
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		addPage(selectSourcePage = new ImportSelectSourcePage<P, ImportWizard<P>>());
	}

	/**
	 * @return the selectSourcePage
	 */
	public ImportSelectSourcePage<P, ? extends ImportWizard<P>> getSelectSourcePage() {
		return selectSourcePage;
	}

}
