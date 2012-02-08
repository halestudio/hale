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

package eu.esdihumboldt.hale.io.html;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.util.Identifiers;

/**
 * Basic class for all cell infos
 * 
 * @author Kevin Mais
 */
public class CellInfo implements ICellInfo {
	
	private final Cell cell;
	private String subDir;
	Identifiers<Cell> cellIds;
	private String id;

	/**
	 * Constructor for a cell info
	 * 
	 * @param cell a cell the created cell info is associated with
	 * @param cellId the cell id
	 * @param subDir the sub directory where files will be created
	 */
	public CellInfo(Cell cell, String cellId, String subDir) {
		this.cell = cell;
		this.id = cellId;
		this.subDir = subDir;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.html.ICellInfo#getImageLocation()
	 */
	@Override
	public String getImageLocation() {
		return subDir + "/" + "img_" + getId() + ".png";
	}
	
	/**
	 * Returns the unique id for a cell
	 * 
	 * @return the unique id for the cell
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}


}
