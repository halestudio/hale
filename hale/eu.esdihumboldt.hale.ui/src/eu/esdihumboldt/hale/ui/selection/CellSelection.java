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
package eu.esdihumboldt.hale.ui.selection;

import org.eclipse.jface.viewers.StructuredSelection;

import eu.esdihumboldt.hale.ui.model.mapping.CellInfo;

/**
 * Cell selection
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CellSelection extends StructuredSelection {
	
	/**
	 * Creates an empty selection
	 */
	public CellSelection() {
		super();
	}
	
	/**
	 * Creates a selection with one cell
	 * 
	 * @param cell the cell
	 */
	public CellSelection(CellInfo cell) {
		super(cell);
	}
	
	/**
	 * Get the selected cell
	 * 
	 * @return the selected cell or <code>null</code>
	 */
	public CellInfo getCellInfo() {
		return (CellInfo) getFirstElement();
	}

}
