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

package eu.esdihumboldt.hale.ui.views.schemas.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.model.schema.TreeObject;
import eu.esdihumboldt.hale.ui.model.schema.TreeParent;

/**
 * Content provider that lists all feature types in a list
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FlatContentProvider extends InheritanceContentProvider {

	/**
	 * @see InheritanceContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			TreeParent item = (TreeParent) parent;
			
			if (item.getParent() == null) {
				// item is root, return all types
				return findAllTypes(item).toArray();
			}
			else if (item.isType()) {
				// item is a type, return only attributes
				Object[] children = super.getChildren(item);
				List<SchemaItem> attributes = new ArrayList<SchemaItem>();
				
				for (Object child : children) {
					if (child instanceof SchemaItem && ((SchemaItem) child).isAttribute()) {
						attributes.add((SchemaItem) child);
					}
				}
				
				return attributes.toArray();
			}
		}
		
		return super.getChildren(parent);
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
	 * @see InheritanceContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parent) {
		return getChildren(parent).length > 0;
	}

	/**
	 * @see ModelContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			TreeObject item = (TreeObject) child;
			if (item.isType()) {
				return findRoot(item);
			}
		}
		
		return super.getParent(child);
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
