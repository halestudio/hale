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

package eu.esdihumboldt.hale.ui.views.properties.cell.explanation;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;
import eu.esdihumboldt.hale.ui.views.properties.cell.AbstractCellSection;

/**
 * Cell {@link Priority} Section for cell nodes.
 * 
 * @author Andrea Antonello
 */
public class CellPrioritySection extends AbstractCellSection implements ISelectionChangedListener {

	private ComboViewer comboViewer;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite page = getWidgetFactory().createComposite(parent);
		page.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(8, 8).create());

		CLabel namespaceLabel = getWidgetFactory().createCLabel(page, Cell.PROPERTY_PRIORITY);
		namespaceLabel.setLayoutData(GridDataFactory.fillDefaults().create());

		comboViewer = new ComboViewer(page, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
		comboViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		comboViewer.setContentProvider(EnumContentProvider.getInstance());
		comboViewer.setInput(Priority.class);
		comboViewer.addSelectionChangedListener(this);

	}

	private void setFromCell() {
		Cell cell = getCell();
		Priority currentPriority = cell.getPriority();
		comboViewer.setSelection(new StructuredSelection(currentPriority));
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	public void refresh() {
		super.refresh();

		Cell cell = getCell();
		if (cell != null) {
			comboViewer.getControl().setEnabled(true);
			setFromCell();
		}
		else {
			comboViewer.getControl().setEnabled(false);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof Priority) {
				Priority priority = (Priority) firstElement;
				Cell cell = getCell();
				if (cell.getPriority() != priority) {
					AlignmentService alignmentService = PlatformUI.getWorkbench()
							.getService(AlignmentService.class);
					alignmentService.setCellProperty(cell.getId(), Cell.PROPERTY_PRIORITY,
							priority);
				}
			}
		}
	}

}
