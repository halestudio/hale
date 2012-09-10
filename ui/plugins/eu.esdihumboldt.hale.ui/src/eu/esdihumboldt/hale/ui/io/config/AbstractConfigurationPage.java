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
