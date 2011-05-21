/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.common.definition;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;

/**
 * Attribute input dialog
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class AttributeInputDialog extends Dialog {
	
	private final AttributeDefinition definition;
	
	private final String message;
	
	private final String title;
	
	private AttributeEditor<?> editor;

	private String text;

	private Object value;

	/**
	 * Create a new attribute input dialog
	 * 
	 * @param definition the attribute definition 
	 * @param parentShell the parent shell
	 * @param title the dialog title
	 * @param message the dialog message
	 */
	public AttributeInputDialog(AttributeDefinition definition, Shell parentShell,
			String title, String message) {
		super(parentShell);
		
		this.title = title;
		this.message = message;
		this.definition = definition;
	}

	/**
	 * @see Dialog#createButtonsForButtonBar(Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
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
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        
        // create editor
        AttributeEditorFactory aef = (AttributeEditorFactory) PlatformUI.getWorkbench().getService(AttributeEditorFactory.class);
        editor = aef.createEditor(composite, definition);
        editor.getControl().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        
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
