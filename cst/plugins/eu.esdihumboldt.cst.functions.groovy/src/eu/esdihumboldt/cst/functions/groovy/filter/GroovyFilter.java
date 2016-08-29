/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.groovy.filter;

import java.util.Map;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctions;
import eu.esdihumboldt.cst.functions.groovy.internal.SynchronizedContextProvider;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.instance.model.ContextAwareFilter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService.ResultProcessor;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Filter based on a Groovy script.
 * 
 * @author Simon Templer
 */
public class GroovyFilter implements ContextAwareFilter {

	private static final ALogger log = ALoggerFactory.getLogger(GroovyFilter.class);

	private final String script;

	private final ThreadLocal<Script> localScript = new ThreadLocal<Script>();

	/**
	 * Constructor.
	 * 
	 * @param script the Groovy script text
	 */
	public GroovyFilter(String script) {
		super();
		this.script = script;
	}

	@Override
	public boolean match(Instance instance) {
		return match(instance, null);
	}

	/**
	 * Get a Groovy script instance and configure it with the given binding.
	 * 
	 * @param service the Groovy service
	 * @param binding the script binding
	 * @return the Groovy script instance
	 */
	protected Script getScript(GroovyService service, Binding binding) {
		/*
		 * The compiled script is stored in a ThreadLocal variable so it needs
		 * only to be created once per filter thread.
		 */
		Script groovyScript = localScript.get();
		if (groovyScript == null) {
			groovyScript = service.parseScript(script, null);
			localScript.set(groovyScript);
		}
		groovyScript.setBinding(binding);

		return groovyScript;
	}

	@Override
	public boolean match(Instance instance, Map<Object, Object> context) {
		GroovyService service = HalePlatform.getService(GroovyService.class);

		Binding binding = new Binding();

		// helper functions
		binding.setVariable(GroovyConstants.BINDING_HELPER_FUNCTIONS,
				HelperFunctions.createDefault());
		// instance
		binding.setVariable("instance", instance);
		// context
		binding.setVariable("withContext", SynchronizedContextProvider.getContextClosure(context));
		// log
		binding.setVariable(GroovyConstants.BINDING_LOG, log);

		Script script = getScript(service, binding);

		try {
			return service.evaluate(script, new ResultProcessor<Boolean>() {

				@Override
				public Boolean process(Script script, Object returnValue) throws Exception {
					if (returnValue != null && returnValue.equals(true)) {
						return true;
					}
					return false;
				}
			});
		} catch (Exception e) {
			throw new IllegalStateException("Error evaluating Groovy filter", e);
		}
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

}
