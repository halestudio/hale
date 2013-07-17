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

package eu.esdihumboldt.hale.io.csv.writer;

/**
 * Common constants for mapping table export.
 * 
 * @author Simon Templer
 */
public interface MappingTableConstants {

	/** Name of the parameter specifying the export mode. */
	public final String PARAMETER_MODE = "mode";

	/** Mode for exporting all cells. */
	public final String MODE_ALL = "all";

	/** Mode for exporting all cells except base alignment cells. */
	public final String MODE_EXCLUDE_BASE = "excludeBase";

	/** Mode for exporting cells organized by type cells. */
	public final String MODE_BY_TYPE_CELLS = "byTypeCells";

	/** Mode for including namespaces */
	public final String INCLUDE_NAMESPACES = "includeNamespaces";

	/** Mode for including the transformation and disabled for column */
	public final String TRANSFORMATION_AND_DISABLED_FOR = "transformationAndDisabled";

	/** Maximum width of a column */
	public final String MAX_COLUMN_WIDTH = "maxColumnWidth";

}
