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

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.util.Map;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTransformationFunction;
import eu.esdihumboldt.hale.common.core.io.Text;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Groovy function utilities.
 * 
 * @author Simon Templer
 */
public class GroovyUtil implements GroovyConstants {

	/**
	 * Get the compiled script.
	 * 
	 * @param function the transformation function the script is associated to
	 * @param binding the binding to set on the script
	 * @return the script
	 * @throws TransformationException if getting the script parameter from the
	 *             function fails
	 */
	@SuppressWarnings("unchecked")
	public static Script getScript(AbstractTransformationFunction<?> function, Binding binding)
			throws TransformationException {
		/*
		 * The compiled script is stored in a ThreadLocal variable in the
		 * execution context, so it needs only to be created once per
		 * transformation thread.
		 */
		ThreadLocal<Script> localScript;
		Map<Object, Object> context = function.getExecutionContext().getCellContext();
		synchronized (context) {
			Object tmp = context.get(CONTEXT_SCRIPT);

			if (tmp instanceof ThreadLocal<?>) {
				localScript = (ThreadLocal<Script>) tmp;
			}
			else {
				localScript = new ThreadLocal<Script>();
				context.put(CONTEXT_SCRIPT, localScript);
			}
		}

		Script groovyScript = localScript.get();
		if (groovyScript == null) {
			// create the script
			// TODO use a specific classloader?
			GroovyShell shell = new GroovyShell();
			ParameterValue scriptValue = function.getParameterChecked(PARAMETER_SCRIPT);

			String script;
			// try retrieving as text
			Text text = scriptValue.as(Text.class);
			if (text != null) {
				script = text.getText();
			}
			else {
				// fall back to string value
				script = scriptValue.as(String.class);
			}

			groovyScript = shell.parse(script);
			localScript.set(groovyScript);
		}

		// set the binding
		groovyScript.setBinding(binding);

		return groovyScript;
	}

}
