/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.csv.ui;

import javax.xml.namespace.QName;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
 * Advance Configuration for instance reader
 * 
 * @author Arun
 */
public class ReadInstanceConfigurationPage extends AbstractCSVConfigurationPage<ImportProvider> {

	private Combo decimal;

	/**
	 * default constructor
	 */
	public ReadInstanceConfigurationPage() {
		super("CSVInstanceRead");
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

	@Override
	public boolean updateConfiguration(ImportProvider provider) {

		String sep = getSeparator().getText();
		String qu = getQuote().getText();
		String esc = getEscape().getText();
		String dec = decimal.getText();

		if (getBmap().get(sep) != null) {
			provider.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of(getBmap().get(sep)));
		}
		else {
			provider.setParameter(CSVConstants.PARAM_SEPARATOR, Value.of(sep));
		}
		if (getBmap().get(qu) != null) {
			provider.setParameter(CSVConstants.PARAM_QUOTE, Value.of(getBmap().get(qu)));
		}
		else {
			provider.setParameter(CSVConstants.PARAM_QUOTE, Value.of(qu));
		}
		if (getBmap().get(esc) != null) {
			provider.setParameter(CSVConstants.PARAM_ESCAPE, Value.of(getBmap().get(esc)));
		}
		else {
			provider.setParameter(CSVConstants.PARAM_ESCAPE, Value.of(esc));
		}

		if (getBmap().get(dec) != null) {
			provider.setParameter(CSVConstants.PARAM_DECIMAL, Value.of(getBmap().get(dec)));
		}
		else {
			provider.setParameter(CSVConstants.PARAM_DECIMAL, Value.of(dec));
		}

		return true;
	}

	@Override
	protected void createContent(Composite page) {
		super.createContent(page);

		// column 1, row 4
		Label decimalLabel = new Label(page, SWT.NONE);
		decimalLabel.setText("Select Decimal Divisor");
		getLabelLayout().applyTo(decimalLabel);

		// column 2, row 4
		decimal = new Combo(page, SWT.NONE);
		decimal.setItems(new String[] { ".", "," });
		decimal.select(0);
		decimal.addModifyListener(this);
		getComboLayout().applyTo(decimal);

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
		setPageComplete(true);
	}
}
