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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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
public class SaveConfigurationInstanceExportNamePage extends
		AbstractConfigurationPage<InstanceWriter, SaveConfigurationInstanceExportWizard> {

	private Text name;

	private Text description;

	private ListViewer configurations;

	private final String param_configurationName = "configurationName";
	private final String param_configurationDescription = "description";

	/**
	 * Default Constructor
	 */
	public SaveConfigurationInstanceExportNamePage() {
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
		provider.setParameter(param_configurationName, Value.of(name.getText()));
		provider.setParameter(param_configurationDescription, Value.of(description.getText()));
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		Label labelName = new Label(page, SWT.NONE);
		labelName.setText("Name:");
		labelName.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		name = new Text(page, SWT.BORDER | SWT.SINGLE);
		name.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false).create());

		Label labelDesc = new Label(page, SWT.NONE);
		labelDesc.setText("Description:");
		labelDesc.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		description = new Text(page, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData data = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
				.grab(true, false).create();
		data.heightHint = 75;
		description.setLayoutData(data);

		Label labelConf = new Label(page, SWT.NONE);
		labelConf.setText("File format:");
		labelConf.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		configurations = new ListViewer(page, SWT.BORDER);
		configurations.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		configurations.setContentProvider(ArrayContentProvider.getInstance());
		configurations.setLabelProvider(new LabelProvider() {

			/**
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof IContentType)
					return ((IContentType) element).getName();
				else
					return super.getText(element);
			}
		});

		// process current selection
		ISelection selection = configurations.getSelection();
		setPageComplete(!selection.isEmpty());

		// process selection changes
		configurations.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				setPageComplete(!selection.isEmpty());
				updateContentType(selection);
			}
		});
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
		configurations.setInput(contentTypes);

		configurations.setSelection(new StructuredSelection(contentTypes.iterator().next()), true);
		updateContentType(configurations.getSelection());
	}

	private void updateContentType(ISelection selection) {
		IContentType type = (IContentType) ((IStructuredSelection) selection).getFirstElement();
		getWizard().setContentType(type);

	}
}
