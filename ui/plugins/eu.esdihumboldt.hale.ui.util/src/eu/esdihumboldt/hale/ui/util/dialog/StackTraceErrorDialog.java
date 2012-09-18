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

package eu.esdihumboldt.hale.ui.util.dialog;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Extended ErrorDialog which displays the stack trace. Can be configured to
 * also show a link to the error log view.
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class StackTraceErrorDialog extends ErrorDialog {

	/**
	 * ID of the Error Log view
	 */
	protected static final String LOG_VIEW_ID = "org.eclipse.pde.runtime.LogView"; //$NON-NLS-1$

	/**
	 * The status that should be shown
	 */
	private IStatus _status;

	/**
	 * The current clipboard
	 */
	private Clipboard _clipboard;

	/**
	 * The list that shows the stack trace
	 */
	private List _list;

	/**
	 * If the error log link shall be shown
	 */
	private boolean showErrorLogLink = false;

	/**
	 * Constructs a new error dialog
	 * 
	 * @see ErrorDialog#ErrorDialog(Shell, String, String, IStatus, int)
	 */
	public StackTraceErrorDialog(Shell parentShell, String dialogTitle, String message,
			IStatus status, int displayMask) {
		super(parentShell, dialogTitle, message, status, displayMask);
		_status = status;
	}

	/**
	 * @param showErrorLogLink if the error log link shall be shown
	 */
	public void setShowErrorLogLink(boolean showErrorLogLink) {
		this.showErrorLogLink = showErrorLogLink;
	}

	/**
	 * @see ErrorDialog#createDropDownList(Composite)
	 */
	@Override
	protected List createDropDownList(Composite parent) {
		_list = super.createDropDownList(parent);
		_list.removeAll();

		// replace context menu
		_list.getMenu().dispose();
		Menu copyMenu = new Menu(_list);
		MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
		copyItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				copyToClipboard();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				copyToClipboard();
			}
		});
		copyItem.setText(JFaceResources.getString("copy")); //$NON-NLS-1$
		_list.setMenu(copyMenu);

		// convert stack trace to string
		String stackTrace = stackTraceToString(_status.getException());
		if (stackTrace != null) {
			// add stack trace to list
			stackTrace = stackTrace.replaceAll("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
			stackTrace = stackTrace.replaceAll("\t", "    "); //$NON-NLS-1$ //$NON-NLS-2$
			String[] lines = stackTrace.split("\n"); //$NON-NLS-1$
			for (String l : lines) {
				_list.add(l);
			}
		}

		return _list;
	}

	/**
	 * Creates a string from a stack trace
	 * 
	 * @param t the exception
	 * @return the stack trace as a string
	 */
	private String stackTraceToString(Throwable t) {
		if (t == null) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		_status.getException().printStackTrace(pw);
		pw.flush();
		return baos.toString();
	}

	/**
	 * Copies the stack trace to the clipboard
	 */
	protected void copyToClipboard() {
		if (_clipboard != null) {
			_clipboard.dispose();
		}

		String stackTrace = stackTraceToString(_status.getException());

		_clipboard = new Clipboard(_list.getDisplay());
		_clipboard.setContents(new Object[] { stackTrace },
				new Transfer[] { TextTransfer.getInstance() });
	}

	private Link createShowErrorLogLink(Composite parent) {
		Link link = new Link(parent, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Workbench.getInstance().getActiveWorkbenchWindow().getActivePage()
							.showView(LOG_VIEW_ID);
				} catch (CoreException ce) {
					StatusManager.getManager().handle(ce, WorkbenchPlugin.PI_WORKBENCH);
				}
			}
		});
		link.setText(WorkbenchMessages.ErrorLogUtil_ShowErrorLogHyperlink);
		link.setToolTipText(WorkbenchMessages.ErrorLogUtil_ShowErrorLogTooltip);
		Dialog.applyDialogFont(link);
		return link;
	}

	/**
	 * @see ErrorDialog#close()
	 */
	@Override
	public boolean close() {
		if (_clipboard != null) {
			_clipboard.dispose();
			_clipboard = null;
		}
		return super.close();
	}

	/**
	 * @see ErrorDialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = (Composite) super.createDialogArea(parent);

		if (shouldDisplayLinkToErrorLog()) {
			Composite space = new Composite(main, SWT.NONE);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
			gridData.heightHint = 1;
			gridData.widthHint = 1;
			space.setLayoutData(gridData);

			Link link = createShowErrorLogLink(main);
			link.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		}

		return main;
	}

	private boolean shouldDisplayLinkToErrorLog() {
		/* no support for error log */
		if (!showErrorLogLink) {
			return false;
		}
		/* view description */
		return Workbench.getInstance().getViewRegistry().find(LOG_VIEW_ID) != null;
	}
}
