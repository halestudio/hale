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

package eu.esdihumboldt.hale.ui.service.align.internal;

import java.util.Map;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.align.BaseAlignmentLoader;

/**
 * Alignment service decorator
 * 
 * @author Simon Templer
 */
public abstract class AlignmentServiceDecorator implements AlignmentService {

	/**
	 * The decorated alignment service
	 */
	protected final AlignmentService alignmentService;

	/**
	 * Create a decorator for the given alignment service
	 * 
	 * @param alignmentService the alignment service
	 */
	public AlignmentServiceDecorator(AlignmentService alignmentService) {
		super();
		this.alignmentService = alignmentService;
	}

	/**
	 * @see AlignmentService#getAlignment()
	 */
	@Override
	public Alignment getAlignment() {
		return alignmentService.getAlignment();
	}

	/**
	 * @see AlignmentService#addOrUpdateAlignment(MutableAlignment)
	 */
	@Override
	public void addOrUpdateAlignment(MutableAlignment alignment) {
		alignmentService.addOrUpdateAlignment(alignment);
	}

	/**
	 * @see AlignmentService#addCell(MutableCell)
	 */
	@Override
	public void addCell(MutableCell cell) {
		alignmentService.addCell(cell);
	}

	/**
	 * @see AlignmentService#removeCells(Cell[])
	 */
	@Override
	public void removeCells(Cell... cells) {
		alignmentService.removeCells(cells);
	}

	/**
	 * @see AlignmentService#clean()
	 */
	@Override
	public void clean() {
		alignmentService.clean();
	}

	/**
	 * @see AlignmentService#addListener(AlignmentServiceListener)
	 */
	@Override
	public void addListener(AlignmentServiceListener listener) {
		alignmentService.addListener(listener);
	}

	/**
	 * @see AlignmentService#removeListener(AlignmentServiceListener)
	 */
	@Override
	public void removeListener(AlignmentServiceListener listener) {
		alignmentService.removeListener(listener);
	}

	/**
	 * @see AlignmentService#replaceCell(Cell, MutableCell)
	 */
	@Override
	public void replaceCell(Cell oldCell, MutableCell newCell) {
		alignmentService.replaceCell(oldCell, newCell);
	}

	/**
	 * @see AlignmentService#replaceCells(Map)
	 */
	@Override
	public void replaceCells(Map<? extends Cell, MutableCell> cells) {
		alignmentService.replaceCells(cells);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentService#addBaseAlignment(eu.esdihumboldt.hale.ui.service.align.BaseAlignmentLoader)
	 */
	@Override
	public boolean addBaseAlignment(BaseAlignmentLoader loader) {
		return alignmentService.addBaseAlignment(loader);
	}

	@Override
	public void addCustomPropertyFunction(CustomPropertyFunction function) {
		alignmentService.addCustomPropertyFunction(function);
	}

	@Override
	public void removeCustomPropertyFunction(String id) {
		alignmentService.removeCustomPropertyFunction(id);
	}

}
