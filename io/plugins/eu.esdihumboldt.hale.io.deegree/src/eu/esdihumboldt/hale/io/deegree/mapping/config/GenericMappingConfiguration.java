/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.deegree.mapping.config;

import java.util.Optional;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.feature.persistence.sql.GeometryStorageParams;
import org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension;
import org.deegree.sqldialect.SQLDialect;
import org.deegree.sqldialect.postgis.PostGISDialect;

import eu.esdihumboldt.util.config.Config;

/**
 * Mapping configuration based on a generic configuration object.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public class GenericMappingConfiguration implements MappingConfiguration {

	public static enum DatabaseType {
		PostGIS;
	}

	public static final String KEY_JDBC_CONNECTION_ID = "database.connectionId";
	public static final String KEY_DATABASE_MAX_NAME_LENGTH = "database.names.maxLength";

	public static final String KEY_DATABASE_TYPE = "database.type";
	public static final String KEY_DATABASE_VERSION = "database.version";

	public static final String KEY_INTEGER_IDS = "database.useIntegerIDs";
	public static final String KEY_NAMESPACE_PREFIX = "database.names.useNamespacePrefix";

	public static final String KEY_MAPPING_MODE = "featureStore.mappingMode";

	public static final String KEY_ID_PREFIX_MODE = "featureStore.idPrefixMode";

	public static final String KEY_PRIMITIVE_LINK_MODE = "featureStore.primitiveLinkMode";

	public static final String KEY_CRS_IDENTIFIER = "featureStore.crs.identifier";
	public static final String KEY_CRS_DIMENSION = "featureStore.crs.dimension";
	public static final String KEY_CRS_SRID = "featureStore.crs.srid";

	public static final DatabaseType DEFAULT_DATABASE_TYPE = DatabaseType.PostGIS;
	public static final MappingMode DEFAULT_MAPPING_MODE = MappingMode.relational;
	public static final IDPrefixMode DEFAULT_ID_PREFIX_MODE = IDPrefixMode.deegree;
	public static final PrimitiveLinkMode DEFAULT_PRIMITIVE_LINK_MODE = PrimitiveLinkMode.none;
	// Note: PostGIS uses lon/lat by default
	public static final String DEFAULT_CRS_IDENTIFIER = "EPSG:4326"; // "urn:ogc:def:crs:epsg::4326";
	public static final String DEFAULT_JDBC_CONNECTION_ID = "db";
	public static final boolean DEFAULT_INTEGER_IDS = false;
	public static final boolean DEFAULT_NAMESPACE_PREFIX_FOR_TABLE_NAMES = true;

	private volatile Config config;

	/**
	 * @param config the configuration object
	 */
	public GenericMappingConfiguration(Config config) {
		super();
		this.config = config;
	}

	/**
	 * Change the backing configuration object.
	 * 
	 * @param config the internal configuration object to set
	 */
	public void setInternalConfig(Config config) {
		this.config = config;
	}

	/**
	 * @return the internal configuration object
	 */
	public Config getInternalConfig() {
		return config;
	}

	public void fillDefaults() {
		setMappingMode(DEFAULT_MAPPING_MODE);

		setIDPrefixMode(DEFAULT_ID_PREFIX_MODE);

		setPrimitiveLinkMode(DEFAULT_PRIMITIVE_LINK_MODE);

		setDatabaseType(DEFAULT_DATABASE_TYPE, getDefaultVersion(DEFAULT_DATABASE_TYPE));

		setCRSIdentifier(DEFAULT_CRS_IDENTIFIER);

		setJDBCConnectionId(DEFAULT_JDBC_CONNECTION_ID);

		setUseIntegerIDs(DEFAULT_INTEGER_IDS);
		setUseNamespacePrefixForTableNames(DEFAULT_NAMESPACE_PREFIX_FOR_TABLE_NAMES);
	}

	@Override
	public MappingMode getMode() {
		return config.get(KEY_MAPPING_MODE, String.class).map(MappingMode::valueOf)
				// fall-back
				.orElse(DEFAULT_MAPPING_MODE);
	}

	/**
	 * Set the mapping mode.
	 * 
	 * @param mode the mapping mode
	 */
	public void setMappingMode(MappingMode mode) {
		config.set(KEY_MAPPING_MODE, mode.name());
	}

	@Override
	public IDPrefixMode getIDPrefixMode() {
		return config.get(KEY_ID_PREFIX_MODE, String.class).map(IDPrefixMode::valueOf)
				// fall-back
				.orElse(DEFAULT_ID_PREFIX_MODE);
	}

	/**
	 * Set the mode for generating ID prefixes.
	 * 
	 * @param mode the prefix ID generation mode
	 */
	public void setIDPrefixMode(IDPrefixMode mode) {
		config.set(KEY_ID_PREFIX_MODE, mode.name());
	}

	@Override
	public PrimitiveLinkMode getPrimitiveLinkMode() {
		return config.get(KEY_PRIMITIVE_LINK_MODE, String.class).map(PrimitiveLinkMode::valueOf)
				// fall-back
				.orElse(DEFAULT_PRIMITIVE_LINK_MODE);
	}

	/**
	 * Set the mode for determining primitive links.
	 * 
	 * @param mode the primitive link mode
	 */
	public void setPrimitiveLinkMode(PrimitiveLinkMode mode) {
		config.set(KEY_PRIMITIVE_LINK_MODE, mode.name());
	}

	@Override
	public SQLDialect getSQLDialect() {
		// get database type and version
		DatabaseType type = getDatabaseType();
		String version = getDatabaseVersion();

		switch (type) {
		case PostGIS:
			if (version == null) {
				version = getDefaultVersion(type);
			}
			return new PostGISDialect(version);
		default:
			throw new IllegalStateException("Unknown database type: " + type);
		}
	}

	protected String getDefaultVersion(DatabaseType type) {
		switch (type) {
		case PostGIS:
			return "2.0";
		}
		return null;
	}

	public DatabaseType getDatabaseType() {
		return config.get(KEY_DATABASE_TYPE, String.class).map(DatabaseType::valueOf)
				.orElse(DEFAULT_DATABASE_TYPE);
	}

	public String getDatabaseVersion() {
		return config.get(KEY_DATABASE_VERSION, String.class).orElse(null);
	}

	/**
	 * Set the database type and version.
	 * 
	 * 
	 * @param type the database type
	 * @param version the database version
	 */
	public void setDatabaseType(DatabaseType type, String version) {
		config.set(KEY_DATABASE_TYPE, type.name());
		config.set(KEY_DATABASE_VERSION, version);
	}

	@Override
	public Optional<Integer> getMaxNameLength() {
		return config.get(KEY_DATABASE_MAX_NAME_LENGTH, Integer.class);
	}

	/**
	 * Set the maximum name length for the database tables and columns.
	 * 
	 * @param maxLength the maximum name length, if any
	 */
	public void setMaxNameLength(Optional<Integer> maxLength) {
		config.set(KEY_DATABASE_MAX_NAME_LENGTH, maxLength.orElse(null));
	}

	@Override
	public String getJDBCConnectionId() {
		return config.get(KEY_JDBC_CONNECTION_ID, String.class).orElse(DEFAULT_JDBC_CONNECTION_ID);
	}

	/**
	 * Set the JDBC connection ID.
	 * 
	 * @param id the connection ID
	 */
	public void setJDBCConnectionId(String id) {
		config.set(KEY_JDBC_CONNECTION_ID, id);
	}

	@Override
	public GeometryStorageParams getGeometryStorageParameters() {
		ICRS crs = CRSManager.getCRSRef(getCRSIdentifier());
		int dim = getDimension().orElse(crs.getDimension());
		String srid = getSRID().orElse(getSQLDialect().getUndefinedSrid());

		CoordinateDimension dimension;
		switch (dim) {
		case 2:
			dimension = CoordinateDimension.DIM_2;
			break;
		case 3:
			dimension = CoordinateDimension.DIM_3;
			break;
		default:
			// TODO message?
			dimension = CoordinateDimension.DIM_2_OR_3;
		}

		return new GeometryStorageParams(crs, srid, dimension);
	}

	public String getCRSIdentifier() {
		return config.get(KEY_CRS_IDENTIFIER, String.class).orElse(DEFAULT_CRS_IDENTIFIER);
	}

	public void setCRSIdentifier(String id) {
		config.set(KEY_CRS_IDENTIFIER, id);
	}

	public Optional<Integer> getDimension() {
		return config.get(KEY_CRS_DIMENSION, Integer.class);
	}

	public void setDimension(Optional<Integer> dim) {
		config.set(KEY_CRS_DIMENSION, dim.orElse(null));
	}

	public Optional<String> getSRID() {
		return config.get(KEY_CRS_SRID, String.class);
	}

	public void setSRID(Optional<String> srid) {
		config.set(KEY_CRS_SRID, srid.orElse(null));
	}

	@Override
	public boolean useNamespacePrefixForTableNames() {
		return config.get(KEY_NAMESPACE_PREFIX, Boolean.class)
				.orElse(DEFAULT_NAMESPACE_PREFIX_FOR_TABLE_NAMES);
	}

	public void setUseNamespacePrefixForTableNames(boolean enabled) {
		config.set(KEY_NAMESPACE_PREFIX, enabled);
	}

	@Override
	public boolean useIntegerIDs() {
		return config.get(KEY_INTEGER_IDS, Boolean.class).orElse(DEFAULT_INTEGER_IDS);
	}

	public void setUseIntegerIDs(boolean enabled) {
		config.set(KEY_INTEGER_IDS, enabled);
	}

}
