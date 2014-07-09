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

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.io.jdbc.GeometryAdvisor;
import eu.esdihumboldt.hale.io.jdbc.constraints.internal.GeometryAdvisorConstraint;

/**
 * Holds information about a geometry type.
 * 
 * @author Simon Templer
 */
public class GeometryTypeInfo implements Identifiable {

//	private static final ALogger log = ALoggerFactory.getLogger(GeometryTypeInfo.class);

	private final String elementId;
	private final Class<?> connectionType;
	private final String typeName;
	private final GeometryAdvisor<?> advisor;
	private final GeometryAdvisorConstraint constraint;

	/**
	 * Create a connection configuration from a corresponding configuration
	 * element.
	 * 
	 * @param elementId the identifier
	 * @param element the configuration element
	 */
	@SuppressWarnings("unchecked")
	public GeometryTypeInfo(String elementId, IConfigurationElement element) {
		this.elementId = elementId;

		connectionType = ExtensionUtil.loadClass(element, "connection");
		Class<? extends GeometryAdvisor<?>> advisorClass = (Class<? extends GeometryAdvisor<?>>) ExtensionUtil
				.loadClass(element, "advisor");
		try {
			advisor = advisorClass.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to create geometry advisor", e);
		}
		constraint = new GeometryAdvisorConstraint(advisor);

		typeName = element.getAttribute("name");
	}

	@Override
	public String getId() {
		return elementId;
	}

	/**
	 * Determines if the geometry type applies to a database with the given
	 * connection.
	 * 
	 * @param connection the database connection
	 * @return if the geometry type is valid for the database
	 */
	public boolean applies(Connection connection) {
		return connectionType.isInstance(connection);
	}

	/**
	 * @return the type name
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @return the associated geometry advisor
	 */
	public GeometryAdvisor<?> getGeometryAdvisor() {
		return advisor;
	}

	/**
	 * @return the geometry advisor constraint
	 */
	public GeometryAdvisorConstraint getConstraint() {
		return constraint;
	}

}
