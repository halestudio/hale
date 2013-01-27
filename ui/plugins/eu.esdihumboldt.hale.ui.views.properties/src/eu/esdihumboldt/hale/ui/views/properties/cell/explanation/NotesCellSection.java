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

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.views.properties.cell.AbstractCellSection;

/**
 * Section for cell nodes.
 * 
 * @author Simon Templer
 */
public class NotesCellSection extends AbstractCellSection {

	private Text textField;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite page = getWidgetFactory().createComposite(parent);
		page.setLayout(GridLayoutFactory.fillDefaults().margins(8, 8).create());

		textField = new Text(page, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		textField.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		textField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Cell cell = getCell();
				if (cell != null) {
					String cellNotes = getNotes(cell);
					String notes = textField.getText();
					if (!notes.equals(cellNotes)) {
						cell.getDocumentation().removeAll(null);
						if (!notes.isEmpty()) {
							cell.getDocumentation().put(null, notes);
						}
						ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
								ProjectService.class);
						if (ps != null) {
							ps.setChanged();
						}
					}
				}
			}
		});
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
			String notes = getNotes(cell);

			textField.setText(notes);
			textField.setEnabled(true);
		}
		else {
			textField.setText("");
			textField.setEnabled(false);
		}
	}

	private String getNotes(Cell cell) {
		List<String> docs = cell.getDocumentation().get(null);
		if (docs.isEmpty() || docs.get(0) == null) {
			return "";
		}
		else {
			return docs.get(0);
		}
	}

}
