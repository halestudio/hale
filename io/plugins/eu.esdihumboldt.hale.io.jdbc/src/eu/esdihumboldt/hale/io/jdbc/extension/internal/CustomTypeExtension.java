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

package eu.esdihumboldt.hale.io.jdbc.extension.internal;

import java.sql.Connection;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;

/**
 * TODO Type description
 * 
 * @author Sameer Sheikh
 */
public class CustomTypeExtension extends IdentifiableExtension<CustomType> {

	/**
	 * extension id
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.hale.io.jdbc.config";
	private static CustomTypeExtension instance;

	/**
	 * Constructor
	 */
	public CustomTypeExtension() {
		super(EXTENSION_ID);

	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension#create(java.lang.String,
	 *      org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected CustomType create(String elementId, IConfigurationElement element) {
		if (element.getName().equals("customtype")) {
			return new CustomType(elementId, element);
		}

		return null;
	}

	/**
	 * @return The CustomType extension
	 */
	public static CustomTypeExtension getInstance() {
		if (instance == null) {
			instance = new CustomTypeExtension();
		}
		return instance;
	}

	/**
	 * @param name column type name
	 * @param connection current connection
	 * @return custom type
	 */
	public CustomType getCustomType(String name, Connection connection) {
		for (CustomType type : getElements()) {
			if (type.getName().equals(name) && type.isInstance(connection)) {
				return type;
			}
		}
		return null;
	}
}
