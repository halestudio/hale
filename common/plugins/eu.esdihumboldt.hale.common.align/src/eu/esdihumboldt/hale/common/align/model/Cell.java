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

package eu.esdihumboldt.hale.common.align.model;

import com.google.common.collect.ListMultimap;

/**
 * An alignment cell represents a mapping between two entities
 * 
 * @author Simon Templer
 */
public interface Cell {

	/**
	 * Get the source entities. For each the name is mapped to the entity.
	 * Multiple entities may share the same name. The map may not be modified.
	 * 
	 * @return the source entities, may be <code>null</code>
	 */
	public ListMultimap<String, ? extends Entity> getSource();

	/**
	 * Get the target entities. For each the name is mapped to the entity.
	 * Multiple entities may share the same name. The map may not be modified.
	 * 
	 * @return the target entities
	 */
	public ListMultimap<String, ? extends Entity> getTarget();

	/**
	 * Get the transformation parameters that shall be applied to the
	 * transformation specified by {@link #getTransformationIdentifier()}.
	 * 
	 * @return the transformation parameters, parameter names are mapped to
	 *         parameter values, may be <code>null</code>
	 */
	public ListMultimap<String, String> getTransformationParameters();

	/**
	 * Get the identifier for the transformation referenced by the cell.
	 * 
	 * @return the transformation identifier
	 */
	public String getTransformationIdentifier();

}
