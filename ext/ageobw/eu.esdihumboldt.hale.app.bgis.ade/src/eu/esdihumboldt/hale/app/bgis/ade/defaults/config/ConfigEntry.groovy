/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.defaults.config


/**
 * A configuration entry.
 * 
 * @author Simon Templer
 */
class ConfigEntry {

	/**
	 * Name of the feature type the configuration is associated to, may be
	 * <code>null</code>. 
	 */
	String featureType

	/**
	 * Name of the attribute the configuration is associated to.
	 */
	String attribute

	/**
	 * The default value assigned to the attribute.
	 */
	String defaultValue
}
