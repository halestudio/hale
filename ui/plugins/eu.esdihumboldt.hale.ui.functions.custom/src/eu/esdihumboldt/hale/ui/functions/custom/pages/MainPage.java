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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

		setPageComplete(false);
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

		GridDataFactory labelData = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER);
		GridDataFactory fieldData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false);

		ModifyListener modify = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState();
			}
		};

		// identifier

		Label labelIdent = new Label(page, SWT.NONE);
		labelIdent.setText("Identifier");
		labelData.applyTo(labelIdent);

		ident = new Text(page, SWT.SINGLE | SWT.BORDER);
		ident.addModifyListener(modify);
		fieldData.applyTo(ident);

		// name

		Label labelName = new Label(page, SWT.NONE);
		labelName.setText("Name");
		labelData.applyTo(labelName);

		name = new Text(page, SWT.SINGLE | SWT.BORDER);
		name.addModifyListener(modify);
		fieldData.applyTo(name);

		// TODO description

		// load from initial function
		DefaultCustomPropertyFunction cf = getWizard().getResultFunction();
		if (cf.getName() != null) {
			name.setText(cf.getName());
		}
		if (cf.getIdentifier() != null) {
			ident.setText(cf.getIdentifier());
		}

		updateState();
	}

	/**
	 * Update the page state.
	 */
	private void updateState() {
		boolean complete = false;
		if (ident != null) {
			String id = ident.getText();
			if (id == null || id.isEmpty()) {
				setErrorMessage("Please specify an identifier for the function");
			}
			else {
				// TODO check identifier for uniqueness?

				String nameStr = name.getText();
				if (nameStr == null || nameStr.isEmpty()) {
					setErrorMessage("Please provide a name for the function");
				}
				else {
					complete = true;
				}
			}
		}

		if (complete) {
			setErrorMessage(null);
		}
		setPageComplete(complete);
	}

}
