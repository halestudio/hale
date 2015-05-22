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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;

/**
 * Base class for parameter pages.
 * 
 * @author Simon Templer
 */
public abstract class AbstractParameterPage extends
		HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {

	private ListMultimap<String, ParameterValue> initialValues;
	private ImmutableMap<String, FunctionParameterDefinition> parametersToHandle;

	/**
	 * @see HaleWizardPage#HaleWizardPage(String)
	 */
	public AbstractParameterPage(String pageName) {
		super(pageName);
	}

	/**
	 * @see HaleWizardPage#HaleWizardPage(String, String, ImageDescriptor)
	 */
	public AbstractParameterPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * Create a parameter page for the given function.
	 * 
	 * @param function the function
	 * @param description the page description, if <code>null</code> the
	 *            function description will be used
	 */
	public AbstractParameterPage(FunctionDefinition<?> function, String description) {
		super(function.getId(), function.getDisplayName(), null);

		if (description == null) {
			setDescription(function.getDescription());
		}
		else {
			setDescription(description);
		}
	}

	/**
	 * @see ParameterPage#setParameter(Set, ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues) {
		Builder<String, FunctionParameterDefinition> builder = ImmutableMap.builder();
		for (FunctionParameterDefinition param : params) {
			builder.put(param.getName(), param);
		}
		this.parametersToHandle = builder.build();
		if (initialValues == null) {
			this.initialValues = ArrayListMultimap.create();
		}
		else {
			this.initialValues = Multimaps.unmodifiableListMultimap(initialValues);
		}
	}

	/**
	 * Get the map of initial values for parameters.
	 * 
	 * @return parameter names mapped to their initial values (unmodifiable)
	 */
	protected ListMultimap<String, ParameterValue> getInitialValues() {
		return initialValues;
	}

	/**
	 * Get the parameters to handle by this parameter page.
	 * 
	 * @return the set of function parameters to handle (unmodifiable)
	 */
	protected ImmutableMap<String, FunctionParameterDefinition> getParametersToHandle() {
		return parametersToHandle;
	}

	/**
	 * Get a single initial value for the given parameter.
	 * 
	 * @param parameterName the parameter name
	 * @param def the default value to return if the value is not present
	 * @return the first parameter value or the provided default value
	 */
	protected ParameterValue getOptionalInitialValue(String parameterName, ParameterValue def) {
		List<ParameterValue> values = getInitialValues().get(parameterName);
		if (values != null && !values.isEmpty()) {
			return values.get(0);
		}
		return def;
	}

}
