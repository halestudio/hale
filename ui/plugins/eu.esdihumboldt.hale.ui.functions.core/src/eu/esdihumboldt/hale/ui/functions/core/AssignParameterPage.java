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

import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.ui.common.definition.editors.EditorChooserEditor;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.transformation.TransformationVariableReplacer;

/**
 * Parameter page for assign function.
 * 
 * @author Kai Schwierczek
 */
public class AssignParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage, AssignFunction {

	private ParameterValue initialValue;
	private AttributeEditor<?> editor;
	private Composite page;
	private Composite title;
	private PropertyDefinition target = null;

	/**
	 * Constructor.
	 */
	public AssignParameterPage() {
		super("assign", "Please enter the value to assign", null);
		setDescription("Press Ctrl+Space for content assistance.");
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// should never be null here, but better be safe than sorry
		if (getWizard().getUnfinishedCell().getTarget() != null) {
			PropertyDefinition propDef = (PropertyDefinition) getWizard().getUnfinishedCell()
					.getTarget().values().iterator().next().getDefinition().getDefinition();
			if (!propDef.equals(target)) {
				// target property definition changed, rebuild editor
				target = propDef;
				if (title != null) {
					title.dispose();
				}
				if (editor != null)
					editor.getControl().dispose();
				createContent(page);
				page.layout();
			}
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set,
	 *      com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues) {
		// this page is only for parameter value, ignore params
		if (initialValues == null)
			return;
		List<ParameterValue> values = initialValues.get(PARAMETER_VALUE);
		if (!values.isEmpty()) {
			initialValue = values.get(0);
			setPageComplete(true);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> configuration = ArrayListMultimap.create(1, 1);
		if (editor != null && !editor.getControl().isDisposed())
			configuration.put(PARAMETER_VALUE,
					new ParameterValue(editor.getValueType(), Value.of(editor.getAsText())));
		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		this.page = page;
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		// check whether a target was chosen (can be null the moment a new cell
		// is created)
		if (getWizard().getUnfinishedCell().getTarget() != null) {
			EntityDefinition entityDef = getWizard().getUnfinishedCell().getTarget().values()
					.iterator().next().getDefinition();
			PropertyDefinition propDef = (PropertyDefinition) entityDef.getDefinition();

			title = new Composite(page, SWT.NONE);
			title.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).create());
//			title.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.END).create());
			DefinitionLabelFactory dlf = PlatformUI.getWorkbench()
					.getService(DefinitionLabelFactory.class);
			dlf.createLabel(title, propDef, false);
			Label label = new Label(title, SWT.NONE);
			label.setText(" = ");

			editor = PlatformUI.getWorkbench().getService(AttributeEditorFactory.class)
					.createEditor(page, propDef, entityDef, false);
			editor.setVariableReplacer(new TransformationVariableReplacer());
			editor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			editor.setPropertyChangeListener(new IPropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(AttributeEditor.IS_VALID))
						setPageComplete((Boolean) event.getNewValue());
				}
			});
		}
		if (editor != null && initialValue != null) {
			editor.setAsText(initialValue.as(String.class));
			if (editor instanceof EditorChooserEditor)
				((EditorChooserEditor<?>) editor).selectEditor(initialValue.getType());
		}

		if (editor != null)
			setPageComplete(editor.isValid());
	}
}
