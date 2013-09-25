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

package eu.esdihumboldt.hale.common.core.io.project;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Extended confiugration service interface.
 * 
 * @author Simon Templer
 */
public interface ComplexConfigurationService extends IConfigurationService {

	/**
	 * Set the property with the given name to the given value.
	 * 
	 * @param name the property name
	 * @param value the property value
	 */
	public void setProperty(String name, Value value);

	/**
	 * Get the property value for the property with the given name.
	 * 
	 * @param name the property name
	 * @return the property value, may be a null value but not <code>null</code>
	 */
	public Value getProperty(String name);

}
