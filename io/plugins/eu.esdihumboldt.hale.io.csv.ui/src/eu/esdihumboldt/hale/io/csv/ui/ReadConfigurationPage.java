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

package eu.esdihumboldt.hale.io.csv.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConfiguration;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConstants;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.ImportWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Advanced configuration for the SchemaReader
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class ReadConfigurationPage extends
		AbstractConfigurationPage<ImportProvider, ImportWizard<ImportProvider>> implements
		ModifyListener {

	private Combo separator;
	private Combo quote;
	private Combo escape;

	private final HashBiMap<String, String> bmap;

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
				|| (bmap.get(sep) == null && sep.length() > 1) || qu.isEmpty() || qu.contains("/")
				|| qu.contains(":") || qu.contains(".")
				|| (bmap.get(qu) == null && qu.length() > 1) || esc.isEmpty() || esc.contains("/")
				|| esc.contains(":") || (bmap.get(esc) == null && esc.length() > 1)) {
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
		/*
		 * Set page to incomplete by default, as only when the page is shown
		 * automated detection takes place. Thus finishing an import with the
		 * page not having been shown may differ from loading the same file with
		 * the page shown.
		 */
		setPageComplete(false);
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

		GridDataFactory labelLayout = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER)
				.grab(false, false);
		GridDataFactory comboLayout = GridDataFactory.swtDefaults()
				.align(SWT.BEGINNING, SWT.CENTER).grab(false, false);

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

		page.pack();

		setPageComplete(true);
	}

	/**
	 * Counts the number of a Character in a String
	 * 
	 * @param input the String
	 * @param toCount the Character to be count
	 * @return the number of the Character in the String
	 */
	private static int countChar(String input, char toCount) {
		int counter = 0;
		for (char c : input.toCharArray()) {
			if (c == toCount)
				counter++;
		}
		return counter;
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		IOProvider p = getWizard().getProvider();
		String[] separatorSelection = new String[] { "TAB", ",", "|", ".", ";" };

		try {
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(getWizard()
					.getProvider().getSource().getInput(), p.getCharset()));
			String line = streamReader.readLine();
			int tab = countChar(line, '\t');
			int comma = countChar(line, ',');
			int pipe = countChar(line, '|');
			int semicolon = countChar(line, ';');

			if (Math.max(tab, comma) == tab && Math.max(tab, pipe) == tab
					&& Math.max(tab, semicolon) == tab) {
				p.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of("TAB"));
			}
			else if (Math.max(comma, tab) == comma && Math.max(comma, pipe) == comma
					&& Math.max(comma, semicolon) == comma) {
				p.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of(","));
			}
			else if (Math.max(semicolon, tab) == semicolon
					&& Math.max(semicolon, comma) == semicolon
					&& Math.max(semicolon, pipe) == semicolon) {
				p.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of(";"));
			}
			else {
				p.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of("|"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		String selection = getWizard().getProvider().getParameter(CSVConstants.PARAM_SEPARATOR)
				.as(String.class);
		for (int i = 0; i < separatorSelection.length; i++) {
			if (separatorSelection[i] == selection) {
				separator.select(i);
				break;
			}
			else {
				separator.select(0);
			}
		}

		if (p instanceof InstanceReader) {
			QName name = QName
					.valueOf(p.getParameter(CSVConstants.PARAM_TYPENAME).as(String.class));

			if (last_name == null || !(last_name.equals(name))) {
				TypeDefinition type = ((InstanceReader) p).getSourceSchema().getType(name);
				CSVConfiguration config = type.getConstraint(CSVConfiguration.class);

				String sep = String.valueOf(config.getSeparator());
				if (bmap.inverse().get(sep) != null) {
					separator.setText(bmap.inverse().get(sep));
				}
				else {
					separator.setText(sep);
				}
				String qu = String.valueOf(config.getQuote());
				if (bmap.inverse().get(qu) != null) {
					quote.setText(bmap.inverse().get(qu));
				}
				else {
					quote.setText(qu);
				}
				String esc = String.valueOf(config.getEscape());
				if (bmap.inverse().get(esc) != null) {
					separator.setText(bmap.inverse().get(esc));
				}
				else {
					escape.setText(esc);
				}

				last_name = name;
			}

		}

		setPageComplete(true);
	}

}
