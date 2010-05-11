/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MappingInfo {
	
	private final Map<String, Map<String, RetypeInfo>> mappedTargets = new HashMap<String, Map<String, RetypeInfo>>();
	private final Map<String, Set<String>> mappedSources = new HashMap<String, Set<String>>();
	
	/**
	 * Cells that have been added
	 */
	private final Set<ICell> cells = new HashSet<ICell>();
	
	/**
	 * Add the mapping information contained in the cell
	 * 
	 * @param cell the cell
	 */
	public void addCell(ICell cell) {
		if (cell.getEntity1() instanceof FeatureClass && cell.getEntity2() instanceof FeatureClass) {
			FeatureClass fc1 = (FeatureClass) cell.getEntity1();
			FeatureClass fc2 = (FeatureClass) cell.getEntity2();
			
			String source = fc1.getAbout().getAbout();
			String target = fc2.getAbout().getAbout();
			
			boolean newRetype = false;
			
			// add source and info
			Map<String, RetypeInfo> sources = mappedTargets.get(target);
			if (sources == null) {
				sources = new HashMap<String, RetypeInfo>();
				mappedTargets.put(target, sources);
			}
			RetypeInfo info = sources.get(source);
			if (info == null) {
				newRetype = true;
				info = new RetypeInfo(source, target);
				sources.put(source, info);
			}
			
			// add cell to info
			info.addCell(cell);
			
			// add target
			Set<String> targets = mappedSources.get(source);
			if (targets == null) {
				targets = new HashSet<String>();
				mappedSources.put(source, targets);
			}
			targets.add(target);
			
			// for instance cardinalities, we're looking for split and merge conditions.
			if (cell.getEntity1() != null && cell.getEntity1().getTransformation() != null) {
				ITransformation t = cell.getEntity1().getTransformation();
				Set<String> parameterNames = new HashSet<String>();
				for (IParameter param : t.getParameters()) {
					parameterNames.add(param.getName());
				}
				
				// look for a merge condition (many to one)
				if (parameterNames.contains("InstanceMergeCondition")) {
					info.updateInstanceCardinality(CellCardinalityType.many_to_one);
				}
				// look for a split condition (one to many)
				else if (parameterNames.contains("InstanceSplitCondition")) {
					info.updateInstanceCardinality(CellCardinalityType.one_to_many);
				}
				else {
					info.updateInstanceCardinality(CellCardinalityType.one_to_one);
				}
			}
			else if (cell.getEntity2() != null && cell.getEntity2().getTransformation() != null) {
				// we got an augmentation here - cardinality is always 1 to 1 for these.
				info.updateInstanceCardinality(CellCardinalityType.one_to_one);
			}
			
			// if a new retype info was created, check all cells that have been previously added
			if (newRetype) {
				for (ICell otherCell : cells) {
					if (isRelevantCell(otherCell, source, target)) {
						info.addCell(otherCell);
					}
				}
			}
		}
		
		// check if the cell is relevant for any existing mapping
		for (Entry<String, Map<String, RetypeInfo>> targetEntry : mappedTargets.entrySet()) {
			String checkTarget = targetEntry.getKey();
			for (Entry<String, RetypeInfo> sourceEntry : targetEntry.getValue().entrySet()) {
				String checkSource = sourceEntry.getKey();
				RetypeInfo info = sourceEntry.getValue();
				
				if (isRelevantCell(cell, checkSource, checkTarget)) {
					info.addCell(cell);
				}
			}
		}
		
		cells.add(cell);
	}
	
	/**
	 * Determines if the given cell is relevant for the mapping between the
	 *   given types
	 *   
	 * @param cell the cell
	 * @param source the source type name
	 * @param target the target type name
	 * 
	 * @return if the cell is relevant for the mapping
	 */
	protected boolean isRelevantCell(ICell cell, String source, String target) {
		return isRelevantEntity(cell.getEntity1(), source)
			&& isRelevantEntity(cell.getEntity2(), target);
	}

	/**
	 * Determines if the given entity is relevant for a mapping with the given type
	 * 
	 * @param entity the entity
	 * @param type the type
	 * 
	 * @return if the entity is relevant for the mapping
	 */
	protected boolean isRelevantEntity(IEntity entity, String type) {
		String identifier;
		if (entity instanceof FeatureClass) {
			identifier = entity.getAbout().getAbout();
		}
		else if (entity instanceof ComposedProperty) {
			// FIXME determine if multiple FTs are involved, and if yes, register all of them!
			Property firstProperty = ((ComposedProperty) entity).getCollection().get(0);
			identifier = firstProperty.getNamespace() + "/" 
					+ firstProperty.getFeatureClassName();
		}
		else if (entity instanceof Property) {
			Property property = (Property) entity;
			identifier = property.getNamespace() + "/" + property.getFeatureClassName();
		}
		else {
			throw new IllegalArgumentException("Unknown entity type");
		}
		
		return isRelevantEntity(identifier, type);
	}

	/**
	 * Determines if the given entity is relevant for a mapping with the given type
	 * 
	 * @param entityType the entity's associated type
	 * @param type the type
	 * 
	 * @return if the entity is relevant for the mapping
	 */
	protected boolean isRelevantEntity(String entityType, String type) {
		if (entityType.equals(type)) {
			return true;
		}
		else {
			String superType = getSuperType(type);
			
			if (superType != null) {
				return isRelevantEntity(entityType, superType);
			}
			else {
				return false;
			}
		}
	}

	/**
	 * Get the super type name for the given type
	 * 
	 * @param type the type name
	 * 
	 * @return the super type or <code>null</code>
	 */
	protected String getSuperType(String type) {
		// try to get target feature type
		FeatureType featureType = TargetSchemaProvider.getInstance().getType(type);
		
		if (featureType != null) {
			// target feature type found
			AttributeType superType = featureType.getSuper();
			if (superType != null) {
				Name name = superType.getName();
				if (name != null) {
					return name.getNamespaceURI() + "/" + name.getLocalPart();
				}
			}
		}
		else {
			//FIXME try to find source feature type
		}
		
		// no success in finding a super type
		return null;
	}

	/**
	 * Get all mapped target types
	 * 
	 * @return the target type names
	 */
	public Collection<String> getTargetTypes() {
		return new ArrayList<String>(mappedTargets.keySet());
	}
	
	/**
	 * Get all mapped source types
	 * 
	 * @return the source type names
	 */
	public Collection<String> getSourceTypes() {
		return new ArrayList<String>(mappedSources.keySet());
	}
	
	/**
	 * Get the target types that are mapped from the the given source type
	 * 
	 * @param sourceType the source type name
	 * 
	 * @return the target type names
	 */
	public Set<String> getTargetTypes(String sourceType) {
		Set<String> targets = mappedSources.get(sourceType);
		if (targets == null) {
			return new HashSet<String>();
		}
		else {
			return new HashSet<String>(targets);
		}
	}
	
	/**
	 * Get the source types that are mapped to the given target type
	 * 
	 * @param targetType the target type name
	 * 
	 * @return the source type names
	 */
	public Set<String> getSourceTypes(String targetType) {
		Map<String, RetypeInfo> sources = mappedTargets.get(targetType);
		if (sources == null) {
			return new HashSet<String>();
		}
		else {
			return new HashSet<String>(sources.keySet());
		}
	}
	
	/**
	 * Get the retype information for the mapping of the given source and target type
	 * 
	 * @param source the source type name
	 * @param target the target type name
	 * 
	 * @return the retype information
	 */
	public RetypeInfo getRetypeInfo(String source, String target) {
		Map<String, RetypeInfo> sources = mappedTargets.get(target);
		if (sources != null) {
			return sources.get(source);
		}
		
		return null;
	}
	
}
