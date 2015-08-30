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

package eu.esdihumboldt.hale.io.jdbc.spatialite;

import java.util.regex.Pattern;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.extension.JDBCSchemaReaderAdvisor;

/**
 * Adapts {@link JDBCSchemaReader} behavior for SpatialLite.
 * 
 * @author Simon Templer
 * @author Stefano Costa, GeoSolutions
 */
public class SpatialLiteSchemaReaderAdvisor implements JDBCSchemaReaderAdvisor {

	/*
	 * taken from SchemaCrawler v. 12.04.02
	 */
	private static final String VIEWS_SQL = "" //
			+ "	SELECT " //
			+ "		NULL AS TABLE_CATALOG, " //
			+ "		NULL AS TABLE_SCHEMA, " //
			+ "		name AS TABLE_NAME, " //
			+ "		sql AS VIEW_DEFINITION, " //
			+ "		'UNKNOWN' AS CHECK_OPTION, " //
			+ "		'N' AS IS_UPDATABLE " //
			+ "	FROM " //
			+ "		sqlite_master " //
			+ "	WHERE " //
			+ "		type = 'view' " //
			+ "	ORDER BY " //
			+ "		name";
	private static final String TRIGGERS_SQL = "" //
			+ "	SELECT " //
			+ "		NULL AS TRIGGER_CATALOG, " //
			+ "		NULL AS TRIGGER_SCHEMA, " //
			+ "		name AS TRIGGER_NAME, " //
			+ "		CASE " //
			+ "			WHEN sql LIKE '%INSERT ON%' THEN 'INSERT' " //
			+ "			WHEN sql LIKE '%UPDATE ON%'  THEN 'UPDATE' " //
			+ "			WHEN sql LIKE '%DELETE ON%'  THEN 'DELETE' " //
			+ "			ELSE 'UNKNOWN' " //
			+ "		END " //
			+ "			AS EVENT_MANIPULATION, " //
			+ "		NULL AS EVENT_OBJECT_CATALOG, " //
			+ "		NULL AS EVENT_OBJECT_SCHEMA, " //
			+ "		tbl_name AS EVENT_OBJECT_TABLE, " //
			+ "		0 AS ACTION_ORDER, " //
			+ "		'' AS ACTION_CONDITION, " //
			+ "		CASE " //
			+ "			WHEN sql LIKE '%ROW%' THEN 'ROW' " //
			+ "			WHEN sql LIKE '%STATEMENT%' THEN 'STATEMENT' " //
			+ "		ELSE 'UNKNOWN' " //
			+ "		END" //
			+ "			AS ACTION_ORIENTATION, " //
			+ "		CASE " //
			+ "			WHEN sql LIKE '%AFTER%' THEN 'AFTER' " //
			+ "			WHEN sql LIKE '%BEFORE%' THEN 'BEFORE' " //
			+ "		ELSE 'INSTEAD OF'" //
			+ "		END" //
			+ "			AS CONDITION_TIMING, " //
			+ "		sql AS ACTION_STATEMENT" //
			+ "	FROM " //
			+ "		sqlite_master " //
			+ "	WHERE " //
			+ "		type = 'trigger' " //
			+ "	ORDER BY " //
			+ "		name";

	@Override
	public void configureSchemaCrawler(SchemaCrawlerOptions options) {

		InformationSchemaViews infoSchemaViews = new InformationSchemaViews();
		infoSchemaViews.setViewsSql(VIEWS_SQL);
		infoSchemaViews.setTriggersSql(TRIGGERS_SQL);

		DatabaseSpecificOverrideOptions dbOvrOptions = new DatabaseSpecificOverrideOptions();
		dbOvrOptions.setIdentifierQuoteString("\"");
		dbOvrOptions.setSupportsSchemas(false); // SQLite has no notion of
												// schemas

		options.setDatabaseSpecificOverrideOptions(dbOvrOptions);
		options.setInformationSchemaViews(infoSchemaViews);

		// exclude system tables / views
		options.setTableInclusionRule(new InclusionRule() {

			private static final long serialVersionUID = -1559715487368953641L;

			@Override
			public boolean test(String t) {
				final String[] excludedTables = new String[] { "spatial_ref_sys",
						"geom_cols_ref_sys", "spatialite_history", "sqlite_sequence",
						"sqlite_stat1", "sql_statements_log", "SpatialIndex", "raster_pyramids",
						"views_layer_statistics" };
				final Pattern geometryColumnsTablePattern = Pattern.compile(".*geometry_columns.*");
				final Pattern indexTablePattern = Pattern.compile("idx.*");
				final Pattern vectorLayersViewPattern = Pattern.compile("vector_layers.*");

				boolean isGeometryColumnsTable = geometryColumnsTablePattern.matcher(t).matches();
				if (isGeometryColumnsTable) {
					return false;
				}

				boolean isIndexTable = indexTablePattern.matcher(t).matches();
				if (isIndexTable) {
					return false;
				}

				boolean isVectorLayersTable = vectorLayersViewPattern.matcher(t).matches();
				if (isVectorLayersTable) {
					return false;
				}

				for (String excludedTable : excludedTables) {
					if (excludedTable.equalsIgnoreCase(t)) {
						return false;
					}
				}

				return true;
			}
		});
	}

	@Override
	public String adaptPathForNamespace(String path) {
		if (path == null) {
			return null;
		}

		// extract file name from path
		int index = path.lastIndexOf("/");
		String name;
		if (index >= 0 && index + 1 < path.length()) {
			name = path.substring(index + 1).toLowerCase();
		}
		else {
			name = path.toLowerCase();
		}
		// remove extension
		if (name.endsWith(".sqlite")) {
			name = name.substring(0, name.length() - 7);
		}

		return name;
	}

}
