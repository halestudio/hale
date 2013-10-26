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

import eu.esdihumboldt.cst.functions.core.Create;
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Script;

/**
 * Creates instances based on a groovy script.
 * 
 * @author Simon Templer
 */
public class GroovyCreate extends Create implements GroovyConstants {

	@Override
	protected MutableInstance createInstance(TypeDefinition type, int index, TransformationLog log,
			Cell cell) throws TransformationException {
		InstanceBuilder builder = new InstanceBuilder();

		Binding binding = new Binding();
		binding.setVariable(BINDING_TARGET, null);
		binding.setVariable(BINDING_INDEX, index);
		binding.setVariable(BINDING_BUILDER, builder);

		try {
			Script script = GroovyUtil.getScript(this, binding);
			script.run();

			Closure<?> closure = (Closure<?>) binding.getVariable(BINDING_TARGET);

			Instance instance = builder.createInstance(type, closure);

			return (MutableInstance) instance;
		} catch (TransformationException e) {
			throw e;
		} catch (Exception e) {
			throw new TransformationException(e.getMessage(), e);
		}
	}

}
