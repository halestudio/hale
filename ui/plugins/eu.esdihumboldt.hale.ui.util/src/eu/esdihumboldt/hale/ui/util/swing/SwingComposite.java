/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
