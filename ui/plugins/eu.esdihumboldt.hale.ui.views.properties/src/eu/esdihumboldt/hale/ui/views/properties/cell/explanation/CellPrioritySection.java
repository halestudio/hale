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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.views.properties.cell.AbstractCellSection;

/**
 * Cell {@link Priority} Section for cell nodes.
 * 
 * @author Andrea Antonello
 */
public class CellPrioritySection extends AbstractCellSection {

	private CCombo combo;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite page = getWidgetFactory().createComposite(parent);
		page.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(8, 8).create());

		CLabel namespaceLabel = getWidgetFactory().createCLabel(page, Cell.PROPERTY_PRIORITY);
		namespaceLabel.setLayoutData(GridDataFactory.fillDefaults().create());

		combo = new CCombo(page, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		for (Priority priority : Priority.values()) {
			String priorityValue = priority.value();
			combo.add(priorityValue);
		}
		combo.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String priorityText = combo.getText();
				Priority priority = Priority.fromValue(priorityText);
				Cell cell = getCell();
				if (cell instanceof MutableCell) {
					MutableCell mutableCell = (MutableCell) cell;
					mutableCell.setPriority(priority);

					ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
							ProjectService.class);
					if (ps != null) {
						ps.setChanged();
					}
				}
			}
		});

	}

	private void setFromCell() {
		Cell cell = getCell();
		Priority currentPriority = cell.getPriority();
		int i = 0;
		int selectedIndex = 0;
		for (Priority priority : Priority.values()) {
			String priorityValue = priority.value();
			if (priorityValue.equals(currentPriority.value())) {
				selectedIndex = i;
			}
			i++;
		}
		combo.select(selectedIndex);
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
			combo.setEnabled(true);
			setFromCell();
		}
		else {
			combo.setEnabled(false);
		}
	}

}
