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

import eu.esdihumboldt.hale.common.core.io.ImportProvider;

/**
 * Reader interface for snippets.
 * 
 * @author Simon Templer
 */
public interface SnippetReader extends ImportProvider {

	/**
	 * Name of the parameter identifying the snippet identifier.
	 */
	static final String PARAM_IDENTIFIER = "identifier";

	/**
	 * Name of the parameter enabling auto-reload (only applicable for files).
	 */
	static final String PARAM_AUTO_RELOAD = "autoReload";

	/**
	 * Get the loaded snippet.
	 * 
	 * @return the snippet
	 */
	Snippet getSnippet();

	/**
	 * Set the snippet identifier.
	 * 
	 * @param identifier the snippet identifier
	 */
	void setIdentifier(String identifier);

	/**
	 * Set if auto reload should be enabled for file based snippets.
	 * 
	 * @param autoReload the value to set
	 */
	void setAutoReload(boolean autoReload);

}
