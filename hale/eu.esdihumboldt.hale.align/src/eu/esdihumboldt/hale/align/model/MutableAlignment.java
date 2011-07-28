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

package eu.esdihumboldt.hale.align.model;

import java.util.Collection;


/**
 * Mutable {@link Alignment} which is used where changes to the alignment are
 * allowed.
 * @author Simon Templer
 */
public interface MutableAlignment extends Alignment {

	/**
	 * Add a cell to the alignment
	 * @param cell the cell to add
	 */
	public void addCell(MutableCell cell);
	
	/**
	 * @see Alignment#getCells()
	 */
	@Override
	public Collection<? extends MutableCell> getCells();

}