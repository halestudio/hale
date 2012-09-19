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
package eu.esdihumboldt.hale.ui.util.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JApplet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite embedding a AWT/Swing components
 * 
 * @author Simon Templer, Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SwingComposite extends Composite {

	private final Frame frame;

	private final JApplet embedded;

	/**
	 * Creates a {@link SwingComposite}
	 * 
	 * @param parent the parent {@link Composite}
	 */
	public SwingComposite(Composite parent) {
		super(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);

		SwingRcpUtilities.setup();

		final Rectangle parentBounds = parent.getBounds();
		parentBounds.x = parentBounds.y = 0;
		setBounds(parentBounds);

		frame = SWT_AWT.new_Frame(this);
		final Rectangle bounds = getBounds();
		frame.setBounds(0, 0, bounds.width, bounds.height);
		frame.setLayout(new BorderLayout());

		// need a heavyweight component inside the frame, preferably a JRootPane
		// -> use JApplet container
		embedded = new JApplet();

		frame.add(embedded);
	}

	/**
	 * Gets the content pane. Add your Swing components to the content pane.
	 * 
	 * @return the content pane
	 */
	public Container getContentPane() {
		return embedded.getContentPane();
	}

}
