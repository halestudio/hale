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

package eu.esdihumboldt.hale.ui.geometry.service;

import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Service that manages default geometry information for definitions.
 * 
 * @author Simon Templer
 */
public interface GeometrySchemaService {

	/**
	 * Get the property path to the default geometry of the given type.<br>
	 * <br>
	 * Note that the path does not necessarily have to point to a property with
	 * {@link GeometryProperty} binding, but can also lead to a property
	 * containing nested geometry properties (e.g. in a choice).
	 * 
	 * @param type the type definition
	 * @return the property path to the default geometry property, or
	 *         <code>null</code> if there is none
	 */
	public List<QName> getDefaultGeometry(TypeDefinition type);

	/**
	 * Set the property path of the default geometry for the given type.<br>
	 * <br>
	 * Note that the path does not necessarily have to point to a property with
	 * {@link GeometryProperty} binding, but can also lead to a property
	 * containing nested geometry properties (e.g. in a choice).
	 * 
	 * @param type the type definition
	 * @param path the property path
	 */
	public void setDefaultGeometry(TypeDefinition type, List<QName> path);

	/**
	 * Adds a listener for service events.
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(GeometrySchemaServiceListener listener);

	/**
	 * Removes a listener for service events.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(GeometrySchemaServiceListener listener);

}
