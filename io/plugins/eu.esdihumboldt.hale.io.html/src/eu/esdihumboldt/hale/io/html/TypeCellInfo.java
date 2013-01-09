/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import eu.esdihumboldt.hale.common.align.model.Alignment;
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
		Collection<? extends Cell> propCells = align.getPropertyCells(getCell());

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
