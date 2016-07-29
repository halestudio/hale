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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/

/**
 * A dialog containing a Swing/AWT component
 * 
 * @author Simon Templer
 */
public class ComponentDialog extends TitleAreaDialog {

	private final Component component;

	private final String title;

	private final String message;

	/**
	 * Constructor
	 * 
	 * @param parentShell the parent shell
	 * @param component the main component
	 * @param title the dialog title
	 * @param message the dialog message
	 */
	public ComponentDialog(Shell parentShell, final Component component, final String title,
			final String message) {
		super(parentShell);

		this.title = title;
		this.message = message;

		this.component = component;

		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * @see TitleAreaDialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		Point size = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point defSize = super.getInitialSize();
		return new Point(Math.max(size.x, defSize.x), Math.max(size.y, defSize.y));
	}

	/**
	 * @see TitleAreaDialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		setMessage(message);
		setTitle(title);

		return control;
	}

	/**
	 * @see TitleAreaDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		SwingComposite page = new SwingComposite(parent);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		page.setLayoutData(data);

		Container container = page.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(component);

		Dimension dim = container.getPreferredSize();
		data.widthHint = dim.width;
		data.heightHint = dim.height;

		return page;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(title);
	}

	/**
	 * @return the component
	 */
	public Component getComponent() {
		return component;
	}

}
