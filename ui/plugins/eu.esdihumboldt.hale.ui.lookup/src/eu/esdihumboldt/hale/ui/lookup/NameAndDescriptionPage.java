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

package eu.esdihumboldt.hale.ui.lookup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.lookup.LookupTableImport;

/**
 * With this page you can set the name and description of the LookupTable
 * 
 * @author Dominik Reuter
 */
public class NameAndDescriptionPage extends LookupTableImportConfigurationPage implements
		ModifyListener {

	/**
	 * The description textfield
	 */
	private Text descText;

	/**
	 * The name textfield
	 */
	private Text nameText;

	/**
	 * Default Constructor
	 */
	public NameAndDescriptionPage() {
		super("Name and Descriptor");
		setTitle("Name and Description");
		setDescription("Set the Name and the Description for the LookupTable");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(LookupTableImport provider) {
		provider.setName(nameText.getText());
		provider.setDescription(descText.getText());
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));
		// Name
		Label name = new Label(page, SWT.NONE);
		name.setText("Name: ");
		nameText = new Text(page, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		nameText.addModifyListener(this);

		// Description
		Label desc = new Label(page, SWT.NONE);
		desc.setText("Description: ");
		descText = new Text(page, SWT.BORDER | SWT.SINGLE);
		descText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		descText.addModifyListener(this);

		setPageComplete(false);
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(ModifyEvent e) {
		if (nameText.getText().isEmpty()) {
			setPageComplete(false);
		}
		else {
			setPageComplete(true);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// nothing to do here.
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// nothing to do here.
	}
}
