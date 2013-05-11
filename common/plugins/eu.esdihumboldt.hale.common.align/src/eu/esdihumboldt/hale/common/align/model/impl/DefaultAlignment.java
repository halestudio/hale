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

package eu.esdihumboldt.hale.common.align.model.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default alignment implementation.
 * 
 * @author Simon Templer
 */
public class DefaultAlignment implements Alignment, MutableAlignment {

	/**
	 * List with all cells contained in the alignment. XXX use a LinkedHashSet
	 * instead?
	 */
	private final Collection<Cell> cells = new ArrayList<Cell>();

	/**
	 * List with all type cells contained in the alignment.
	 */
	private final Collection<Cell> typeCells = new ArrayList<Cell>();

	/**
	 * Entity definitions mapped to alignment cells.
	 */
	private final ListMultimap<EntityDefinition, Cell> cellsPerEntity = ArrayListMultimap.create();

	/**
	 * Source types mapped to alignment cells.
	 */
	private final ListMultimap<TypeDefinition, Cell> cellsPerSourceType = ArrayListMultimap
			.create();

	/**
	 * Target types mapped to alignment cells.
	 */
	private final ListMultimap<TypeDefinition, Cell> cellsPerTargetType = ArrayListMultimap
			.create();

	private final Map<String, URI> baseAlignments = new HashMap<String, URI>();

	private final Map<String, Cell> idToCell = new HashMap<String, Cell>();

	/**
	 * Default constructor.
	 */
	public DefaultAlignment() {
		// do nothing
	}

	/**
	 * Copy constructor. Adds all cells of the given alignment.
	 * 
	 * @param alignment the alignment to copy
	 */
	public DefaultAlignment(Alignment alignment) {
		// Since the cells came out of another alignment just pass addCell
		for (Cell cell : alignment.getCells())
			internalAdd(cell);
		baseAlignments.putAll(alignment.getBaseAlignments());
	}

	/**
	 * @see MutableAlignment#addCell(MutableCell)
	 */
	@Override
	public void addCell(MutableCell cell) {
		if (cell.getId() == null) {
			// the cell has to be a newly created cell
			String id;
			do {
				id = "C" + UUID.randomUUID().toString();
			} while (idToCell.containsKey(id));
			cell.setId(id);
		}
		internalAdd(cell);
	}

	/**
	 * Add a cell to the various internal containers.
	 * 
	 * @param cell the cell to add
	 */
	private void internalAdd(Cell cell) {
		cells.add(cell);
		idToCell.put(cell.getId(), cell);

		// check if cell is a type cell
		if (AlignmentUtil.isTypeCell(cell)) {
			typeCells.add(cell);
		}

		// add to maps
		internalAddToMaps(cell.getSource(), cell);
		internalAddToMaps(cell.getTarget(), cell);
	}

	/**
	 * Add a cell to the internal indexes, based on the given associated
	 * entities.
	 * 
	 * @param entities the cell entities (usually either source or target)
	 * @param cell the cell to add
	 */
	private void internalAddToMaps(ListMultimap<String, ? extends Entity> entities, Cell cell) {
		if (entities == null) {
			return;
		}

		for (Entity entity : entities.values()) {
			EntityDefinition entityDef = entity.getDefinition();
			cellsPerEntity.put(entityDef, cell);

			switch (entityDef.getSchemaSpace()) {
			case TARGET:
				cellsPerTargetType.put(entityDef.getType(), cell);
				break;
			case SOURCE:
				cellsPerSourceType.put(entityDef.getType(), cell);
				break;
			default:
				throw new IllegalStateException(
						"Entity definition with illegal schema space encountered");
			}
		}
	}

	/**
	 * @see Alignment#getCells(EntityDefinition)
	 */
	@Override
	public Collection<? extends Cell> getCells(EntityDefinition entityDefinition) {
		return getCells(entityDefinition, false);
	}

	/**
	 * @see Alignment#getCells(EntityDefinition, boolean)
	 */
	@Override
	public Collection<? extends Cell> getCells(EntityDefinition entityDefinition,
			boolean includeInherited) {
		if (!includeInherited)
			return Collections.unmodifiableCollection(cellsPerEntity.get(entityDefinition));
		else {
			// Set for safety to return each cell only once.
			// Duplicates shouldn't happen in usual cases, though.
			Collection<Cell> cells = new HashSet<Cell>();
			EntityDefinition e = entityDefinition;
			do {
				cells.addAll(cellsPerEntity.get(e));
				if (e.getFilter() != null) {
					cells.addAll(cellsPerEntity.get(AlignmentUtil.createEntity(e.getType(),
							e.getPropertyPath(), e.getSchemaSpace(), null)));
				}
				TypeDefinition superType = e.getType().getSuperType();
				e = superType == null ? null : AlignmentUtil.createEntity(superType,
						e.getPropertyPath(), e.getSchemaSpace(), e.getFilter());
			} while (e != null);
			return cells;
		}
	}

