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

package eu.esdihumboldt.hale.ui.instancevalidation.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.views.properties.PropertySheet;

import com.google.common.collect.Iterators;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.impl.DefaultInstanceValidationMessage;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.instancevalidation.InstanceValidationUIPlugin;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter;
import eu.esdihumboldt.hale.ui.views.data.TransformedDataView;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPage;

/**
 * Custom report details page for instance validation reports.
 * 
 * @author Kai Schwierczek
 */
public class InstanceValidationReportDetailsPage implements CustomReportDetailsPage {

	private TreeViewer treeViewer;
	private InstanceValidationReportDetailsContentProvider contentProvider;
	private Image reportImage;
	private int more = 0;

	/**
	 * @see CustomReportDetailsPage#createControls(Composite)
	 */
	@Override
	public Control createControls(Composite parent) {
		// filtered tree sets itself GridData, so set layout to gridlayout
		parent.setLayout(GridLayoutFactory.fillDefaults().create());

		// create pattern filter for FilteredTree
		PatternFilter filter = new TreePathPatternFilter();
		filter.setIncludeLeadingWildcard(true);

		// create FilteredTree
		FilteredTree filteredTree = new TreePathFilteredTree(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);

		treeViewer = filteredTree.getViewer();

		// set content provider
		contentProvider = new InstanceValidationReportDetailsContentProvider();
		treeViewer.setContentProvider(contentProvider);

		// set label provider
		treeViewer.setLabelProvider(
				new InstanceValidationReportDetailsLabelProvider(contentProvider));

		// set comparator
		treeViewer.setComparator(new ViewerComparator() {

			/**
			 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
			 */
			@Override
			public int category(Object element) {
				if (element instanceof QName || element instanceof Definition<?>)
					return 0; // Path
				else if (element instanceof String)
					return 1; // Category
				else
					return 2; // InstanceValidationMessage
			}
		});

		// add menu on right-click
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(treeViewer.getTree());

		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (getValidSelection() == null)
					return;

				// selection is valid, offer link to transformed data view
				manager.add(new ShowExampleAction());
			}
		});

		// remove previous menus
		menuMgr.setRemoveAllWhenShown(true);

		// add menu to tree
		treeViewer.getTree().setMenu(menu);

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				showSelectionInDataView();
			}
		});

		Image noReportBaseImage = InstanceValidationUIPlugin.getDefault().getImageRegistry()
				.get(InstanceValidationUIPlugin.IMG_INSTANCE_VALIDATION);
		reportImage = new DecorationOverlayIcon(noReportBaseImage,
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
						ISharedImages.IMG_DEC_FIELD_WARNING),
				IDecoration.BOTTOM_LEFT).createImage();

		return filteredTree;
	}

	@Override
	public void setMore(int more) {
		this.more = more;
	}

	/**
	 * @see CustomReportDetailsPage#setInput(Collection, MessageType)
	 */
	@Override
	public void setInput(Collection<? extends Message> messages, MessageType type) {
		if (more > 0) {
			Collection<Message> messageList = new ArrayList<>(messages);

			String title = MessageFormat.format("{0} more warnings", more);
			String message = MessageFormat
					.format("{0} more validation warnings are not listed explicitly", more);

			messageList.add(new DefaultInstanceValidationMessage(null, null,
					Collections.<QName> emptyList(), title, message));

			treeViewer.setInput(messageList);
		}
		else {
			treeViewer.setInput(messages);
		}

		// initially expand all levels
		treeViewer.expandAll();
	}

	/**
	 * Returns a valid instance selection for the current selection of the tree
	 * viewer. Returns <code>null</code> if there is none.
	 * 
	 * @return a valid instance selection for the current selection of the tree
	 *         viewer or <code>null</code>
	 */
	private InstanceSelection getValidSelection() {
		ISelection viewerSelection = treeViewer.getSelection();
		if (!(viewerSelection instanceof ITreeSelection) || viewerSelection.isEmpty())
			return null;

		ITreeSelection treeSelection = (ITreeSelection) treeViewer.getSelection();
		TreePath firstPath = treeSelection.getPaths()[0]; // XXX use all paths
															// instead of first
															// only?

		InstanceValidationMessage firstMessage;
		Iterator<InstanceValidationMessage> restIter;

		if (firstPath.getLastSegment() instanceof InstanceValidationMessage) {
			firstMessage = (InstanceValidationMessage) firstPath.getLastSegment();
			restIter = Iterators.emptyIterator();
		}
		else {
			Collection<InstanceValidationMessage> messages = contentProvider
					.getMessages(treeSelection.getPaths()[0]);
			if (messages.isEmpty())
				return null; // shouldn't happen, but doesn't really matter
			restIter = messages.iterator();
			firstMessage = restIter.next();
		}

		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		// check first message for valid instance reference
		if (firstMessage.getInstanceReference() == null
				|| is.getInstance(firstMessage.getInstanceReference()) == null)
			return null;

		Set<InstanceReference> references = new HashSet<InstanceReference>();
		references.add(firstMessage.getInstanceReference());
		while (restIter.hasNext())
			references.add(restIter.next().getInstanceReference());

		return new DefaultInstanceSelection(references.toArray());
	}

	/**
	 * Shows the current selection (if valid) in the transformed data view.
	 */
	private void showSelectionInDataView() {
		InstanceSelection selection = getValidSelection();
		if (selection != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage();

			// pin the property sheet if possible
			IViewReference ref = page.findViewReference(IPageLayout.ID_PROP_SHEET);
			if (ref != null) {
				IViewPart part = ref.getView(true);
				if (part instanceof PropertySheet)
					((PropertySheet) part).setPinned(true);
			}

			// show transformed data view with selection if possible
			try {
				TransformedDataView transformedDataView = (TransformedDataView) page
						.showView(TransformedDataView.ID);
				transformedDataView.showSelection(selection, reportImage);
			} catch (PartInitException e) {
				// if it's not there, we cannot do anything
			}
		}
	}

	/**
	 * @see CustomReportDetailsPage#dispose()
	 */
	@Override
	public void dispose() {
		if (reportImage != null) {
			reportImage.dispose();
		}
	}

	/**
	 * Action that opens the transformed data view with example instances
	 * selected here.
	 */
	private final class ShowExampleAction extends Action {

		/**
		 * Default constructor.
		 */
		private ShowExampleAction() {
			super("Show example instances in Transformed data view");
		}

		/**
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			showSelectionInDataView();
		}
	}
}
