/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.doc.user.ioproviders;

import org.eclipse.help.ITopic;

/**
 * Constants for InstanceIOReference
 * 
 * @author Yasmina Kammeyer
 */
public interface IOReferenceConstants {

	/**
	 * The identifier of this plug-in
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.doc.user.ioproviders";

	/**
	 * The path referencing the plug-ins root
	 */
	public static final String PLUGINS_ROOT = "PLUGINS_ROOT";

	/**
	 * The base path of function topics
	 */
	public static final String OVERVIEW_TOPIC_PATH = "overview/";

	/**
	 * The base path of function topics
	 */
	public static final String IO_PROVIDERS_TOPIC_PATH = "ioproviders/";

	/**
	 * Empty topics array
	 */
	public static final ITopic[] NO_TOPICS = new ITopic[] {};
}
