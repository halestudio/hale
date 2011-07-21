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
 * An alignment contains alignment cells 
 * @author Simon Templer
 */
public interface Alignment {
	
	/**
	 * Get the collection of cells contained in the alignment.
	 * @return the alignment cells
	 */
	public Collection<? extends Cell> getCells();
	
	//TODO get cells by involved types?!
	
	// getTypeCells(TypeEntityDefinition)
	
	// getTypeCells(TypeEntityDefinition, TypeEntityDefinition)
	
	// getPropertyCells(TypeEntityDefinition)
	
	// getPropertyCells(PropertyEntityDefinition)
	
	// getPropertyCells(TypeEntityDefinition, TypeEntityDefinition)

}
