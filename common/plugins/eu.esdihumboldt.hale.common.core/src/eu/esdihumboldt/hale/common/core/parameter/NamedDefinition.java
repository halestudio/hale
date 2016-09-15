/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.parameter;

/**
 * Interface for named definitions, e.g. parameters.
 * 
 * @author Simon Templer
 */
public interface NamedDefinition {

	/**
	 * @return the parameter name
	 */
	public abstract String getName();

	/**
	 * Get the display name for the parameter. If present the parameter label
	 * will be used, otherwise the parameter name is returned. In case the
	 * parameter name is <code>null</code> an empty string is returned.
	 * 
	 * @return the parameter display name
	 */
	public abstract String getDisplayName();

	/**
	 * Get the parameter description
	 * 
	 * @return the description, may be <code>null</code>
	 */
	public abstract String getDescription();

}