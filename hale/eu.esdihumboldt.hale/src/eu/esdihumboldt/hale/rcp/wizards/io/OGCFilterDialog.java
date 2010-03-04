package eu.esdihumboldt.hale.rcp.wizards.io;

import org.apache.log4j.Logger;
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

public class OGCFilterDialog extends Dialog {
	private final static Logger _log = Logger.getLogger(WFSDataReaderDialog.class);
	private String _filter = null;
	
	FeatureType featureType;
	OGCFilterBuilder filterBuilder;

	public OGCFilterDialog(Shell parent, int style) {
		super(parent, style);
	}
	
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
			if (!display.readAndDispatch()) display.sleep();
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
		finishButton.setText("Use this filter");
		finishButton.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event event) {
				// do finish
				try {
					_filter = filterBuilder.buildFilter();
					shell.dispose();
				}
				catch (IllegalStateException e) {
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					box.setText("OGC Filter error");
					box.setMessage(e.getMessage());
					box.open();
				}
			}
		});
		
		final Button cancelButton = new Button(buttons, SWT.NONE);
		cancelButton.setText("     Cancel     ");
		cancelButton.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});
	}
	
	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}
}
