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

package eu.esdihumboldt.hale.ui.util.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * Base class for wizards adapting a configuration object.
 * 
 * @author Simon Templer
 * @param <T> the configuration type
 */
public abstract class ConfigurationWizard<T> extends Wizard {

	private final T configuration;

	/**
	 * Constructor.
	 * 
	 * @param configuration the initial configuration
	 */
	public ConfigurationWizard(T configuration) {
		this.configuration = configuration;
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean performFinish() {
		for (IWizardPage page : getPages()) {
			boolean valid = ((ConfigurationWizardPage<? super T>) page)
					.updateConfiguration(configuration);
			if (!valid) {
				return false;
			}
		}

		boolean success = validate(configuration);

		return success;
	}

	/**
	 * Validate the configuration object.
	 * 
	 * @param configuration the configuration object
	 * @return <code>true</code> if the configuration may be accepted
	 */
	protected abstract boolean validate(T configuration);

	/**
	 * @return the configuration
	 */
	public T getConfiguration() {
		return configuration;
	}

}
