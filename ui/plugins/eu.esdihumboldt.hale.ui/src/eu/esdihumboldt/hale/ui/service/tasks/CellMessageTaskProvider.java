/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.service.tasks;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.tasks.AbstractTaskProvider;
import eu.esdihumboldt.hale.common.tasks.CellTaskFactory;
import eu.esdihumboldt.hale.common.tasks.TaskService;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;

/**
 * Task provider for cell message annotations
 * 
 * @author Florian Esser
 */
public class CellMessageTaskProvider extends AbstractTaskProvider {

	private final CellTaskFactory taskFactory;
	private AlignmentService alignmentService;

	/**
	 * Default constructor
	 */
	public CellMessageTaskProvider() {
		super();
		addFactory(taskFactory = new CellTaskFactory());
	}

	@Override
	protected void doActivate(TaskService taskService) {
		alignmentService = HaleUI.getServiceProvider().getService(AlignmentService.class);

		// create tasks from the current schema
		generateTasks(taskService);

		// create tasks when cells have been removed
		alignmentService.addListener(new AlignmentServiceAdapter() {

			@Override
			public void alignmentChanged() {
				generateTasks(taskService);
			}

			@Override
			public void alignmentCleared() {
				taskService.clearTasks();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				generateTasks(taskService);
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				for (Cell cell : cells) {
					generateTasks(cell, taskService);
				}
			}
		});
	}

	private void generateTasks(TaskService taskService) {
		alignmentService.getAlignment().getActiveTypeCells().stream()
				.forEach(c -> generateTasks(c, taskService));
	}

	private void generateTasks(Cell cell, TaskService taskService) {
		taskService.addTasks(taskFactory.createTasks(cell));
	}
}
