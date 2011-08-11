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
package eu.esdihumboldt.hale.ui.selection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import eu.esdihumboldt.hale.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * A selection with source and target {@link EntityDefinition}s
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefaultSchemaSelection implements IStructuredSelection, SchemaSelection {
	
	private final Set<EntityDefinition> sourceItems = new LinkedHashSet<EntityDefinition>();
	
	private final Set<EntityDefinition> targetItems = new LinkedHashSet<EntityDefinition>();

	/**
	 * Creates an empty selection
	 */
	public DefaultSchemaSelection() {
		this(null, null);
	}
	
	/**
	 * Creates a selection that is initialized with the given items
	 * 
	 * @param sourceItems the source items
	 * @param targetItems the target items
	 */
	public DefaultSchemaSelection(final Collection<EntityDefinition> sourceItems, 
			final Collection<EntityDefinition> targetItems) {
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
	public void addSourceItem(EntityDefinition item) {
		sourceItems.add(item);
	}
	
	/**
	 * Adds a target item
	 * 
	 * @param item the item to add
	 */
	public void addTargetItem(EntityDefinition item) {
		targetItems.add(item);
	}
	
	/**
	 * @see eu.esdihumboldt.hale.ui.selection.SchemaSelection#getSourceItems()
	 */
	@Override
	public Set<EntityDefinition> getSourceItems() {
		return Collections.unmodifiableSet(sourceItems);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.selection.SchemaSelection#getTargetItems()
	 */
	@Override
	public Set<EntityDefinition> getTargetItems() {
		return Collections.unmodifiableSet(targetItems);
	}
	
	/**
	 * @see eu.esdihumboldt.hale.ui.selection.SchemaSelection#getFirstSourceItem()
	 */
	@Override
	public EntityDefinition getFirstSourceItem() {
		return getFirstItem(SchemaSpaceID.SOURCE);
	}
	
	/**
	 * @see eu.esdihumboldt.hale.ui.selection.SchemaSelection#getFirstTargetItem()
	 */
	@Override
	public EntityDefinition getFirstTargetItem() {
		return getFirstItem(SchemaSpaceID.TARGET);
	}
	
	/**
	 * @see eu.esdihumboldt.hale.ui.selection.SchemaSelection#getFirstItem(eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID)
	 */
	@Override
	public EntityDefinition getFirstItem(SchemaSpaceID schema) {
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
	 * @see IStructuredSelection#getFirstElement()
	 */
	@Override
	public Object getFirstElement() {
		if (!sourceItems.isEmpty()) {
			return getFirstSourceItem();
		}
		else {
			return getFirstTargetItem();
		}
	}

	/**
	 * @see IStructuredSelection#iterator()
	 */
	@Override
	public Iterator<?> iterator() {
		return toList().iterator();
	}

	/**
	 * @see IStructuredSelection#size()
	 */
	@Override
	public int size() {
		return sourceItems.size() + targetItems.size();
	}

	/**
	 * @see IStructuredSelection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return toList().toArray();
	}

	/**
	 * @see IStructuredSelection#toList()
	 */
	@Override
	public List<EntityDefinition> toList() {
		List<EntityDefinition> list = new ArrayList<EntityDefinition>(
				sourceItems.size() + targetItems.size());
		list.addAll(sourceItems);
		list.addAll(targetItems);
		return list;
	}

	/**
	 * @see ISelection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return sourceItems.isEmpty() && targetItems.isEmpty();
	}

}