	/**
	 * @see Alignment#getCells(TypeDefinition, SchemaSpaceID)
	 */
	@Override
	public Collection<? extends Cell> getCells(TypeDefinition type, SchemaSpaceID schemaSpace) {
		switch (schemaSpace) {
		case SOURCE:
			return Collections.unmodifiableCollection(cellsPerSourceType.get(type));
		case TARGET:
			return Collections.unmodifiableCollection(cellsPerTargetType.get(type));
		default:
			throw new IllegalArgumentException("Illegal schema space provided");
		}
	}

	/**
	 * @see Alignment#getPropertyCells(Cell)
	 */
	@Override
	public Collection<? extends Cell> getPropertyCells(Cell typeCell) {
		return getPropertyCells(typeCell, false);
	}

	/**
	 * @see Alignment#getPropertyCells(Cell, boolean)
	 */
	@Override
	public Collection<? extends Cell> getPropertyCells(Cell typeCell, boolean includeDisabled) {
		if (CellUtil.getFirstEntity(typeCell.getTarget()) == null) {
			if (CellUtil.getFirstEntity(typeCell.getSource()) == null)
				return Collections.emptySet();
			else if (!(typeCell.getSource().values().iterator().next() instanceof Type))
				throw new IllegalArgumentException("Given cell is not a type cell.");

			// query with sources only
			Collection<TypeEntityDefinition> sourceTypes = new ArrayList<TypeEntityDefinition>();
			Iterator<? extends Entity> it = typeCell.getSource().values().iterator();
			while (it.hasNext())
				sourceTypes.add((TypeEntityDefinition) it.next().getDefinition());

			List<Cell> result = new ArrayList<Cell>();
			for (Cell cell : cells)
				if (!AlignmentUtil.isTypeCell(cell)
						&& matchesSources(cell.getSource(), sourceTypes))
					result.add(cell);
			return result;
		}

		if (!AlignmentUtil.isTypeCell(typeCell))
			throw new IllegalArgumentException("Given cell is not a type cell.");

		List<Cell> result = new ArrayList<Cell>();

		TypeDefinition typeCellType = typeCell.getTarget().values().iterator().next()
				.getDefinition().getType();

		// collect source entity definitions
		Collection<TypeEntityDefinition> sourceTypes = new ArrayList<TypeEntityDefinition>();
		// null check in case only target type is in question
		if (typeCell.getSource() != null) {
			Iterator<? extends Entity> it = typeCell.getSource().values().iterator();
			while (it.hasNext())
				sourceTypes.add((TypeEntityDefinition) it.next().getDefinition());
		}

		while (typeCellType != null) {
			// select all cells of the target type
			for (Cell cell : cellsPerTargetType.get(typeCellType)) {
				// check all cells associated to the target type
				if (!AlignmentUtil.isTypeCell(cell)
						&& (includeDisabled || !cell.getDisabledFor().contains(typeCell))) {
					// cell is a property cell that isn't disabled
					// the target type matches, too
					if (AlignmentUtil.isAugmentation(cell) || sourceTypes.isEmpty()
							|| matchesSources(cell.getSource(), sourceTypes)) {
						// cell matches on the source side, too
						result.add(cell);
					}
				}
			}

			// continue with super type for inheritance
			typeCellType = typeCellType.getSuperType();
		}

		return result;
	}

	/**
	 * Determines if the given type entity definition of the test cell is
	 * associated to at least one of the given type entity definitions of a type
	 * cell.
	 * 
	 * @param testCellType type entity definition of the test cell
	 * @param typeCellTypes type entity definitions of a type cell
	 * @return whether the entity definition is associated to at least one of
	 *         the others
	 */
	private boolean matchesSources(TypeEntityDefinition testCellType,
			Iterable<TypeEntityDefinition> typeCellTypes) {
		TypeDefinition def = testCellType.getDefinition();
		Filter filter = testCellType.getFilter();
		for (TypeEntityDefinition typeCellType : typeCellTypes) {
			if (DefinitionUtil.isSuperType(typeCellType.getDefinition(), def)
					&& (filter == null || filter.equals(typeCellType.getFilter())))
				return true;
		}
		return false;
	}

	/**
	 * Determines if all of the given entities are associated to at least one of
	 * the given type entity definitions.
	 * 
	 * @param testCellSources the entities
	 * @param typeCellTypes the type entity definitions
	 * @return whether all entities are associated to at least one of the types
	 */
	private boolean matchesSources(ListMultimap<String, ? extends Entity> testCellSources,
			Iterable<TypeEntityDefinition> typeCellTypes) {
		if (testCellSources == null)
			return true;
		for (Entity entity : testCellSources.values()) {
			// if one of the property cell's sources is not part of the type
			// cell it should not be included
			// XXX this is only true for the transformation?
			if (!matchesSources(AlignmentUtil.getTypeEntity(entity.getDefinition()), typeCellTypes))
				return false;
		}

		return true;
	}

