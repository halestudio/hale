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

package eu.esdihumboldt.hale.ui.io.instance.exportconfig;

import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.ExportConfigurations;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Save configuration for exporting instances
 * 
 * @author Patrick Lieb
 */
public class SaveConfigurationInstanceExportPage
		extends AbstractConfigurationPage<InstanceWriter, SaveConfigurationInstanceExportWizard>
		implements ExportConfigurations {

	private Text name;
	private Text description;
	private ComboViewer fileFormats;

	/**
	 * Default Constructor
	 */
	public SaveConfigurationInstanceExportPage() {
		super("instancExport.Namepage");

		setTitle("Custom export configuration settings");
		setDescription("Configure the export configuration");
	}

	@Override
	public boolean updateConfiguration(InstanceWriter provider) {
		getWizard().setConfigurationName(name.getText());

		// set additional information to the provider
		provider.setParameter(PARAM_CONFIGURATION_DESCRIPTION, Value.of(description.getText()));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		// set grid layout with two columns
		page.setLayout(new GridLayout(2, false));

		// create name text field
		Label labelName = new Label(page, SWT.NONE);
		labelName.setText("Name:");
		labelName.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		name = new Text(page, SWT.BORDER | SWT.SINGLE);
		// add listener to set page complete if name is inserted
		name.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		});
		name.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false).create());

		// create description text field
		Label labelDesc = new Label(page, SWT.NONE);
		labelDesc.setText("Description:");
		labelDesc.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		description = new Text(page, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		// add listener to set page complete if description is inserted
		description.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		});
		GridData data = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false).create();
		data.heightHint = 75;
		description.setLayoutData(data);

		// create viewer for possible content types
		Label labelConf = new Label(page, SWT.NONE);
		labelConf.setText("Format:");
		labelConf.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		fileFormats = new ComboViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		data = GridDataFactory.fillDefaults().grab(true, false).create();
		// adapt viewer to size of current font
		fileFormats.getControl().setLayoutData(data);
		fileFormats.setContentProvider(ArrayContentProvider.getInstance());
		fileFormats.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof IContentType) {
					return ((IContentType) element).getName();
				}
				return super.getText(element);
			}
		});

		// process current selection
		ISelection selection = fileFormats.getSelection();
		setPageComplete(!selection.isEmpty());

		// process selection changes
		fileFormats.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				updateContentType(selection);
				update();
			}
		});

		update();
	}

	@Override
	public void enable() {
		// do nothing
	}

	@Override
	public void disable() {
		// do nothing
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		Set<IContentType> contentTypes = getWizard().getProviderFactory().getSupportedTypes();
		// content types are not available when the page is created, so it has
		// to be set here
		fileFormats.setInput(contentTypes);
		fileFormats.setSelection(new StructuredSelection(contentTypes.iterator().next()), true);
		updateContentType(fileFormats.getSelection());
	}

	// set content type selected in configuration list viewer to the wizard
	private void updateContentType(ISelection selection) {
		IContentType type = (IContentType) ((IStructuredSelection) selection).getFirstElement();
		getWizard().setContentType(type);
	}

	/**
	 * Update the page state.
	 */
	protected void update() {
		if (fileFormats.getSelection().isEmpty()) {
			setErrorMessage("Please select a format");
			setPageComplete(false);
			return;
		}

		String confName = name.getText();
		if (confName == null || confName.isEmpty()) {
			setErrorMessage("Please provide a name for the preset to easily identify it");
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);

		// configuration with that name already present?
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		IOConfiguration conf = ps.getExportConfiguration(confName);
		if (conf == null) {
			setMessage(null);
		}
		else {
			setMessage("Overrides an existing configuration with the same name",
					DialogPage.WARNING);
		}
		setPageComplete(true);
	}
}
