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

package eu.esdihumboldt.hale.common.align.model;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * An alignment contains alignment cells
 * 
 * @author Simon Templer
 */
public interface Alignment {

	/**
	 * Get the base alignments.
	 * 
	 * @return the base alignments
	 */
	public Map<String, URI> getBaseAlignments();

	/**
	 * Get all cells belonging to the given base alignment.
	 * 
	 * @param baseAlignment the base alignment URI
	 * @return all cells belonging to the given base alignment
	 */
	public Iterable<BaseAlignmentCell> getBaseAlignmentCells(URI baseAlignment);

	/**
	 * Yield defined custom property functions.
	 * 
	 * @return function identifiers mapped to function descriptors
	 */
	public Map<String, CustomPropertyFunction> getAllCustomPropertyFunctions();

	/**
	 * Yield custom property functions defined directly in the alignment.
	 * 
	 * @return function identifiers mapped to function descriptors
	 */
	public Map<String, CustomPropertyFunction> getCustomPropertyFunctions();

	/**
	 * Yield defined custom property functions from base alignments.
	 * 
	 * @return function identifiers mapped to function descriptors
	 */
	public Map<String, CustomPropertyFunction> getBasePropertyFunctions();

	/**
	 * Add a custom property function that is saved as part of the alignment.
	 * 
	 * @param function the custom function
	 */
	public void addCustomPropertyFunction(CustomPropertyFunction function);

	/**
	 * Get the collection of cells contained in the alignment.
	 * 
	 * @return the alignment cells
	 */
	public Collection<? extends Cell> getCells();

	/**
	 * Get the cells representing a mapping between types
	 * 
	 * @return the type cells
	 */
	public Collection<? extends Cell> getTypeCells();

	/**
	 * Get the cells associated directly with the given entity definition.
	 * 
	 * @param entityDefinition the entity definition
	 * @return the associated cells or an empty collection
	 */
	public Collection<? extends Cell> getCells(EntityDefinition entityDefinition);

	/**
	 * Get the cells associated with the given entity definition.<br>
	 * 
	 * @param entityDefinition the entity definition
	 * @param includeInherited if set, it will also include cells that are
	 *            mapped to the given entity definition on super types
	 * @return the associated cells or an empty collection
	 */
	public Collection<? extends Cell> getCells(EntityDefinition entityDefinition,
			boolean includeInherited);

	/**
	 * Get the cells associated with the given type. These may be cells
	 * associated to the type or its properties.
	 * 
	 * @param type the type definition
	 * @param schemaSpace the type schema space
	 * @return the cells associated with the given type
	 */
	public Collection<? extends Cell> getCells(TypeDefinition type, SchemaSpaceID schemaSpace);

	/**
	 * Get the cells representing a mapping between properties that are
	 * associated with the given cell, not including disabled cells.<br>
	 * The type cell needs to have at least a source or a target set for this
	 * method to return anything.
	 * 
	 * @see #getPropertyCells(Cell, boolean, boolean)
	 * @param typeCell the cell in question, has to be a type cell
	 * @return the property cells associated with the given type cell.
	 */
	public Collection<? extends Cell> getPropertyCells(Cell typeCell);

	/**
	 * Get the cells representing a mapping between properties that are
	 * associated with the given cell.<br>
	 * The type cell needs to have at least a source or a target set for this
	 * method to return anything.
	 * 
	 * @param typeCell the cell in question, has to be a type cell
	 * @param includeDisabled also get cells that are disabled
	 * @return the property cells associated with the given type cell.
	 */
//	public Collection<? extends Cell> getPropertyCells(Cell typeCell, boolean includeDisabled);

	/**
	 * Get the cells representing a mapping between properties that are
	 * associated with the given cell.<br>
	 * The type cell needs to have at least a source or a target set for this
	 * method to return anything.
	 * 
	 * @param typeCell the cell in question, has to be a type cell
	 * @param includeDisabled also get cells that are disabled
	 * @param ignoreEmptySource if an empty source in the type cell should be
	 *            ignored and any source accepted instead
	 * @return the property cells associated with the given type cell.
	 */
	Collection<? extends Cell> getPropertyCells(Cell typeCell, boolean includeDisabled,
			boolean ignoreEmptySource);

	/**
	 * Get all type cells that match the given query cell's sources and targets.<br>
	 * If the query cell has neither sources nor a target, all type cells are
	 * returned. Otherwise matching means, that the type cell's sources and
	 * target have to be the same or super types of the query cell's.
	 * 
	 * @param queryCell the query cell
	 * @return matching type cells
	 */
	public Collection<? extends Cell> getTypeCells(Cell queryCell);

	/**
	 * Returns the cell referenced by the given id string or <code>null</code>
	 * if it cannot be found.
	 * 
	 * @param cellId the cell id
	 * @return the cell or <code>null</code> if it cannot be found
	 */
	public Cell getCell(String cellId);

	/**
	 * Get type cells that are configured as being active.
	 * 
	 * @see TransformationMode
	 * @return the active type cells or an empty collection
	 */
	public Collection<? extends Cell> getActiveTypeCells();

}
