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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;

/**
 * Default content provider for the schema model
 *
 * @author cjauss, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ConfigurableModelContentProvider 
	extends ModelContentProvider {
	
	/**
	 * Empty children array
	 */
	protected static final Object[] EMPTY_CHILDREN = new Object[0];
	
	/**
	 * If the type hierarchy shall be flattened 
	 */
	private boolean flatten;
	
	/**
	 * Suppress displaying property aggregation
	 */
	private boolean suppressAggregation;
	
	/**
	 * Suppress displaying inherited attributes
	 */
	private boolean suppressInheritedAttributes;

	/**
	 * Constructor
	 * 
	 * @param flatten if the type hierarchy shall be flattened
	 * @param suppressAggregation if displaying property aggregation shall be suppressed 
	 * @param suppressInheritedAttributes if displaying inherited attributes shall be suppressed
	 */
	public ConfigurableModelContentProvider(boolean flatten,
			boolean suppressAggregation, boolean suppressInheritedAttributes) {
		super();
		this.flatten = flatten;
		this.suppressAggregation = suppressAggregation;
		this.suppressInheritedAttributes = suppressInheritedAttributes;
	}

	/**
	 * @param flatten the flatten to set
	 */
	public void setFlatten(boolean flatten) {
		this.flatten = flatten;
	}

	/**
	 * @param suppressAggregation the suppressAggregation to set
	 */
	public void setSuppressAggregation(boolean suppressAggregation) {
		this.suppressAggregation = suppressAggregation;
	}

	/**
	 * @param suppressInheritedAttributes the suppressInheritedAttributes to set
	 */
	public void setSuppressInheritedAttributes(boolean suppressInheritedAttributes) {
		this.suppressInheritedAttributes = suppressInheritedAttributes;
	}

	/**
	 * @see ModelContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parent) {
		if (flatten) {
			if (parent instanceof TreeParent) {
				TreeParent item = (TreeParent) parent;
				
				if (item.getParent() == null) {
					// item is root, return all types
					return findAllTypes(item).toArray();
				}
				else if (item.isType()) {
					// item is a type, return only attributes
					Object[] children = getFilteredChildren(item);
					List<SchemaItem> attributes = new ArrayList<SchemaItem>();
					
					for (Object child : children) {
						if (child instanceof SchemaItem && ((SchemaItem) child).isAttribute()) {
							attributes.add((SchemaItem) child);
						}
					}
					
					return attributes.toArray();
				}
			}
			
			return getFilteredChildren(parent);
		}
		else {
			return getFilteredChildren(parent);
		}
	}
	
	private Object[] getFilteredChildren(Object parent) {
		if (parent instanceof TreeParent) {
			TreeParent item = (TreeParent) parent;
			
			// suppress property aggregation
			if (suppressAggregation && item.isAttribute()) {
				return EMPTY_CHILDREN;
			}
			
			// suppress inherited properties
			if (suppressInheritedAttributes && item.isType()) {
				Object[] children = super.getChildren(parent);
				List<SchemaItem> attributes = new ArrayList<SchemaItem>();
				String id = item.getDefinition().getIdentifier();
				
				for (Object child : children) {
					if (child instanceof SchemaItem) {
						SchemaItem childItem = (SchemaItem) child;
						
						if (childItem.isAttribute()) {
							Definition definition = ((SchemaItem) child).getDefinition();
							if (definition instanceof AttributeDefinition &&
									((AttributeDefinition) definition).getDeclaringType().getIdentifier().equals(id)) {
								attributes.add((SchemaItem) child);
							}
						}
						else {
							attributes.add(childItem);
						}
					}
				}
				
				return attributes.toArray();
			}
		}
		
		return super.getChildren(parent);
	}
	
	/**
	 * @see ModelContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent) {
			TreeParent item = (TreeParent) parent;
			
			// suppress property aggregation
			if (suppressAggregation && item.isAttribute()) {
				return false;
			}
			
			// suppress inherited properties
			if (suppressInheritedAttributes && item.isType()) {
				return getChildren(parent).length > 0;
			}
		}
		
		if (flatten) {
			return getChildren(parent).length > 0;
		}
		else {
			return super.hasChildren(parent);
		}
	}

	/**
	 * @see ModelContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object child) {
		if (flatten) {
			if (child instanceof TreeObject) {
				TreeObject item = (TreeObject) child;
				if (item.isType()) {
					return findRoot(item);
				}
			}
			
			return super.getParent(child);
		}
		else {
			return super.getParent(child);
		}
	}

	/**
	 * Find all type schema items  
	 * 
	 * @param root the root item
	 * 
	 * @return the type schema items
	 */
	private static Set<SchemaItem> findAllTypes(SchemaItem root) {
		Set<SchemaItem> items = new TreeSet<SchemaItem>();
		
		Queue<SchemaItem> queue = new LinkedList<SchemaItem>();
		queue.add(root);
		
		while (!queue.isEmpty()) {
			SchemaItem item = queue.poll();
			
			if (item.isType()) {
				items.add(item);
			}
			
			for (SchemaItem child : item.getChildren()) {
				if (!child.isAttribute()) {
					queue.add(child);
				}
			}
		}
		
		return items;
	}
	
	/**
	 * Get the schema root
	 * 
	 * @param item a schema item
	 * 
	 * @return the schema root
	 */
	private static SchemaItem findRoot(SchemaItem item) {
		while (item.getParent() != null) {
			item = item.getParent();
		}
		
		return item;
	}
	
}