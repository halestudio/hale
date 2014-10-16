/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Page to select/set the source of the auto correlation function
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationTypesPage extends HaleWizardPage<AutoCorrelationFunctionWizard> {

	private Composite pageComposite;
	private Button processEntireSchema;

	/**
	 * @param pageName The name of the page
	 */
	protected AutoCorrelationTypesPage(String pageName) {
		super(pageName);

		setTitle(pageName);
		setDescription("Please choose/confirm your desired source types.");
	}

	/**
	 * Check if the page is valid and set the
	 * 
	 * @return true, if the page's state is valid
	 */
	private boolean isValid() {
		if (processEntireSchema.getSelection()) {
			setPageComplete(true);
			return true;
		}

		setPageComplete(false);
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		pageComposite.layout();
		pageComposite.pack();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		pageComposite = page;

		GridLayout layout = new GridLayout(1, false);
		page.setLayout(layout);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(page);

		Composite typeSelectorSpace = new Composite(page, SWT.NONE);
		typeSelectorSpace.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(typeSelectorSpace);

		// Source Type
		Label sourceLabel = new Label(typeSelectorSpace, SWT.NONE);
		sourceLabel.setText("Source Type: ");
		Label sourceType = new Label(typeSelectorSpace, SWT.BEGINNING);
		sourceType.setText("Wildcard source Type Selector");

		// Target Type
		Label targetLabel = new Label(typeSelectorSpace, SWT.NONE);
		targetLabel.setText("Target Type: ");
		Label targetType = new Label(typeSelectorSpace, SWT.BEGINNING);
		targetType.setText("Wildcard target Type Selector");

		// Checkbox entire Schema
		processEntireSchema = new Button(page, SWT.CHECK);
		processEntireSchema.setText("Process Entire Schema");
		processEntireSchema.setSelection(false);
		processEntireSchema.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (processEntireSchema.getSelection()) {
					// TODO disable type selector
				}
				else {
					// TODO enable type selector
				}
				isValid();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (processEntireSchema.getSelection()) {
					// TODO disable type selector
				}
				else {
					// TODO enable type selector
				}
				isValid();
			}
		});
		GridDataFactory.swtDefaults().grab(true, false).applyTo(processEntireSchema);

		setPageComplete(false);
		page.layout();
		page.pack();
	}
}
