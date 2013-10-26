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

package eu.esdihumboldt.cst.functions.groovy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Property transformation based on a Groovy script.
 * 
 * @author Simon Templer
 */
public class GroovyTransformation extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> implements GroovyConstants {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		Binding binding = createGroovyBinding(variables.get(ENTITY_VARIABLE), getCell().getSource()
				.get(ENTITY_VARIABLE));

		Object result;
		try {
			Script groovyScript = GroovyUtil.getScript(this, binding);

			// run the script
			result = groovyScript.run();
		} catch (Throwable e) {
			throw new TransformationException("Error evaluating the cell script", e);
		}

		if (result == null) {
			throw new NoResultException();
		}
		return result;
	}

	/**
	 * Create a Groovy binding from the list of variables.
	 * 
	 * @param vars the variable values
	 * @param varDefs definition of the assigned variables, in case some
	 *            variable values are not set, may be <code>null</code>
	 * @return the binding for use with {@link GroovyShell}
	 */
	public static Binding createGroovyBinding(List<PropertyValue> vars,
			List<? extends Entity> varDefs) {
		Binding binding = new Binding();

		// collect definitions to check if all were provided
		Set<EntityDefinition> notDefined = new HashSet<>();
		if (varDefs != null) {
			for (Entity entity : varDefs) {
				notDefined.add(entity.getDefinition());
			}
		}
		// keep only defs where no value is provided
		if (!notDefined.isEmpty()) {
			for (PropertyValue var : vars) {
				notDefined.remove(var.getProperty());
			}
		}

		// add null value for missing variables
		if (!notDefined.isEmpty()) {
			vars = new ArrayList<>(vars);
			for (EntityDefinition entityDef : notDefined) {
				if (entityDef instanceof PropertyEntityDefinition) {
					vars.add(new PropertyValueImpl(null, (PropertyEntityDefinition) entityDef));
				}
			}
		}

		for (PropertyValue var : vars) {
			// add the variable to the environment

			// determine the variable value
			Object value = var.getValue();
			if (value instanceof Instance) {
				// TODO make this dependent on parameter
				value = ((Instance) value).getValue();
			}
			if (value instanceof Number) {
				// use numbers as is
			}
			else {
				// try conversion to String as default
				value = var.getValueAs(String.class);
			}

			// determine the variable name
			String name = var.getProperty().getDefinition().getName().getLocalPart();

			// add with short name, but ensure no variable with only a short
			// name is overridden
			if (binding.getVariables().get(name) == null
					|| var.getProperty().getPropertyPath().size() == 1) {
				binding.setVariable(name, value);
			}

			// add with long name if applicable
			if (var.getProperty().getPropertyPath().size() > 1) {
				List<String> names = new ArrayList<String>();
				for (ChildContext context : var.getProperty().getPropertyPath()) {
					names.add(context.getChild().getName().getLocalPart());
				}
				String longName = Joiner.on('_').join(names);
				binding.setVariable(longName, value);
			}
		}

		return binding;
	}

}
