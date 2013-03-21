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

import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
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
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Save configuration for exporting instances
 * 
 * @author Patrick Lieb
 */
public class SaveConfigurationInstanceExportPage extends
		AbstractConfigurationPage<InstanceWriter, SaveConfigurationInstanceExportWizard> implements
		InstanceExportConfigurations {

	private Text name;
	private Text description;
	private ListViewer fileFormats;
	private String fileFormat;

	/**
	 * Default Constructor
	 */
	public SaveConfigurationInstanceExportPage() {
		super("instancExport.Namepage");

		setTitle("Name of the export configuration");
		setDescription("Set name for the export configuration");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceWriter provider) {
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		Project p = (Project) ps.getProjectInfo();
		// prevent saving with an empty name or description or a name which is
		// already used
		if (name.getText().isEmpty() || p.getExportConfigurations().contains(name.getText())
				|| description.getText().isEmpty())
			return false;
		// set additional information to the provider
		provider.setParameter(PARAM_CONFIGURATION_NAME, Value.of(name.getText()));
		provider.setParameter(PARAM_CONFIGURATION_DESCRIPTION, Value.of(description.getText()));
		provider.setParameter(PARAM_FILE_FORMAT, Value.of(fileFormat));
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
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
				setPageComplete(!fileFormats.getSelection().isEmpty()
						&& isNameAndDescriptionValid());
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
				setPageComplete(!fileFormats.getSelection().isEmpty()
						&& isNameAndDescriptionValid());
			}
		});
		GridData data = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false).create();
		data.heightHint = 75;
		description.setLayoutData(data);

		// create viewer for possible content types
		Label labelConf = new Label(page, SWT.NONE);
		labelConf.setText("File format:");
		labelConf.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());
		fileFormats = new ListViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		data = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.create();
		fileFormats.getControl().setLayoutData(data);
		fileFormats.setContentProvider(ArrayContentProvider.getInstance());
		fileFormats.setLabelProvider(new LabelProvider() {

			/**
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof IContentType) {
					fileFormat = ((IContentType) element).getName();
					return fileFormat;
				}
				else
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
				setPageComplete(!selection.isEmpty() && isNameAndDescriptionValid());
				updateContentType(selection);
			}
		});
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// do nothing
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		Set<IContentType> contentTypes = getWizard().getProviderFactory().getSupportedTypes();
		// content types are not available when the page is created, so it has
		// to be setted here
		fileFormats.setInput(contentTypes);
		fileFormats.setSelection(new StructuredSelection(contentTypes.iterator().next()), true);
		updateContentType(fileFormats.getSelection());
	}

	// set content type selected in configuration list viewer to the wizard
	private void updateContentType(ISelection selection) {
		IContentType type = (IContentType) ((IStructuredSelection) selection).getFirstElement();
		getWizard().setContentType(type);
	}

	// check if name and description fields are valid
	// should not be empty or contain a name which is already used
	private boolean isNameAndDescriptionValid() {
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		Project p = (Project) ps.getProjectInfo();
		// prevent saving with an empty name or description or a name which is
		// already used
		return !(name.getText().isEmpty() || p.getExportConfigurations().contains(name.getText()) || description
				.getText().isEmpty());
	}
}
