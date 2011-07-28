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

package eu.esdihumboldt.hale.align.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.align.model.Alignment;
import eu.esdihumboldt.hale.align.model.MutableAlignment;
import eu.esdihumboldt.hale.align.model.MutableCell;

/**
 * Default alignment implementation
 * @author Simon Templer
 */
public class DefaultAlignment implements Alignment, MutableAlignment {
	
	//FIXME simple collection should be replaced later on
	private final Collection<MutableCell> cells = new ArrayList<MutableCell>();

	/**
	 * @see MutableAlignment#addCell(MutableCell)
	 */
	@Override
	public void addCell(MutableCell cell) {
		cells.add(cell);
	}
	
	/**
	 * @see Alignment#getCells()
	 */
	@Override
	public Collection<MutableCell> getCells() {
		return Collections.unmodifiableCollection(cells);
	}

}
