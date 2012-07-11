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

package eu.esdihumboldt.hale.io.jdbc.extension.internal;

import java.sql.Connection;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension;

/**
 * Extension for identifying geometry types.
 * @author Simon Templer
 */
public class GeometryTypeExtension extends IdentifiableExtension<GeometryTypeInfo> {
	
	private static GeometryTypeExtension instance;
	
	/**
	 * Get the extension instance.
	 * @return the extension instance
	 */
	public static GeometryTypeExtension getInstance() {
		if (instance == null) {
			instance = new GeometryTypeExtension();
		}
		return instance;
	}
	
	/**
	 * Default constructor
	 */
	public GeometryTypeExtension() {
		super(ConnectionConfigurerExtension.EXTENSION_ID);
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension#create(java.lang.String, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected GeometryTypeInfo create(String elementId,
			IConfigurationElement element) {
		if (element.getName().equals("geometrytype")) {
			return new GeometryTypeInfo(elementId, element);
		}
		return null;
	}

	/**
	 * Get the geometry type info associated to a type name and connection type,
	 * if any.
	 * @param name the (column) type name
	 * @param connection the database connection
	 * @return the geometry type info or <code>null</code> if there is none
	 *   associated
	 */
	public GeometryTypeInfo getTypeInfo(String name, Connection connection) {
		for (GeometryTypeInfo info : getElements()) {
			if (info.getTypeName().equals(name) && info.applies(connection)) {
				return info;
			}
		}
		
		return null;
	}
	
}
