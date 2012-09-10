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

package eu.esdihumboldt.hale.ui.io.instance;

import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Base class for {@link InstanceReader} configuration pages
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class InstanceReaderConfigurationPage extends
		AbstractConfigurationPage<InstanceReader, InstanceImportWizard> {

	/**
	 * @see AbstractConfigurationPage#AbstractConfigurationPage(String, String,
	 *      ImageDescriptor)
	 */
	protected InstanceReaderConfigurationPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see AbstractConfigurationPage#AbstractConfigurationPage(String)
	 */
	protected InstanceReaderConfigurationPage(String pageName) {
		super(pageName);
	}

}
