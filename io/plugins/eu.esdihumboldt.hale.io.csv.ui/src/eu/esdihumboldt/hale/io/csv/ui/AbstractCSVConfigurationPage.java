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

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.HashBiMap;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.csv.reader.CSVConstants;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Abstract configuration page for csv I/O<br>
 * Sets the separator, quote and escape string in the provider
 * 
 * @author Kevin Mais
 * @param
 * 			<P>
 *            the provider type
 */
public abstract class AbstractCSVConfigurationPage<P extends IOProvider>
		extends AbstractConfigurationPage<P, IOWizard<P>>implements ModifyListener {

	private Combo separator;
	private Combo quote;
	private Combo escape;

	private final HashBiMap<String, String> bmap;

	private QName last_name;

	private final GridDataFactory labelLayout;
	private final GridDataFactory comboLayout;

	/**
	 * Default Constructor
	 * 
	 * @param pageName name of the page
	 */
	protected AbstractCSVConfigurationPage(String pageName) {
		super(pageName);
		setTitle("CSV Settings");
		setDescription("Set the Separating, Quote, Escape characters with decimal divisor");

		bmap = HashBiMap.create();
		bmap.put("TAB", "\t");

		labelLayout = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).grab(false, false);
		comboLayout = GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false,
				false);

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
				|| (bmap.get(sep) == null && sep.length() > 1) || qu.isEmpty() || qu.contains("/")
				|| qu.contains(":") || qu.contains(".") || (bmap.get(qu) == null && qu.length() > 1)
				|| esc.isEmpty() || esc.contains("/") || esc.contains(":")
				|| (bmap.get(esc) == null && esc.length() > 1)) {
			setPageComplete(false);
			setErrorMessage("You have not entered valid characters!");
		}
		else if (sep.equals(qu) || qu.equals(esc) || esc.equals(sep)) {
			setPageComplete(false);
			setErrorMessage("Your signs must be different!");
		}
		else {
			setPageComplete(true);
			setErrorMessage(null);
		}

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not required

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// not required

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(P provider) {

		String sep = separator.getText();
		String qu = quote.getText();
		String esc = escape.getText();

		if (bmap.get(sep) != null) {
			provider.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of(bmap.get(sep)));
		}
		else {
			provider.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of(sep));
		}
		if (bmap.get(qu) != null) {
			provider.setParameter(CSVConstants.PARAM_QUOTE, Value.of(bmap.get(qu)));
		}
		else {
			provider.setParameter(CSVConstants.PARAM_QUOTE, Value.of(qu));
		}
		if (bmap.get(esc) != null) {
			provider.setParameter(CSVConstants.PARAM_ESCAPE, Value.of(bmap.get(esc)));
		}
		else {
			provider.setParameter(CSVConstants.PARAM_ESCAPE, Value.of(esc));
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, true));
		String[] separatorSelection = new String[] { "TAB", ",", "|", ".", ";" };

		// column 1, row 1
		Label separatorLabel = new Label(page, SWT.NONE);
		separatorLabel.setText("Select Separating Sign");
		labelLayout.applyTo(separatorLabel);
		// column 2, row 1
		separator = new Combo(page, SWT.NONE);
		separator.setItems(separatorSelection);
		separator.addModifyListener(this);
		comboLayout.applyTo(separator);

		// column 1, row 2
		Label quoteLabel = new Label(page, SWT.NONE);
		quoteLabel.setText("Select Quote Sign");
		labelLayout.applyTo(quoteLabel);

		// column 2, row 2
		quote = new Combo(page, SWT.NONE);
		quote.setItems(new String[] { "\"", "\'", ",", "-" });
		quote.select(0);
		quote.addModifyListener(this);
		comboLayout.applyTo(quote);

		// column 1, row 3
		Label escapeLabel = new Label(page, SWT.NONE);
		escapeLabel.setText("Select Escape Sign");
		labelLayout.applyTo(escapeLabel);

		// column 2, row 3
		escape = new Combo(page, SWT.NONE);
		escape.setItems(new String[] { "\\", "." });
		escape.select(0);
		escape.addModifyListener(this);
		comboLayout.applyTo(escape);

		// select first item
		separator.select(0);
	}

	/**
	 * @return the separator
	 */
	public Combo getSeparator() {
		return separator;
	}

	/**
	 * @return the quote
	 */
	public Combo getQuote() {
		return quote;
	}

	/**
	 * @return the escape
	 */
	public Combo getEscape() {
		return escape;
	}

	/**
	 * @return the last_name
	 */
	public QName getLast_name() {
		return last_name;
	}

	/**
	 * @param last_name the last_name to set
	 */
	public void setLast_name(QName last_name) {
		this.last_name = last_name;
	}

	/**
	 * @return the bmap
	 */
	public HashBiMap<String, String> getBmap() {
		return bmap;
	}

	/**
	 * @return the labelLayout
	 */
	protected GridDataFactory getLabelLayout() {
		return labelLayout;
	}

	/**
	 * @return the comboLayout
	 */
	protected GridDataFactory getComboLayout() {
		return comboLayout;
	}

}
