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

package eu.esdihumboldt.hale.io.groovy.snippets.impl;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.io.groovy.snippets.Snippet;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Snippet that is already loaded.
 * 
 * @author Simon Templer
 */
public class StringSnippet implements Snippet {

	private Script lastScript = null;
	private final String id;
	private final String script;

	/**
	 * Create a new snippet.
	 * 
	 * @param script the script text
	 * @param id the snippet identifier
	 */
	public StringSnippet(String script, String id) {
		this.script = script;
		this.id = id;
	}

	@Override
	public void invalidate() {
		lastScript = null;
	}

	@Override
	public synchronized Script getScript(ServiceProvider services) throws Exception {
		if (lastScript == null) {
			GroovyService service = services.getService(GroovyService.class);
			Binding binding = null;

			lastScript = service.parseScript(script, binding);
		}

		return lastScript;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

}
