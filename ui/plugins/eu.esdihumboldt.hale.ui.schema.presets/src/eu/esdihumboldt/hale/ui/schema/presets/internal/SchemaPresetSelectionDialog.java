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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.schema.presets.internal;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.ui.schema.presets.extension.SchemaPreset;
import eu.esdihumboldt.hale.ui.schema.presets.extension.SchemaPresetExtension;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Dialog for selecting a {@link SchemaPreset}.
 * 
 * @author Simon Templer
 */
public class SchemaPresetSelectionDialog extends
		AbstractViewerSelectionDialog<SchemaPreset, TreeViewer> {

	/**
	 * Create a dialog for selecting a {@link SchemaPreset}.
	 * 
	 * @param parentShell the parent shell
	 * @param initialSelection the initial selection
	 */
	public SchemaPresetSelectionDialog(Shell parentShell, SchemaPreset initialSelection) {
		super(parentShell, "Select a schema", initialSelection);
	}

	/**
	 * @see AbstractViewerSelectionDialog#createViewer(Composite)
	 */
	@Override
	protected TreeViewer createViewer(Composite parent) {
		PatternFilter patternFilter = new PatternFilter();
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER, patternFilter, true);
		tree.getViewer().setComparator(new SchemaPresetComparator());
		return tree.getViewer();
	}

	/**
	 * @see AbstractViewerSelectionDialog#setupViewer(StructuredViewer, Object)
	 */
	@Override
	protected void setupViewer(TreeViewer viewer, SchemaPreset initialSelection) {
		viewer.setLabelProvider(new SchemaPresetLabelProvider());
		viewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// do nothing
			}

			@Override
			public void dispose() {
				// ignore
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return ArrayContentProvider.getInstance().getElements(inputElement);
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return new Object[] {};
			}
		});

		viewer.setInput(SchemaPresetExtension.getInstance().getElements());

		if (initialSelection != null) {
			viewer.setSelection(new StructuredSelection(initialSelection));
		}
	}

	/**
	 * @see AbstractViewerSelectionDialog#getObjectFromSelection(ISelection)
	 */
	@Override
	protected SchemaPreset getObjectFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof SchemaPreset) {
				return (SchemaPreset) element;
			}
		}

		return null;
	}

}
