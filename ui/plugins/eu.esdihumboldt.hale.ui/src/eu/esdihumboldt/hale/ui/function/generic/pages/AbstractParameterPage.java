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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;

/**
 * Base class for parameter pages.
 * 
 * @author Simon Templer
 */
public abstract class AbstractParameterPage extends
		HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {

	private ListMultimap<String, String> initialValues;
	private ImmutableMap<String, FunctionParameter> parametersToHandle;

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
	public AbstractParameterPage(Function function, String description) {
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
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, String> initialValues) {
		Builder<String, FunctionParameter> builder = ImmutableMap.builder();
		for (FunctionParameter param : params) {
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
	protected ListMultimap<String, String> getInitialValues() {
		return initialValues;
	}

	/**
	 * Get the parameters to handle by this parameter page.
	 * 
	 * @return the set of function parameters to handle (unmodifiable)
	 */
	protected ImmutableMap<String, FunctionParameter> getParametersToHandle() {
		return parametersToHandle;
	}

	/**
	 * Get a single initial value for the given parameter.
	 * 
	 * @param parameterName the parameter name
	 * @param def the default value to return if the value is not present
	 * @return the first parameter value or the provided default value
	 */
	protected String getOptionalInitialValue(String parameterName, String def) {
		List<String> values = getInitialValues().get(parameterName);
		if (values != null && !values.isEmpty()) {
			return values.get(0);
		}
		return def;
	}

}
