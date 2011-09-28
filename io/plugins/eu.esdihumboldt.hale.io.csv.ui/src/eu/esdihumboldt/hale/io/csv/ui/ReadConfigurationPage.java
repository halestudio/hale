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

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.HashBiMap;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConfiguration;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConstants;
import eu.esdihumboldt.hale.ui.io.ImportWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Advanced configuration for the SchemaReader
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class ReadConfigurationPage
		extends
		AbstractConfigurationPage<ImportProvider, IOProviderFactory<ImportProvider>, ImportWizard<ImportProvider, IOProviderFactory<ImportProvider>>>
		implements ModifyListener {

	private Combo separator;
	private Combo quote;
	private Combo escape;

	private HashBiMap<String, String> bmap;

	private QName last_name;

	/**
	 * default constructor
	 */
	public ReadConfigurationPage() {
		super("CSVRead");

		setTitle("Reader Settings");
		setDescription("Set the Separating character, Quote character and Escape character");

		bmap = HashBiMap.create();
		bmap.put("TAB", "\t");

	}

	/**
	 * sets the PageComplete boolean to true, if the text is valid
	 * 
	 * @see org.eclipse.swt.events.ModifyListener
	 */
	@Override
	public void modifyText(ModifyEvent e) {

		String sep = separator.getText();
		String qu = quote.getText();
		String esc = escape.getText();

		if (sep.isEmpty() || sep.contains("/") || sep.contains(":")
				|| (bmap.get(sep) == null && sep.length() > 1) || qu.isEmpty()
				|| qu.contains("/") || qu.contains(":") || qu.contains(".")
				|| (bmap.get(qu) == null && qu.length() > 1) || esc.isEmpty()
				|| esc.contains("/") || esc.contains(":")
				|| (bmap.get(esc) == null && esc.length() > 1)) {
			setPageComplete(false);
			setErrorMessage("You have not entered valid characters!");
		} else if (sep.equals(qu) || qu.equals(esc) || esc.equals(sep)) {
			setPageComplete(false);
			setErrorMessage("Your signs must be different!");
		} else {
			setPageComplete(true);
			setErrorMessage(null);
		}

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
	public boolean updateConfiguration(ImportProvider provider) {

		String sep = separator.getText();
		String qu = quote.getText();
		String esc = escape.getText();

		if (bmap.get(sep) != null) {
			provider.setParameter(CSVConstants.PARAM_SEPARATOR, bmap.get(sep));
		} else {
			provider.setParameter(CSVConstants.PARAM_SEPARATOR, sep);
		}
		if (bmap.get(qu) != null) {
			provider.setParameter(CSVConstants.PARAM_QUOTE, bmap.get(qu));
		} else {
			provider.setParameter(CSVConstants.PARAM_QUOTE, qu);
		}
		if (bmap.get(esc) != null) {
			provider.setParameter(CSVConstants.PARAM_ESCAPE, bmap.get(esc));
		} else {
			provider.setParameter(CSVConstants.PARAM_ESCAPE, esc);
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, true));
		GridData layoutData = new GridData();
		layoutData.widthHint = 30;
		// column 1, row 1
		Label separatorLabel = new Label(page, SWT.NONE);
		separatorLabel.setText("Select Separating Sign");
		// column 2, row 1
		separator = new Combo(page, SWT.NONE);
		separator.setLayoutData(GridDataFactory.copyData(layoutData));
		separator.setItems(new String[] { "TAB", ",", "." });
		separator.select(0);
		separator.addModifyListener(this);

		// column 1, row 2
		Label quoteLabel = new Label(page, SWT.NONE);
		quoteLabel.setText("Select Quote Sign");

		// column 2, row 2
		quote = new Combo(page, SWT.NONE);
		quote.setLayoutData(GridDataFactory.copyData(layoutData));
		quote.setItems(new String[] { "\"", "\'", ",", "-" });
		quote.select(0);
		quote.addModifyListener(this);

		// column 1, row 3
		Label escapeLabel = new Label(page, SWT.NONE);
		escapeLabel.setText("Select Escape Sign");

		// column 2, row 3
		escape = new Combo(page, SWT.NONE);
		escape.setLayoutData(GridDataFactory.copyData(layoutData));
		escape.setItems(new String[] { "\\", "." });
		escape.select(0);
		escape.addModifyListener(this);

		page.pack();

		setPageComplete(true);

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		super.onShowPage();

		IOProvider p = getWizard().getProvider();

		if (p instanceof InstanceReader) {
			QName name = QName.valueOf(p
					.getParameter(CSVConstants.PARAM_TYPENAME));

			if (last_name == null || !(last_name.equals(name))) {
				TypeDefinition type = ((InstanceReader) p).getSourceSchema()
						.getType(name);
				CSVConfiguration config = type
						.getConstraint(CSVConfiguration.class);

				String sep = String.valueOf(config.getSeparator());
				if (bmap.inverse().get(sep) != null) {
					separator.setText(bmap.inverse().get(sep));
				} else {
					separator.setText(sep);
				}
				String qu = String.valueOf(config.getQuote());
				if (bmap.inverse().get(qu) != null) {
					quote.setText(bmap.inverse().get(qu));
				} else {
					quote.setText(qu);
				}
				String esc = String.valueOf(config.getEscape());
				if (bmap.inverse().get(esc) != null) {
					separator.setText(bmap.inverse().get(esc));
				} else {
					escape.setText(esc);
				}

				last_name = name;
			}

		}
		
		setPageComplete(true);
	}

}
