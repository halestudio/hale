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

package eu.esdihumboldt.cst;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.cst.internal.EngineManager;
import eu.esdihumboldt.cst.internal.TransformationContext;
import eu.esdihumboldt.cst.internal.TreePropertyTransformer;
import eu.esdihumboldt.cst.internal.util.CountingInstanceSink;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.service.TransformationFunctionService;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.CellLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.DefaultTransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.SubtaskProgressIndicator;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import net.jcip.annotations.Immutable;

/**
 * Transformation service implementation
 * 
 * @author Simon Templer
 * @since 2.5
 */
@Immutable
// stateless
public class ConceptualSchemaTransformer implements TransformationService {

	/**
	 * @see TransformationService#transform(Alignment, InstanceCollection,
	 *      InstanceSink, ServiceProvider, ProgressIndicator)
	 */
	@Override
	public TransformationReport transform(Alignment alignment, InstanceCollection source,
			InstanceSink target, ServiceProvider serviceProvider,
			ProgressIndicator progressIndicator) {
		TransformationReporter reporter = new DefaultTransformationReporter(
				"Instance transformation", true);
		TransformationContext context = new TransformationContext(serviceProvider, alignment);

		TransformationFunctionService functions = serviceProvider
				.getService(TransformationFunctionService.class);

		final SubtaskProgressIndicator sub = new SubtaskProgressIndicator(progressIndicator) {

			@Override
			protected String getCombinedTaskName(String taskName, String subtaskName) {
				return taskName + " (" + subtaskName + ")";
			}

		};
		progressIndicator = sub;

		target = new CountingInstanceSink(target) {

			private long lastUpdate = 0;

			@Override
			protected void countChanged(int count) {
				long now = System.currentTimeMillis();
				if (now - lastUpdate > 100) { // only update every 100
												// milliseconds
					lastUpdate = now;
					sub.subTask(count + " transformed instances");
				}
			}

		};

		progressIndicator.begin("Transformation", ProgressIndicator.UNKNOWN);
		try {
			EngineManager engines = new EngineManager();

			PropertyTransformer transformer = new TreePropertyTransformer(alignment, reporter,
					target, engines, context);

			Collection<? extends Cell> typeCells = alignment.getActiveTypeCells();

			// sort type cell by priority
			typeCells = sortTypeCells(typeCells);

			for (Cell typeCell : typeCells) {
				if (progressIndicator.isCanceled()) {
					break;
				}

				List<TypeTransformationFactory> transformations = functions
						.getTypeTransformations(typeCell.getTransformationIdentifier());

				if (transformations == null || transformations.isEmpty()) {
					reporter.error(new TransformationMessageImpl(typeCell,
							MessageFormat.format(
									"No transformation for function {0} found. Skipped type transformation.",
									typeCell.getTransformationIdentifier()),
							null));
				}
				else {
					// TODO select based on e.g. preferred transformation
					// engine?
					TypeTransformationFactory transformation = transformations.iterator().next();

					doTypeTransformation(transformation, typeCell, source, target, alignment,
							engines, transformer, context, reporter, progressIndicator);
				}
			}

			progressIndicator.setCurrentTask("Wait for property transformer to complete");

			// wait for the property transformer to complete
			// cancel property transformations if process was canceled - this
			// may leave transformed instances in inconsistent state
			transformer.join(progressIndicator.isCanceled());

			engines.dispose();

			reporter.setSuccess(true);
			return reporter;
		} finally {
			progressIndicator.end();
		}
	}

	/**
	 * Sort type cells to define order of execution.
	 * 
	 * @param typeCells the type cells to sort
	 * @return the sorted list of cells
	 */
	private List<? extends Cell> sortTypeCells(Collection<? extends Cell> typeCells) {
		List<Cell> cells = new ArrayList<>(typeCells);

		Collections.sort(cells, new Comparator<Cell>() {

			@Override
			public int compare(Cell o1, Cell o2) {
				Priority p1 = o1.getPriority();
				Priority p2 = o2.getPriority();

				return p1.compareTo(p2);
			}
		});

		return cells;
	}

