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

package eu.esdihumboldt.hale.ui.io.project.update;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.schema.SchemaImportAdvisor;
import eu.esdihumboldt.hale.ui.io.schema.SchemaImportWizard;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Component that allows updating a schema location / configuration.
 * 
 * @author Simon Templer
 */
public class SchemaUpdateComponent extends Composite {

	private Button updateButton;
	private TableViewer tableViewer;
	private String actionId;
	private List<IOConfiguration> configurations;

	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param actionId the action ID
	 * @param configurations the list of configurations (the component may make
	 *            changes to this list)
	 */
	public SchemaUpdateComponent(Composite parent, final String actionId,
			final List<IOConfiguration> configurations) {
		super(parent, SWT.NONE);
		this.actionId = actionId;
		this.configurations = configurations;

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(this);

		// intro label
		Label intro = new Label(this, SWT.WRAP);
		intro.setText("Please select a schema from the list to update/replace it:");
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(intro);

		// viewer with schema list

		Composite tableContainer = new Composite(this, SWT.NONE);
		tableContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableColumnLayout layout = new TableColumnLayout();
		tableContainer.setLayout(layout);

		tableViewer = new TableViewer(tableContainer, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IOConfiguration) {
					return actionId.equals(((IOConfiguration) element).getActionId());
				}
				return false;
			}

		});

		TableViewerColumn typeColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		layout.setColumnData(typeColumn.getColumn(), new ColumnWeightData(1));
		typeColumn.getColumn().setText("Type");
		typeColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				IOConfiguration config = (IOConfiguration) element;
				return IOProviderExtension.getInstance().getFactory(config.getProviderId())
						.getDisplayName();
			}
		});

		TableViewerColumn locationColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		layout.setColumnData(locationColumn.getColumn(), new ColumnWeightData(3));
		locationColumn.getColumn().setText("Location");
		locationColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				IOConfiguration config = (IOConfiguration) element;
				return config.getProviderConfiguration().get(ImportProvider.PARAM_SOURCE)
						.toString();
			}
		});

		tableViewer.setInput(configurations);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState();
			}
		});

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				updateSelectedSchema();
			}
		});

		// update button
		updateButton = new Button(this, SWT.PUSH);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(updateButton);
		updateButton.setText("Update");
		updateButton.setToolTipText("Update/replace the selected schema");
		updateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSelectedSchema();
			}
		});

		// initial state update
		updateState();
	}

	/**
	 * 
	 */
	protected void updateSelectedSchema() {
		ISelection sel = tableViewer.getSelection();
		if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
			final IOConfiguration selected = (IOConfiguration) ((IStructuredSelection) sel)
					.getFirstElement();

			SchemaImportWizard wizard = new SchemaImportWizard() {

				@Override
				public boolean performFinish() {
					if (!applyConfiguration()) {
						return false;
					}

					IOConfiguration configuration = new IOConfiguration();
					configuration.setActionId(getActionId());
					configuration.setProviderId(getProviderFactory().getIdentifier());
					getProvider().storeConfiguration(configuration.getProviderConfiguration());

					// replace the previously selected I/O configuration
					int index = configurations.indexOf(selected);
					configurations.set(index, configuration);

					// refresh table viewer to reflect the changes
					tableViewer.refresh(true);

					return true;
				}

			};

			// configure advisor
			// FIXME
			SchemaImportAdvisor advisor = new SchemaImportAdvisor(SchemaSpaceID.TARGET);
			advisor.setServiceProvider(HaleUI.getServiceProvider());
			advisor.setActionId(SchemaIO.ACTION_LOAD_TARGET_SCHEMA);
			wizard.setAdvisor(advisor, actionId);

			// open wizard
			Shell shell = Display.getCurrent().getActiveShell();
			HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
			dialog.open();
		}
	}

	/**
	 * Update the component state.
	 */
	private void updateState() {
		boolean selected = !tableViewer.getSelection().isEmpty();
		updateButton.setEnabled(selected);
	}

}
