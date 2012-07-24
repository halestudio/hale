/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.instancevalidation.report;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.views.properties.PropertySheet;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instancevalidator.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.views.data.TransformedDataView;
import eu.esdihumboldt.hale.ui.views.report.properties.details.extension.CustomReportDetailsPage;

/**
 * Custom report details page for instance validation reports.
 *
 * @author Kai Schwierczek
 */
public class InstanceValidationReportDetailsPage implements CustomReportDetailsPage {
	private TreeViewer treeViewer;

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
		FilteredTree filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);

		treeViewer = filteredTree.getViewer();

		// set content provider
		treeViewer.setContentProvider(new InstanceValidationReportDetailsContentProvider());

		// set label provider
		treeViewer.setLabelProvider(new InstanceValidationReportDetailsLabelProvider());

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

		return filteredTree;
	}

	/**
	 * @see CustomReportDetailsPage#setInput(Collection, MessageType)
	 */
	@Override
	public void setInput(Collection<? extends Message> messages, MessageType type) {
		treeViewer.setInput(messages);
	}

	/**
	 * Returns a valid instance selection for the current selection of the tree viewer.
	 * Returns <code>null</code> if there is none.
	 *
	 * @return a valid instance selection for the current selection of the tree viewer or <code>null</code>
	 */
	private InstanceSelection getValidSelection() {
		ISelection viewerSelection = treeViewer.getSelection();
		if (!(viewerSelection instanceof IStructuredSelection) || viewerSelection.isEmpty())
			return null;
		List<?> selection = (List<?>) ((IStructuredSelection) viewerSelection).getFirstElement();
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		// check first message for valid instance reference
		InstanceValidationMessage first = (InstanceValidationMessage) selection.get(0);
		if (first.getInstanceReference() == null || is.getInstance(first.getInstanceReference()) == null)
			return null;

		Set<InstanceReference> references = new HashSet<InstanceReference>();
		for (Object o : selection)
			references.add(((InstanceValidationMessage) o).getInstanceReference());

		return new DefaultInstanceSelection(references.toArray());
	}

	/**
	 * Shows the current selection (if valid) in the transformed data view.
	 */
	private void showSelectionInDataView() {
		InstanceSelection selection = getValidSelection();
		if (selection != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			// pin the property sheet if possible
			IViewReference ref = page.findViewReference(IPageLayout.ID_PROP_SHEET);
			if (ref != null) {
				IViewPart part = ref.getView(true);
				if (part instanceof PropertySheet)
					((PropertySheet) part).setPinned(true);
			}

			// show transformed data view with selection if possible
			try {
				TransformedDataView transformedDataView = (TransformedDataView) page.showView(TransformedDataView.ID);
				transformedDataView.showSelection(selection);
			} catch (PartInitException e) {
				// if it's not there, we cannot do anything
			}
		}
	}

	/**
	 * Action that opens the transformed data view with example instances selected here.
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
