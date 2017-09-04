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

package eu.esdihumboldt.hale.ui.views.report.properties.details;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.impl.MessageImpl;
import eu.esdihumboldt.hale.ui.util.dialog.StackTraceErrorDialog;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPage;
import eu.esdihumboldt.hale.ui.views.report.properties.details.tree.ReportTreeContentProvider;
import eu.esdihumboldt.hale.ui.views.report.properties.details.tree.ReportTreeLabelProvider;

/**
 * Default report details page showing a filtered tree listing all messages.
 * 
 * @author Kai Schwierczek
 */
public class DefaultReportDetailsPage implements CustomReportDetailsPage {

	private TreeViewer treeViewer;
	private MessageType messageType;

	private int more = 0;

	/**
	 * @see CustomReportDetailsPage#createControls(Composite)
	 */
	@Override
	public Control createControls(Composite parent) {
		// filtered tree sets itself GridData, so set layout to gridlayout
		parent.setLayout(GridLayoutFactory.fillDefaults().create());

		// create pattern filter for FilteredTree
		PatternFilter filter = new PatternFilter();

		// create FilteredTree
		FilteredTree filteredTree = new FilteredTree(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);

		treeViewer = filteredTree.getViewer();

		// set content provider
		treeViewer.setContentProvider(new ReportTreeContentProvider());

		// set label provider
		treeViewer.setLabelProvider(new ReportTreeLabelProvider() {

			@Override
			public MessageType getMessageType(Message message) {
				// the current message type
				return messageType;
			}

		});

		// add menu on right-click
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(treeViewer.getTree());

		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (treeViewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) treeViewer
							.getSelection();

					Object o = selection.getFirstElement();

					if (o instanceof Message) {
						Message m = (Message) o;
						// check if a stacktrace exists
						if (m.getStackTrace() != null && !m.getStackTrace().equals("")) {
							// add Action to the menu
							manager.add(new ShowStackTraceAction("Show Stack Trace", null, m));
						}
					}
				}
			}
		});

		// remove previous menus
		menuMgr.setRemoveAllWhenShown(true);

		// add menu to viewer
		treeViewer.getTree().setMenu(menu);

		// open stacktrace on double click
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeSelection o = (TreeSelection) event.getSelection();

				if (o.getFirstElement() instanceof Message) {
					Message m = (Message) o.getFirstElement();
					DefaultReportDetailsPage.this.onDoubleClick(m);
				}
			}
		});

		return filteredTree;
	}

	@Override
	public void setMore(int more) {
		this.more = more;
	}

	/**
	 * Called when a double click on a message occurred. By default shows a
	 * stack trace if available.
	 * 
	 * @param m the selected message
	 */
	protected void onDoubleClick(Message m) {
		// check if a stacktrace exists
		if (m.getStackTrace() != null && !m.getStackTrace().equals("")) {
			// create action and run it
			(new ShowStackTraceAction("Show Stack Trace", null, m)).run();
		}
	}

	@Override
	public void setInput(Collection<? extends Message> messages, MessageType type) {
		this.messageType = type;
		if (more > 0) {
			Collection<Message> messageList = new ArrayList<>(messages);

			String message;
			switch (messageType) {
			case Error:
				message = MessageFormat.format("{0} more errors that are not listed", more);
				break;
			case Warning:
				message = MessageFormat.format("{0} more warnings that are not listed", more);
				break;
			case Information:
			default:
				message = MessageFormat.format("{0} more messages that are not listed", more);
			}

			messageList.add(new MessageImpl(message, null));

			treeViewer.setInput(messageList);
		}
		else {
			treeViewer.setInput(messages);
		}
	}

	/**
	 * @see CustomReportDetailsPage#dispose()
	 */
	@Override
	public void dispose() {
		// override me
	}

	/**
	 * Internal Exception wrapper.
	 */
	private static class ShowException extends Exception {

		/**
		 * Version id.
		 */
		private static final long serialVersionUID = 4961655914112792450L;

		/**
		 * Stacktrace message.
		 */
		private final String stackTrace;

		/**
		 * Constructor.
		 * 
		 * @param stackTrace stackTrace
		 */
		public ShowException(String stackTrace) {
			this.stackTrace = stackTrace;
		}

		@Override
		public void printStackTrace(PrintWriter pw) {
			pw.append(stackTrace);
		}
	}

	/**
	 * Action for displaying a StackTrace(String).
	 */
	private static class ShowStackTraceAction extends Action {

		/**
		 * Contains the message.
		 */
		private final Message m;

		/**
		 * Constructor.
		 * 
		 * @param text the action's text, or null if there is no text
		 * @param image the action's image, or null if there is no image
		 * @param m the message
		 */
		public ShowStackTraceAction(String text, ImageDescriptor image, Message m) {
			super(text, image);
			this.m = m;
		}

		@Override
		public void run() {
			Status status = new Status(IStatus.ERROR, "eu.esdihumboldt.hale.ui.views.report",
					"See details", new ShowException(m.getStackTrace()));
			StackTraceErrorDialog d = new StackTraceErrorDialog(
					Display.getCurrent().getActiveShell(), "Message Details", m.getMessage(),
					status, IStatus.ERROR);
			d.open();
		}
	}
}
