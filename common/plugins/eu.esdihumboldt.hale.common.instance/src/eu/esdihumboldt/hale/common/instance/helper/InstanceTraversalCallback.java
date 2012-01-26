/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.instance.helper;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Callback for {@link InstanceTraverser}s.
 * @author Simon Templer
 */
public interface InstanceTraversalCallback {
	
	/**
	 * Visit an instance.
	 * @param instance the instance
	 * @return if traversal shall be continued
	 */
	public boolean visit(Instance instance);
	
	/**
	 * Visit a group that is not an {@link Instance}.
	 * @param group the group
	 * @return if traversal shall be continued
	 */
	public boolean visit(Group group);
	
	/**
	 * Visit a value that is neither {@link Instance} nor {@link Group}.
	 * @param value the value
	 * @return if traversal shall be continued
	 */
	public boolean visit(Object value);

}
