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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.ui.common.definition.selector.TypeDefinitionSelector;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Configuration page for exporting of instances
 * 
 * @author Patrick Lieb
 */
public class InstanceExportConfigurationPage
		extends AbstractConfigurationPage<InstanceWriter, IOWizard<InstanceWriter>> {

	protected Button solveNestedProperties;
	protected Button useSchema;
	private TypeDefinitionSelector typeSelector;
	protected Composite page;

	private final ViewerFilter validTypesToSelect = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (!(element instanceof TypeDefinition))
				return false;
			InstanceService ins = PlatformUI.getWorkbench().getService(InstanceService.class);
			// select all source type which has at least one instance
			if (!ins.getInstances(DataSet.SOURCE).select(new TypeFilter((TypeDefinition) element))
					.isEmpty()) {
				return true;
			}
			// select all type which has at least one transformed instance
			if (!ins.getInstances(DataSet.TRANSFORMED)
					.select(new TypeFilter((TypeDefinition) element)).isEmpty()) {
				return true;
			}
			return false;
		}
	};

	/**
	 * 
	 */
	public InstanceExportConfigurationPage() {
		super("xlsInstanceExport.configPage");
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
		provider.setParameter(InstanceTableIOConstants.EXPORT_TYPE,
				Value.of(typeSelector.getSelectedObject().getName().toString()));
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
		solveNestedProperties.setText("Solve nested properties");
		solveNestedProperties.setSelection(true);

		useSchema = new Button(page, SWT.CHECK);
		useSchema.setText("Use the source schema for the order of the exported columns");
		useSchema.setSelection(true);

		final Label label = new Label(page, SWT.NONE);
		label.setText("Choose your Type you want to export:");

		page.pack();

		// wait for selected type
		setPageComplete(false);
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		if (firstShow) {
			ViewerFilter[] filters = { validTypesToSelect };

			typeSelector = new TypeDefinitionSelector(page, "Select the corresponding schema type",
					getWizard().getProvider().getTargetSchema(), filters);

			typeSelector.getControl().setLayoutData(
					GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
			typeSelector.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					setPageComplete(!(event.getSelection().isEmpty()));
					if (typeSelector.getSelectedObject() != null) {
						// TypeDefinition type =
						// typeSelector.getSelectedObject();
						// label.getParent().layout();
						page.layout();
						page.pack();
					}
				}
			});
		}
		page.layout();
		page.pack();
	}
}
