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
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
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
		implements GroovyConstants {

	@Override
	public void execute(String transformationIdentifier, TransformationEngine engine,
			Map<String, String> executionParameters, TransformationLog log, Cell cell)
					throws TransformationException {

		TypeDefinition targetType = getTarget().values().iterator().next().getDefinition()
				.getDefinition();
		Iterable<MutableInstance> target = createInstances(targetType, log, cell);
		for (MutableInstance instance : target) {
			getPropertyTransformer().publish(null, instance, log, cell);
		}
	}

	private Iterable<MutableInstance> createInstances(TypeDefinition type, TransformationLog log,
			Cell cell) throws TransformationException {
		InstanceBuilder builder = new InstanceBuilder(false);

		Binding binding = GroovyUtil.createBinding(builder, cell, cell, log, getExecutionContext());

		try {
			GroovyService service = getExecutionContext().getService(GroovyService.class);
			Script script = GroovyUtil.getScript(this, binding, service);
			return GroovyUtil.evaluateAll(script, builder, type, service);
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
