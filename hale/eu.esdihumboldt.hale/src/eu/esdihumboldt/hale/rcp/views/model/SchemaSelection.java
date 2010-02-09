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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;

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
