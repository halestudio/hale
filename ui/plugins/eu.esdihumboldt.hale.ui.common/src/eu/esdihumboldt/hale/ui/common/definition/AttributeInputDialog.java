/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.definition;

import javax.annotation.Nullable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.VariableReplacer;

/**
 * Attribute input dialog
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class AttributeInputDialog extends Dialog {

	private final PropertyDefinition definition;

	private final String message;

	private final String title;

	private AttributeEditor<?> editor;

	private String text;

	private Object value;

	private final EntityDefinition entity;

	private final VariableReplacer replacer;

	/**
	 * Create a new attribute input dialog
	 * 
	 * @param definition the attribute definition
	 * @param entity the property entity definition representing the property,
	 *            may be <code>null</code> if unknown or unavailable
	 * @param parentShell the parent shell
	 * @param title the dialog title
	 * @param message the dialog message
	 * @param replacer the variable replacer or <code>null</code>
	 */
	public AttributeInputDialog(PropertyDefinition definition, EntityDefinition entity,
			Shell parentShell, String title, String message, @Nullable VariableReplacer replacer) {
		super(parentShell);

		this.title = title;
		this.message = message;
		this.definition = definition;
		this.entity = entity;
		this.replacer = replacer;
	}

	/**
	 * @see Dialog#createButtonsForButtonBar(Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

		okButton.setEnabled(editor.isValid());
	}

	/**
	 * @see Dialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);

		// create message
		if (message != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText(message);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
					| GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(
					IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(data);
			label.setFont(parent.getFont());
		}

		// create editor
		AttributeEditorFactory aef = PlatformUI.getWorkbench()
				.getService(AttributeEditorFactory.class);
		editor = aef.createEditor(composite, definition, entity, false);
		editor.setVariableReplacer(replacer);
		editor.getControl().setLayoutData(
				new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		editor.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(AttributeEditor.IS_VALID))
					getButton(IDialogConstants.OK_ID).setEnabled((Boolean) event.getNewValue());
			}
		});

		applyDialogFont(composite);
		return composite;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	/**
	 * @return the editor
	 */
	public AttributeEditor<?> getEditor() {
		return editor;
	}

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		value = editor.getValue();
		text = editor.getAsText();

		super.okPressed();
	}

	/**
	 * @return the text
	 */
	public String getValueAsText() {
		return text;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

}
