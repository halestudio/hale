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

package eu.esdihumboldt.hale.io.wfs.ui.legacy.wizard;

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
