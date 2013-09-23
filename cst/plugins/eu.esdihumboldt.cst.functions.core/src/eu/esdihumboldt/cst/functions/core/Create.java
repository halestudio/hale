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

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.CreateFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Function that creates target instances independently from source instances.
 * As it is not bound to source instances, it should only be executed once, not
 * per source.
 * 
 * @author Simon Templer
 */
@Immutable
public class Create extends AbstractTypeTransformation<TransformationEngine> implements
		CreateFunction {

	@Override
	public void execute(String transformationIdentifier, TransformationEngine engine,
			Map<String, String> executionParameters, TransformationLog log, Cell cell)
			throws TransformationException {
		int number = getOptionalParameter(PARAM_NUMBER, Value.of(1)).as(Integer.class);

		for (int i = 0; i < number; i++) {
			// create <number> of instances of the target type
			TypeDefinition targetType = getTarget().values().iterator().next().getDefinition()
					.getDefinition();
			MutableInstance target = getInstanceFactory().createInstance(targetType);
			getPropertyTransformer().publish(null, target, log, cell);
		}
	}

}
