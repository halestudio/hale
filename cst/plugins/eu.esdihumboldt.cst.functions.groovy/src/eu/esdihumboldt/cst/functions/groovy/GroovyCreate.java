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
public class GroovyCreate extends Create implements GroovyConstants {

	@Override
	protected MutableInstance createInstance(TypeDefinition type, int index, TransformationLog log,
			Cell cell) throws TransformationException {
		InstanceBuilder builder = new InstanceBuilder(false);

		Binding binding = createBinding(index, builder);

		try {
			GroovyService service = getExecutionContext().getService(GroovyService.class);
			Script script = GroovyUtil.getScript(this, binding, service);
			return GroovyUtil.evaluate(script, builder, type, service);
		} catch (TransformationException e) {
			throw e;
		} catch (Exception e) {
			throw new TransformationException(e.getMessage(), e);
		}
	}

	/**
	 * Create the binding for the Groovy Create script function.
	 * 
	 * @param index the instance index
	 * @param builder the instance builder
	 * @return the binding
	 */
	public static Binding createBinding(int index, InstanceBuilder builder) {
		Binding binding = new Binding();
		binding.setVariable(BINDING_TARGET, null);
		binding.setVariable(BINDING_INDEX, index);
		binding.setVariable(BINDING_BUILDER, builder);
		return binding;
	}

}
