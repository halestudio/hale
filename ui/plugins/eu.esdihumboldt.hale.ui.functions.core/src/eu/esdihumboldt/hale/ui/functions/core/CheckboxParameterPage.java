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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page showing a checkbox for the specified function params.
 * 
 * @author Kai Schwierczek
 */
public class CheckboxParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage {

	private HashMap<FunctionParameter, Boolean> selected;

	/**
	 * Constructor.
	 */
	public CheckboxParameterPage() {
		super("checkbox", "Please choose", null);
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

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#setParameter(java.util.Set,
	 *      com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, ParameterValue> initialValues) {
		selected = new HashMap<FunctionParameter, Boolean>((int) (params.size() * 1.4));
		for (FunctionParameter param : params) {
			if (initialValues != null && !initialValues.get(param.getName()).isEmpty())
				selected.put(
						param,
						Boolean.parseBoolean(initialValues.get(param.getName()).get(0)
								.getStringValue()));
			else
				selected.put(param, false);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> result = ArrayListMultimap.create(selected.size(), 1);
		for (Entry<FunctionParameter, Boolean> entry : selected.entrySet())
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
		for (final FunctionParameter fp : selected.keySet()) {
			Group group = new Group(page, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			group.setText(fp.getDisplayName());
			group.setLayout(GridLayoutFactory.swtDefaults().create());
			if (fp.getDescription() != null) {
				Label description = new Label(group, SWT.NONE);
				description.setText(fp.getDescription());
			}
			Button checkbox = new Button(group, SWT.CHECK);
			checkbox.setSelection(selected.get(fp));
			checkbox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					selected.put(fp, !((Boolean) selected.get(fp)));
				}
			});
		}
	}
}
