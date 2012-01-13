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

package eu.esdihumboldt.hale.ui.common.definition.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.common.definition.AbstractAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.internal.CommonUIPlugin;
import eu.esdihumboldt.hale.ui.common.internal.Messages;

/**
 * Validating attribute editor based on a single line text field
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @param <T> the attribute type/binding
 */
public abstract class StringValidatingAttributeEditor<T> extends
		AbstractAttributeEditor<T> {

	private final Composite container;
	private final Label errorLabel;
	private final Text editor;
	private final Image okImage;

	private boolean isValid;
	private String value;

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite 
	 */
	public StringValidatingAttributeEditor(Composite parent) {
		super();
		
		okImage = CommonUIPlugin.getImageDescriptor("icons/ok.gif").createImage(); //$NON-NLS-1$
		
		container = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		container.setLayout(gridLayout);
		
		// text field
		editor = new Text(container, SWT.BORDER | SWT.SINGLE);
		editor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// error label
		errorLabel = new Label(container, SWT.NONE);
		errorLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		updateValidation();
		
		editor.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateValidation();
				fireValueChanged(VALUE, value, editor.getText());
				value = editor.getText();
			}
		});
		
		container.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				okImage.dispose();
			}
		});
	}
	
	/**
	 * Get the error image
	 * 
	 * @return the error image
	 */
	protected Image getErrorImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	}
	
	private void updateValidation() {
		String valid = validate(getAsText());
		
		boolean oldValid = isValid;
		isValid = valid == null;
		if (oldValid != isValid)
			fireStateChanged(IS_VALID, oldValid, isValid);
		
		if (valid == null) {
			errorLabel.setImage(okImage);
			errorLabel.setToolTipText(getValidToolTip());
		}
		else {
			errorLabel.setImage(getErrorImage());
			errorLabel.setToolTipText(valid);
		}
		container.layout(true, true);
	}

	/**
	 * Get the tooltip for a valid value
	 * 
	 * @return the tooltip for a valid value
	 */
	protected String getValidToolTip() {
		return Messages.StringValidatingAttributeEditor_1; //$NON-NLS-1$
	}

	/**
	 * Convert the given text to an editor value
	 * 
	 * @param text the text
	 * 
	 * @return the corresponding editor value
	 */
	protected abstract T valueFromString(String text);
	
	/**
	 * Convert the given value to a string
	 * 
	 * @param value the value
	 * 
	 * @return the value's string representation
	 */
	protected abstract String stringFromValue(T value);
	
	/**
	 * Validate the given string
	 * 
	 * @param text the string
	 * 
	 * @return <code>null</code> if the string is valid to be converted to a
	 *   editor value, otherwise the error message
	 */
	protected abstract String validate(String text);
	
	/**
	 * Determines if an empty string stands for a <code>null</code> value
	 * 
	 * @return if an empty string stands for a <code>null</code> value
	 */
	protected abstract boolean emptyStringIsNull();
	
	/**
	 * @see AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		String text = editor.getText();
		if (emptyStringIsNull() && text.isEmpty()) {
			return null;
		}
		else {
			return text;
		}
	}

	/**
	 * @see AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return container;
	}

	/**
	 * @see AttributeEditor#setAsText(String)
	 */
	@Override
	public void setAsText(String text) {
		if (text == null && emptyStringIsNull()) {
			editor.setText(""); //$NON-NLS-1$
		}
		else {
			editor.setText(text);
		}
		updateValidation();
	}

	/**
	 * @see AttributeEditor#setValue(Object)
	 */
	@Override
	public void setValue(T value) {
		setAsText(stringFromValue(value));
	}

	/**
	 * @see AttributeEditor#getValue()
	 */
	@Override
	public T getValue() {
		return valueFromString(getAsText());
	}

	/**
	 * @see AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return isValid;
	}

}
