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
import groovy.lang.Script;

/**
 * Snippet that is already loaded.
 * 
 * @author Simon Templer
 */
public class LoadedSnippet implements Snippet {

	private final Script script;
	private final String id;

	/**
	 * Create a new snippet.
	 * 
	 * @param script the snippet script
	 * @param id the snippet identifier
	 */
	public LoadedSnippet(Script script, String id) {
		this.script = script;
		this.id = id;
	}

	@Override
	public Script getScript(ServiceProvider services) {
		return script;
	}

	@Override
	public String getIdentifier() {
		return id;
	}

}
