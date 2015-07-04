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

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Configures the custom types information
 * 
 * @author Sameer Sheikh
 */
public class CustomType implements Identifiable {

	private final String elementId;
	private final Class<?> connectionType;
	private final int sqltype;
	private final Class<?> binding;
	private final String name;

	/**
	 * Create a customtype configuration from a configuration element.
	 * 
	 * @param elementId the identifier
	 * @param element the configuration element
	 */
	public CustomType(String elementId, IConfigurationElement element) {
		this.elementId = elementId;
		this.connectionType = ExtensionUtil.loadClass(element, "connection");
		this.binding = ExtensionUtil.loadClass(element, "binding");
		this.name = element.getAttribute("name");
		this.sqltype = Integer.parseInt(element.getAttribute("sqltype"));

	}

	@Override
	public String getId() {
		return elementId;
	}

	/**
	 * @return configured custom type name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return configured sqltype
	 */
	public int getSQLType() {
		return sqltype;
	}

	/**
	 * @param connection current connection
	 * @return true if current connection is an instance of configured
	 *         connection type.
	 */
	public boolean isInstance(Connection connection) {
		return connectionType.isInstance(connection);

	}

	/**
	 * @return configured binding
	 */
	public Class<?> getBinding() {
		return binding;
	}

}
