/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.html.svg.mapping.json;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder;

/**
 * Implementations can extend the Json generated for a alignment cell.
 * 
 * @author Simon Templer
 */
public interface CellJsonExtension {

	/**
	 * Augment the Json generated for a cell.
	 * 
	 * @param cell the cell the Json representation is generated for
	 * @param json the Json builder
	 */
	void augmentCellJson(Cell cell, JsonStreamBuilder json);

	/**
	 * Augment the Json generated for an entity.
	 * 
	 * @param entity the entity the Json representation is generated for
	 * @param name the name of the entity in context of the alignment cell
	 * @param json the Json builder
	 */
	void augmentEntityJson(Entity entity, String name, JsonStreamBuilder json);

}
