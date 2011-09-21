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

package eu.esdihumboldt.hale.io.csv.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;
import eu.esdihumboldt.hale.ui.io.schema.SchemaReaderConfigurationPage;

/**
 * Advanced configuration for the SchemaReader
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class ReadConfigurationPage extends SchemaReaderConfigurationPage
		implements ModifyListener {

	private Label separator;
	private Label quote;
	private Label escape;

	private Combo combo;
	private Combo combo2;
	private Combo combo3;

	private HashMap<String, String> map = new HashMap<String, String>();

	/**
	 * default constructor
	 */
	public ReadConfigurationPage() {
		super("CSVRead");

		setTitle("Reader Settings");
		setDescription("Set the Separating character, Quote character and Escape character");
	}

	/**
	 * sets the PageComplete boolean to true, if the text is modified
	 * 
	 * @see org.eclipse.swt.events.ModifyListener
	 */
	@Override
	public void modifyText(ModifyEvent e) {
		setPageComplete(true);

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(SchemaReader provider) {

		setErrorMessage("You have not entered valid characters");

		map.put("TAB", "\t");

//		if (map.containsKey(combo.getText())
//				&& map.get(combo.getText()).length() <= 2) {
//			provider.setParameter(CSVSchemaReader.PARAM_SEPARATOR,
//					map.get(combo.getText()));
//		} else if (combo.getText().length() == 1
//				&& !(map.containsKey(combo.getText()))) {
//			provider.setParameter(CSVSchemaReader.PARAM_SEPARATOR,
//					combo.getText());
//		}
//		getErrorMessage();
//		System.out.println(map.get(combo.getText()));
//		setPageComplete(false);

		provider.setParameter(CSVSchemaReader.PARAM_SEPARATOR, map.get(combo.getText()));
		provider.setParameter(CSVSchemaReader.PARAM_QUOTE, combo2.getText());
		provider.setParameter(CSVSchemaReader.PARAM_ESCAPE, combo3.getText());
		
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));
		// column 1, row 1
		separator = new Label(page, SWT.NONE);
		separator.setText("Select Separating Sign");
		// column 2, row 1
		combo = new Combo(page, SWT.NONE);
		combo.setItems(new String[] { "TAB", ",", "." });
		combo.addModifyListener(this);

		// column 1, row 2
		quote = new Label(page, SWT.NONE);
		quote.setText("Select Quote Sign");

		// column 2, row 2
		combo2 = new Combo(page, SWT.NONE);
		combo2.setItems(new String[] { "\" ", "\'", ",", "-" });
		combo2.addModifyListener(this);

		// column 1, row 3
		escape = new Label(page, SWT.NONE);
		escape.setText("Select Escape Sign");

		// column 2, row 3
		combo3 = new Combo(page, SWT.NONE);
		combo3.setItems(new String[] { "\\", "." });
		combo3.addModifyListener(this);

		page.pack();

		setPageComplete(false);

	}

}
