/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.cst.internal.EngineManager;
import eu.esdihumboldt.cst.internal.TreePropertyTransformer;
import eu.esdihumboldt.cst.internal.util.CountingInstanceSink;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.FamilyInstance;
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
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Transformation service implementation
 * @author Simon Templer
 * @since 2.5
 */
@Immutable // stateless
public class ConceptualSchemaTransformer implements TransformationService {

	/**
	 * @see TransformationService#transform(Alignment, InstanceCollection, InstanceSink, ProgressIndicator)
	 */
	@Override
	public TransformationReport transform(Alignment alignment, InstanceCollection source,
			InstanceSink target, ProgressIndicator progressIndicator) {
		TransformationReporter reporter = new DefaultTransformationReporter(
				"Instance transformation", true);
		
		final SubtaskProgressIndicator sub = new SubtaskProgressIndicator(progressIndicator) {

			@Override
			protected String getCombinedTaskName(String taskName,
					String subtaskName) {
				return taskName + " (" + subtaskName + ")";
			}
			
		};
		progressIndicator = sub;
		
		target = new CountingInstanceSink(target) {
			
			private long lastUpdate = 0;

			@Override
			protected void countChanged(int count) {
				long now = System.currentTimeMillis();
				if (now - lastUpdate > 100) { // only update every 100 milliseconds
					lastUpdate = now;
					sub.subTask(count + " transformed instances");
				}
			}
			
		};
		
		progressIndicator.begin("Transformation", ProgressIndicator.UNKNOWN);
		try {
			EngineManager engines = new EngineManager();
			
			PropertyTransformer transformer = new TreePropertyTransformer(
					alignment, reporter, target, engines);
			
			TypeTransformationExtension typesTransformations = 
					TypeTransformationExtension.getInstance();
			
			Collection<? extends Cell> typeCells = alignment.getTypeCells();
			for (Cell typeCell : typeCells) {
				List<TypeTransformationFactory> transformations = typesTransformations.getTransformations(typeCell.getTransformationIdentifier());
				
				if (transformations == null || transformations.isEmpty()) {
					reporter.error(new TransformationMessageImpl(typeCell, 
							MessageFormat.format("No transformation for function {0} found. Skipped type transformation.",
									typeCell.getTransformationIdentifier()), null));
				}
				else {
					//TODO select based on e.g. preferred transformation engine?
					TypeTransformationFactory transformation = transformations.iterator().next();
					
					doTypeTransformation(transformation, typeCell, source, target, 
							alignment, engines, transformer, reporter, progressIndicator);
				}
			}
			
			progressIndicator.setCurrentTask("Wait for property transformer to complete");
			
			// wait for the property transformer to complete
			transformer.join();
	
			engines.dispose();
			
			reporter.setSuccess(true);
			return reporter;
		} finally {
			progressIndicator.end();
		}
	}

	/**
	 * Execute a type transformation based on single type cell
	 * @param transformation the transformation to use
	 * @param typeCell the type cell
	 * @param target the target instance sink
	 * @param source the source instances
	 * @param alignment the alignment
	 * @param engines the engine manager
	 * @param transformer the property transformer
	 * @param reporter the reporter
	 * @param progressIndicator the progress indicator 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doTypeTransformation(TypeTransformationFactory transformation,
			Cell typeCell, InstanceCollection source, InstanceSink target, 
			Alignment alignment, EngineManager engines, 
			PropertyTransformer transformer, TransformationReporter reporter, 
			ProgressIndicator progressIndicator) {
		TransformationLog cellLog = new CellLog(reporter, typeCell);
		
		TypeTransformation<?> function;
		try {
			function = transformation.createExtensionObject();
		} catch (Exception e) {
			reporter.error(new TransformationMessageImpl(typeCell, "Error creating transformation function.", e));
			return;
		}
		
		TransformationEngine engine = engines.get(
				transformation.getEngineId(), cellLog);
		
		if (engine == null) {
			//TODO instead try another transformation
			cellLog.error(cellLog.createMessage(
					"Skipping type transformation: No matching transformation engine found", null));
			return;
		}

		// prepare transformation configuration
		ListMultimap<String, Type> targetTypes = ArrayListMultimap.create();
		for (Entry<String, ? extends Entity> entry : typeCell.getTarget().entries()) {
			targetTypes.put(entry.getKey(), (Type) entry.getValue());
		}
		ListMultimap<String, String> parameters = typeCell.getTransformationParameters();
		if (parameters != null) {
			parameters = Multimaps.unmodifiableListMultimap(parameters);
		}
		Map<String, String> executionParameters = transformation.getExecutionParameters(); 
		
		// Step 1: selection
		// Select only instances that are relevant for the transformation.
		source = source.select(new TypeCellFilter(typeCell));
		
		// Step 2: partition
		ResourceIterator<FamilyInstance> iterator;
		// use InstanceHandler if available - for example merge or join
		InstanceHandler instanceHandler = function.getInstanceHandler();
		if (instanceHandler != null) {
			progressIndicator.setCurrentTask("Perform instance partitioning");
			try {
				iterator = instanceHandler.partitionInstances(source, 
						transformation.getFunctionId(), engine, parameters,
						executionParameters, cellLog);
			} catch (TransformationException e) {
				cellLog.error(cellLog.createMessage("Type transformation: partitioning failed", e));
				return;
			}
		} else {
			// else just use every instance as is
			iterator = new GenericResourceIteratorAdapter<Instance, FamilyInstance>(source.iterator()) {
				/**
				 * @see eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter#convert(java.lang.Object)
				 */
				@Override
				protected FamilyInstance convert(Instance next) {
					return new FamilyInstanceImpl(next);
				}
			};
		}
		
		progressIndicator.setCurrentTask("Execute type transformations");

		try {
			while (iterator.hasNext()) {
				function.setSource(iterator.next());
				function.setPropertyTransformer(transformer);
				function.setParameters(parameters);
				function.setTarget(targetTypes);
				
				try {
					((TypeTransformation) function).execute(transformation.getFunctionId(), engine, 
							executionParameters, cellLog);
				} catch (TransformationException e) {
					cellLog.error(cellLog.createMessage("Type transformation failed, skipping instance.", e));
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
	private static class TypeCellFilter implements Filter{
		private final HashMap<TypeDefinition, Object> lookup = new HashMap<TypeDefinition, Object>();

		/**
		 * Constructs a filter that matches all instances relevant to the given cell.
		 * 
		 * @param typeCell the type cell
		 */
		private TypeCellFilter(Cell typeCell) {
			for (Entity sourceEntity : typeCell.getSource().values()) {
				Type sourceType = (Type) sourceEntity;
				Filter filter = sourceType.getDefinition().getFilter();
				lookup.put(sourceType.getDefinition().getDefinition(), filter == null ? NO_FILTER : filter);
			}
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.Filter#match(eu.esdihumboldt.hale.common.instance.model.Instance)
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
