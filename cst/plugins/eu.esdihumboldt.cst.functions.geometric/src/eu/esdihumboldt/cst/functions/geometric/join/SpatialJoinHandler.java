/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.geometric.join;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.vividsolutions.jts.geom.Geometry;

import de.fhg.igd.geom.BoundingBox;
import de.fhg.igd.geom.Localizable;
import eu.esdihumboldt.cst.functions.geometric.join.SpatialJoinParameter.SpatialJoinCondition;
import eu.esdihumboldt.cst.functions.geometric.join.SpatialRelationEvaluator.StandardRelation;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.core.service.ServiceProviderAware;
import eu.esdihumboldt.hale.common.instance.index.spatial.SpatialIndexService;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResolvableInstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Join based on the spatial relation of geometry properties.
 * 
 * @author Florian Esser
 */
public class SpatialJoinHandler implements InstanceHandler<TransformationEngine>,
		SpatialJoinFunction, ServiceProviderAware {

	private ServiceProvider services;

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler#partitionInstances(eu.esdihumboldt.hale.common.instance.model.InstanceCollection,
	 *      java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap, java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	public ResourceIterator<FamilyInstance> partitionInstances(InstanceCollection instances,
			String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, ParameterValue> transformationParameters,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {

		if (transformationParameters == null
				|| !transformationParameters.containsKey(PARAMETER_SPATIAL_JOIN)
				|| transformationParameters.get(PARAMETER_SPATIAL_JOIN).isEmpty()) {
			throw new TransformationException("No join parameter defined");
		}

		if (services == null) {
			throw new IllegalStateException(
					"ServiceProvider must be set before calling partitionInstances");
		}

		SpatialJoinParameter joinParameter = transformationParameters.get(PARAMETER_SPATIAL_JOIN)
				.get(0).as(SpatialJoinParameter.class);

		String validation = joinParameter.validate();
		if (validation != null) {
			throw new TransformationException("Spatial Join parameter invalid: " + validation);
		}

		List<TypeEntityDefinition> types = joinParameter.types;
		// ChildType -> DirectParentType
		int[] directParent = new int[joinParameter.types.size()];
		// ChildType -> (ParentType -> Collection<JoinCondition>)
		Map<Integer, Multimap<Integer, SpatialJoinCondition>> joinTable = new HashMap<>();

		for (SpatialJoinCondition condition : joinParameter.conditions) {
			int baseTypeIndex = types.indexOf(AlignmentUtil.getTypeEntity(condition.baseProperty));
			int joinTypeIndex = types.indexOf(AlignmentUtil.getTypeEntity(condition.joinProperty));
			Multimap<Integer, SpatialJoinCondition> typeTable = joinTable.get(joinTypeIndex);
			if (typeTable == null) {
				typeTable = ArrayListMultimap.create(2, 2);
				joinTable.put(joinTypeIndex, typeTable);
			}
			typeTable.put(baseTypeIndex, condition);

			// update highest type if necessary
			if (directParent[joinTypeIndex] < baseTypeIndex) {
				directParent[joinTypeIndex] = baseTypeIndex;
			}
		}

		// remember instances of first type to start join afterwards
		Collection<InstanceReference> startInstances = new LinkedList<InstanceReference>();

		// iterate once over all instances
		ResourceIterator<Instance> iterator = instances.iterator();
		try {
			while (iterator.hasNext()) {
				Instance next = iterator.next();

				// remember instances of first type
				if (next.getDefinition().equals(types.get(0).getDefinition())) {
					startInstances.add(instances.getReference(next));
				}
			}
		} finally {
			iterator.close();
		}

		return new SpatialJoinIterator(instances, startInstances, directParent, services,
				joinTable);
	}

	private class SpatialJoinIterator
			extends GenericResourceIteratorAdapter<InstanceReference, FamilyInstance> {

		private final InstanceCollection instances;
		// type -> direct-parent
		private final int[] parent;
		// ChildType -> (ParentType -> Collection<JoinCondition>)
		private final ServiceProvider provider;
		private final Map<Integer, Multimap<Integer, SpatialJoinCondition>> joinTable;

		protected SpatialJoinIterator(InstanceCollection instances,
				Collection<InstanceReference> startInstances, int[] parent,
				ServiceProvider provider,
				Map<Integer, Multimap<Integer, SpatialJoinCondition>> joinTable) {
			super(startInstances.iterator());
			this.instances = instances;
			this.parent = parent;
			this.provider = provider;
			this.joinTable = joinTable;
		}

		/**
		 * @see eu.esdihumboldt.hale.common.instance.model.impl.GenericResourceIteratorAdapter#convert(java.lang.Object)
		 */
		@Override
		protected FamilyInstance convert(InstanceReference next) {
			FamilyInstance base = new FamilyInstanceImpl(instances.getInstance(next));
			FamilyInstance[] currentInstances = new FamilyInstance[parent.length];
			currentInstances[0] = base;

			join(currentInstances, 0);

			return base;
		}

		// Joins all direct children of the given type to currentInstances.
		private void join(FamilyInstance[] currentInstances, int currentType) {
			@SuppressWarnings("unchecked")
			SpatialIndexService<Localizable, Localizable> index = provider
					.getService(SpatialIndexService.class);

			// Join all types that are direct children of the last type.
			for (int i = currentType + 1; i < parent.length; i++) {
				if (parent[i] == currentType) {
					// Get join condition for the direct child type.
					Multimap<Integer, SpatialJoinCondition> joinConditions = joinTable.get(i);
					// Collect intersection of conditions. null marks beginning
					// in contrast to an empty set.
					Set<InstanceReference> possibleInstances = null;
					// ParentType -> JoinConditions
					for (Map.Entry<Integer, SpatialJoinCondition> joinCondition : joinConditions
							.entries()) {
						Collection<Object> currentValues = AlignmentUtil.getValues(
								currentInstances[joinCondition.getKey()],
								joinCondition.getValue().baseProperty, true);

						if (currentValues == null) {
							possibleInstances = Collections.emptySet();
							break;
						}

						SpatialRelationEvaluator relation;
						// Check if a standard spatial relation was used
						final StandardRelation stdRel = StandardRelation
								.valueOfOrNull(joinCondition.getValue().relation);
						if (stdRel != null) {
							relation = stdRel.relation();
						}
						else {
							// TODO Provide extension point to register further
							// spatial relations, e.g. BOUNDARY_COVERS
							throw new IllegalArgumentException(MessageFormat.format(
									"Spatial join condition has invalid spatial relation: {0}",
									joinCondition.getValue().relation));
						}

						PropertyEntityDefinition joinProperty = joinCondition
								.getValue().joinProperty;
						QName joinPropertyName = joinProperty.getDefinition().getName();

						// Allow targets with any of the property values.
						HashSet<InstanceReference> matches = new HashSet<InstanceReference>();
						for (Object currentValue : currentValues) {
							Geometry geom = getGeometry(currentValue);
							if (geom == null) {
								continue;
							}

							BoundingBox box = BoundingBox.compute(geom);
							Collection<Localizable> possibleMatches = index.retrieve(box,
									Arrays.asList(joinProperty.getDefinition().getParentType()));
							for (Localizable possibleMatch : possibleMatches) {
								if (possibleMatch instanceof InstanceReference) {
									InstanceReference ref = (InstanceReference) possibleMatch;
									// try to resolve
									// ResolvableInstanceReferences
									Instance inst = ResolvableInstanceReference.tryResolve(ref);
									if (inst != null) {
										Object[] values = inst.getProperty(joinPropertyName);
										if (testJoinCondition(values, geom, relation)) {
											matches.add((InstanceReference) possibleMatch);
										}
									}
								}
							}
						}

						if (possibleInstances == null) {
							possibleInstances = matches;
						}
						else {
							// Intersect!
							Iterator<InstanceReference> iter = possibleInstances.iterator();
							while (iter.hasNext()) {
								InstanceReference ref = iter.next();
								if (!matches.contains(ref))
									iter.remove();
							}
						}

						// Break if set is empty.
						if (possibleInstances.isEmpty())
							break;
					}

					if (possibleInstances != null && !possibleInstances.isEmpty()) {
						FamilyInstance parent = currentInstances[currentType];
						for (InstanceReference ref : possibleInstances) {
							Instance inst = ResolvableInstanceReference.tryResolve(ref);
							if (inst == null) {
								inst = instances.getInstance(ref);
							}
							FamilyInstance child = new FamilyInstanceImpl(inst);
							parent.addChild(child);
							currentInstances[i] = child;
							join(currentInstances, i);
						}
						currentInstances[i] = null;
					}
				}
			}
		}
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
//	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private boolean testJoinCondition(Object[] leftValues, Geometry rightGeometry,
			SpatialRelationEvaluator evaluator) {
		for (Object leftValue : leftValues) {
			Geometry leftGeometry = getGeometry(leftValue);
			if (evaluator.evaluate(leftGeometry, rightGeometry)) {
				return true;
			}
		}

		return false;
	}

	private static Geometry getGeometry(Object value) {
		if (value instanceof GeometryProperty<?>) {
			Object geomObj = ((GeometryProperty<?>) value).getGeometry();
			if (geomObj instanceof Geometry) {
				return (Geometry) geomObj;
			}
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.service.ServiceProviderAware#setServiceProvider(eu.esdihumboldt.hale.common.core.service.ServiceProvider)
	 */
	@Override
	public void setServiceProvider(ServiceProvider services) {
		this.services = services;
	}

}
