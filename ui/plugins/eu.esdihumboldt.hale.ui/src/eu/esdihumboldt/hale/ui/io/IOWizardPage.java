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

import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Abstract I/O wizard page
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class IOWizardPage<P extends IOProvider, W extends IOWizard<P>> extends
		HaleWizardPage<W> {

	/**
	 * @see HaleWizardPage#HaleWizardPage(String, String, ImageDescriptor)
	 */
	protected IOWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see HaleWizardPage#HaleWizardPage(String)
	 */
	protected IOWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * Update the configuration (of the I/O provider)
	 * 
	 * @param provider the I/O provider to update
	 * @return if the page is valid and updating the provider was successful
	 */
	public abstract boolean updateConfiguration(P provider);

	/**
	 * Load a preselection into the wizard page (eg. load a text into the
	 * textfield, or select an item in the combo viewer). It will not work, if
	 * it is not implemented by the used wizard page<br>
	 * <b>Currently not used</b>
	 * 
	 * @param conf the configuration which should be loaded
	 */
	public void loadPreSelection(IOConfiguration conf) {
		// has to be implemented by upper classes
	}

}
