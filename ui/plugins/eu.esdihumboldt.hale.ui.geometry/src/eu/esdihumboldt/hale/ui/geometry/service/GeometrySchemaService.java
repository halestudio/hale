/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
