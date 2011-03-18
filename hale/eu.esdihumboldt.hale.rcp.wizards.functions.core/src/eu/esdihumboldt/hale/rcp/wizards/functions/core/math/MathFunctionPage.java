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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.math;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;

/**
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MathFunctionPage extends AbstractSingleComposedCellWizardPage {
	
	private MathExpressionFieldEditor expressionEditor;
	
	private String initialExpression = null;
	
	/**
	 * @see AbstractSingleComposedCellWizardPage#AbstractSingleComposedCellWizardPage(String, String, ImageDescriptor)
	 */
	public MathFunctionPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see AbstractSingleComposedCellWizardPage#AbstractSingleComposedCellWizardPage(String)
	 */
	public MathFunctionPage(String pageName) {
		super(pageName);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		page.setLayout(layout);
		
		Set<String> variables = new TreeSet<String>();
		for (SchemaItem var : getParent().getSourceItems()) {
			variables.add(var.getName().getLocalPart());
		}
		
		SchemaItem target = getParent().getFirstTargetItem();
		
		// target attribute label
		Control attributeLabel = dlf.createLabel(page, target.getDefinition(), false);
		attributeLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		// expression
		expressionEditor = new MathExpressionFieldEditor("expression", //$NON-NLS-1$
				"=", page, //$NON-NLS-1$
				variables);
		expressionEditor.setStringValue(initialExpression);
		expressionEditor.setEmptyStringAllowed(false);
		expressionEditor.setPage(this);
		expressionEditor.setPropertyChangeListener(new IPropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					update();
				}
			}
		});
		
		// spacer
		new Label(page, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 3));
		new Label(page, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		
		// variables
		Label label = new Label(page, SWT.NONE);
		label.setText(Messages.MathFunctionPage_2);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		// spacer
		new Label(page, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		
		final ListViewer varList = new ListViewer(page);
		varList.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		varList.setContentProvider(new ArrayContentProvider());
		varList.setInput(variables);
		varList.getList().addMouseListener(new MouseAdapter() {

			/**
			 * @see MouseAdapter#mouseDoubleClick(MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = varList.getList().getSelectionIndex();
				if (index >= 0) {
					String var = varList.getList().getItem(index);
					expressionEditor.insert(var);
					
					expressionEditor.setFocus();
					
					update();
				}
			}
			
		});
		
		// re-set the layout because the field editor breaks it and sets its own
		page.setLayout(layout);
		page.layout(true, true);
		
		setControl(page);
		
		setMessage(Messages.MathFunctionPage_3, DialogPage.INFORMATION);
		
		update();
	}
	
	private void update() {
		setPageComplete(expressionEditor.isValid());
	}
	
	/**
	 * Get the expression
	 * 
	 * @return the expression
	 */
	public String getExpression() {
		return expressionEditor.getStringValue();
	}

	/**
	 * Set the initial expression
	 * 
	 * @param expression the expression
	 */
	public void setInitialExpression(String expression) {
		this.initialExpression = expression;
	}

}