	/**
	 * @see Alignment#getTypeCells(Cell)
	 */
	@Override
	public Collection<? extends Cell> getTypeCells(Cell queryCell) {
		Set<TypeEntityDefinition> sources = new HashSet<TypeEntityDefinition>();
		if (queryCell.getSource() != null) {
			Iterator<? extends Entity> it = queryCell.getSource().values().iterator();
			while (it.hasNext())
				sources.add(AlignmentUtil.getTypeEntity(it.next().getDefinition()));
		}

		Entity targetEntity = CellUtil.getFirstEntity(queryCell.getTarget());
		TypeDefinition target = targetEntity == null ? null : targetEntity.getDefinition()
				.getType();

		if (sources.isEmpty() && target == null)
			return getTypeCells();

		List<Cell> result = new ArrayList<Cell>();

		for (Cell typeCell : typeCells) {
			TypeDefinition typeCellTarget = CellUtil.getFirstEntity(typeCell.getTarget())
					.getDefinition().getType();
			if (target == null || DefinitionUtil.isSuperType(target, typeCellTarget)) {
				// target matches
				if (sources.isEmpty() || matchesSources(typeCell.getSource(), sources)) {
					// source matches, too
					result.add(typeCell);
				}
			}
		}

		return result;
	}

	/**
	 * @see Alignment#getCells()
	 */
	@Override
	public Collection<? extends Cell> getCells() {
		return Collections.unmodifiableCollection(cells);
	}

	/**
	 * @see MutableAlignment#removeCell(Cell)
	 */
	@Override
	public boolean removeCell(Cell cell) {
		boolean removed = cells.remove(cell);
		if (removed) {
			typeCells.remove(cell);

			// remove from maps
			internalRemoveFromMaps(cell.getSource(), cell);
			internalRemoveFromMaps(cell.getTarget(), cell);
		}

		return removed;
	}

	/**
	 * Removes a cell from the internal indexes, based on the given associated
	 * entities.
	 * 
	 * @param entities the cell entities (usually either source or target)
	 * @param cell the cell to remove
	 */
	private void internalRemoveFromMaps(ListMultimap<String, ? extends Entity> entities, Cell cell) {
		if (entities == null) {
			return;
		}

		for (Entity entity : entities.values()) {
			EntityDefinition entityDef = entity.getDefinition();
			cellsPerEntity.remove(entityDef, cell);

			switch (entityDef.getSchemaSpace()) {
			case TARGET:
				cellsPerTargetType.remove(entityDef.getType(), cell);
				break;
			case SOURCE:
				cellsPerSourceType.remove(entityDef.getType(), cell);
				break;
			default:
				throw new IllegalStateException(
						"Entity definition with illegal schema space encountered");
			}
		}
	}

	/**
	 * @see Alignment#getTypeCells()
	 */
	@Override
	public Collection<Cell> getTypeCells() {
		return Collections.unmodifiableCollection(typeCells);
	}

	@Override
	public Collection<? extends Cell> getActiveTypeCells() {
		List<Cell> result = new ArrayList<Cell>();
		for (Cell cell : getTypeCells()) {
			if (cell.getTransformatioMode() == TransformationMode.active) {
				result.add(cell);
			}
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.MutableAlignment#addBaseAlignment(java.lang.String,java.net.URI,java.lang.Iterable)
	 */
	@Override
	public void addBaseAlignment(String prefix, URI alignment, Iterable<BaseAlignmentCell> cells) {
		if (baseAlignments.containsValue(alignment))
			throw new IllegalArgumentException("base alignment " + alignment + " already included");
		if (baseAlignments.containsKey(prefix))
			throw new IllegalArgumentException("prefix " + prefix + " already in use.");
		baseAlignments.put(prefix, alignment);
		for (BaseAlignmentCell cell : cells)
			internalAdd(cell);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Alignment#getBaseAlignments()
	 */
	@Override
	public Map<String, URI> getBaseAlignments() {
		return Collections.unmodifiableMap(baseAlignments);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Alignment#getCell(java.lang.String)
	 */
	@Override
	public Cell getCell(String cellId) {
		return idToCell.get(cellId);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.Alignment#getBaseAlignmentCells(java.net.URI)
	 */
	@Override
	public Iterable<BaseAlignmentCell> getBaseAlignmentCells(URI baseAlignment) {
		// expect this operation to not be needed regularly and thus do not
		// optimize it
		Collection<BaseAlignmentCell> baseCells = new ArrayList<BaseAlignmentCell>();
		for (Cell cell : cells) {
			if (cell instanceof BaseAlignmentCell) {
				BaseAlignmentCell bac = (BaseAlignmentCell) cell;
				if (bac.getBaseAlignment().equals(baseAlignment))
					baseCells.add(bac);
			}
		}
		return baseCells;
	}

}
