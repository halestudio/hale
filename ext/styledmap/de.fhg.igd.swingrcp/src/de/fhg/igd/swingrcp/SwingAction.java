/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.swingrcp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Wraps a Eclipse JFace action in a Swing action
 * 
 * @author Simon Templer
 */
public class SwingAction extends AbstractAction {

	private static final long serialVersionUID = 2848483445310125320L;

	private final IAction action;

	/**
	 * Creates a Swing action from a JFace action
	 * 
	 * @param action the JFace action
	 */
	public SwingAction(IAction action) {
		super();
		this.action = action;

		putValue(Action.NAME, action.getText());
		putValue(Action.SHORT_DESCRIPTION, action.getToolTipText());
		putValue(Action.LONG_DESCRIPTION, action.getDescription());

		ImageDescriptor imageDesc = action.getImageDescriptor();
		if (imageDesc != null) {
			ImageData imageData = imageDesc.getImageData(100);

			if (imageData != null) {
				BufferedImage img = SwingRCPUtilities.convertToAWT(imageData, true);
				ImageIcon icon = new ImageIcon(img);
				putValue(Action.SMALL_ICON, icon);
			}
		}
	}

	/**
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				action.run();
			}
		});
	}

}
