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

package eu.esdihumboldt.hale.ui.io.config;

import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;

/**
 * Base type for I/O configuration wizard pages. A configuration page can either
 * be enabled or disabled, when created it is disabled.
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractConfigurationPage<P extends IOProvider, W extends IOWizard<P>>
		extends IOWizardPage<P, W> {

	/**
	 * @see IOWizardPage#IOWizardPage(String, String, ImageDescriptor)
	 */
	protected AbstractConfigurationPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see IOWizardPage#IOWizardPage(String)
	 */
	protected AbstractConfigurationPage(String pageName) {
		super(pageName);
	}

	/**
	 * Enable the configuration page
	 */
	public abstract void enable();

	/**
	 * Disable the configuration page
	 */
	public abstract void disable();

}
