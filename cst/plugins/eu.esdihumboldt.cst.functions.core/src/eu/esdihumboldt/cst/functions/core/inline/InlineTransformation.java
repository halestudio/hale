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

package eu.esdihumboldt.cst.functions.core.inline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.DefaultInstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.impl.ThreadSafeInstanceSink;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Transformation that runs a type transformation on a property.
 * 
 * @author Simon Templer
 */
public class InlineTransformation extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		List<PropertyValue> sources = variables.get(null);
		if (sources.isEmpty()) {
			throw new NoResultException("No source available to transform");
		}

		PropertyValue source = sources.get(0);
		Object sourceValue = source.getValue();
		if (sourceValue == null) {
			throw new NoResultException("Source value is null");
		}
		if (!(sourceValue instanceof Instance)) {
			throw new TransformationException("Sources for inline transformation must be instances");
		}
		Instance sourceInstance = (Instance) sourceValue;
		TypeDefinition sourceType = sourceInstance.getDefinition();

		// get the original alignment
		Alignment orgAlignment = getExecutionContext().getAlignment();
		MutableAlignment alignment = new DefaultAlignment(orgAlignment);

		// identify relevant type cell(s)
		MutableCell queryCell = new DefaultCell();
		ListMultimap<String, Type> sourceEntities = ArrayListMultimap.create();
		sourceEntities.put(null, new DefaultType(new TypeEntityDefinition(sourceType,
				SchemaSpaceID.SOURCE, null)));
		queryCell.setSource(sourceEntities);
		ListMultimap<String, Type> targetEntities = ArrayListMultimap.create();
		targetEntities.put(null, new DefaultType(new TypeEntityDefinition(resultProperty
				.getDefinition().getPropertyType(), SchemaSpaceID.TARGET, null)));
		queryCell.setTarget(targetEntities);
		Collection<? extends Cell> candidates = alignment.getTypeCells(queryCell);
		if (candidates.isEmpty()) {
			log.error(log.createMessage("No type transformations found for inline transformation",
					null));
			throw new NoResultException();
		}

		// filter alignment -> only keep relevant type relations
		List<Cell> allTypeCells = new ArrayList<>(alignment.getTypeCells());
		for (Cell cell : allTypeCells) {
			// remove cell
			alignment.removeCell(cell);

			if (!cell.getTransformationMode().equals(TransformationMode.disabled)) {
				// only readd if not disabled

				MutableCell copy = new DefaultCell(cell);
				if (candidates.contains(cell)) {
					// readd as active
					copy.setTransformationMode(TransformationMode.active);
				}
				else {
					// readd as passive
					copy.setTransformationMode(TransformationMode.passive);
				}
				alignment.addCell(copy);
			}
		}

		// prepare transformation input/output
		DefaultInstanceCollection sourceInstances = new DefaultInstanceCollection();
		sourceInstances.add(sourceInstance);
		DefaultInstanceSink target = new DefaultInstanceSink();

		// run transformation
		TransformationService ts = getExecutionContext().getService(TransformationService.class);
		if (ts == null) {
			throw new TransformationException(
					"Transformation service not available for inline transformation");
		}

		ProgressIndicator progressIndicator = new LogProgressIndicator();
		TransformationReport report = ts.transform(alignment, sourceInstances,
				new ThreadSafeInstanceSink<InstanceSink>(target), getExecutionContext(),
				progressIndicator);

		// copy report messages
		log.importMessages(report);

		if (!report.isSuccess()) {
			// copy report messages
			log.importMessages(report);
			throw new TransformationException("Inline transformation failed");
		}

		// extract result
		List<Instance> targetList = target.getInstances();
		if (targetList.isEmpty()) {
			log.error(log.createMessage("Inline transformation yielded no result", null));
			throw new NoResultException("No result from inline transformation");
		}

		if (targetList.size() > 1) {
			log.error(log.createMessage(
					"Inline transformation yielded multiple results, only first result is used",
					null));
		}

		return targetList.get(0);
	}
}
