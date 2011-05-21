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

import eu.esdihumboldt.hale.ui.model.schema.TreeParent;

/**
 * Model content provider that suppresses displaying property aggregations
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class InheritanceContentProvider extends ModelContentProvider {

	/**
	 * @see ModelContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent) {
			TreeParent item = (TreeParent) parent;
			if (item.isAttribute()) {
				return false;
			}
		}
		
		return super.hasChildren(parent);
	}

	/**
	 * @see ModelContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			TreeParent item = (TreeParent) parent;
			if (item.isAttribute()) {
				return EMPTY_CHILDREN;
			}
		}
		
		return super.getChildren(parent);
	}

}
