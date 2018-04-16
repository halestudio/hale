/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.groovy.snippets;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.groovy.runtime.InvokerHelper;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.Script;

/**
 * Access snippets in Groovy binding.
 * 
 * @author Simon Templer
 */
public class GroovySnippets extends GroovyObjectSupport {

	private final SnippetService snippets;
	private final Binding parentBinding;
	private final ServiceProvider services;

	/**
	 * Constructor.
	 * 
	 * @param services the service provider
	 * @param parentBinding the binding of the parent script
	 */
	public GroovySnippets(ServiceProvider services, Binding parentBinding) {
		super();
		this.services = services;
		this.snippets = services.getService(SnippetService.class);
		this.parentBinding = parentBinding;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Script loadScript(String id, Map<?, ?> moreVariables) throws Exception {
		Optional<Snippet> snippet = snippets.getSnippet(id);
		if (snippet.isPresent()) {
			Script script = snippet.get().getScript(services);
			// "clone" the script
			script = script.getClass().newInstance();
			Map variables = new HashMap<>();
			if (parentBinding != null) {
				variables.putAll(parentBinding.getVariables());
			}
			if (moreVariables != null) {
				variables.putAll(moreVariables);
			}
			Binding binding = new Binding(variables);
			script.setBinding(binding);
			return script;
		}
		else {
			return null;
		}
	}

	@Override
	public Object getProperty(String property) {
		try {
			Script script = loadScript(property, null);
			if (script == null) {
				throw new IllegalArgumentException(MessageFormat
						.format("Snippet with identifer \"{0}\" not found.", property));
			}

			return script;

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object invokeMethod(String name, Object args) {
		List<?> argList = new ArrayList<>(InvokerHelper.asList(args));

		try {
			Map<Object, Object> vars = new HashMap<>();

			Optional<?> firstMap = argList.stream().filter(v -> v instanceof Map).findFirst();
			if (firstMap.isPresent()) {
				vars.putAll((Map) firstMap.get());
				argList.remove(firstMap.get());
			}

			Script script = loadScript(name, vars);
			if (script == null) {
				throw new IllegalArgumentException(
						MessageFormat.format("Snippet with identifer \"{0}\" not found.", name));
			}

			if (argList.isEmpty()) {
				// run script
				return script.run();
			}
			else if (argList.size() == 1 && argList.get(0) instanceof Closure) {
				// run closure on script
				Closure cl = (Closure) argList.get(0);
				cl.setDelegate(script);
				cl.setResolveStrategy(Closure.OWNER_FIRST);
				return cl.call();
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return super.invokeMethod(name, args);
	}

}
