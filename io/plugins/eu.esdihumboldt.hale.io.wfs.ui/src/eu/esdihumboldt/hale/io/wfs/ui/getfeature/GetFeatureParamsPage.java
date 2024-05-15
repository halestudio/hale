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

package eu.esdihumboldt.hale.io.wfs.ui.getfeature;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * Page for specifying configuration parameters for a GetFeature WFS request.
 * 
 * @author Simon Templer
 */
public class GetFeatureParamsPage extends ConfigurationWizardPage<WFSGetFeatureConfig> {

	private final String RESOLVE_DEPTH_ALL = "*";

	private Button maxFeaturesEnabled;
	private Spinner maxFeatures;
	private Button resolveLinks;
	private Button resolveLinksRadioValue;
	private Spinner resolveLinksValueSpinner;
	private Button resolveLinksRadioAllValues;

	/**
	 * Create a new wizard page.
	 * 
	 * @param wizard the parent wizard
	 */
	public GetFeatureParamsPage(ConfigurationWizard<? extends WFSGetFeatureConfig> wizard) {
		super(wizard, "wfsGetFeature");
		setTitle("WFS GetFeature");
		setDescription("Additional configuration options for the GetFeature request");

		// defaults are OK
		setPageComplete(true);
	}

	@Override
	public boolean updateConfiguration(WFSGetFeatureConfig configuration) {
		if (maxFeaturesEnabled.getSelection()) {
			configuration.setMaxFeatures(maxFeatures.getSelection());
		}
		else {
			configuration.setMaxFeatures(null);
		}

		if (resolveLinks.getSelection()) {
			if (resolveLinksRadioValue.getSelection()) {
				// Set resolve depth based on the value from the spinner
				int selectedDepth = resolveLinksValueSpinner.getSelection();
				configuration.setResolveDepth(String.valueOf(selectedDepth));
			}
			else if (resolveLinksRadioAllValues.getSelection()) {
				// Set resolve depth to a predefined value for all values
				configuration.setResolveDepth(RESOLVE_DEPTH_ALL);
			}
		}
		else {
			configuration.setResolveDepth(null);
		}
		return true;
	}

	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);

		Group maxFeaturesGroup = new Group(page, SWT.NONE);
		maxFeaturesGroup.setText("Number of features");
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(maxFeaturesGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(maxFeaturesGroup);

		maxFeaturesEnabled = new Button(maxFeaturesGroup, SWT.CHECK);
		maxFeaturesEnabled.setText("restrict to");
		maxFeaturesEnabled.setSelection(false);

		maxFeatures = new Spinner(maxFeaturesGroup, SWT.BORDER);
		maxFeatures.setMinimum(1);
		maxFeatures.setMaximum(500000);
		maxFeatures.setIncrement(100);
		maxFeatures.setPageIncrement(1000);
		maxFeatures.setSelection(10000);
		maxFeatures.setEnabled(false);

		maxFeaturesEnabled.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				maxFeatures.setEnabled(maxFeaturesEnabled.getSelection());
			}
		});

		Group resolveLinksGroup = new Group(page, SWT.NONE);
		resolveLinksGroup.setText("Resolve nested references (resolve depth)");
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(resolveLinksGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(resolveLinksGroup);

		resolveLinks = new Button(resolveLinksGroup, SWT.CHECK);
		resolveLinks.setText("Resolve nested references");
		resolveLinks.setSelection(false);

		// Add an empty string in the second column
		Label emptyLabel = new Label(resolveLinksGroup, SWT.NONE);
		emptyLabel.setText(""); // Set an empty string

		resolveLinksRadioValue = new Button(resolveLinksGroup, SWT.RADIO);
		resolveLinksRadioValue.setText("Resolve nested references to a depth of");
		resolveLinksRadioValue.setSelection(true);
		resolveLinksRadioValue.setEnabled(false);

		resolveLinksValueSpinner = new Spinner(resolveLinksGroup, SWT.BORDER);
		resolveLinksValueSpinner.setMinimum(1);
		resolveLinksValueSpinner.setMaximum(10);
		resolveLinksValueSpinner.setIncrement(1);
		resolveLinksValueSpinner.setPageIncrement(1);
		resolveLinksValueSpinner.setSelection(1);
		resolveLinksValueSpinner.setEnabled(false);

		resolveLinksRadioAllValues = new Button(resolveLinksGroup, SWT.RADIO);
		resolveLinksRadioAllValues.setText("Resolve all immediate and nested references ('*')");
		resolveLinksRadioAllValues.setEnabled(false);

		// Add an empty string in the second column
		Label emptyLabelAll = new Label(resolveLinksGroup, SWT.NONE);
		emptyLabelAll.setText(""); // Set an empty string

		resolveLinks.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				resolveLinksRadioValue.setEnabled(resolveLinks.getSelection());
				if (resolveLinks.getSelection() && resolveLinksRadioValue.getSelection()) {
					resolveLinksValueSpinner.setEnabled(resolveLinks.getSelection());
				}
				resolveLinksRadioAllValues.setEnabled(resolveLinks.getSelection());
			}
		});

		resolveLinksRadioValue.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				resolveLinksValueSpinner.setEnabled(resolveLinksRadioValue.getSelection());
			}
		});

		setControl(page);
	}

}
