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

package eu.esdihumboldt.hale.ui.io.action.wizard;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.action.ActionUI;
import eu.esdihumboldt.hale.ui.util.wizard.AbstractWizardNode;
import eu.esdihumboldt.hale.ui.util.wizard.ExtendedWizardNode;

/**
 * Wizard node based on {@link ActionUI}
 * 
 * @author Simon Templer
 */
public class ActionUIWizardNode extends AbstractWizardNode {

	private final ActionUI actionUI;

	private Image image;

	/**
	 * Create a wizard node
	 * 
	 * @param actionUI the action UI
	 * @param container the wizard container
	 */
	public ActionUIWizardNode(ActionUI actionUI, IWizardContainer container) {
		super(container);
		this.actionUI = actionUI;
	}

	/**
	 * @see ExtendedWizardNode#getDescription()
	 */
	@Override
	public String getDescription() {
		// XXX description?
		return null;
	}

	/**
	 * @see AbstractWizardNode#createWizard()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected IWizard createWizard() {
		try {
			IOWizard<?> wizard = actionUI.createExtensionObject();
			IOAdvisor<?> advisor = IOAdvisorExtension.getInstance().findAdvisor(
					actionUI.getActionID());
			((IOWizard) wizard).setAdvisor(advisor, actionUI.getActionID());
			return wizard;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the actionUI
	 */
	public ActionUI getActionUI() {
		return actionUI;
	}

	/**
	 * Get the wizard image
	 * 
	 * @return the image
	 */
	public Image getImage() {
		if (image == null) {
			URL iconURL = actionUI.getIconURL();
			if (iconURL != null) {
				image = ImageDescriptor.createFromURL(iconURL).createImage();
			}
		}

		return image;
	}

	/**
	 * @see AbstractWizardNode#dispose()
	 */
	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
		}

		super.dispose();
	}

}
