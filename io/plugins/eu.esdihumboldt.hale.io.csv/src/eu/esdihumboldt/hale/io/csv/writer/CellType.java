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
 * The cell types of the map
 * 
 * @author Patrick Lieb
 */
public enum CellType {

	/** Cell identifier */
	ID("ID"),

	/** source type namespace */
	SOURCE_TYPE_NAMESPACE("Namespace"),

	/** source type */
	SOURCE_TYPE("Source Type"),

	/** source type conditions */
	SOURCE_TYPE_CONDITIONS("Source type conditions"),

	/** source properties namespace */
	SOURCE_PROPERTIES_NAMESPACE("Namespace"),

	/** source properties */
	SOURCE_PROPERTIES("Source properties"),

	/** source property conditions */
	SOURCE_PROPERTY_CONDITIONS("Source property conditions"),

	/** target type namespace */
	TARGET_TYPE_NAMESPACE("Namespace"),

	/** target type */
	TARGET_TYPE("Target type"),

	/** target properties namespace */
	TARGET_PROPERTIES_NAMESPACE("Namespace"),

	/** target properties */
	TARGET_PROPERTIES("Target properties"),

	/** relation name */
	RELATION_NAME("Relation name"),

	/** cell priority */
	PRIORITY("Priority"),

	/** cell explanation */
	CELL_EXPLANATION("Cell explanation"),

	/** cell notes */
	CELL_NOTES("Cell notes"),

	/** if cell is from base alignment */
	BASE_CELL("Base alignment"),

	/** transformation mode and disabled for of the cell */
	TRANSFORMATION_AND_DISABLED("Transformation/Disabled for");

	private final String name;

	CellType(String name) {
		this.name = name;
	}

	/**
	 * @return string representation of enum
	 */
	public String getName() {
		return name;
	}

}
