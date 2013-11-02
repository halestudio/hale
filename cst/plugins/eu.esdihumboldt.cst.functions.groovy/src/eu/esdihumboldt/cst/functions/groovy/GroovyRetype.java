/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy;

import java.util.Map;

import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * 1:1 retype based on a Groovy script.
 * 
 * @author Simon Templer
 */
public class GroovyRetype extends AbstractTypeTransformation<TransformationEngine> implements
		GroovyConstants {

	@Override
	public void execute(String transformationIdentifier, TransformationEngine engine,
			Map<String, String> executionParameters, TransformationLog log, Cell cell)
			throws TransformationException {
		// for each source instance create a target instance
		TypeDefinition targetType = getTarget().values().iterator().next().getDefinition()
				.getDefinition();

		InstanceBuilder builder = new InstanceBuilder();

		Binding binding = createBinding(getSource(), builder);

		try {
			Script script = GroovyUtil.getScript(this, binding);
			MutableInstance target = GroovyUtil.evaluate(script, builder, targetType);

			getPropertyTransformer().publish(getSource(), target, log, cell);
		} catch (TransformationException e) {
			throw e;
		} catch (Exception e) {
			throw new TransformationException(e.getMessage(), e);
		}
	}

	/**
	 * Create the binding for the Groovy Retype script function.
	 * 
	 * @param source the source instance
	 * @param builder the instance builder
	 * @return the binding
	 */
	public static Binding createBinding(FamilyInstance source, InstanceBuilder builder) {
		Binding binding = new Binding();
		binding.setVariable(BINDING_TARGET, null);
		binding.setVariable(BINDING_BUILDER, builder);
		binding.setVariable(BINDING_SOURCE, source);
		return binding;
	}

}
