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
package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.filter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.opengis.feature.type.FeatureType;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * Dialog for creating a filter
 * 
 * @author unknown
 */
public class OGCFilterDialog extends Dialog {

	private final static ALogger _log = ALoggerFactory.getLogger(OGCFilterDialog.class);
	private String _filter = null;

	FeatureType featureType;
	OGCFilterBuilder filterBuilder;

	/**
	 * Constructor
	 * 
	 * @param parent the parent shell
	 * @param style the dialog style
	 */
	public OGCFilterDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Constructor
	 * 
	 * @param parent the parent shell
	 * @param title the dialog title
	 */
	public OGCFilterDialog(Shell parent, String title) {
		super(parent, SWT.NONE);
		this.setText(title);
	}

	/**
	 * @see org.eclipse.swt.widgets.Dialog
	 * @return any Object.
	 */
	public String open() {
		Shell parent = super.getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(586, 252);
		shell.setLayout(new GridLayout());
		shell.setText(super.getText());

		this.createControls(shell);

		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		_log.debug("returning result."); //$NON-NLS-1$

		return _filter;
	}

	private void createControls(final Shell shell) {
		_log.debug("Creating Controls"); //$NON-NLS-1$

		filterBuilder = new OGCFilterBuilder(shell, featureType);

		final Composite buttons = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		buttons.setLayout(layout);
		buttons.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		final Button finishButton = new Button(buttons, SWT.NONE);
		finishButton.setText(Messages.OGCFilterDialog_0); //$NON-NLS-1$
		finishButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// do finish
				try {
					_filter = filterBuilder.buildFilter();
					shell.dispose();
				} catch (IllegalStateException e) {
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					box.setText(Messages.OGCFilterDialog_1); //$NON-NLS-1$
					box.setMessage(e.getMessage());
					box.open();
				}
			}
		});

		final Button cancelButton = new Button(buttons, SWT.NONE);
		cancelButton.setText(Messages.OGCFilterDialog_2); //$NON-NLS-1$
		cancelButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});
	}

	/**
	 * Set the feature type to be filtered
	 * 
	 * @param featureType the feature type
	 */
	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}
}
