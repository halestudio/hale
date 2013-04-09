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

package eu.esdihumboldt.hale.io.csv.writer.internal;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.io.impl.AbstractAlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.filter.AbstractGeotoolsFilter;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * Provider to write the alignment to a csv file
 * 
 * @author Patrick Lieb
 */
public class CSVAlignmentWriter extends AbstractAlignmentWriter {

	// create unique keys for cells who do not have a source
	private int counter;

	private Map<String, List<String>> allRelations;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		allRelations = new HashMap<String, List<String>>();

		CSVWriter writer = new CSVWriter(new OutputStreamWriter(getTarget().getOutput()));

		List<String> header = Arrays.asList("Source type", "Source type conditions",
				"Source properties", "Source property conditions", "Target type",
				"Target properties", "Relation name", "Cell explanation", "Cell notes");
		allRelations.put("header", header);

		counter = 0;

		for (Cell cell : getAlignment().getCells()) {
			copyCellData(cell);
		}
		Object[] allRel = allRelations.values().toArray();
		for (int i = allRel.length - 1; i >= 0; i--) {
			// XXX better way? -> cast necessary?
			writer.writeNext(((List<String>) allRel[i]).toArray(new String[] {}));
		}
		writer.close();

		reporter.setSuccess(true);

		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "CSV HALE Alignment";
	}

	// get the information of the cell and copy them into the map
	private void copyCellData(Cell cell) {
		String key = "notSet";

		// write a row in the csv file
		List<String> cellInfo = new ArrayList<String>();

		// reset all entries
		String sourceType = "";
		String sourceTypeConditions = "";
		String sourceProperties = "";
		String sourcePropertyConditions = "";
		String targetType = "";
		String targetProperties = "";
		String relationName = "";
		String cellExplanation = "";
		String cellNotes = "";

		List<String> foundCell = null;

		if (cell.getSource() != null) {
			for (Entity entity : cell.getSource().values()) {
				// column source type
				sourceType = entity.getDefinition().getDefinition().getDisplayName();
				key = sourceType;
				if (allRelations.containsKey(key)) {
					foundCell = allRelations.get(key);
				}

				// column source type conditions
				Filter entityFilter;
				if ((entityFilter = entity.getDefinition().getFilter()) != null)
					if (entityFilter instanceof AbstractGeotoolsFilter)
						sourceTypeConditions = ((AbstractGeotoolsFilter) entityFilter)
								.getFilterTerm();

				for (ChildContext cdc : entity.getDefinition().getPropertyPath()) {
					// column source properties
					PropertyDefinition child = cdc.getChild().asProperty();
					if (child != null)
						sourceProperties = child.getDisplayName();

					// column source property conditions
					Filter contextFilter;
					if (cdc.getCondition() != null) {
						contextFilter = cdc.getCondition().getFilter();
						if (contextFilter instanceof AbstractGeotoolsFilter)
							// XXX more info! index, name, ...
							sourcePropertyConditions = ((AbstractGeotoolsFilter) contextFilter)
									.getFilterTerm();
					}
				}
			}
		}
		else {
			// assign key
			key += counter++;
		}
		if (cell.getTarget() != null)
			for (Entity entity : cell.getTarget().values()) {
				// column target type
				targetType = entity.getDefinition().getDefinition().getDisplayName();

				// target properties
				Filter entityFilter;
				if ((entityFilter = entity.getDefinition().getFilter()) != null)
					if (entityFilter instanceof AbstractGeotoolsFilter)
						targetProperties = ((AbstractGeotoolsFilter) entityFilter).getFilterTerm();
			}

		AbstractFunction<?> function = FunctionUtil.getFunction(cell.getTransformationIdentifier());
		// column Relation name
		relationName = function.getDisplayName();

		// column cell explanation
		CellExplanation cellExpl = function.getExplanation();
		if (cellExpl != null)
			cellExplanation = function.getExplanation().getExplanation(cell,
					HaleUI.getServiceProvider());

		// column cell notes
		List<String> docs = cell.getDocumentation().get(null);
		if (!docs.isEmpty()) {
			String notes = docs.get(0);
			if (notes != null && !notes.isEmpty())
				cellNotes = notes;
		}

		// XXX improve code style
		if (foundCell != null) {
			foundCell.set(1,
					foundCell.get(1).concat(CSVWriter.DEFAULT_LINE_END)
							.concat(sourceTypeConditions));
			foundCell.set(2,
					foundCell.get(2).concat(CSVWriter.DEFAULT_LINE_END).concat(sourceProperties));
			foundCell.set(
					3,
					foundCell.get(3).concat(CSVWriter.DEFAULT_LINE_END)
							.concat(sourcePropertyConditions));
			foundCell
					.set(4, foundCell.get(4).concat(CSVWriter.DEFAULT_LINE_END).concat(targetType));
			foundCell.set(5,
					foundCell.get(5).concat(CSVWriter.DEFAULT_LINE_END).concat(targetProperties));
			foundCell.set(6,
					foundCell.get(6).concat(CSVWriter.DEFAULT_LINE_END).concat(relationName));
			foundCell.set(7,
					foundCell.get(7).concat(CSVWriter.DEFAULT_LINE_END).concat(cellExplanation));
			foundCell.set(8, foundCell.get(8).concat(CSVWriter.DEFAULT_LINE_END).concat(cellNotes));
		}
		else {
			// add complete content to the row
			cellInfo.add(sourceType);
			cellInfo.add(sourceTypeConditions);
			cellInfo.add(sourceProperties);
			cellInfo.add(sourcePropertyConditions);
			cellInfo.add(targetType);
			cellInfo.add(targetProperties);
			cellInfo.add(relationName);
			cellInfo.add(cellExplanation);
			cellInfo.add(cellNotes);

			// write the row
			allRelations.put(key, cellInfo);
		}
	}
}
