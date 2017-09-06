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

import java.util.Collections;
import java.util.Map;

import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.CreateFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Creates instances based on a Groovy script.
 * 
 * @author Simon Templer
 */
public class GroovyCreate extends AbstractTypeTransformation<TransformationEngine>
		implements GroovyConstants, CreateFunction {

	@Override
	public void execute(String transformationIdentifier, TransformationEngine engine,
			Map<String, String> executionParameters, TransformationLog log, Cell cell)
					throws TransformationException {
		// get number of executions
		int num;
		String numberExpr = getOptionalParameter(PARAM_NUMBER, Value.of(1)).as(String.class);
		if (numberExpr != null) {
			// replace variables
			numberExpr = getExecutionContext().getVariables().replaceVariables(numberExpr);
			try {
				num = Integer.parseInt(numberExpr);
			} catch (NumberFormatException e) {
				log.error(log.createMessage(
						"Unable to parse expression for number of instances to create", e));
				num = 1;
			}
		}
		else {
			num = 1;
		}

		TypeDefinition targetType = getTarget().values().iterator().next().getDefinition()
				.getDefinition();

		for (int i = 0; i < num; i++) {
			Iterable<MutableInstance> target = createInstances(targetType, log, cell, i);
			for (MutableInstance instance : target) {
				getPropertyTransformer().publish(null, instance, log, cell);
			}
		}
	}

	private Iterable<MutableInstance> createInstances(TypeDefinition type, TransformationLog log,
			Cell cell, int index) throws TransformationException {
		InstanceBuilder builder = new InstanceBuilder(false);

		Binding binding = GroovyUtil.createBinding(builder, cell, cell, log, getExecutionContext(),
				type);
		binding.setProperty(BINDING_INDEX, index);

		try {
			GroovyService service = getExecutionContext().getService(GroovyService.class);
			Script script = GroovyUtil.getScript(this, binding, service);
			return GroovyUtil.evaluateAll(script, builder, type, service, log);
		} catch (TransformationException e) {
			throw e;
		} catch (NoResultException e) {
			log.info(log.createMessage(
					"Skipping target instance because received NoResultException from script",
					null));
			return Collections.emptyList();
		} catch (Exception e) {
			throw new TransformationException(e.getMessage(), e);
		}
	}

}
