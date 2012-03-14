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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.cst.internal.EngineManager;
import eu.esdihumboldt.cst.internal.TreePropertyTransformer;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationExtension;
import eu.esdihumboldt.hale.common.align.extension.transformation.TypeTransformationFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.MergeHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.MultiTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.SingleTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation;
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
import eu.esdihumboldt.hale.common.filter.TypeFilter;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

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
	protected void doTypeTransformation(TypeTransformationFactory transformation,
			Cell typeCell, InstanceCollection source, InstanceSink target, 
			Alignment alignment, EngineManager engines, 
			PropertyTransformer transformer, TransformationReporter reporter, 
			ProgressIndicator progressIndicator) {
		TransformationLog cellLog = new CellLog(reporter, typeCell); 
		
		// TODO Auto-generated method stub
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
		
		if (function instanceof SingleTypeTransformation<?>) {
			doSingleTypeTransformation((SingleTypeTransformation<?>) function,
					typeCell, source, target, transformer, cellLog, engine,
					transformation, progressIndicator);
		}
		
		if (function instanceof MultiTypeTransformation<?>) {
			//TODO
		}
	}

	/**
	 * Execute a single type transformation
	 * @param function the transformation function 
	 * @param typeCell the type transformation cell
	 * @param source the source instances
	 * @param target the target instance sink
	 * @param transformer the property transformer
	 * @param cellLog the transformation log
	 * @param transformation the transformation function factory 
	 * @param engine the transformation engine
	 * @param progressIndicator the progress indicator
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doSingleTypeTransformation(
			SingleTypeTransformation<?> function, Cell typeCell, InstanceCollection source,
			InstanceSink target, PropertyTransformer transformer,
			TransformationLog cellLog, TransformationEngine engine, 
			TypeTransformationFactory transformation, ProgressIndicator progressIndicator) {
		
		// prepare transformation configuration
		Type sourceType = (Type) typeCell.getSource().values().iterator().next();
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
		//TODO filters defined on entity! additional to type filter
		source = source.select(new TypeFilter(
				sourceType.getDefinition().getDefinition()));
		
		// apply entity filter
		Filter entityFilter = sourceType.getDefinition().getFilter();
		if (entityFilter != null) {
			source = source.select(entityFilter);
		}
		
		// Step 2: partition
		// Partition instances into sets to be transformed together.
		// In case of a SingleTypeTransformation each (merged) instance may be
		// transformed separately.
		// If a merge handler is present, the partitioning and merging is
		// performed by the merge handler, otherwise the instance collection
		// is used as is.
		MergeHandler mergeHandler = function.getMergeHandler();
		if (mergeHandler != null) {
			progressIndicator.setCurrentTask("Perform instance merge");
			try {
				source = mergeHandler.mergeInstances(source, 
						transformation.getFunctionId(), engine, parameters,
						executionParameters, cellLog);
			} catch (TransformationException e) {
				cellLog.error(cellLog.createMessage("Merge operation failed, type transformation.", e));
				return;
			}
		}
		
		progressIndicator.setCurrentTask("Execute type transformations");
		
		ResourceIterator<Instance> it = source.iterator();
		try {
			while (it.hasNext()) {
				Instance sourceInstance = it.next();
				
				function.setSource(sourceType, sourceInstance);
				function.setPropertyTransformer(transformer);
				function.setParameters(parameters);
				function.setTarget(targetTypes);
				
				try {
					((SingleTypeTransformation) function).execute(transformation.getFunctionId(), engine, 
							executionParameters, cellLog);
				} catch (TransformationException e) {
					cellLog.error(cellLog.createMessage("Type transformation failed, skipping instance.", e));
				}
			}
		} finally {
			it.close();
		}
	}

}
