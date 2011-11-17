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

package eu.esdihumboldt.cst.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableGroup;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.OGroup;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Basic property transformer implementation
 * @author Simon Templer
 */
public class SimplePropertyTransformer implements PropertyTransformer {
	
	private final Alignment alignment;
	
	private final TransformationReporter reporter;

	private final InstanceSink sink;

	/**
	 * Create a simple property transformer
	 * @param alignment the alignment
	 * @param reporter the transformation log to report any transformation 
	 *   messages to
	 * @param sink the target instance sink
	 */
	public SimplePropertyTransformer(Alignment alignment, 
			TransformationReporter reporter, InstanceSink sink) {
		this.alignment = alignment;
		this.reporter = reporter;
		this.sink = sink;
	}

	/**
	 * @see PropertyTransformer#publish(Collection, Instance, MutableInstance)
	 */
	@Override
	public void publish(Collection<? extends Type> sourceTypes,
			Instance source, MutableInstance target) {
		Collection<TypeEntityDefinition> sourceDefinitions = new ArrayList<TypeEntityDefinition>();
		for (Type sourceType : sourceTypes) {
			sourceDefinitions.add(sourceType.getDefinition());
		}
		TypeEntityDefinition targetDefinition = new TypeEntityDefinition(
				target.getDefinition(), SchemaSpaceID.TARGET);
		// identify transformations to be executed on given instances
		Collection<? extends Cell> propertyCells = alignment.getPropertyCells(
				sourceDefinitions, targetDefinition);
		
		//TODO execute property transformations
		//XXX for now only (guarantee to) handle the most simple kind of cell - 1 source entity and 1 target entity
		
		// group cells by target parent definitions
		Multimap<EntityDefinition, Cell> cellTargetParents = HashMultimap.create(); 
		for (Cell propertyCell : propertyCells) {
			EntityDefinition parent = null;
			Collection<? extends Entity> targets = propertyCell.getTarget().values();
			for (Entity targetEntity : targets) {
				EntityDefinition parentCandidate = AlignmentUtil.getParent(
						targetEntity.getDefinition());
				
				if (parent == null) {
					parent = parentCandidate;
				}
				else if (!parent.equals(parentCandidate)) {
					// parent mismatch -> targets in different contexts!
					//FIXME what to do in this case?
					//XXX for now prevent adding cell
					parent = null;
					//TODO report error
					break;
				}
			}
			
			if (parent != null) {
				cellTargetParents.put(parent, propertyCell);
			}
			//TODO else report warning?
		}
		
		// sort target parent definitions, shortest child paths first
		List<EntityDefinition> targetParents = new ArrayList<EntityDefinition>(
				cellTargetParents.keySet());
		Collections.sort(targetParents, new Comparator<EntityDefinition>() {
			@Override
			public int compare(EntityDefinition o1, EntityDefinition o2) {
				// compare the path length
				List<ChildContext> p1 = o1.getPropertyPath();
				List<ChildContext> p2 = o2.getPropertyPath();
				Integer length1 = (p1 == null)?(0):(p1.size());
				Integer length2 = (p2 == null)?(0):(p2.size());
				int result = length1.compareTo(length2);
				
				if (result == 0) {
					result = o1.getType().compareTo(o2.getType());
					
					if (p1 != null && p2 != null) {
						for (int i = 0; i < p1.size() && result == 0; i++) {
							result = p1.get(i).getChild().compareTo(
									p2.get(i).getChild());
						}
					}
				}
				
				return result;
			}
		});
		
		// create map for instances of target parents
		Map<EntityDefinition, MutableGroup> parentGroups = new HashMap<EntityDefinition, MutableGroup>();
		// add target root instance
		parentGroups.put(targetDefinition, target);
		
		// execute cells following the parent order
		for (EntityDefinition targetParent : targetParents) {
			MutableGroup instance = parentGroups.get(targetParent);
			if (instance == null) {
				instance = createGroup(targetParent, parentGroups);
			}
			//XXX what about values set on instances?! how to merge them?
			
			Collection<Cell> targetCells = cellTargetParents.get(targetParent);
			for (Cell cell : targetCells) {
				handleCell(cell, source, instance);
			}
		}
		
		//XXX after property transformations, publish the target instance
		sink.addInstance(target);
	}

	/**
	 * Handle a property transformation cell
	 * @param cell the property transformation
	 * @param source the source instance
	 * @param targetParent the target parent group
	 */
	private static void handleCell(Cell cell, Instance source, MutableGroup targetParent) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Create a group or instance associated with the given entity definition.
	 * @param definition the entity definition of the instance
	 * @param parentGroups the available parent groups/instances
	 * @return the created group/instance
	 */
	private static MutableGroup createGroup(EntityDefinition definition,
			Map<EntityDefinition, MutableGroup> parentGroups) {
		TypeDefinition instanceType = null;
		
		Definition<?> def = definition.getDefinition();
		if (def instanceof TypeDefinition) {
			instanceType = (TypeDefinition) def;
		}
		else if (def instanceof PropertyDefinition) {
			instanceType = ((PropertyDefinition) def).getPropertyType();
		}
		
		MutableGroup result;
		if (instanceType != null) {
			result = new OInstance(instanceType);
		}
		else if (def instanceof DefinitionGroup) {
			result = new OGroup((DefinitionGroup) def);
		}
		else {
			// now, that's illegal!
			throw new IllegalStateException();
		}
		
		//TODO add instance/group to parent, if parent not there, create it too
		//XXX or do this at another place when it is ensured that the group is not empty?!
		
		return result;
	}

}
