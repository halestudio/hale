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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
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
import eu.esdihumboldt.hale.io.csv.reader.CSVConstants;
import eu.esdihumboldt.hale.io.csv.reader.DefaultCSVLookupReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVLookupReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVUtil;
import eu.esdihumboldt.hale.io.csv.writer.LookupTableExportConstants;
import eu.esdihumboldt.hale.io.xls.reader.DefaultXLSLookupTableReader;
import eu.esdihumboldt.hale.ui.lookup.LookupTableImportConfigurationPage;

/**
 * The page to specify which column should be matched with which column
 * 
 * @author Dominik Reuter, Patrick Lieb
 */
public class LookupTablePage extends LookupTableImportConfigurationPage implements
		SelectionListener {

	private Combo choose;
	private Combo keyColumn;
	private Combo valueColumn;
	private Label l;
	private boolean skip;

	private Map<Value, Value> lookupTable = new HashMap<Value, Value>();

	private TableViewer tableViewer;
	private TableViewerColumn sourceColumn;
	private TableViewerColumn targetColumn;
	private Composite tableContainer;

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

		// header selection
		Label withHeadlines = new Label(head, SWT.NONE);
		withHeadlines.setText("Select if the first row contains headlines");

		choose = new Combo(head, SWT.READ_ONLY);
		String[] selection = new String[] { "Yes", "No" };
		choose.setItems(selection);
		choose.select(1);
		choose.addSelectionListener(this);

		// selection of columns to be connected
		l = new Label(page, SWT.NONE);
		l.setText("Specify which column will be connected with which column");

		Composite middle = new Composite(page, SWT.NONE);
		middle.setLayout(new GridLayout(2, false));

		GridData layoutData = new GridData();
		layoutData.widthHint = 200;

		keyColumn = new Combo(middle, SWT.READ_ONLY);
		keyColumn.setLayoutData(GridDataFactory.copyData(layoutData));

		keyColumn.addSelectionListener(this);

		valueColumn = new Combo(middle, SWT.READ_ONLY);
		valueColumn.setLayoutData(GridDataFactory.copyData(layoutData));

		valueColumn.addSelectionListener(this);

		// table preview with current selection
		addPreview(page);

		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// input file is selected, so we can read it
		String[] header = readHeader();
		int numberOfColumns = header.length;
		String[] items = new String[numberOfColumns];
		for (int i = 0; i < numberOfColumns; i++) {
			int tmp = i + 1;
			items[i] = "Column " + tmp;
		}
		keyColumn.setItems(items);
		valueColumn.setItems(items);
		keyColumn.select(0);
		valueColumn.select(1);

		// refresh table with new content
		refreshTable();

		// page is complete since page is shown
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(LookupTableImport provider) {
		provider.setParameter(LookupTableExportConstants.PARAM_SKIP_FIRST_LINE, Value.of(skip));

		if (keyColumn.getSelectionIndex() != -1 && valueColumn.getSelectionIndex() != -1) {
			provider.setParameter(LookupTableExportConstants.LOOKUP_KEY_COLUMN,
					Value.of(keyColumn.getSelectionIndex()));
			provider.setParameter(LookupTableExportConstants.LOOKUP_VALUE_COLUMN,
					Value.of(valueColumn.getSelectionIndex()));
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
		if (e.getSource() != null) {
			lookupTable.clear();
			sourceColumn.getColumn().setText("");
			targetColumn.getColumn().setText("");
			tableViewer.refresh();
			if (e.getSource().equals(choose)) {
				l.setVisible(true);
				String[] header = readHeader();
				if (((Combo) e.getSource()).getSelectionIndex() == 0) {
					// yes is selected
					skip = true;
					keyColumn.setItems(header);
					valueColumn.setItems(header);
				}
				else {
					// no is selected
					skip = false;
					int numberOfColumns = header.length;
					String[] items = new String[numberOfColumns];
					for (int i = 0; i < numberOfColumns; i++) {
						int tmp = i + 1;
						items[i] = "Column " + tmp;
					}
					keyColumn.setItems(items);
					valueColumn.setItems(items);
				}
				keyColumn.select(0);
				valueColumn.select(1);
				refreshTable();
			}
			else if (keyColumn.getSelectionIndex() >= 0 && valueColumn.getSelectionIndex() >= 0) {
				refreshTable();
			}
		}
	}

	// read line from a file specified by provider of corresponding wizard
	private String[] readHeader() {
		LookupTableImport provider = getWizard().getProvider();
		List<String> items = new ArrayList<String>();
		try {
			if (provider instanceof CSVLookupReader) {
				CSVReader reader = CSVUtil.readFirst(getWizard().getProvider());
				return reader.readNext();
			}
			else {
				Workbook workbook;
				// write xls file
				String file = provider.getSource().getLocation().getPath();
				String fileExtension = file.substring(file.lastIndexOf("."), file.length());
				if (fileExtension.equals(".xls")) {
					workbook = new HSSFWorkbook(provider.getSource().getInput());
				}
				// write xlsx file
				else if (fileExtension.equals(".xlsx")) {
					workbook = new XSSFWorkbook(provider.getSource().getInput());
				}
				else
					return new String[0];
				Sheet sheet = workbook.getSheetAt(0);
				Row currentRow = sheet.getRow(0);
				for (int cell = 0; cell < currentRow.getPhysicalNumberOfCells(); cell++) {
					items.add(currentRow.getCell(cell).getStringCellValue());
				}
				return items.toArray(new String[0]);
			}
		} catch (IOException e) {
			return new String[0];
		}
	}

	// add table for the preview to the given composite
	private void addPreview(Composite page) {

		tableContainer = new Composite(page, SWT.NONE);
		tableContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableColumnLayout layout = new TableColumnLayout();
		tableContainer.setLayout(layout);

		tableViewer = new TableViewer(tableContainer, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		sourceColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		layout.setColumnData(sourceColumn.getColumn(), new ColumnWeightData(1));
		sourceColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				@SuppressWarnings("unchecked")
				Entry<Value, Value> entry = (Entry<Value, Value>) element;
				return entry.getKey().getStringRepresentation();
			}
		});

		targetColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		layout.setColumnData(targetColumn.getColumn(), new ColumnWeightData(1));
		targetColumn.setLabelProvider(new StyledCellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				@SuppressWarnings("unchecked")
				Entry<Value, Value> entry = (Entry<Value, Value>) cell.getElement();
				if (entry.getValue() == null) {
					StyledString styledString = new StyledString("(unmapped)",
							StyledString.DECORATIONS_STYLER);
					cell.setText(styledString.getString());
					cell.setStyleRanges(styledString.getStyleRanges());
				}
				else {
					cell.setText(entry.getValue().getStringRepresentation());
					cell.setStyleRanges(null);
				}
				super.update(cell);
			}
		});
	}

	// read lookup table from file (specified by provider in corresponding
	// wizard)
	private Map<Value, Value> readLookupTable() {
		Map<Value, Value> lookupTable = new HashMap<Value, Value>();
		try {
			LookupTableImport provider = getWizard().getProvider();
			if (provider instanceof CSVLookupReader) {
				DefaultCSVLookupReader reader = new DefaultCSVLookupReader();
				lookupTable = reader
						.read(provider.getSource().getInput(), provider.getCharset(), provider
								.getParameter(CSVConstants.PARAM_SEPARATOR).as(String.class)
								.charAt(0),
								provider.getParameter(CSVConstants.PARAM_QUOTE).as(String.class)
										.charAt(0), provider
										.getParameter(CSVConstants.PARAM_ESCAPE).as(String.class)
										.charAt(0), skip, keyColumn.getSelectionIndex(),
								valueColumn.getSelectionIndex());
			}
			else {
				Workbook workbook;
				// write xls file
				String file = provider.getSource().getLocation().getPath();
				String fileExtension = file.substring(file.lastIndexOf("."), file.length());
				if (fileExtension.equals(".xls")) {
					workbook = new HSSFWorkbook(provider.getSource().getInput());
				}
				// write xlsx file
				else if (fileExtension.equals(".xlsx")) {
					workbook = new XSSFWorkbook(provider.getSource().getInput());
				}
				else
					return new HashMap<Value, Value>();
				DefaultXLSLookupTableReader reader = new DefaultXLSLookupTableReader();
				lookupTable = reader.read(workbook, skip, keyColumn.getSelectionIndex(),
						valueColumn.getSelectionIndex());
			}
		} catch (IOException e) {
			return lookupTable;
		}
		return lookupTable;
	}

	// read lookup table from file and refresh the table view
	private void refreshTable() {

		lookupTable = readLookupTable();

		sourceColumn.getColumn().setText(keyColumn.getText());
		targetColumn.getColumn().setText(valueColumn.getText());

		tableViewer.setInput(lookupTable.entrySet());
		tableViewer.refresh();

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
