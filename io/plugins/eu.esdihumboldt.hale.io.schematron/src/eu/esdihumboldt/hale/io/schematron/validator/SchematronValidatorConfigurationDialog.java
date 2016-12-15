/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.schematron.validator;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationDialog;
import eu.esdihumboldt.hale.ui.util.io.OpenFileFieldEditor;

/**
 * Configuration dialog for {@link SchematronInstanceValidator}
 * 
 * @author Florian Esser
 */
public class SchematronValidatorConfigurationDialog
		extends AbstractConfigurationDialog<SchematronInstanceValidator> {

	private OpenFileFieldEditor schematronRulesFile;

	/**
	 * Creates a configuration dialog. Uses the active {@link Shell}.
	 */
	public SchematronValidatorConfigurationDialog() {
		super();
	}

	/**
	 * @param parentShell
	 */
	public SchematronValidatorConfigurationDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);

//		// create message
//		if (message != null) {
//			Label label = new Label(composite, SWT.WRAP);
//			label.setText(message);
//			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
//					| GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
//			data.widthHint = convertHorizontalDLUsToPixels(
//					IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
//			label.setLayoutData(data);
//			label.setFont(parent.getFont());
//		}

		schematronRulesFile = new OpenFileFieldEditor("metadataFile", "Schematron rules file", true,
				FileFieldEditor.VALIDATE_ON_KEY_STROKE, composite);
		schematronRulesFile.setEmptyStringAllowed(true);
		schematronRulesFile.setFileExtensions(new String[] { "*.xml", "*.sch" });

		SchematronInstanceValidator validator = this.getProvider();
		if (validator != null && validator.getSchematronLocation() != null) {
			schematronRulesFile.setStringValue(validator.getSchematronLocation().toString());
		}
		else {
			// isValid starts with false even if emptyStringAllowed is true.
			// -> force validation hack
			schematronRulesFile.setStringValue(" ");
			schematronRulesFile.setStringValue("");
		}

		return composite;
	}

	public String getSchematronRulesFile() {
		if (schematronRulesFile == null) {
			return null;
		}

		return schematronRulesFile.getStringValue();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationDialog#configureProvider(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	protected boolean configureProvider(SchematronInstanceValidator provider) {
		String rulesFileName = schematronRulesFile.getStringValue();
		if (rulesFileName == null || rulesFileName.trim().isEmpty()) {
			return false;
		}

		File rules = new File(schematronRulesFile.getStringValue());
		if (!rules.exists()) {
			return false;
		}

		provider.setSchematronLocation(rules.toURI());
		return true;
	}
}