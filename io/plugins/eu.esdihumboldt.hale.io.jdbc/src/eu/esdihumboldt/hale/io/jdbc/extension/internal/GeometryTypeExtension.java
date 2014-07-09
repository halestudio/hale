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

package eu.esdihumboldt.hale.io.jdbc.extension.internal;

import java.sql.Connection;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

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
	 * @see de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension#create(java.lang.String, org.eclipse.core.runtime.IConfigurationElement)
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
