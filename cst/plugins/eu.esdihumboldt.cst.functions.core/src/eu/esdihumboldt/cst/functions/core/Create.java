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

package eu.esdihumboldt.cst.functions.core;

import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.CreateFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import net.jcip.annotations.Immutable;

/**
 * Function that creates target instances independently from source instances.
 * As it is not bound to source instances, it should only be executed once, not
 * per source.
 * 
 * @author Simon Templer
 */
@Immutable
public class Create extends AbstractTypeTransformation<TransformationEngine>
		implements CreateFunction {

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

		for (int i = 0; i < num; i++) {
			// create <number> of instances of the target type
			TypeDefinition targetType = getTarget().values().iterator().next().getDefinition()
					.getDefinition();
			MutableInstance target = createInstance(targetType, i, log, cell);
			getPropertyTransformer().publish(null, target, log, cell);
		}
	}

	/**
	 * Create an instance.
	 * 
	 * @param type the instance type
	 * @param index the instance index
	 * @param log the transformation log
	 * @param cell the type cell
	 * @return the created instance
	 * @throws TransformationException if a transformation error occurs
	 */
	@SuppressWarnings("unused")
	protected MutableInstance createInstance(TypeDefinition type, int index, TransformationLog log,
			Cell cell) throws TransformationException {
		return getInstanceFactory().createInstance(type);
	}

}
