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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;
import eu.esdihumboldt.hale.ui.io.schema.SchemaReaderConfigurationPage;

/**
 * Creates the Page used for the Schema Type
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class SchemaTypePage extends SchemaReaderConfigurationPage {

	private Text nameText;
	private String defaultString = "Please insert the Typename";

	/**
	 * default constructor
	 */
	public SchemaTypePage() {
		super("Schema Type");
		// is never used
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.ui.io.IOProvider)
	 */
	@SuppressWarnings("restriction")
	@Override
	public boolean updateConfiguration(SchemaReader provider) {
		if (nameText.getText() != defaultString) {

			provider.setParameter(CSVSchemaReader.PARAM_TYPENAME,
					nameText.getText());
			return true;
		}

		provider.setParameter(CSVSchemaReader.PARAM_TYPENAME, "default Typename");
		return false;
		
	}

	/**
	 * @see HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(page, SWT.NONE);
		nameLabel.setText("Typename:");

		nameText = new Text(page, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		nameText.setLayoutData(gridData);
		nameText.setText(defaultString);

		page.pack();

	}

}
