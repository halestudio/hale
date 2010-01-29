package eu.esdihumboldt.hale.rcp.wizards.io;

import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class OGCFilterDialog extends Dialog {
	private final static Logger _log = Logger.getLogger(WFSDataReaderDialog.class);
	private String _filter = null;

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
		shell.setSize(500, 450);
		shell.setLayout(new GridLayout());
		shell.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		shell.setText(super.getText());
		
		this.createControls(shell);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		_log.debug("returning result.");
		
		return this._filter;
	}
	
	private void createControls(final Shell shell) {
		_log.debug("Creating Controls");
		
		
		// Create Fields for URL entry.
		final Composite c = new Composite(shell, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL |
				GridData.GRAB_VERTICAL | GridData.FILL_VERTICAL));
		
		final Group filterDef = new Group(c, SWT.NONE); 
		filterDef.setText("Filter operators...");
		filterDef.setLayoutData( new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		
		
	}
}
