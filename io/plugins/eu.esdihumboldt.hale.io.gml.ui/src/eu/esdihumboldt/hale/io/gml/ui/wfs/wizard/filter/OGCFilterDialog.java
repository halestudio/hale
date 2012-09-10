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

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.io.gml.ui.internal.Messages;

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
