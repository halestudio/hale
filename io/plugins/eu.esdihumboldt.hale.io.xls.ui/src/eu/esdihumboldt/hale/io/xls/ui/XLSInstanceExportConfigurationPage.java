/*
 * Copyright (c) 2023 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.xls.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.csv.InstanceTableIOConstants;
import eu.esdihumboldt.hale.io.csv.ui.CommonInstanceExportConfigurationPage;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Configuration page for exporting Excel
 * 
 * @author Emanuela Epure
 */
public class XLSInstanceExportConfigurationPage extends CommonInstanceExportConfigurationPage {

	private CheckboxTableViewer featureTypeTable;
	private Button selectAll = null;
	private Group chooseFeatureTypes;
	private Table table;
	private Button ignoreEmptyFeaturetypes = null;

	/**
	 * 
	 */
	public XLSInstanceExportConfigurationPage() {
		super("xlsInstanceExport.configPage");
		setTitle("Additonal Export Options");
		setDescription("Select if nested properties should be solved and a type");
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.ui.InstanceExportConfigurationPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		super.createContent(page);

		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);
		chooseFeatureTypes = new Group(page, SWT.NONE);
		chooseFeatureTypes.setLayout(new GridLayout(1, false));
		chooseFeatureTypes.setText("Choose your Type you want to export");
		groupData.applyTo(chooseFeatureTypes);

		page.pack();

		// wait for selected type
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.ui.InstanceExportConfigurationPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		if (firstShow) {

			ignoreEmptyFeaturetypes = new Button(chooseFeatureTypes, SWT.CHECK);
			ignoreEmptyFeaturetypes.setText("Exclude feature types without data");
			ignoreEmptyFeaturetypes
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

			selectAll = new Button(chooseFeatureTypes, SWT.CHECK);
			selectAll.setText("Select all");
			selectAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

			selectAll.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					featureTypeTable.setAllChecked(((Button) e.getSource()).getSelection());
					page.layout();
					page.pack();
					setPageComplete(validate());
				}
			});

			table = new Table(chooseFeatureTypes, SWT.CHECK | SWT.MULTI | SWT.SCROLL_PAGE);
			table.setHeaderVisible(false);
			table.setLinesVisible(false);
			table.setBackground(PlatformUI.getWorkbench().getDisplay()
					.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);
			groupData.applyTo(table);

			featureTypeTable = new CheckboxTableViewer(table);
			featureTypeTable.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					if ((element instanceof TypeDefinition) && hasNoData(element))
						return ((TypeDefinition) element).getDisplayName() + " [no data]";

					return ((TypeDefinition) element).getDisplayName();
				}

			});
			featureTypeTable.setContentProvider(ArrayContentProvider.getInstance());

			featureTypeTable.setInput(
					getWizard().getProvider().getTargetSchema().getMappingRelevantTypes());

			featureTypeTable.addCheckStateListener(new ICheckStateListener() {

				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					// Programmatic action to toggle the state
					selectAll.setSelection(
							featureTypeTable.getCheckedElements().length == featureTypeTable
									.getTable().getItemCount());

					page.layout();
					page.pack();
					setPageComplete(validate());
				}
			});

			page.layout();
			page.pack();
		}
	}

	/**
	 * @param element element to be checked if has data
	 * @return true if the element has no data
	 */
	private boolean hasNoData(Object element) {
		InstanceService instanceService = PlatformUI.getWorkbench()
				.getService(InstanceService.class);

		Set<TypeDefinition> instanceSourceTypes = instanceService.getInstanceTypes(DataSet.SOURCE);
		if (instanceSourceTypes.contains(element)) {
			return false;
		}

		Set<TypeDefinition> instanceTransformedTypes = instanceService
				.getInstanceTypes(DataSet.TRANSFORMED);
		if (instanceTransformedTypes.contains(element)) {
			return false;
		}
		return true;
	}

	private boolean validate() {
		return (featureTypeTable.getCheckedElements().length > 0);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.ui.InstanceExportConfigurationPage#updateConfiguration(eu.esdihumboldt.hale.common.instance.io.InstanceWriter)
	 */
	@Override
	public boolean updateConfiguration(InstanceWriter provider) {
		super.updateConfiguration(provider);

		List<Object> sourceList = new ArrayList<>(
				Arrays.asList(featureTypeTable.getCheckedElements()));
		sourceList.removeAll(Arrays.asList(featureTypeTable.getGrayedElements()));

		String param = "";
		for (Object el : sourceList) {
			param = param + ((TypeDefinition) el).getName().toString() + ",";
		}
		provider.setParameter(InstanceTableIOConstants.EXPORT_TYPE, Value.of(param));
		provider.setParameter(InstanceTableIOConstants.EXPORT_IGNORE_EMPTY_FEATURETYPES,
				Value.of(ignoreEmptyFeaturetypes.getSelection()));

		return true;
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

}
