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

package eu.esdihumboldt.hale.ui.functions.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Base parameter page for parameter pages that contain a listing of source
 * types which can be put together to a target value.
 * 
 * @author Kai Schwierczek
 */
public abstract class TextSourceListParameterPage extends SourceListParameterPage<Text> implements
		ParameterPage {

	/**
	 * @see HaleWizardPage#HaleWizardPage(String, String, ImageDescriptor)
	 */
	protected TextSourceListParameterPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see HaleWizardPage#HaleWizardPage(String)
	 */
	protected TextSourceListParameterPage(String pageName) {
		super(pageName);
	}

	/**
	 * Subclasses can override this method to specify, that the text field
	 * should have multiple lines. By default it is not.
	 * 
	 * @return true if the text field should have multiple lines.
	 */
	protected boolean useMultilineInput() {
		return false;
	}

	/**
	 * @see SourceListParameterPage#getText(Object)
	 */
	@Override
	protected String getText(Text textField) {
		return textField.getText();
	}

	/**
	 * @see SourceListParameterPage#insertTextAtCurrentPos(Object, String)
	 */
	@Override
	protected void insertTextAtCurrentPos(Text textField, String insert) {
		textField.insert(insert);
		textField.setFocus();
	}

	/**
	 * @see SourceListParameterPage#setText(Object, String)
	 */
	@Override
	protected void setText(Text textField, String value) {
		textField.setText(value);
	}

	/**
	 * @see SourceListParameterPage#createAndLayoutTextField(Composite)
	 */
	@Override
	protected Text createAndLayoutTextField(Composite parent) {
		int lineStyle = useMultilineInput() ? SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE;
		Text textField = new Text(parent, lineStyle | SWT.BORDER);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, useMultilineInput()));
		return textField;
	}

}
