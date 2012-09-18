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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import org.eclipse.jface.resource.ImageDescriptor;

import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;

/**
 * Base class for parameter pages.
 * 
 * @author Simon Templer
 */
public abstract class AbstractParameterPage extends
		HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {

	/**
	 * @see HaleWizardPage#HaleWizardPage(String)
	 */
	public AbstractParameterPage(String pageName) {
		super(pageName);
	}

	/**
	 * @see HaleWizardPage#HaleWizardPage(String, String, ImageDescriptor)
	 */
	public AbstractParameterPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * Create a parameter page for the given function.
	 * 
	 * @param function the function
	 * @param description the page description, if <code>null</code> the
	 *            function description will be used
	 */
	public AbstractParameterPage(Function function, String description) {
		super(function.getId(), function.getDisplayName(),
				(function.getIconURL() != null) ? (ImageDescriptor.createFromURL(function
						.getIconURL())) : (null));

		if (description == null) {
			setDescription(function.getDescription());
		}
		else {
			setDescription(description);
		}
	}

}
