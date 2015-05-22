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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page showing a checkbox for the specified function params. Default
 * for an unspecified or illegal parameter value is always <code>false</code>.
 * 
 * @author Kai Schwierczek
 */
public class CheckboxParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage {

	private HashMap<FunctionParameterDefinition, Boolean> selected;

	/**
	 * Constructor.
	 */
	public CheckboxParameterPage() {
		super("checkbox", "Please specify the function parameters", null);
		setDescription("Enable or disable the given options");
		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		setPageComplete(true);
	}

	@Override
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues) {
		selected = new HashMap<FunctionParameterDefinition, Boolean>((int) (params.size() * 1.4));
		for (FunctionParameterDefinition param : params) {
			if (initialValues != null && !initialValues.get(param.getName()).isEmpty())
				selected.put(param,
						initialValues.get(param.getName()).get(0).as(Boolean.class, false));
			else
				selected.put(param, false);
		}

//		if (params.size() == 1) {
//			setDescription(params.iterator().next().getDisplayName());
//		}
	}

	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> result = ArrayListMultimap.create(selected.size(), 1);
		for (Entry<FunctionParameterDefinition, Boolean> entry : selected.entrySet())
			result.put(entry.getKey().getName(), new ParameterValue(entry.getValue().toString()));

		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().create());

		// create section for each function parameter
		for (final FunctionParameterDefinition fp : selected.keySet()) {
			Button checkbox = new Button(page, SWT.CHECK);
			checkbox.setText(fp.getDisplayName());
			checkbox.setSelection(selected.get(fp));
			checkbox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					selected.put(fp, !((Boolean) selected.get(fp)));
				}
			});
			if (fp.getDescription() != null) {
				checkbox.setToolTipText(fp.getDescription());
			}
		}
	}
}
