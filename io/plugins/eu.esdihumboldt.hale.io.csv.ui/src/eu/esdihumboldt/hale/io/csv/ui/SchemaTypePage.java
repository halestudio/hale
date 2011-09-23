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

import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVSchemaReader;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.io.schema.SchemaReaderConfigurationPage;

/**
 * Creates the Page used for the Schema Type
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class SchemaTypePage extends SchemaReaderConfigurationPage {

	private String defaultString = "";
	private StringFieldEditor sfe;
	private StringFieldEditor propField;
	private Group group;

	/**
	 * default constructor
	 */
	public SchemaTypePage() {
		super("Schema Type");
		// is never used

		setTitle("Typename Settings");
		setDescription("Enter a valid Name for your Type");
		
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
	 * @see IOWizardPage#updateConfiguration
	 */
	@Override
	public boolean updateConfiguration(SchemaReader provider) {

		provider.setParameter(CSVSchemaReader.PARAM_TYPENAME,
				sfe.getStringValue());
		return true;

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {

		LocatableInputSupplier<? extends InputStream> source = getWizard()
				.getProvider().getSource();

		int indexStart = 0;
		int indexEnd = source.getLocation().getPath().length() - 1;

		if (source.getLocation().getPath() != null) {
			indexStart = source.getLocation().getPath().lastIndexOf("/") + 1;
			if (source.getLocation().getPath().lastIndexOf(".") >= 0) {
				indexEnd = source.getLocation().getPath().lastIndexOf(".");
			}

			defaultString = source.getLocation().getPath()
					.substring(indexStart, indexEnd);
			sfe.setStringValue(defaultString);
			setPageComplete(sfe.isValid());
		}
		
		try {
			CSVReader reader = CSVSchemaReader.readFirst(getWizard().getProvider());
			String[] firstLine = reader.readNext();
			int length = firstLine.length;
			
			for (int i = 0; i < length; i++) {
		        propField = new TypeNameField("properties", Integer.toString(i+1) , group);
		        propField.setEmptyStringAllowed(false);
				propField.setErrorMessage("Please enter a valid Property Name");
				propField.setPropertyChangeListener(new IPropertyChangeListener() {
		
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						if (event.getProperty().equals(StringFieldEditor.IS_VALID)) {
							setPageComplete((Boolean) event.getNewValue());
						}
					}
				});
		        propField.setStringValue(firstLine[i]);
		    }
			
		} catch (IOException e) {
			setErrorMessage("File could not be read");
			setPageComplete(false);
			e.printStackTrace();
		}

		group.layout();
		group.getParent().layout(true, true);
		super.onShowPage();
	}

	/**
	 * @see HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));

		sfe = new TypeNameField("typename", "Typename", page);
		sfe.setEmptyStringAllowed(false);
		sfe.setErrorMessage("Please enter a valid Type Name");
		sfe.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(StringFieldEditor.IS_VALID)) {
					setPageComplete((Boolean) event.getNewValue());
				}
			}
		});

		sfe.setStringValue(defaultString);
		sfe.setPage(this);

		group = new Group(page, SWT.NONE);
		group.setText("Properties");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(5, 5).create());
		
		setPageComplete(sfe.isValid());
	}

}
