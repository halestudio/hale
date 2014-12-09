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

package eu.esdihumboldt.hale.ui.autocorrelation;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.ui.HaleWizardPage;

/**
 * Page to select/set parameter of the auto correlation function
 * 
 * @author Yasmina Kammeyer
 */
public class AutoCorrelationParameterPage extends HaleWizardPage<AutoCorrelationFunctionWizard> {

	private Composite pageComposite;
	private Button useSuperType;
	private Button ignoreNamespace;
	private Button useStructuralRename;
	private Combo mode;

	/**
	 * @param pageName The name of the page
	 */
	protected AutoCorrelationParameterPage(String pageName) {
		super(pageName);

		setTitle(pageName);
		setDescription("Please configure the parameter.");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {

		GridLayout layout = new GridLayout(1, true);
		page.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(page);

		pageComposite = new Composite(page, SWT.NONE);
		pageComposite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(pageComposite);
		Label test = new Label(pageComposite, SWT.NONE);
		test.setText("Choose 'Rename only' if you want to ignore type matching.");
		mode = new Combo(pageComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		mode.add("Retype and Rename (based on Type matching)");
		mode.add("Retype only (based on Type matching)");
		// mode.add("Rename only (based on Attribute matching)");
		mode.select(0); // default selection
		GridDataFactory.swtDefaults().grab(true, false).applyTo(mode);

		Label superTypeLabel = new Label(pageComposite, SWT.NONE);
		superTypeLabel.setText("Check if attribute mapping should be inherited to sub types.");
		useSuperType = new Button(pageComposite, SWT.CHECK);
		useSuperType.setText("Use super type");
		useSuperType.setSelection(true);
		GridDataFactory.swtDefaults().grab(true, false).applyTo(useSuperType);

		Label ignoreNamespaceLabel = new Label(pageComposite, SWT.NONE);
		ignoreNamespaceLabel
				.setText("Check if only types or properties with equal namespaces should be mapped.");
		ignoreNamespace = new Button(pageComposite, SWT.CHECK);
		ignoreNamespace.setText("Ignore Namespace");
		ignoreNamespace.setSelection(true);
		GridDataFactory.swtDefaults().grab(true, false).applyTo(ignoreNamespace);

		Label useStructuralRenameLabel = new Label(pageComposite, SWT.WRAP);
		useStructuralRenameLabel
				.setText("Check if congruent properties with same sup-property structure should be atomatically mapped with 'Structural Rename' option.");
		useStructuralRename = new Button(pageComposite, SWT.CHECK);
		useStructuralRename.setText("Use Structural Rename");
		useStructuralRename.setSelection(true);
		GridDataFactory.createFrom(new GridData(GridData.FILL_HORIZONTAL)).grab(true, false)
				.applyTo(useStructuralRenameLabel);
		GridDataFactory.swtDefaults().grab(true, false).applyTo(useStructuralRename);

		setPageComplete(true);
		pageComposite.layout();
		pageComposite.pack();
	}

	/**
	 * @return the superType
	 */
	public boolean getUseSuperType() {
		return useSuperType.getSelection();
	}

	/**
	 * @return the ignoreNamespace
	 */
	public boolean getIgnoreNamespace() {
		return ignoreNamespace.getSelection();
	}

	/**
	 * @return the ignoreNamespace
	 */
	public boolean getUseStructuralRename() {
		return useStructuralRename.getSelection();
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode.getSelectionIndex();
	}

}
