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

package eu.esdihumboldt.util.resource.internal;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;


/**
 * Represents a resource type.
 * @author Simon Templer
 */
public class ResourceType implements Identifiable {
	
	private final String id;
	
	private final String name;

	/**
	 * @param id the resource type ID
	 * @param name the resource type name, may be <code>null</code>
	 */
	public ResourceType(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the resource type name. If no name was explicitly specified the name
	 * is equal to the resource type ID.
	 * @return the resource type name
	 */
	public String getName() {
		return (name != null)?(name):(id);
	}

}
