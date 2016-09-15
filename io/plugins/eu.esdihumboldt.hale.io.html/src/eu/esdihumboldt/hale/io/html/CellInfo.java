/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.html;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.util.Identifiers;

/**
 * Basic class for all cell information
 * 
 * @author Kevin Mais
 * @author Patrick Lieb
 */
public class CellInfo implements ICellInfo {

	/**
	 * Identify the cell as source or target
	 * 
	 * @author Patrick Lieb
	 */
	private enum CellType {
		/**  */
		SOURCE,
		/**  */
		TARGET
	}

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
	 * @param cell a cell the created cell info is associated with
	 * @param cellIds the cell identifier
	 * @param subDir the sub directory where files will be created
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
		return getExplanationInternal(false);
	}

	/**
	 * @see ICellInfo#getExplanationAsHtml
	 */
	@Override
	public String getExplanationAsHtml() {
		return getExplanationInternal(true);
	}

	private String getExplanationInternal(boolean asHtml) {
		if (cellExpl == null) {
			// determine cell explanation
			FunctionDefinition<?> function = FunctionUtil.getFunction(
					cell.getTransformationIdentifier(), null);
			if (function != null) {
				cellExpl = function.getExplanation();
				if (cellExpl == null) {
					return null;
				}
			}
			else
				return null;
		}
		if (asHtml) {
			return cellExpl.getExplanationAsHtml(cell, HaleUI.getServiceProvider());
		}
		else {
			return cellExpl.getExplanation(cell, HaleUI.getServiceProvider());
		}
	}

	/**
	 * @see ICellInfo#getImageLocation()
	 */
	@Override
	public String getImageLocation() {
		return subDir + "/" + "img_" + getId() + ".png";
	}

	/**
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
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}

	@Override
	public String getNotes() {
		List<String> docs = getCell().getDocumentation().get(null);
		if (!docs.isEmpty()) {
			String notes = docs.get(0);
			if (notes != null && !notes.isEmpty()) {
				return notes;
			}
		}
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.html.ICellInfo#getCompleteSourceName()
	 */
	@Override
	public String getCompleteSourceName() {
		// TODO Auto-generated method stub
		return getName(CellType.SOURCE, true);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.html.ICellInfo#getCompleteTargetName()
	 */
	@Override
	public String getCompleteTargetName() {
		// TODO Auto-generated method stub
		return getName(CellType.TARGET, true);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.html.ICellInfo#getSourceName()
	 */
	@Override
	public String getSourceName() {
		return getName(CellType.SOURCE, false);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.html.ICellInfo#getTargetName()
	 */
	@Override
	public String getTargetName() {
		return getName(CellType.TARGET, false);
	}

	private String getName(CellType cellType, boolean fullName) {
		Iterator<? extends Entity> iterator;
		ListMultimap<String, ? extends Entity> entities;
		PropertyDefinition child = null;
		switch (cellType) {
		case SOURCE:
			if ((entities = getCell().getSource()) == null)
				return null;
			iterator = entities.values().iterator();
			break;
		case TARGET:
			if ((entities = getCell().getTarget()) == null)
				return null;
			iterator = entities.values().iterator();
			break;
		default:
			return null;
		}
		StringBuffer sb = new StringBuffer();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (fullName) {
				for (ChildContext childContext : entity.getDefinition().getPropertyPath()) {
					child = childContext.getChild().asProperty();
					if (child != null) {
						sb.append(child.getDisplayName());
						sb.append(".");
					}
				}
				sb.append(entity.getDefinition().getDefinition().getDisplayName());
				sb.append(",\n");
			}
			else {
				sb.append(entity.getDefinition().getDefinition().getDisplayName());
				sb.append(", ");
			}
		}
		String result = sb.toString();

		if (fullName)
			return result.substring(0, result.lastIndexOf(",\n"));
		else
			return result.substring(0, result.lastIndexOf(","));
	}
}
