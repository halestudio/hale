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

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import groovy.lang.Script;

/**
 * Interface for snippets.
 * 
 * @author Simon Templer
 */
public interface Snippet {

	/**
	 * Get the script.
	 * 
	 * @param services the service provider
	 * @return the snippet script
	 * @throws Exception if an error occurs getting the script
	 */
	Script getScript(ServiceProvider services) throws Exception;

	/**
	 * Get the snippet identifier
	 * 
	 * @return the snippet identifier
	 */
	String getIdentifier();

	/**
	 * Invalidate the snippet to trigger reloading after Groovy restrictions
	 * settings have changed.
	 */
	void invalidate();

}
