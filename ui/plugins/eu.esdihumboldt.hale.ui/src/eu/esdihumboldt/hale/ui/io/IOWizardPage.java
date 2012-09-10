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

import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
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

}
