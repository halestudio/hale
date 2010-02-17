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
package eu.esdihumboldt.hale.rcp.views.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.omwg.ComposedFeatureClass;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;

/**
 * Cell information structure
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CellInfo {
	
	private final ICell cell;
	
	private final Set<SchemaItem> sourceItems;
	
	private final Set<SchemaItem> targetItems;

//	/**
//	 * Constructor
//	 * 
//	 * @param cell the cell
//	 * @param sourceItems the source items
//	 * @param targetItems the target items
//	 */
//	public CellInfo(ICell cell, Set<SchemaItem> sourceItems, Set<SchemaItem> targetItems) {
//		super();
//		this.cell = cell;
//		this.sourceItems = sourceItems;
//		this.targetItems = targetItems;
//	}
	
	/**
	 * Constructor
	 * 
	 * @param cell the cell
	 * @param sourceItem the source item
	 * @param targetItem the target item
	 */
	public CellInfo(ICell cell, SchemaItem sourceItem, SchemaItem targetItem) {
		super();
		this.cell = cell;
		
		this.sourceItems = determineItems(cell.getEntity1(), sourceItem);
		this.targetItems = determineItems(cell.getEntity2(), targetItem);
	}

	/**
	 * Determine the schema items for a given entity
	 * 
	 * @param entity the entity
	 * @param item a schema item of the entity's schema
	 * 
	 * @return the schema items represented by the given entity
	 */
	private Set<SchemaItem> determineItems(IEntity entity, SchemaItem item) {
		final List<? extends IEntity> entities;
		
		// composed property
		if (entity instanceof ComposedProperty) {
			entities = new ArrayList<Property>(((ComposedProperty) entity).getCollection());
		}
		// composed feature type
		else if (entity instanceof ComposedFeatureClass) {
			entities = new ArrayList<FeatureClass>(((ComposedFeatureClass) entity).getCollection());
		}
		// default case
		else {
			entities = Collections.singletonList(entity);
		}
		
		final Set<SchemaItem> result = new HashSet<SchemaItem>();
		for (IEntity candidate : entities) {
			SchemaItem candidateItem = findItem(candidate, item);
			if (candidateItem != null) {
				result.add(candidateItem);
			}
			else {
				throw new RuntimeException("Schema item for entity " + //$NON-NLS-1$
						candidate.getAbout().getAbout() + " not found."); //$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * Find the schema item for the given entity
	 * 
	 * @param entity the entity
	 * @param item a schema item of the entity's schema
	 * 
	 * @return the schema item that represents the given entity
	 *   or <code>null</code> if none is found 
	 */
	private SchemaItem findItem(IEntity entity, SchemaItem item) {
		if (entitiesEqual(entity, item.getEntity())) {
			return item;
		}
		else {
			// find parent schema item
			while (item.getParent() != null) {
				item = item.getParent();
			}
			
			Queue<SchemaItem> itemQueue = new LinkedList<SchemaItem>();
			itemQueue.add(item);
			
			while (!itemQueue.isEmpty()) {
				SchemaItem current = itemQueue.poll();
				
				if (entitiesEqual(entity, current.getEntity())) {
					return current;
				}
				else if (current.getChildren() != null) {
					for (SchemaItem child : current.getChildren()) {
						itemQueue.add(child);
					}
				}
			}
			
			return null;
		}
	}

	/**
	 * Determines if two entities are equal
	 * 
	 * @param entity1 the first entity
	 * @param entity2 the second entity
	 * 
	 * @return if both entities are equal
	 */
	private boolean entitiesEqual(IEntity entity1, Entity entity2) {
		if (entity1 == null || entity2 == null) {
			return false;
		}
		return entity1.getAbout().getAbout().equals(entity2.getAbout().getAbout());
	}

	/**
	 * @return the cell
	 */
	public ICell getCell() {
		return cell;
	}

	/**
	 * @return the sourceItem
	 */
	public Set<SchemaItem> getSourceItems() {
		return sourceItems;
	}

	/**
	 * @return the targetItem
	 */
	public Set<SchemaItem> getTargetItems() {
		return targetItems;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CellInfo) {
			return getCell().equals(((CellInfo) obj).getCell());
		}
		
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getCell().hashCode();
	}

}
