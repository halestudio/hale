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

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.reader.CSVConstants;
import eu.esdihumboldt.hale.io.csv.reader.CommonSchemaConstants;
import eu.esdihumboldt.hale.io.csv.reader.internal.CSVConfiguration;
import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Advanced configuration for the SchemaReader
 * 
 * @author Kevin Mais
 */
public class ReadConfigurationPage extends AbstractCSVConfigurationPage<ImportProvider> {

	/**
	 * default constructor
	 */
	public ReadConfigurationPage() {
		super("CSVRead");
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
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		super.createContent(page);
		page.pack();
		setPageComplete(true);
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
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(
					getWizard().getProvider().getSource().getInput(), p.getCharset()));
			String line = streamReader.readLine();
			int tab = 0, comma = 0, pipe = 0, semicolon = 0;
			if (line != null) {
				tab = countChar(line, '\t');
				comma = countChar(line, ',');
				pipe = countChar(line, '|');
				semicolon = countChar(line, ';');
			}

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
			if (separatorSelection[i].equals(selection)) {
				getSeparator().select(i);
				break;
			}
			else {
				getSeparator().select(0);
			}
		}

		if (p instanceof InstanceReader) {
			QName name = QName
					.valueOf(p.getParameter(CommonSchemaConstants.PARAM_TYPENAME).as(String.class));

			if (getLast_name() == null || !(getLast_name().equals(name))) {
				TypeDefinition type = ((InstanceReader) p).getSourceSchema().getType(name);
				CSVConfiguration config = type.getConstraint(CSVConfiguration.class);

				String sep = String.valueOf(config.getSeparator());
				if (getBmap().inverse().get(sep) != null) {
					getSeparator().setText(getBmap().inverse().get(sep));
				}
				else {
					getSeparator().setText(sep);
				}
				String qu = String.valueOf(config.getQuote());
				if (getBmap().inverse().get(qu) != null) {
					getQuote().setText(getBmap().inverse().get(qu));
				}
				else {
					getQuote().setText(qu);
				}
				String esc = String.valueOf(config.getEscape());
				if (getBmap().inverse().get(esc) != null) {
					getSeparator().setText(getBmap().inverse().get(esc));
				}
				else {
					getEscape().setText(esc);
				}

				setLast_name(name);
			}
		}
		setPageComplete(true);
	}
}
