/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.model.filtering;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.views.model.TreeObject;
import eu.esdihumboldt.hale.rcp.views.model.TreeParent;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * This {@link ViewerFilter} allows the user to type in any search string to 
 * filter the items shown in the Schema Explorer.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class PatternViewFilter 
	extends ViewerFilter {
	
	private Set<TreeObjectType> attribute_filters = new HashSet<TreeObjectType>();

	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof TreeObject || element instanceof TreeParent) {
			TreeObject to = (TreeObject) element;
			// the root node always remains visible.
			if (to.getType().equals(TreeObjectType.ROOT)) {
				return true;
			}
			// other nodes are filtered.
			// TODO find a way of retaining nodes even when they are under a filtered node.
			if (this.attribute_filters.contains(to.getType())) {
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	public void addAttributeFilter(TreeObjectType filterName) {
		this.attribute_filters.add(filterName);
	}
	
	public void removeAttributeFilter(TreeObjectType filterName) {
		this.attribute_filters.remove(filterName);
	}

}
