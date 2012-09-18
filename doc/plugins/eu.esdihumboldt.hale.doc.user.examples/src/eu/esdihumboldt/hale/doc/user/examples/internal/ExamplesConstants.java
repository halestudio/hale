/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.doc.user.examples.internal;

import org.eclipse.help.ITopic;

/**
 * Constants related to examples and help.
 * 
 * @author Simon Templer
 */
public interface ExamplesConstants {

	/**
	 * The ID of this plug-in.
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.doc.user.examples";

	/**
	 * The path referencing the plug-ins root.
	 */
	public static final String PLUGINS_ROOT = "PLUGINS_ROOT";

	/**
	 * The pat of the overview page in this bundle.
	 */
	public static final String PATH_OVERVIEW = "overview.html";

	/**
	 * The prefix for project pages
	 */
	public static final String PATH_PREFIX_PROJECT = "projects/";

	/**
	 * The suffix for references to the mapping documentation
	 */
	public static final String PATH_SUFFIX_MAPPINGDOC = "/mapping";

	/**
	 * Empty topics array
	 */
	public static final ITopic[] NO_TOPICS = new ITopic[] {};

}
