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

package eu.esdihumboldt.hale.io.csv.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for exporting of instances
 * 
 * @author Emanuela Epure
 */
public class CommonInstanceExportConfigurationPage
		extends AbstractConfigurationPage<InstanceWriter, IOWizard<InstanceWriter>> {

	protected Button solveNestedProperties;
	protected Button useSchema;
	protected Composite page;

	/**
	 * @param title
	 * 
	 */
	public CommonInstanceExportConfigurationPage(String title) {
		super(title);
		setTitle("Additonal Export Options");
		setDescription("Select if nested properties should be solved and a type");
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
	public boolean updateConfiguration(InstanceWriter provider) {
		provider.setParameter(InstanceTableIOConstants.SOLVE_NESTED_PROPERTIES,
				Value.of(solveNestedProperties.getSelection()));
		provider.setParameter(InstanceTableIOConstants.USE_SCHEMA,
				Value.of(useSchema.getSelection()));
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		this.page = page;

		page.setLayout(new GridLayout(1, false));

		solveNestedProperties = new Button(page, SWT.CHECK);
		solveNestedProperties.setText("Add nested properties");
		solveNestedProperties.setSelection(true);

		useSchema = new Button(page, SWT.CHECK);
		useSchema.setText("Use the source schema for the order of the exported columns");
		useSchema.setSelection(true);
	}

}
