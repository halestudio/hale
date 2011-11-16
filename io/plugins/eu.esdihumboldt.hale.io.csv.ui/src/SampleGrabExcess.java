/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
//package org.eclipse.swt.snippets;
/*
 * ScrolledComposite example snippet: scroll a control in a scrolled composite
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.io.csv.ui.TypeNameField;

@SuppressWarnings("javadoc")
public class SampleGrabExcess {
	
	private static StringFieldEditor sfe;
	private static Group group;
	private static Group geom;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		// this button has a minimum size of 400 x 400. If the window is resized
		// to be big
		// enough to show more than 400 x 400, the button will grow in size. If
		// the window
		// is made too small to show 400 x 400, scrollbars will appear.
		ScrolledComposite c2 = new ScrolledComposite(shell, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		c2.setExpandHorizontal(true);
		c2.setExpandVertical(true);
		c2.setMinWidth(400);
		c2.setMinHeight(400);
		
		Composite c3 = new Composite(c2, SWT.NONE);
		c3.setLayout(new GridLayout(2, false));

		sfe = new TypeNameField("typename", "Typename", c3);
		sfe.setEmptyStringAllowed(false);
		sfe.setErrorMessage("Please enter a valid Type Name");
		sfe.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
			// do nothin
				}
			
		});

		sfe.setStringValue("muhkuh");

		group = new Group(c3, SWT.NONE);
		group.setText("Properties");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
				.span(2, 1).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3)
				.equalWidth(false).margins(5, 5).create());

		geom = new Group(c3, SWT.NONE);
		geom.setText("Geometry Settings");
		geom.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
				.span(2, 1).create());
		geom.setLayout(GridLayoutFactory.swtDefaults().numColumns(3)
				.equalWidth(false).margins(5, 5).create());
		
		c2.setContent(c3);
		
		shell.setSize(600, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}