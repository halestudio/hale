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

package eu.esdihumboldt.cst.extension.hooks;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook.TreeState;

/**
 * Factory for {@link TransformationTreeHook}s.
 * 
 * @author Simon Templer
 */
public interface TransformationTreeHookFactory extends
		ExtensionObjectFactory<TransformationTreeHook> {

	/**
	 * Get the tree state associated to the hook.
	 * 
	 * @return the tree state
	 */
	public TreeState getTreeState();

}
