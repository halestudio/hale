/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.custom.pages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.functions.custom.CustomPropertyFunctionWizard;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class MainPage extends HaleWizardPage<CustomPropertyFunctionWizard> implements
		CustomFunctionWizardPage {

	private Text ident;
	private Text name;

	/**
	 * Default constructor.
	 */
	public MainPage() {
		super("customFunction");

		setTitle("Custom function definition");
		setDescription("Please provide information to identify your custom function");
	}

	@Override
	public void apply() {
		if (ident != null && name != null) {
			DefaultCustomPropertyFunction cf = getWizard().getResultFunction();
			cf.setIdentifier(ident.getText());
			cf.setName(name.getText());
		}
	}

	@Override
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(page);

		GridDataFactory labelData = GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING);
		GridDataFactory fieldData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false);

		// identifier

		Label labelIdent = new Label(page, SWT.NONE);
		labelIdent.setText("Identifier");
		labelData.applyTo(labelIdent);

		ident = new Text(page, SWT.SINGLE | SWT.BORDER);
		fieldData.applyTo(ident);

		// name

		Label labelName = new Label(page, SWT.NONE);
		labelName.setText("Name");
		labelData.applyTo(labelName);

		name = new Text(page, SWT.SINGLE | SWT.BORDER);
		fieldData.applyTo(name);

		// TODO description
	}

}
