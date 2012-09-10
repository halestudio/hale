/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * Abstract WFS wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @param <T> the WFS configuration type
 */
public abstract class AbstractWfsWizard<T extends WfsConfiguration> extends Wizard {

	/**
	 * The WFS configuration
	 */
	protected final T configuration;

	/**
	 * The capabilities page
	 */
	private CapabilitiesPage capabilities;

	/**
	 * The types page
	 */
	private AbstractTypesPage<? super T> types;

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS client configuration
	 */
	public AbstractWfsWizard(T configuration) {
		this.configuration = configuration;

		setNeedsProgressMonitor(true);
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(capabilities = new CapabilitiesPage(configuration));
		addPage(types = new FeatureTypesPage(configuration, capabilities));
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean performFinish() {
		for (IWizardPage page : getPages()) {
			boolean valid = ((AbstractWfsPage<T>) page).updateConfiguration(configuration);
			if (!valid) {
				return false;
			}
		}

		boolean success = configuration.validateSettings();

		if (success) {
			capabilities.updateRecent();
		}

		return success;
	}

	/**
	 * @return the capabilities
	 */
	public CapabilitiesPage getCapabilities() {
		return capabilities;
	}

	/**
	 * @return the types
	 */
	public AbstractTypesPage<? super T> getTypes() {
		return types;
	}

}