	/**
	 * @see TransformationService#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return true;
	}

	/**
	 * Execute a type transformation based on single type cell
	 * 
	 * @param transformation the transformation to use
	 * @param typeCell the type cell
	 * @param target the target instance sink
	 * @param source the source instances
	 * @param alignment the alignment
	 * @param engines the engine manager
	 * @param transformer the property transformer
	 * @param context the transformation execution context
	 * @param reporter the reporter
	 * @param progressIndicator the progress indicator
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doTypeTransformation(TypeTransformationFactory transformation, Cell typeCell,
			InstanceCollection source, InstanceSink target, Alignment alignment,
			EngineManager engines, PropertyTransformer transformer, TransformationContext context,
			TransformationReporter reporter, ProgressIndicator progressIndicator) {
		TransformationLog cellLog = new CellLog(reporter, typeCell);

		TypeTransformation<?> function;
		try {
			function = transformation.createExtensionObject();
		} catch (Exception e) {
			reporter.error(new TransformationMessageImpl(typeCell,
					"Error creating transformation function.", e));
			return;
		}

		TransformationEngine engine = engines.get(transformation.getEngineId(), cellLog);

		if (engine == null) {
			// TODO instead try another transformation
			cellLog.error(cellLog.createMessage(
					"Skipping type transformation: No matching transformation engine found", null));
			return;
		}

		// prepare transformation configuration
		ListMultimap<String, Type> targetTypes = ArrayListMultimap.create();
		for (Entry<String, ? extends Entity> entry : typeCell.getTarget().entries()) {
			targetTypes.put(entry.getKey(), (Type) entry.getValue());
		}
		ListMultimap<String, ParameterValue> parameters = typeCell.getTransformationParameters();
		if (parameters != null) {
			parameters = Multimaps.unmodifiableListMultimap(parameters);
		}
		Map<String, String> executionParameters = transformation.getExecutionParameters();

		// break on cancel
		if (progressIndicator.isCanceled()) {
			return;
		}

		ResourceIterator<FamilyInstance> iterator;
		if (typeCell.getSource() == null || typeCell.getSource().isEmpty()) {
			// type cell w/o source
			// -> execute exactly once w/ null source
			source = null;
			iterator = new GenericResourceIteratorAdapter<Object, FamilyInstance>(
					Collections.singleton(null).iterator()) {

				@Override
				protected FamilyInstance convert(Object next) {
					return null;
				}
			};
		}
		else {
			// Step 1: selection
			// Select only instances that are relevant for the transformation.
			source = source.select(new TypeCellFilter(typeCell));

			// Step 2: partition
			// use InstanceHandler if available - for example merge or join
			InstanceHandler instanceHandler = function.getInstanceHandler();
			if (instanceHandler != null) {
				progressIndicator.setCurrentTask("Perform instance partitioning");
				try {
					iterator = instanceHandler.partitionInstances(source,
							transformation.getFunctionId(), engine, parameters, executionParameters,
							cellLog);
				} catch (TransformationException e) {
					cellLog.error(
							cellLog.createMessage("Type transformation: partitioning failed", e));
					return;
				}
			}
			else {
				// else just use every instance as is
				iterator = new GenericResourceIteratorAdapter<Instance, FamilyInstance>(
						source.iterator()) {

					@Override
					protected FamilyInstance convert(Instance next) {
						return new FamilyInstanceImpl(next);
					}
				};
			}
		}

		progressIndicator.setCurrentTask("Execute type transformations");

		try {
			while (iterator.hasNext()) {
				// break on cancel
				if (progressIndicator.isCanceled()) {
					return;
				}

				function.setSource(iterator.next());
				function.setPropertyTransformer(transformer);
				function.setParameters(parameters);
				function.setTarget(targetTypes);
				function.setExecutionContext(context.getCellContext(typeCell));

				try {
					((TypeTransformation) function).execute(transformation.getFunctionId(), engine,
							executionParameters, cellLog, typeCell);
				} catch (TransformationException e) {
					cellLog.error(cellLog
							.createMessage("Type transformation failed, skipping instance.", e));
				}
			}
		} finally {
			iterator.close();
		}
	}

	private static final Object NO_FILTER = new Object();

	/**
	 * A filter that matches all instances relevant to a given type cell.
	 * 
	 * @author Kai Schwierczek
	 */
	private static class TypeCellFilter implements Filter {

		private final HashMap<TypeDefinition, Object> lookup = new HashMap<TypeDefinition, Object>();

		/**
		 * Constructs a filter that matches all instances relevant to the given
		 * cell.
		 * 
		 * @param typeCell the type cell
		 */
		private TypeCellFilter(Cell typeCell) {
			for (Entity sourceEntity : typeCell.getSource().values()) {
				Type sourceType = (Type) sourceEntity;
				Filter filter = sourceType.getDefinition().getFilter();
				lookup.put(sourceType.getDefinition().getDefinition(),
						filter == null ? NO_FILTER : filter);
			}
		}

		/**
		 * @see Filter#match(Instance)
		 */
		@Override
		public boolean match(Instance instance) {
			Object filter = lookup.get(instance.getDefinition());
			if (filter == null)
				return false;
			else
				return filter == NO_FILTER || ((Filter) filter).match(instance);
		}
	}
}
