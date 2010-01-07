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
package eu.esdihumboldt.hale.rcp.wizards.augmentations;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

/**
 * Wizard page that knows about its {@link AugmentationWizard}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AugmentationWizardPage extends WizardPage {

	private AugmentationWizard parent;

	/**
	 * @see WizardPage#WizardPage(String, String, ImageDescriptor)
	 */
	public AugmentationWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see WizardPage#WizardPage(String)
	 */
	public AugmentationWizardPage(String pageName) {
		super(pageName);
	}

	/**
	 * @return the parent
	 */
	public AugmentationWizard getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(AugmentationWizard parent) {
		this.parent = parent;
	}

}
