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

package eu.esdihumboldt.hale.ui.io.action.wizard;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.ui.HaleUI;
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
					actionUI.getActionID(), HaleUI.getServiceProvider());
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
