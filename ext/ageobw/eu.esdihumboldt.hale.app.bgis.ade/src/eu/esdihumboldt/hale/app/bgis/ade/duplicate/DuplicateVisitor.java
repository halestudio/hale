/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.duplicate;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppConstants;
import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppUtil;
import eu.esdihumboldt.hale.app.bgis.ade.common.EntityVisitor;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Entity visitor that creates cells from example cells.
 * 
 * @author Simon Templer
 */
public class DuplicateVisitor extends EntityVisitor implements BGISAppConstants {

	/**
	 * The created cells.
	 */
	private final List<MutableCell> cells = new ArrayList<MutableCell>();

	private final Multimap<String, Cell> exampleCells;

	private final String cellNote;

	/**
	 * Create an example cell visitor creating derived cells.
	 * 
	 * @param exampleCells the example cells, with the target ADE property name
	 *            as key
	 * @param cellNote note to append to generated cell's notes
	 */
	public DuplicateVisitor(Multimap<String, Cell> exampleCells, String cellNote) {
		this.exampleCells = exampleCells;
		this.cellNote = cellNote;
	}

	@Override
	protected boolean visit(PropertyEntityDefinition ped) {
		if (ADE_NS.equals(ped.getDefinition().getName().getNamespaceURI())) {
			// property is from ADE

			for (Cell exampleCell : exampleCells.get(ped.getDefinition().getName().getLocalPart())) {
				// handle each example cell

				// copy cell
				DefaultCell cell = new DefaultCell(exampleCell);
				// reset ID
				cell.setId(null);
				// assign new target
				ListMultimap<String, Entity> target = ArrayListMultimap.create();
				target.put(cell.getTarget().keys().iterator().next(), new DefaultProperty(ped));
				cell.setTarget(target);

				BGISAppUtil.appendNote(cell, cellNote);

				cells.add(cell);
			}

			return true;
		}

		return false;
	}

	/**
	 * Get the created cells.
	 * 
	 * @return the cells assigning default values
	 */
	public List<MutableCell> getCells() {
		return cells;
	}

}
