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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.scripting.scripts.groovy;

import javax.script.ScriptException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.core.convert.ConversionException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.scripting.Script;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

/**
 * Groovy script implementation.
 * 
 * @author Kai Schwierczek
 */
public class GroovyScript implements Script {

	/**
	 * @see eu.esdihumboldt.hale.common.scripting.Script#evaluate(java.lang.String,
	 *      java.lang.Iterable)
	 */
	@Override
	public Object evaluate(String script, Iterable<PropertyValue> variables) throws ScriptException {
		Binding binding = createGroovyBinding(variables, true);

		Object result;
		try {
			GroovyShell shell = new GroovyShell(binding);
			result = shell.evaluate(script);
		} catch (Exception e) {
			throw new ScriptException(e);
		} catch (Throwable t) {
			// XXX ScriptEngine's constructor does not allow a Throwable?
			throw new ScriptException(t.toString());
		}

		if (result == null) {
			// XXX throw new NoResultException(); ? as cause for SE?
			throw new ScriptException("no result");
		}
		return result;
	}

	/**
	 * Create a Groovy binding from the list of variables.
	 * 
	 * FIXME why is here an additional implementation of this as already used in
	 * GroovyTransformation? Could the implementation in GroovyTransformation be
	 * used instead?
	 * 
	 * @param variables the variables
	 * @param useNullForMissingBindings if the binding should provide
	 *            <code>null</code> values for variables that are not provided
	 *            in the given variable list
	 * @return the binding for use with {@link GroovyShell}
	 */
	private Binding createGroovyBinding(Iterable<PropertyValue> variables,
			boolean useNullForMissingBindings) {
		Binding binding;
		if (useNullForMissingBindings) {
			binding = new Binding() {

				@Override
				public Object getVariable(String name) {
					try {
						return super.getVariable(name);
					} catch (MissingPropertyException mpe) {
						// use null value for variables that are not defined
						return null;
					}
				}

			};
		}
		else
			binding = new Binding();

		for (PropertyValue var : variables) {
			// add the variable to the environment

			// determine the variable value
			Object value = var.getValue();
			if (value instanceof Instance) {
				value = ((Instance) value).getValue(); // XXX check if there are
														// any properties?
			}
			if (value instanceof Number) {
				// use numbers as is
			}
			else {
				// try conversion to String as default
				try {
					value = var.getValueAs(String.class);
				} catch (ConversionException ce) {
					// XXX currently ignored conversion exception
					continue;
				}
			}

			// add with short name, if it does not override something
			String name = var.getProperty().getDefinition().getName().getLocalPart();
			if (!binding.getVariables().containsKey(name))
				binding.setVariable(name, value);

			// add with full name
			binding.setVariable(getVariableName(var.getProperty()), value);
		}

		return binding;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.scripting.Script#getVariableName(eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition)
	 */
	@Override
	public String getVariableName(PropertyEntityDefinition entityDefinition) {
		return Joiner.on('_').join(
				Lists.transform(entityDefinition.getPropertyPath(),
						new Function<ChildContext, String>() {

							/**
							 * @see com.google.common.base.Function#apply(java.lang.Object)
							 */
							@Override
							public String apply(ChildContext input) {
								return input.getChild().getName().getLocalPart();
							}
						}));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.scripting.Script#validate(java.lang.String,
	 *      java.lang.Iterable)
	 */
	@Override
	public String validate(String script, Iterable<PropertyValue> variables) {
		Binding binding = createGroovyBinding(variables, false);

		try {
			GroovyShell shell = new GroovyShell(binding);
			shell.parse(script);
			// TODO Also run the script? Since it is dynamically typed, parsing
			// alone is probably not enough.
		} catch (CompilationFailedException e) {
			return e.getLocalizedMessage();
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.scripting.Script#getId()
	 */
	@Override
	public String getId() {
		return "eu.esdihumboldt.hale.common.scripting.groovy";
	}
}
