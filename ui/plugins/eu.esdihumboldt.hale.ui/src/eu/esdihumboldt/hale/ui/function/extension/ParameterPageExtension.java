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

package eu.esdihumboldt.hale.ui.function.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.hale.ui.function.extension.impl.ParameterPageFactoryImpl;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * {@link ParameterPageFactory} extension.
 * 
 * @author Kai Schwierczek
 */
public class ParameterPageExtension extends AbstractExtension<ParameterPage, ParameterPageFactory> {

	private static ParameterPageExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the parameter page extension
	 */
	public static ParameterPageExtension getInstance() {
		if (instance == null) {
			instance = new ParameterPageExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	public ParameterPageExtension() {
		super(FunctionWizardExtension.ID);
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ParameterPageFactory createFactory(IConfigurationElement conf) throws Exception {
		if (conf.getName().equals("propertyParameterPage")
				|| conf.getName().equals("typeParameterPage"))
			return new ParameterPageFactoryImpl(conf);

		return null;
	}
}
