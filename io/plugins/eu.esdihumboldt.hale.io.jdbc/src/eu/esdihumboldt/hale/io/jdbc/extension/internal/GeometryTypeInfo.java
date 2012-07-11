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

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.eclipse.extension.ExtensionUtil;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Holds information about a geometry type.
 * @author Simon Templer
 */
public class GeometryTypeInfo implements Identifiable {
	
//	private static final ALogger log = ALoggerFactory.getLogger(GeometryTypeInfo.class);

	private final String elementId;
	private final Class<?> connectionType;
	private final String typeName;
	private final Class<? extends Geometry> geometryType;
	
	/**
	 * Create a connection configuration from a corresponding 
	 * configuration element.
	 * @param elementId the identifier
	 * @param element the configuration element
	 */
	@SuppressWarnings("unchecked")
	public GeometryTypeInfo(String elementId,
			IConfigurationElement element) {
		this.elementId = elementId;
		
		connectionType = ExtensionUtil.loadClass(element, "connection");
		geometryType = (Class<? extends Geometry>) ExtensionUtil.loadClass(element, "type");
		
		typeName = element.getAttribute("name");
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable#getId()
	 */
	@Override
	public String getId() {
		return elementId;
	}
	
	/**
	 * Determines if the geometry type applies to a database
	 * with the given connection.
	 * @param connection the database connection
	 * @return if the geometry type is valid for the database
	 */
	public boolean applies(Connection connection) {
		return connectionType.isInstance(connection);
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @return the geometryType
	 */
	public Class<? extends Geometry> getGeometryType() {
		return geometryType;
	}

}
