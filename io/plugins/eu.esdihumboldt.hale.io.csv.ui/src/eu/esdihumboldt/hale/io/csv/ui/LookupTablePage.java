/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.csv.ui;

import java.io.IOException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.lookup.LookupTableImport;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.ui.lookup.LookupTableImportConfigurationPage;

/**
 * The page to specify which column should be matched with which column
 * 
 * @author Dominik Reuter
 */
@SuppressWarnings("restriction")
public class LookupTablePage extends LookupTableImportConfigurationPage implements
		SelectionListener {

	private Combo choose;
	private Combo keyColumn;
	private Combo valueColumn;
	private Label l;
	private boolean skip;

	/**
	 * Default Constructor
	 */
	public LookupTablePage() {
		super("LookupTablePage");
		setTitle("Specify which column will be connected with which column");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout());
		// head composite
		Composite head = new Composite(page, SWT.NONE);
		head.setLayout(new GridLayout(2, false));

		Label withHeadlines = new Label(head, SWT.NONE);
		withHeadlines.setText("Select if the first row contains headlines");

		choose = new Combo(head, SWT.READ_ONLY);
		String[] selection = new String[] { "Yes", "No" };
		choose.setItems(selection);
		choose.addSelectionListener(this);

		// Label
		l = new Label(page, SWT.NONE);
		l.setText("Specify which column will be connected with which column");
		l.setVisible(false);

		// composite
		Composite middle = new Composite(page, SWT.NONE);
		middle.setLayout(new GridLayout(2, false));

		GridData layoutData = new GridData();
		layoutData.widthHint = 200;

		keyColumn = new Combo(middle, SWT.READ_ONLY);
		keyColumn.setLayoutData(GridDataFactory.copyData(layoutData));
		keyColumn.setVisible(false);
		keyColumn.addSelectionListener(this);

		valueColumn = new Combo(middle, SWT.READ_ONLY);
		valueColumn.setLayoutData(GridDataFactory.copyData(layoutData));
		valueColumn.setVisible(false);
		valueColumn.addSelectionListener(this);

		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(LookupTableImport provider) {
		provider.setParameter(CSVConstants.PARAM_SKIP_FIRST_LINE, Value.of(skip));

		if (keyColumn.getSelectionIndex() != -1 && valueColumn.getSelectionIndex() != -1) {
			provider.setParameter(CSVConstants.LOOKUP_KEY_COLUMN, Value.of(keyColumn.getSelectionIndex()));
			provider.setParameter(CSVConstants.LOOKUP_VALUE_COLUMN, Value.of(valueColumn.getSelectionIndex()));
			return true;
		}
		else {
			setErrorMessage("You have to match the columns");
			return false;
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		try {
			if (e.getSource() != null) {
				if (e.getSource().equals(choose)) {
					l.setVisible(true);
					keyColumn.setVisible(true);
					valueColumn.setVisible(true);
					CSVReader reader = CSVUtil.readFirst(getWizard().getProvider());
					if (((Combo) e.getSource()).getSelectionIndex() == 0) {
						// yes is selected
						// Load the csv-file to get the column informations
						skip = true;
						String[] items = reader.readNext();
						keyColumn.setItems(items);
						valueColumn.setItems(items);
						setPageComplete(true);
					}
					else {
						// no is selected
						skip = false;
						int numberOfColumns = reader.readNext().length;
						String[] items = new String[numberOfColumns];
						for (int i = 0; i < numberOfColumns; i++) {
							int tmp = i + 1;
							items[i] = "Column " + tmp;
						}
						keyColumn.setItems(items);
						valueColumn.setItems(items);
						setPageComplete(true);
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// nothing to do here.
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// nothing to do here.
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// nothing to do here.
	}
}
