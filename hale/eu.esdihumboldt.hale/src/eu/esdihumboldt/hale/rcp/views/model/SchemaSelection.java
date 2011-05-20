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
package eu.esdihumboldt.hale.rcp.views.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty;
import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.utils.SchemaItemService;
import eu.esdihumboldt.hale.rcp.views.mapping.CellInfo;
import eu.esdihumboldt.hale.rcp.wizards.augmentations.NullSchemaItem;
import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.IEntity;

/**
 * A selection with source and target {@link SchemaItem}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaSelection implements ISelection {
	
	private final Set<SchemaItem> sourceItems = new LinkedHashSet<SchemaItem>();
	
	private final Set<SchemaItem> targetItems = new LinkedHashSet<SchemaItem>();

	/**
	 * Creates an empty selection
	 */
	public SchemaSelection() {
		this(null, null);
	}
	
	/**
	 * Get cells and cell infos for the current selection
	 * 
	 * @return the matching cells and cell infos
	 */
	public Map<ICell, CellInfo> getCellsForSelection() {
		Map<ICell, CellInfo> cells = new HashMap<ICell, CellInfo>();
		
		AlignmentService alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
			
		Set<SchemaItem> sourceItems = new LinkedHashSet<SchemaItem>(this
				.getSourceItems());
		Set<SchemaItem> targetItems = new LinkedHashSet<SchemaItem>(this
				.getTargetItems());

		sourceItems.addAll(getChildren(sourceItems));
		targetItems.addAll(getChildren(targetItems));

		// add NullSchemaItem to find augmentations
		sourceItems.add(NullSchemaItem.INSTANCE);
		
		Collection<ICell> cellList = alignmentService.getCells();
		for (ICell cell : cellList) {
			IEntity e1 = cell.getEntity1();
			IEntity e2 = cell.getEntity2();
			
			if (e1 != null && e2 != null) {
				Collection<SchemaItem> sourceCandidates = getSchemaItems(e1, SchemaType.SOURCE);
				
				if (sourceCandidates != null && !sourceCandidates.isEmpty()) {
					SchemaItem source = containsAny(sourceItems, sourceCandidates);
					
					if (source != null) {
						Collection<SchemaItem> targetCandidates = getSchemaItems(e2, SchemaType.TARGET);
						
						if (targetCandidates != null && !targetCandidates.isEmpty()) {
							SchemaItem target = containsAny(targetItems, targetCandidates);
						
							if (target != null) {
								cells.put(cell, new CellInfo(cell, source, target));
							}
						}
					}
				}
			}
		}
		return cells;
	}
	
	/**
	 * Get all schema items that match the given entity
	 * 
	 * @param entity the entity
	 * @param type the schema type
	 * 
	 * @return the schema items
	 */
	private Collection<SchemaItem> getSchemaItems(IEntity entity, SchemaType type) {
		if (entity.getAbout().getAbout().equals(Entity.NULL_ENTITY.getAbout().getAbout())) {
			// special case null entity
			return Collections.singleton(NullSchemaItem.INSTANCE);
		}
		else {
			if (entity instanceof ComposedProperty) {
				List<Property> properties = ((ComposedProperty) entity).getCollection();
				ArrayList<SchemaItem> result = new ArrayList<SchemaItem>();
				for (Property property : properties) {
					if (property instanceof ComposedProperty) {
						result.addAll(getSchemaItems(property, type));
					}
					else {
						SchemaItem item = getSingleSchemaItem(property, type);
						if (item != null) {
							result.add(item);
						}
					}
				}
				return result;
			}
			else if (entity instanceof Property || entity instanceof FeatureClass) {
				return Collections.singleton(getSingleSchemaItem(entity, type));
			}
			//TODO check composed feature types
		}
		
		return null;
	}

	/**
	 * Get a schema item that matches the given entity
	 * 
	 * @param entity the entity which may not be a composed one
	 * @param type the schema type
	 * 
	 * @return the matching schema item or <code>null</code>
	 */
	private SchemaItem getSingleSchemaItem(IEntity entity, SchemaType type) {
		// get the schema item matching the entity
		SchemaItemService items = (SchemaItemService) PlatformUI.getWorkbench().getService(SchemaItemService.class);
		return items.getSchemaItem(entity, type);
	}

	/**
	 * Checks if any of the candidates is contained in the items and returns it
	 * 
	 * @param items the items
	 * @param candidates the candidates
	 * @return a candidate that is contained in the items or <code>null</code>
	 */
	private SchemaItem containsAny(Set<SchemaItem> items,
			Collection<SchemaItem> candidates) {
		for (SchemaItem candidate : candidates) {
			if (items.contains(candidate)) {
				return candidate;
			}
		}
		
		return null;
	}

	/**
	 * Recursively get the children of the given items
	 * 
	 * @param items the items
	 * @return the set of children
	 */
	private Set<? extends SchemaItem> getChildren(
			Set<SchemaItem> items) {
		Set<SchemaItem> children = new LinkedHashSet<SchemaItem>();
		
		// add children
		for (SchemaItem item : items) {
			if (item.hasChildren()) {
				for (SchemaItem child : item.getChildren()) {
					children.add(child);
				}
			}
		}
		
		if (!children.isEmpty()) {
			children.addAll(getChildren(children));
		}
		
		return children;
	}
	
	/**
	 * Creates a selection that is initialized with the given items
	 * 
	 * @param sourceItems the source items
	 * @param targetItems the target items
	 */
	public SchemaSelection(final Collection<SchemaItem> sourceItems, 
			final Collection<SchemaItem> targetItems) {
		super();
		
		if (sourceItems != null) {
			this.sourceItems.addAll(sourceItems);
		}
		
		if (targetItems != null) {
			this.targetItems.addAll(targetItems);
		}
	}
	
	/**
	 * Adds a source item
	 * 
	 * @param item the item to add
	 */
	public void addSourceItem(SchemaItem item) {
		sourceItems.add(item);
	}
	
	/**
	 * Adds a target item
	 * 
	 * @param item the item to add
	 */
	public void addTargetItem(SchemaItem item) {
		targetItems.add(item);
	}
	
	/**
	 * @return the sourceItems
	 */
	public Set<SchemaItem> getSourceItems() {
		return sourceItems;
	}

	/**
	 * @return the targetItems
	 */
	public Set<SchemaItem> getTargetItems() {
		return targetItems;
	}
	
	/**
	 * Get the first selected source item
	 * 
	 * @return the first selected source item or <code>null</code>
	 */
	public SchemaItem getFirstSourceItem() {
		return getFirstItem(SchemaType.SOURCE);
	}
	
	/**
	 * Get the first selected target item
	 * 
	 * @return the first selected target item or <code>null</code>
	 */
	public SchemaItem getFirstTargetItem() {
		return getFirstItem(SchemaType.TARGET);
	}
	
	/**
	 * Get the first selected item of the given schema
	 * 
	 * @param schema the schema type
	 * @return the first selected item or <code>null</code>
	 */
	public SchemaItem getFirstItem(SchemaType schema) {
		try {
			switch (schema) {
			case SOURCE:
				return sourceItems.iterator().next();
			case TARGET:
				return targetItems.iterator().next();
			default:
				return null;
			}
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * @see ISelection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return sourceItems.isEmpty() && targetItems.isEmpty();
	}

}
