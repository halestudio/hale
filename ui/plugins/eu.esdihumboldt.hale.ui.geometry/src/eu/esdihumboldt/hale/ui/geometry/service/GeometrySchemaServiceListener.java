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

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Listener for {@link GeometrySchemaService} events.
 * 
 * @author Simon Templer
 */
public interface GeometrySchemaServiceListener {

	/**
	 * Notifies the listener that the default geometry for the given type has
	 * changed.
	 * 
	 * @param type the type definition
	 */
	public void defaultGeometryChanged(TypeDefinition type);

}
