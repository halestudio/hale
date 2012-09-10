/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.io.project;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizardListener;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;

/**
 * Wizard page that allows changing project name and author.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SaveProjectDetailsPage extends IOWizardPage<ProjectWriter, SaveProjectWizard> {

	private StringFieldEditor name;
	private StringFieldEditor author;
	private Text description;

	/**
	 * Default constructor
	 */
	public SaveProjectDetailsPage() {
		super("project.save.details");
		setTitle("Project details");
		setDescription("Please specify project name and author.");
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));

		// name
		name = new StringFieldEditor("name", "Project name:", page);
		name.setEmptyStringAllowed(false);
		name.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		name.setErrorMessage("The project name must be specified.");
		name.setPage(this);

		// author
		author = new StringFieldEditor("author", "Project author:", page);
		author.setEmptyStringAllowed(false);
		author.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		author.setPage(this);

		// description
		Label descLabel = new Label(page, SWT.NONE);
		descLabel.setText("Description:");
		descLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING)
				.create());

		description = new Text(page, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		description.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// listen for state changes on field editors
		IPropertyChangeListener stateListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(StringFieldEditor.IS_VALID)) {
					updateState();
				}
			}
		};
		name.setPropertyChangeListener(stateListener);
		author.setPropertyChangeListener(stateListener);

		// listen for provider changes
		getWizard().addIOWizardListener(new IOWizardListener<ProjectWriter, SaveProjectWizard>() {

			@Override
			public void providerDescriptorChanged(IOProviderDescriptor providerFactory) {
				// update fields as the provider will have changed
				updateFields();
			}

			@Override
			public void contentTypeChanged(IContentType contentType) {
				// ignore
			}
		});

		updateState();
		updateFields();
	}

	/**
	 * @see DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		name.setFocus();
		if (!name.isValid()) {
			name.showErrorMessage();
		}
	}

	private void updateFields() {
		ProjectWriter writer = getWizard().getProvider();
		if (writer != null && writer.getProject() != null) {
			ProjectInfo p = writer.getProject();
			name.setStringValue(p.getName());
			author.setStringValue(p.getAuthor());
			description.setText((p.getDescription() == null) ? ("") : (p.getDescription()));
		}
		else {
			name.setStringValue("");
			author.setStringValue("");
			description.setText("");
		}
	}

	private void updateState() {
		setPageComplete(name.isValid() && author.isValid());
	}

	/**
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(ProjectWriter provider) {
		Project p = provider.getProject();

		if (p != null) {
			p.setName(name.getStringValue());
			p.setAuthor(author.getStringValue());
			p.setDescription((description.getText().isEmpty()) ? (null) : (description.getText()));

			return true;
		}

		return false;
	}

}
