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

package eu.esdihumboldt.hale.ui.functions.core;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.service.project.ProjectVariablesContentProposalProvider;

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
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage#configure(java.lang.Object)
	 */
	@Override
	protected void configure(Text textField) {
		// Add content assist for variables
		final ControlDecoration infoDeco = new ControlDecoration(textField, SWT.TOP | SWT.LEFT);
		infoDeco.setDescriptionText("Type Ctrl+Space for content assistance");
		infoDeco.setImage(FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		infoDeco.setMarginWidth(2);

		ContentProposalAdapter adapter = new ContentProposalAdapter(textField,
				new TextContentAdapter(), this, ProjectVariablesContentProposalProvider.CTRL_SPACE,
				/* new char[] { '{' } */ null);
		adapter.setAutoActivationDelay(0);
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
