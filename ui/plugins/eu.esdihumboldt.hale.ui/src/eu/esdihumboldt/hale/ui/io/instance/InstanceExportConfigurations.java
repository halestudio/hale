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

package eu.esdihumboldt.hale.ui.io.instance;

/**
 * Interface to define parameters for the export configurations
 * 
 * @author Patrick Lieb
 */
public interface InstanceExportConfigurations {

	/**
	 * Name of the export configuration stored as a parameter in the provider
	 */
	public final String PARAM_CONFIGURATION_NAME = "configurationName";

	/**
	 * Description of the export configuration stored as a parameter in the
	 * provider
	 */
	public final String PARAM_CONFIGURATION_DESCRIPTION = "description";

	/**
	 * Format of the file that should be export in the export configuration
	 * stored as a parameter in the provider
	 */
	public final String PARAM_FILE_FORMAT = "fileFormat";

}
