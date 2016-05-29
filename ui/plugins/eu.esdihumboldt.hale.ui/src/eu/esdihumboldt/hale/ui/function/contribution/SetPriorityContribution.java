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

package eu.esdihumboldt.hale.ui.function.contribution;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.ui.function.contribution.internal.SetPriorityAction;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerUtil;

/**
 * Contribution item offering possibilities to set a cell's priority.
 * 
 * @author Andrea Antonello
 */
public class SetPriorityContribution extends ContributionItem {

	private final Cell originalCell;

	/**
	 * Constructor
	 * 
	 * @param originalCell the original cell
	 */
	public SetPriorityContribution(Cell originalCell) {
		super();

		this.originalCell = originalCell;
	}

	/**
	 * Default constructor. Uses the first cell in the current
	 * {@link IStructuredSelection}.
	 */
	public SetPriorityContribution() {
		this(null);
	}

	/**
	 * @see AbstractFunctionWizardContribution#fill(ToolBar, int)
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		if (getOriginalCell() == null)
			return;
		super.fill(parent, index);
	}

	/**
	 * @see AbstractFunctionWizardContribution#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		if (getOriginalCell() == null)
			return;

		AlignmentService alignmentService = PlatformUI.getWorkbench()
				.getService(AlignmentService.class);

		Cell cell = getOriginalCell();
		Priority oldPriority = cell.getPriority();
		for (Priority priority : Priority.values()) {
			if (priority != oldPriority) {
				SetPriorityAction setPriorityAction = new SetPriorityAction(priority, cell.getId(),
						alignmentService);
				IContributionItem item = new ActionContributionItem(setPriorityAction);
				item.fill(menu, index++);
			}
		}
	}

	/**
	 * Get the cell
	 * 
	 * @return the cell
	 */
	public Cell getOriginalCell() {
		if (this.originalCell != null) {
			return originalCell;
		}

		// retrieve first selected cell
		IStructuredSelection sel = SelectionTrackerUtil.getTracker()
				.getSelection(IStructuredSelection.class);
		for (Object object : sel.toList()) {
			if (object instanceof Cell) {
				return (Cell) object;
			}
		}
		return null;
	}

}
