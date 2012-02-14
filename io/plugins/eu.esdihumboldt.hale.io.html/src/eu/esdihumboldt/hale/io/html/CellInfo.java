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

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.util.Identifiers;

/**
 * Basic class for all cell infos
 * 
 * @author Kevin Mais
 */
public class CellInfo implements ICellInfo {

	private final Cell cell;
	
	/**
	 * the sub directory where the files will be created
	 */
	protected final String subDir;
	
	/**
	 * the cell identifier
	 */
	protected final Identifiers<Cell> cellIds;
	
	private CellExplanation cellExpl;

	/**
	 * Constructor for a cell info
	 * 
	 * @param cell
	 *            a cell the created cell info is associated with
	 * @param cellIds
	 *            the cell identifier
	 * @param subDir
	 *            the sub directory where files will be created
	 */
	public CellInfo(Cell cell, Identifiers<Cell> cellIds, String subDir) {
		this.cell = cell;
		this.cellIds = cellIds;
		this.subDir = subDir;
	}

	/**
	 * @see ICellInfo#getExplanation()
	 */
	@Override
	public String getExplanation() {
		if (cellExpl == null) {
			// determine cell explanation
			AbstractFunction<?> function = FunctionUtil.getFunction(cell.getTransformationIdentifier());
			if (function != null) {
				cellExpl = function.getExplanation();
				if(cellExpl == null) {
					return null;
				}
			}
		}
		
		return cellExpl.getExplanation(cell);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.html.ICellInfo#getExplanationAsHtml
	 */
	@Override
	public String getExplanationAsHtml() {
		if (cellExpl == null) {
			// determine cell explanation
			AbstractFunction<?> function = FunctionUtil.getFunction(cell.getTransformationIdentifier());
			if (function != null) {
				cellExpl = function.getExplanation();
				if(cellExpl == null) {
					return null;
				}
			}
		}
		
		return cellExpl.getExplanationAsHtml(cell);
	}
	
	/**
	 * @see ICellInfo#getImageLocation()
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
		if (cell != null) {
			String id = cellIds.getId(cell);
			return id;
		}
		return null;
	}

	/**
	 * Getter for the cell
	 * 
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}

}
