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
package eu.esdihumboldt.hale.ui.model.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.omwg.ComposedProperty;
import eu.esdihumboldt.commons.goml.omwg.Property;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * {@link AlignmentInfo} based on a {@link SchemaSelection}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaSelectionInfo implements AlignmentInfo {
	
	private final SchemaSelection selection;
	
	private final AlignmentService alignment;

	/**
	 * Constructor
	 * 
	 * @param selection the schema selection
	 * @param alignment the alignment service
	 */
	public SchemaSelectionInfo(SchemaSelection selection,
			AlignmentService alignment) {
		super();
		this.selection = selection;
		this.alignment = alignment;
	}

	/**
	 * @see AlignmentInfo#getAlignment(SchemaItem, SchemaItem)
	 */
	@Override
	public ICell getAlignment(SchemaItem source, SchemaItem target) {
		return alignment.getCell(source.getEntity(), target.getEntity());
	}

	/**
	 * @see AlignmentInfo#getSourceItemCount()
	 */
	@Override
	public int getSourceItemCount() {
		return selection.getSourceItems().size();
	}

	/**
	 * @see AlignmentInfo#getSourceItems()
	 */
	@Override
	public Collection<SchemaItem> getSourceItems() {
		return new ArrayList<SchemaItem>(selection.getSourceItems());
	}

	/**
	 * @see AlignmentInfo#getTargetItemCount()
	 */
	@Override
	public int getTargetItemCount() {
		return selection.getTargetItems().size();
	}

	/**
	 * @see AlignmentInfo#getTargetItems()
	 */
	@Override
	public Collection<SchemaItem> getTargetItems() {
		return new ArrayList<SchemaItem> (selection.getTargetItems());
	}

	/**
	 * @see AlignmentInfo#hasAlignment(SchemaItem, SchemaItem)
	 */
	@Override
	public boolean hasAlignment(SchemaItem source, SchemaItem target) {
		return getAlignment(source, target) != null;
	}

	/**
	 * @see AlignmentInfo#getFirstSourceItem()
	 */
	@Override
	public SchemaItem getFirstSourceItem() {
		return selection.getFirstSourceItem();
	}

	/**
	 * @see AlignmentInfo#getFirstTargetItem()
	 */
	@Override
	public SchemaItem getFirstTargetItem() {
		return selection.getFirstTargetItem();
	}

	/**
	 * @see AlignmentInfo#getAlignment(Collection, Collection)
	 */
	@Override
	public ICell getAlignment(Collection<SchemaItem> source,
			Collection<SchemaItem> target) {
		Entity entity1 = determineEntity(source);
		Entity entity2 = determineEntity(target);
		
		if (entity1 == null || entity2 == null) {
			return null;
		}
		else {
			return alignment.getCell(entity1, entity2);
		}
	}

	/**
	 * Determine the entity for a collection of {@link SchemaItem}s
	 * 
	 * @param items the items
	 * @return the entity or <code>null</code> if there could not be
	 *   created a valid entity
	 */
	public static Entity determineEntity(Collection<SchemaItem> items) {
		if (items == null || items.isEmpty()) {
			return null;
		}
		else if (items.size() == 1) {
			return items.iterator().next().getEntity();
		}
		else {
			Iterator<SchemaItem> it = items.iterator();
			Entity itemEntity = it.next().getEntity();
			String namespace = itemEntity.getNamespace();
			
			// handle different cases
			
			// composed property
			if (itemEntity instanceof Property) {
				Property property = (Property) itemEntity;
				List<Property> properties = new ArrayList<Property>(items.size());
				properties.add(property);
				
				while (it.hasNext()) {
					itemEntity = it.next().getEntity();
					if (itemEntity instanceof Property &&
							itemEntity.getNamespace().equals(namespace)) {
						// add property to list
						property = (Property) itemEntity;
						properties.add(property);
					}
					else {
						// no property or namespace doesn't match
						return null;
					}
				}
				
				ComposedProperty result = new ComposedProperty(namespace);
				result.setCollection(properties);
				return result;
			}
			
			//TODO composed feature type?
			
			return null;
		}
	}

	/**
	 * @see AlignmentInfo#hasAlignment(Collection, Collection)
	 */
	@Override
	public boolean hasAlignment(Collection<SchemaItem> source,
			Collection<SchemaItem> target) {
		return getAlignment(source, target) != null;
	}

}
