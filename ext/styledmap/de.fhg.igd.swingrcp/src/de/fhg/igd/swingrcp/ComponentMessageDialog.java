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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
 * Message dialog that contains an AWT/Swing component
 * 
 * @author Simon Templer
 */
@SuppressWarnings("all")
public class ComponentMessageDialog extends MessageDialog {

	private final Component component;

	/**
	 * {@inheritDoc}
	 * 
	 * @param component the component
	 */
	public ComponentMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
			String dialogMessage, int dialogImageType, String[] dialogButtonLabels,
			int defaultIndex, Component component) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType,
				dialogButtonLabels, defaultIndex);
		this.component = component;
	}

	/**
	 * @see MessageDialog#createCustomArea(Composite)
	 */
	@Override
	protected Control createCustomArea(Composite parent) {
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
	 * @see MessageDialog#openQuestion(Shell, String, String)
	 * 
	 * @param component the component
	 */
	public static boolean openQuestion(Shell parent, String title, String message,
			Component component) {
		return open(QUESTION, parent, title, message, SWT.NONE, component);
	}

	/**
	 * @see MessageDialog#open(int, Shell, String, String, int)
	 * 
	 * @param component the component
	 */
	private static boolean open(int kind, Shell parent, String title, String message, int style,
			Component component) {
		ComponentMessageDialog dialog = new ComponentMessageDialog(parent, title, null, message,
				kind, getButtonLabels(kind), 0, component);
		style &= SWT.SHEET;
		dialog.setShellStyle(dialog.getShellStyle() | style);
		return dialog.open() == 0;
	}

	/**
	 * @see MessageDialog#getButtonLabels(int)
	 */
	static String[] getButtonLabels(int kind) {
		String[] dialogButtonLabels;
		switch (kind) {
		case ERROR:
		case INFORMATION:
		case WARNING: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
			break;
		}
		case CONFIRM: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL,
					IDialogConstants.CANCEL_LABEL };
			break;
		}
		case QUESTION: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL,
					IDialogConstants.NO_LABEL };
			break;
		}
		case QUESTION_WITH_CANCEL: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL,
					IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
			break;
		}
		default: {
			throw new IllegalArgumentException("Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
		}
		}
		return dialogButtonLabels;
	}

}
