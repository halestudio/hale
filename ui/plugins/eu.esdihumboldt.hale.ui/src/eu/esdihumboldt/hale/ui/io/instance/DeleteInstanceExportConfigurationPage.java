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

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * TODO Type description
 * 
 * @author Patrick Lieb
 */
public class DeleteInstanceExportConfigurationPage extends
		AbstractConfigurationPage<InstanceWriter, DeleteInstanceExportConfigurationWizard> {

	private ListViewer list;

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
		page.setLayout(new GridLayout(1, false));

		// add filtered list
		list = new ListViewer(page, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		list.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IOConfiguration) {
					String name = ((IOConfiguration) element).getProviderConfiguration()
							.get("configurationName").getStringRepresentation();

					return name;
				}
				return super.getText(element);
			}

		});
		list.setContentProvider(ArrayContentProvider.getInstance());
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		layoutData.widthHint = SWT.DEFAULT;
		layoutData.heightHint = 8 * list.getList().getItemHeight();
		list.getControl().setLayoutData(layoutData);

		list.setInput(getWizard().getExportConfigurations());

		// page status update
		list.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				setPageComplete(!selection.isEmpty());
				updateWizard((StructuredSelection) selection);
			}
		});
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

//	@SuppressWarnings({ "unchecked", "unused" })
//	// get IOConfigurations here
//	// XXX implement here
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
