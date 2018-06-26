/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc;

import java.util.function.Supplier;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import schemacrawler.schema.BaseColumn;
import schemacrawler.schema.ColumnDataType;

/**
 * Database specific advisor for handling geometries. It may hold no state, as
 * ideally there will be only one instance of an advisor handling all geometry
 * columns and values.
 * 
 * @param <C> the connection type
 * 
 * @author Simon Templer
 */
public interface GeometryAdvisor<C> {

	/**
	 * Determines if for a column type detected as geometry type and associated
	 * to this advisor is a fixed type, i.e. if it is to be configured the same
	 * for all columns where it is used.
	 * 
	 * @param columnType the column data type
	 * @return if there should be only one type definition for all columns with
	 *         the given type
	 */
	public boolean isFixedType(ColumnDataType columnType);

	/**
	 * Configure the type for a geometry column.
	 * 
	 * @param connection the JDBC connection
	 * @param column the geometry column
	 * @param type the type definition associated to the column. It may be
	 *            adapted by adding custom constraints, but {@link GeometryType}
	 *            and {@link Binding} will be set by the caller
	 * @param log the log
	 * @return the geometry type that should be used for the
	 *         {@link GeometryType} binding
	 */
	public Class<? extends Geometry> configureGeometryColumnType(C connection, BaseColumn<?> column,
			DefaultTypeDefinition type, SimpleLog log);

	/**
	 * Convert a property to a value that can be stored in the database.
	 * 
	 * @param geom the geometry property value consisting of geometry and the
	 *            associated CRS definition
	 * @param columnType the type definition of the associated geometry column
	 *            that was previously configured using
	 *            {@link #configureGeometryColumnType(Object, BaseColumn, DefaultTypeDefinition, SimpleLog)}
	 * @param connection Connection
	 * @param log the log
	 * @return the converted geometry that can be written to the database
	 * @throws Exception something went wrong
	 */
	public Object convertGeometry(GeometryProperty<?> geom, TypeDefinition columnType, C connection,
			SimpleLog log) throws Exception;

	/**
	 * Convert a geometry read from the database to a geometry property.
	 * 
	 * @param geom the geometry read from the database
	 * @param columnType the type definition of the associated geometry column
	 *            that was previously configured using
	 *            {@link #configureGeometryColumnType(Object, BaseColumn, DefaultTypeDefinition, SimpleLog)}
	 * @param connection The connection
	 * @param crsProvider The CRS provider
	 * @param log the log
	 * @return the geometry property
	 * @throws Exception if the conversion failed
	 */
	public GeometryProperty<?> convertToInstanceGeometry(Object geom, TypeDefinition columnType,
			C connection, Supplier<CRSDefinition> crsProvider, SimpleLog log) throws Exception;

}
