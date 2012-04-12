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

import javax.xml.namespace.QName;

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
	 * @param name the property name the instance is a value of, 
	 *   <code>null</code> if it is the traversal root
	 * @return if traversal shall be continued
	 */
	public boolean visit(Instance instance, QName name);
	
	/**
	 * Visit a group that is not an {@link Instance}.
	 * @param group the group
	 * @param name the property name the group is a value of, 
	 *   <code>null</code> if it is the traversal root
	 * @return if traversal shall be continued
	 */
	public boolean visit(Group group, QName name);
	
	/**
	 * Visit a value that is neither {@link Instance} nor {@link Group}.
	 * @param value the value
	 * @param name the property name the object is a value of, 
	 *   <code>null</code> if it is the traversal root
	 * @return if traversal shall be continued
	 */
	public boolean visit(Object value, QName name);
	
}
