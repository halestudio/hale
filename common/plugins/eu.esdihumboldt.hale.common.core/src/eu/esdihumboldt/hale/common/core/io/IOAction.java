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

package eu.esdihumboldt.hale.common.core.io;

import java.util.Set;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents an I/O action
 * 
 * @author Simon Templer
 */
public interface IOAction extends Identifiable {

	/**
	 * Get the I/O provider type supported by the action.
	 * 
	 * @return the I/O provider type
	 */
	public Class<? extends IOProvider> getProviderType();

	/**
	 * Get the name for this kind of resource.
	 * 
	 * @return the resource name
	 */
	public String getResourceName();

	/**
	 * Get the category name for this kind of resource.
	 * 
	 * @return the resource category name
	 */
	public String getResourceCategoryName();

	/**
	 * Get the dependencies of the action.
	 * 
	 * @return the list of identifiers of other actions the action depends on
	 *         for sequential execution, e.g. when loading a project
	 */
	public Set<String> getDependencies();

	/**
	 * Get the action name
	 * 
	 * @return the name, may be <code>null</code>
	 */
	public String getName();

}
