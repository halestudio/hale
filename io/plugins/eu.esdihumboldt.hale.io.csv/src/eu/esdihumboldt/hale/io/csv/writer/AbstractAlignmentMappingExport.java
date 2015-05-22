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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Wrapper for all information of the alignment (mapping) saved in a list of
 * maps <br>
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractAlignmentMappingExport extends AbstractAlignmentWriter implements
		MappingTableConstants {

	private Cell currentTypeCell;
	private boolean includeNamespaces = false;
	private boolean transformationAndDisabledMode = false;
	private List<CellType> cellTypes;

	/**
	 * the default line break in a cell
	 */
	public final String DEFAULT_LINE_END = "\n";

	private List<Map<CellType, CellInformation>> allRelations;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		cellTypes = new ArrayList<CellType>();
		includeNamespaces = getParameter(INCLUDE_NAMESPACES).as(Boolean.class);
		transformationAndDisabledMode = getParameter(TRANSFORMATION_AND_DISABLED_FOR).as(
				Boolean.class);
		// disable namespace and transformationAndDisable columns if necessary
		for (CellType type : CellType.values()) {
			if (includeNamespaces
					|| !type.getName().equals(CellType.SOURCE_PROPERTIES_NAMESPACE.getName()))
				if (transformationAndDisabledMode
						|| !type.getName().equals(CellType.TRANSFORMATION_AND_DISABLED.getName()))
					cellTypes.add(type);
		}

		return reporter;
	}

	/**
	 * @return all available cell types
	 */
	public List<CellType> getCellTypes() {
		return cellTypes;
	}

	/**
	 * Header for all columns in the Mapping file
	 * 
	 * @return list with strings of header for all columns
	 */
	public List<String> getMappingHeader() {
		List<String> list = new ArrayList<String>();
		for (CellType type : cellTypes) {
			list.add(type.getName());
		}
		return list;
	}

	/**
	 * Get all mappings. Each list entry represents one mapping.
	 * 
	 * @return list of all mappings
	 */
	public List<Map<CellType, CellInformation>> getMappingList() {

		allRelations = new ArrayList<Map<CellType, CellInformation>>();

		String mapping = getParameter(PARAMETER_MODE).as(String.class);
		boolean noBaseAlignments = mapping.equals(MODE_EXCLUDE_BASE);
		boolean propertyCells = mapping.equals(MODE_BY_TYPE_CELLS);

		if (propertyCells) {
			for (Cell typeCell : getAlignment().getTypeCells()) {
				addCellData(typeCell);
				for (Cell propertyCell : getAlignment().getPropertyCells(typeCell, true, false)) {
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
	public String getCellValue(Map<CellType, CellInformation> mapping, CellType cellType) {
		StringBuilder result = new StringBuilder();
		CellInformation cellInfo = mapping.get(cellType);
		List<String> text = cellInfo.getText();
		List<Integer> positions = cellInfo.getPositions();
		switch (cellType) {
		// same behavior of SOURCE_TYPE, SORUCE_PROPERTY_CONDITIONS and
		// TARGET_PROPERTIES
		case SOURCE_TYPE:
		case SOURCE_TYPE_NAMESPACE:
		case SOURCE_PROPERTY_CONDITIONS:
		case TARGET_TYPE:
		case TARGET_TYPE_NAMESPACE:
		case SOURCE_PROPERTIES_NAMESPACE:
		case TARGET_PROPERTIES_NAMESPACE:
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
		case ID:
		case CELL_EXPLANATION:
		case CELL_NOTES:
		case BASE_CELL:
		case PRIORITY:
		case TRANSFORMATION_AND_DISABLED:
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

		Map<CellType, CellInformation> cellInfos = new HashMap<CellType, CellInformation>();
		// create all entries
		List<CellType> cellTypes = getCellTypes();

		for (int i = 0; i < cellTypes.size(); i++) {
			cellInfos.put(cellTypes.get(i), new CellInformation());
		}

		cellInfos.get(CellType.ID).addText(cell.getId(), 0);

		if (cell.getSource() != null) {
			// save the hierarchy of the properties
			// all entries (in the same CellInfo) with the same hierarchy level
			// have to be shown on the same height
			int position = 0;
			for (Entity entity : cell.getSource().values()) {

				// column source type
				cellInfos.get(CellType.SOURCE_TYPE).addText(
						entity.getDefinition().getType().getName().getLocalPart(), position);

				if (includeNamespaces)
					// column source type namespace
					cellInfos.get(CellType.SOURCE_TYPE_NAMESPACE).addText(
							entity.getDefinition().getType().getName().getNamespaceURI(), position);

				// column source type conditions
				Filter entityFilter;
				if ((entityFilter = entity.getDefinition().getFilter()) != null) {
					cellInfos.get(CellType.SOURCE_TYPE_CONDITIONS).addText(
							FilterDefinitionManager.getInstance().asString(entityFilter), position);
					entity.getDefinition().getType().getName().getLocalPart();
				}

				for (ChildContext childContext : entity.getDefinition().getPropertyPath()) {
					PropertyDefinition child = childContext.getChild().asProperty();
					if (child != null) {

						// column source properties
						cellInfos.get(CellType.SOURCE_PROPERTIES).addText(
								child.getName().getLocalPart(), position);

						if (includeNamespaces)
							// column source properties namespace
							cellInfos.get(CellType.SOURCE_PROPERTIES_NAMESPACE).addText(
									child.getName().getNamespaceURI(), position);

						Filter contextFilter;
						if (childContext.getCondition() != null) {
							contextFilter = childContext.getCondition().getFilter();
							// column source property conditions
							cellInfos.get(CellType.SOURCE_PROPERTY_CONDITIONS).addText(
									FilterDefinitionManager.getInstance().asString(contextFilter),
									position);
						}

						// add dummy to adapt position of source type and source
						// type conditions
						cellInfos.get(CellType.SOURCE_TYPE).addText("", position);
						cellInfos.get(CellType.SOURCE_TYPE_CONDITIONS).addText("", position);

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
				cellInfos.get(CellType.TARGET_TYPE).addText(
						entity.getDefinition().getType().getDisplayName(), position);

				if (includeNamespaces)
					// column target type namespace
					cellInfos.get(CellType.TARGET_TYPE_NAMESPACE).addText(
							entity.getDefinition().getType().getName().getNamespaceURI(), position);

				for (ChildContext childContext : entity.getDefinition().getPropertyPath()) {
					PropertyDefinition child = childContext.getChild().asProperty();
					if (child != null) {
						// column target properties
						cellInfos.get(CellType.TARGET_PROPERTIES).addText(
								child.getName().getLocalPart(), position);

						if (includeNamespaces)
							// column target properties namespace
							cellInfos.get(CellType.TARGET_PROPERTIES_NAMESPACE).addText(
									child.getName().getNamespaceURI(), position);

						// add dummy to adapt position of target type
						cellInfos.get(CellType.TARGET_TYPE).addText("", position);

						position++;
					}
				}
				position++;
			}
		}

		FunctionDefinition<?> function = FunctionUtil.getFunction(
				cell.getTransformationIdentifier(), getServiceProvider());

		if (function != null) {
			// column relation name
			cellInfos.get(CellType.RELATION_NAME).addText(function.getDisplayName(), 0);

			// column cell explanation
			CellExplanation cellExpl = function.getExplanation();
			if (cellExpl != null) {
				cellInfos.get(CellType.CELL_EXPLANATION).addText(
						function.getExplanation().getExplanation(cell, null), 0);
			}
		}

		// column cell notes
		List<String> docs = cell.getDocumentation().get(null);
		if (!docs.isEmpty()) {
			String notes = docs.get(0);
			if (notes != null && !notes.isEmpty()) {
				cellInfos.get(CellType.CELL_NOTES).addText(notes, 0);
			}
		}

		// cell priority
		cellInfos.get(CellType.PRIORITY).addText(cell.getPriority().value(), 0);

		// base cell
		if (cell.isBaseCell()) {
			cellInfos.get(CellType.BASE_CELL).addText("yes", 0);
		}
		else {
			cellInfos.get(CellType.BASE_CELL).addText("no", 0);
		}

		// column transformation/disabled
		if (transformationAndDisabledMode) {
			if (AlignmentUtil.isTypeCell(cell)) {
				currentTypeCell = cell;
				cellInfos.get(CellType.TRANSFORMATION_AND_DISABLED).addText(
						cell.getTransformationMode().displayName(), 0);
			}
			else {
				Set<String> disabledCells = cell.getDisabledFor();
				if (disabledCells.contains(currentTypeCell.getId())) {
					for (String disCell : disabledCells) {
						cellInfos.get(CellType.TRANSFORMATION_AND_DISABLED).addText(disCell, 0);
					}
				}
			}
		}

		// add the row to the map
		allRelations.add(cellInfos);
	}

	/**
	 * @return if the writer mode is configured to display the cells per type
	 *         cell
	 */
	protected boolean isByTypeCell() {
		String mode = getParameter(PARAMETER_MODE).as(String.class);
		return mode.equals(MODE_BY_TYPE_CELLS);
	}
}
