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

package eu.esdihumboldt.hale.ui.function.contribution.internal;

import org.eclipse.jface.action.Action;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Action to set the {@link Priority} of a {@link Cell}.
 * 
 * @author Andrea Antonello
 */
public class SetPriorityAction extends Action {

	private final Priority _priority;
	private final String _cellID;
	private final AlignmentService _alignmentService;

	/**
	 * @param priority the {@link Priority} to set.
	 * @param cellID the id of the {@link Cell} to modify.
	 * @param alignmentService the service to use to set the property.
	 */
	public SetPriorityAction(Priority priority, String cellID, AlignmentService alignmentService) {
		_priority = priority;
		_cellID = cellID;
		_alignmentService = alignmentService;
	}

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return _priority.value();
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		_alignmentService.setCellProperty(_cellID, Cell.PROPERTY_PRIORITY, _priority);
	}
}
