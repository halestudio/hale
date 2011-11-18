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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Represents a source type, group or property
 * @author Simon Templer
 */
public interface SourceNode {
	
	/**
	 * Get the associated definition
	 * @return the type, group or property definition
	 */
	public Definition<?> getDefinition();
	
	/**
	 * Get the group or property's parent
	 * @return the parent node or <code>null</code>
	 */
	public SourceNode getParent();

}
