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
 * Mutable {@link Cell} which is used where changes to the cell are allowed.
 * 
 * @author Simon Templer
 */
public interface MutableCell extends Cell {

	/**
	 * Set the identifier for the transformation referenced by the cell.
	 * 
	 * @param transformation the transformation identifier
	 */
	public void setTransformationIdentifier(String transformation);

	/**
	 * @param parameters the parameters to set
	 */
	public void setTransformationParameters(ListMultimap<String, String> parameters);

	/**
	 * @param source the source to set
	 */
	public void setSource(ListMultimap<String, ? extends Entity> source);

	/**
	 * @param target the target to set
	 */
	public void setTarget(ListMultimap<String, ? extends Entity> target);

}