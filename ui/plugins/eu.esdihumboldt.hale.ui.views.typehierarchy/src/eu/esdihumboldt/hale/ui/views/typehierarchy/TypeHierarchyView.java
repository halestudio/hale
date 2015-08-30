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

package eu.esdihumboldt.hale.ui.views.typehierarchy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.util.selection.SelectionFilter;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;
import eu.esdihumboldt.hale.ui.views.typehierarchy.TypeHierarchyContentProvider.ParentPath;

/**
 * View that shows the hierarchy of a {@link TypeDefinition}
 * 
 * @author Simon Templer
 */
public class TypeHierarchyView extends PropertiesViewPart {

	/**
	 * The ID of the view
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.typehierarchy";

	private TreeViewer viewer;

	private ISelectionListener selectionListener;

	private SelectionFilter selectionProvider;

	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.type_hierarchy";
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new TypeHierarchyContentProvider());
		viewer.setLabelProvider(new TypeHierarchyLabelProvider(viewer));
		viewer.setComparator(new DefinitionComparator());

		contributeToActionBars();

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				update(event.getSelection());
			}
		});

		getSite().getWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(selectionListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						if (!(part instanceof PropertiesViewPart)) {
							// only update the selection if it originates from a
							// part that provides definition or instance
							// selections
							return;
						}

						if (part != TypeHierarchyView.this) {
							update(selection);
						}
					}
				});

		getSite().setSelectionProvider(selectionProvider = new SelectionFilter(viewer) {

			@Override
			protected ISelection filter(ISelection selection) {
				if (selection != null && !selection.isEmpty()
						&& selection instanceof IStructuredSelection) {
					List<Object> elements = new ArrayList<Object>();

					for (Object element : ((IStructuredSelection) selection).toList()) {
						if (element instanceof ParentPath) {
							// add parent path head instead of parent path
							elements.add(((ParentPath) element).getHead());
						}
						else {
							elements.add(element);
						}
					}
					return new StructuredSelection(elements);
				}
				else {
					return selection;
				}
			}
		});

		new ViewerMenu(getSite(), viewer);
	}

	/**
	 * Update the hierarchy view with the given selection
	 * 
	 * @param selection the selection
	 */
	protected void update(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof ParentPath) {
				element = ((ParentPath) element).getHead();
			}
			if (element instanceof Entity) {
				element = ((Entity) element).getDefinition();
			}
			if (element instanceof EntityDefinition) {
				element = ((EntityDefinition) element).getDefinition();
			}
			viewer.setInput(element);
			ParentPath path = TypeHierarchyContentProvider.createPath(element);
			viewer.expandAll();
			if (path != null) {
				viewer.setSelection(new StructuredSelection(path.getMainPath()));
			}
		}
		else {
			viewer.setInput(null);
		}
	}

	private void contributeToActionBars() {
//		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
//		fillLocalToolBar(bars.getToolBarManager());
	}

//	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		manager.add(new Separator());
//		drillDownAdapter.addNavigationActions(manager);
//		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(selectionListener);
		}

		if (selectionProvider != null) {
			selectionProvider.dispose();
		}

		super.dispose();
	}

	/**
	 * @param type the TypeDefinition to set
	 */
	public void setType(TypeDefinition type) {
		update(new StructuredSelection(type));

	}

}
