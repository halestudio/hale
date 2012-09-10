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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.util.Identifiers;

/**
 * The type cell info for a type cell
 * 
 * @author Kevin Mais
 */
public class TypeCellInfo extends CellInfo implements ICellInfo {

	private final Alignment align;

	/**
	 * Constructor for a type cell info
	 * 
	 * @param cell the type cell
	 * @param align the alignment
	 * @param cellIds the cell identifier
	 * @param subDir the sub directory where files will be created
	 */
	public TypeCellInfo(Cell cell, Alignment align, Identifiers<Cell> cellIds, String subDir) {
		super(cell, cellIds, subDir);
		this.align = align;
	}

	/**
	 * Returns a collection of property cells info
	 * 
	 * @return property cells info, may be <code>null</code>
	 */
	public Collection<ICellInfo> getPropertyCellsInfo() {
		Collection<ICellInfo> propCellInfo = new ArrayList<ICellInfo>();
		Collection<? extends Cell> propCells = AlignmentUtil.getPropertyCellsFromTypeCell(align,
				getCell());

		Iterator<? extends Cell> it = propCells.iterator();

		while (it.hasNext()) {
			Cell propCell = it.next();
			propCellInfo.add(new CellInfo(propCell, cellIds, subDir));
		}

		return propCellInfo;

	}

	/**
	 * Returns the displayed name of the source cell(s)
	 * 
	 * @return the name of the source cell(s)
	 */
	public String getSourceName() {
		Iterator<? extends Entity> it = getCell().getSource().values().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			Entity entity = it.next();
			sb.append(entity.getDefinition().getDefinition().getDisplayName());
			sb.append(", ");
		}
		String result = sb.toString();

		return result.substring(0, result.lastIndexOf(","));
	}

	/**
	 * Returns the displayed name of the target cell(s)
	 * 
	 * @return the name of the target cell(s)
	 */
	public String getTargetName() {
		Iterator<? extends Entity> it = getCell().getTarget().values().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			Entity entity = it.next();
			sb.append(entity.getDefinition().getDefinition().getDisplayName());
			sb.append(", ");
		}
		String result = sb.toString();

		return result.substring(0, result.lastIndexOf(","));
	}

}
