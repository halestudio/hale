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

package eu.esdihumboldt.hale.ui.codelist.inspire.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Dialog for selecting a {@link CodeListRef}.
 * 
 * @author Simon Templer
 */
public class CodeListSelectionDialog extends AbstractViewerSelectionDialog<CodeListRef, TreeViewer> {

	/**
	 * Create a dialog for selecting a {@link CodeListRef}.
	 * 
	 * @param parentShell the parent shell
	 * @param initialSelection the initial selection
	 */
	public CodeListSelectionDialog(Shell parentShell, CodeListRef initialSelection) {
		super(parentShell, "Select a code list", initialSelection);
	}

	/**
	 * @see AbstractViewerSelectionDialog#createViewer(Composite)
	 */
	@Override
	protected TreeViewer createViewer(Composite parent) {
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER, patternFilter, true);
		tree.getViewer().setComparator(new CodeListComparator());

		// set filter to only accept code list selection (must be set after
		// pattern filter is created)
		setFilters(new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof CodeListRef;
			}
		} });

		return tree.getViewer();
	}

	/**
	 * @see AbstractViewerSelectionDialog#setupViewer(StructuredViewer, Object)
	 */
	@Override
	protected void setupViewer(final TreeViewer viewer, final CodeListRef initialSelection) {
		viewer.setLabelProvider(new CodeListLabelProvider());
		viewer.setContentProvider(new CodeListContentProvider());

		ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
		final Display display = Display.getCurrent();
		try {
			dlg.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Loading available code lists from INSPIRE registry",
							IProgressMonitor.UNKNOWN);

					final Collection<CodeListRef> codeLists = RegistryCodeLists.loadCodeLists();

					display.asyncExec(new Runnable() {

						@Override
						public void run() {
							viewer.setInput(codeLists);

							if (initialSelection != null) {
								viewer.setSelection(new StructuredSelection(initialSelection));
							}
						}
					});

					monitor.done();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new IllegalStateException("Failed to load code lists", e);
		}
	}

	/**
	 * @see AbstractViewerSelectionDialog#getObjectFromSelection(ISelection)
	 */
	@Override
	protected CodeListRef getObjectFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof CodeListRef) {
				return (CodeListRef) element;
			}
		}

		return null;
	}

}
