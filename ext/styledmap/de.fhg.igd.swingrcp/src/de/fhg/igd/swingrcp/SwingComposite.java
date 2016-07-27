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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JApplet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * A Composite that provides a content pane for Swing components
 *
 * @author Simon Templer
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
		this(parent, 0);
	}

	/**
	 * Creates a {@link SwingComposite}
	 * 
	 * @param parent the parent {@link Composite}
	 * @param additionalStyle additional styling attributes
	 */
	public SwingComposite(Composite parent, int additionalStyle) {
		super(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND | additionalStyle);

		SwingRCPUtilities.setup();

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
	 * Gets the content pane. Add your Swing components to the content pane
	 * 
	 * @return the content pane
	 */
	public Container getContentPane() {
		return embedded.getContentPane();
	}

}
