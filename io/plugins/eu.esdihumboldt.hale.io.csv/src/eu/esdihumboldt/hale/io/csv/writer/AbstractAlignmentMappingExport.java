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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Wrapper for all information of the alignment (mapping) saved in a list of
 * maps
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractAlignmentMappingExport extends AbstractAlignmentWriter implements
		MappingTableConstants {

	/**
	 * the default line break in a cell
	 */
	public final String DEFAULT_LINE_END = "\n";

	/**
	 * The cell types of the map
	 * 
	 * @author Patrick Lieb
	 */
	public static enum CellType {
		/** source type key */
		SOURCE_TYPE,

		/** source type conditions key */
		SOURCE_TYPE_CONDITIONS,

		/** source properties key */
		SOURCE_PROPERTIES,

		/** source property conditions key */
		SOURCE_PROPERTY_CONDITIONS,

		/** target type key */
		TARGET_TYPE,

		/** target properties key */
		TARGET_PROPERTIES,

		/** relation name key */
		RELATION_NAME,

		/** cell explanation key */
		CELL_EXPLANATION,

		/** cell notes key */
		CELL_NOTES
	}

	/**
	 * Header for all columns in the Mapping file
	 */
	public final List<String> MAPPING_HEADER = Arrays.asList("Source type",
			"Source type conditions", "Source properties", "Source property conditions",
			"Target type", "Target properties", "Relation name", "Cell explanation", "Cell notes");

	private List<Map<CellType, CellInfo>> allRelations;

	/**
	 * Get all mappings. Each list entry represents one mapping.
	 * 
	 * @return list of all mappings
	 */
	public List<Map<CellType, CellInfo>> getMappingList() {

		allRelations = new ArrayList<Map<CellType, CellInfo>>();

		String mapping = getParameter(PARAMETER_MODE).as(String.class);
		boolean noBaseAlignments = mapping.equals(MODE_EXCLUDE_BASE);
		boolean propertyCells = mapping.equals(MODE_BY_TYPE_CELLS);

		if (propertyCells) {
			for (Cell typeCell : getAlignment().getTypeCells()) {
				addCellData(typeCell);
				for (Cell propertyCell : getAlignment().getPropertyCells(typeCell)) {
					addCellData(propertyCell);
				}
			}
		}
		else {
			for (Cell cell : getAlignment().getCells()) {
				if (!noBaseAlignments || !cell.isBaseCell()) {
					addCellData(cell);
				}
			}
		}

		return allRelations;
	}

	/**
	 * @param mapping the relevant mapping of {@link #getMappingList()}
	 * @param cellType the cell type
	 * @return the string representation of the given mapping and cell type
	 */
	public String getCellValue(Map<CellType, CellInfo> mapping, CellType cellType) {
		StringBuilder result = new StringBuilder();
		CellInfo cellInfo = mapping.get(cellType);
		List<String> text = cellInfo.getText();
		List<Integer> positions = cellInfo.getPositions();
		switch (cellType) {
		// same behavior of SOURCE_TYPE, SORUCE_PROPERTY_CONDITIONS and
		// TARGET_PROPERTIES
		case SOURCE_TYPE:
		case SOURCE_PROPERTY_CONDITIONS:
		case TARGET_TYPE:
			if (text.size() == 1) {
				result.append(text.get(0));
				for (int position = 0; position < positions.get(0); position++) {
					result.append(DEFAULT_LINE_END);
				}
			}
			else {
				int position = 0;
				for (int i = 0; i < text.size(); i++) {
					String info = text.get(i);
					int currentPosition = positions.get(i);
					for (; position < currentPosition; position++) {
						result.append(DEFAULT_LINE_END);
					}
					result.append(info);
				}
			}
			break;

		// same behavior of SOURCE_TYPE_CONDITIONS, SOURCE_PROPERTIES and
		// TARGET_PROPERTIES
		case SOURCE_TYPE_CONDITIONS:
		case SOURCE_PROPERTIES:
		case TARGET_PROPERTIES:
			int lastPosition = -1;
			for (int i = 0; i < cellInfo.getText().size(); i++) {
				String info = text.get(i);
				int position = positions.get(i);
				if (position == 0) {
					result.append(info);
				}
				else {
					if (position == lastPosition + 1 && !info.isEmpty()) {
						// sub properties are signed with a point
						info = "." + info;
					}
					else if (position == lastPosition + 2) {
						info = DEFAULT_LINE_END + info;
					}
					result.append(DEFAULT_LINE_END);
					result.append(info);
				}
				lastPosition = position;
			}
			break;

		// same behavior of RELATION_NAME, CELL_EXPLANATION and CELL_NOTES
		case RELATION_NAME:
		case CELL_EXPLANATION:
		case CELL_NOTES:
			// append all info to one string divided by line breaks
			for (int i = 0; i < text.size(); i++) {
				if (positions.get(i) == 0) {
					result.append(text.get(i));
				}
				else {
					result.append(DEFAULT_LINE_END);
					result.append(text.get(i));
				}
			}
			break;
		}
		return result.toString();
	}

	// get the information of the cell and add them to the map
	private void addCellData(Cell cell) {
		Map<CellType, CellInfo> entry = new HashMap<CellType, CellInfo>();

		// reset all entries
		CellInfo sourceType = new CellInfo();
		CellInfo sourceTypeConditions = new CellInfo();
		CellInfo sourceProperties = new CellInfo();
		CellInfo sourcePropertyConditions = new CellInfo();
		CellInfo targetType = new CellInfo();
		CellInfo targetProperties = new CellInfo();
		CellInfo relationName = new CellInfo();
		CellInfo cellExplanation = new CellInfo();
		CellInfo cellNotes = new CellInfo();

		if (cell.getSource() != null) {
			// save the hierarchy of the properties
			// all entries (in the same CellInfo) with the same hierarchy level
			// have to be shown together
			int position = 0;
			for (Entity entity : cell.getSource().values()) {

				// column source type
				sourceType.addText(entity.getDefinition().getType().getDisplayName(), position);

				// column source type conditions
				Filter entityFilter;
				if ((entityFilter = entity.getDefinition().getFilter()) != null) {
					sourceTypeConditions.addText(
							FilterDefinitionManager.getInstance().asString(entityFilter), position);
				}

				for (ChildContext childContext : entity.getDefinition().getPropertyPath()) {
					PropertyDefinition child = childContext.getChild().asProperty();
					if (child != null) {

						// column source properties
						sourceProperties.addText(child.getDisplayName(), position);

						Filter contextFilter;
						if (childContext.getCondition() != null) {
							contextFilter = childContext.getCondition().getFilter();
							// column source property conditions
							// XXX more info! index, name, ...
							sourcePropertyConditions.addText(FilterDefinitionManager.getInstance()
									.asString(contextFilter), position);
						}

						// add dummy to adapt position of source type and source
						// type conditions
						sourceType.addText("", position);
						sourceTypeConditions.addText("", position);

						position++;
					}
				}

				// next entries must have higher position
				position++;
			}
		}
		if (cell.getTarget() != null) {
			int position = 0;
			for (Entity entity : cell.getTarget().values()) {
				// column target type
				targetType.addText(entity.getDefinition().getType().getDisplayName(), position);

				for (ChildContext childContext : entity.getDefinition().getPropertyPath()) {
					PropertyDefinition child = childContext.getChild().asProperty();
					if (child != null) {
						// column target properties
						targetProperties.addText(child.getDisplayName(), position);

						// add dummy to adapt position of target type
						targetType.addText("", position);

						position++;
					}
				}
				position++;
			}
		}

		AbstractFunction<?> function = FunctionUtil.getFunction(cell.getTransformationIdentifier());
		// column relation name
		relationName.addText(function.getDisplayName(), 0);

		// column cell explanation
		CellExplanation cellExpl = function.getExplanation();
		if (cellExpl != null) {
			cellExplanation.addText(function.getExplanation().getExplanation(cell, null), 0);
		}

		// column cell notes
		List<String> docs = cell.getDocumentation().get(null);
		if (!docs.isEmpty()) {
			String notes = docs.get(0);
			if (notes != null && !notes.isEmpty()) {
				cellNotes.addText(notes, 0);
			}
		}
		// the entry represents one mapping with all given information
		entry.put(CellType.SOURCE_TYPE, sourceType);
		entry.put(CellType.SOURCE_TYPE_CONDITIONS, sourceTypeConditions);
		entry.put(CellType.SOURCE_PROPERTIES, sourceProperties);
		entry.put(CellType.SOURCE_PROPERTY_CONDITIONS, sourcePropertyConditions);
		entry.put(CellType.TARGET_TYPE, targetType);
		entry.put(CellType.TARGET_PROPERTIES, targetProperties);
		entry.put(CellType.RELATION_NAME, relationName);
		entry.put(CellType.CELL_EXPLANATION, cellExplanation);
		entry.put(CellType.CELL_NOTES, cellNotes);

		// add the row to the map
		allRelations.add(entry);
	}
}
