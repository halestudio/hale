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

import java.util.List;
import java.util.Set;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page for assign function.
 * 
 * @author Kai Schwierczek
 */
public class AssignParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {
	private String initialValue;
	private AttributeEditor<?> editor;
	private Composite page;
	
	/**
	 * Constructor.
	 */
	public AssignParameterPage() {
		super("assign", "Please enter the value to assign", null);
		setPageComplete(false);
	}
	
	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		// selected target could've changed!
		if (editor != null)
			editor.getControl().dispose();
		createContent(page);
		page.layout();
		if (firstShow)
			setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set, com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params, ListMultimap<String, String> initialValues) {
		// this page is only for parameter value, ignore params
		if (initialValues == null)
			return;
		List<String> values = initialValues.get("value");
		if (!values.isEmpty()) {
			initialValue = values.get(0);
			setPageComplete(true);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, String> getConfiguration() {
		ListMultimap<String, String> configuration = ArrayListMultimap.create(1, 1);
		configuration.put("value", editor.getAsText());
		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		this.page = page;
		page.setLayout(new GridLayout());
		// check whether a target was chosen (can be null the moment a new cell is created)
		if (getWizard().getUnfinishedCell().getTarget() != null) {
			editor = ((AttributeEditorFactory) PlatformUI.getWorkbench().getService(AttributeEditorFactory.class))
					.createEditor(page, (PropertyDefinition) getWizard().getUnfinishedCell().getTarget().values().iterator().next().getDefinition().getDefinition());
			editor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			editor.setPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(AttributeEditor.IS_VALID))
						setPageComplete((Boolean) event.getNewValue());
				}
			});
		}
		if (editor != null && initialValue != null)
			editor.setAsText(initialValue);
	}
}
