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

package eu.esdihumboldt.hale.ui.io.instance;

import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Wizard page for choosing export configurations to delete
 * 
 * @author Patrick Lieb
 */
public class DeleteInstanceExportConfigurationPage extends
		AbstractConfigurationPage<InstanceWriter, DeleteInstanceExportConfigurationWizard>
		implements InstanceExportConfigurations {

	private ListViewer configurations;

	/**
	 * Default constructor
	 */
	public DeleteInstanceExportConfigurationPage() {
		super("del.exportConfig");

		setTitle("Delete an export configuration");
		setDescription("Please select the export configuration(s) to delete");
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		// layout of the page is a grid layout with one column
		page.setLayout(new GridLayout(1, false));

		// configure the list with all available export configurations
		configurations = new ListViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.MULTI
				| SWT.BORDER);
		configurations.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOConfiguration) {
					Map<String, Value> providerConf = ((IOConfiguration) element)
							.getProviderConfiguration();
					String name = providerConf.get(param_configurationName)
							.getStringRepresentation();
					String contentType = providerConf.get(ExportProvider.PARAM_CONTENT_TYPE)
							.getStringRepresentation();
					return name + "  (" + contentType + ")";
				}
				return super.getText(element);
			}
		});
		configurations.setContentProvider(ArrayContentProvider.getInstance());

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		configurations.getControl().setLayoutData(layoutData);

		// set all available export configurations in the list viewer
		configurations.setInput(getWizard().getExportConfigurations());

		// process selection
		configurations.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				setPageComplete(!selection.isEmpty());
				updateWizard((StructuredSelection) selection);
			}
		});
		// no configuration is selected
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not relevant on this page

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// not relevant on this page
	}

	// set all export configurations which should be deleted in the wizard
	@SuppressWarnings("unchecked")
	private void updateWizard(StructuredSelection selection) {
		getWizard().setSelection(selection.toList());

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceWriter provider) {
		return true;
	}

}
