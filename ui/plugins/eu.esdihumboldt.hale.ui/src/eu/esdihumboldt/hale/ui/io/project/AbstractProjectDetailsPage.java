/*
 * Copyright (c) 2017 wetransform GmbH
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
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardListener;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;

/**
 * Wizard page that allows changing project name and author.
 * 
 * @author Simon Templer
 * 
 * @param
 * 			<P>
 *            the project writer type
 * @param <E> the export wizard type
 */
public abstract class AbstractProjectDetailsPage<P extends ProjectWriter, E extends ExportWizard<P>>
		extends IOWizardPage<P, E> {

	private StringFieldEditor name;
	private StringFieldEditor author;
	private Text description;

	/**
	 * @see IOWizardPage#IOWizardPage(String)
	 */
	public AbstractProjectDetailsPage(String pageName) {
		super(pageName);
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
		descLabel.setLayoutData(
				GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.BEGINNING).create());

		description = new Text(page, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		description.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).hint(500, SWT.DEFAULT).create());

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
		getWizard().addIOWizardListener(new IOWizardListener<P, E>() {

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
