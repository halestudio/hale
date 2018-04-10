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

package eu.esdihumboldt.hale.ui.service.align.internal.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigrator;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.migrate.impl.DefaultAlignmentMigrator;
import eu.esdihumboldt.hale.common.align.migrate.impl.MigrationOptionsImpl;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.report.impl.SimpleReporter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.migrate.UserMigration;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Handler for replacing entities of a cell / cells.
 * 
 * @author Simon Templer
 */
public abstract class ReplaceEntitiesHandler extends AbstractHandler {

	private final SchemaSpaceID schemaSpace;

	/**
	 * Create a new handler for replacing cell entities.
	 * 
	 * @param schemaSpace the schema space where to replace entities
	 */
	public ReplaceEntitiesHandler(SchemaSpaceID schemaSpace) {
		this.schemaSpace = schemaSpace;
	}

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// collect cells from selection
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection) {
			List<?> list = ((IStructuredSelection) selection).toList();
			// create dummy alignment
			DefaultAlignment dummy = new DefaultAlignment();
			for (Object object : list) {
				if (object instanceof MutableCell) {
					dummy.addCell((MutableCell) object);
					// FIXME what about others?
				}
			}

			/*
			 * Replace entities on cells
			 */
			SimpleReporter reporter = new SimpleReporter("Replace entities for cells", null, false);
			try {
				// create migrator
				AlignmentMigrator migrator = new DefaultAlignmentMigrator(
						HaleUI.getServiceProvider());
				AlignmentMigration migration = new UserMigration(schemaSpace);

				MigrationOptions options = new MigrationOptionsImpl(
						schemaSpace.equals(SchemaSpaceID.SOURCE),
						schemaSpace.equals(SchemaSpaceID.TARGET), false);
				Alignment updated = migrator.updateAligmment(dummy, migration, options, reporter);

				AlignmentService as = HaleUI.getServiceProvider()
						.getService(AlignmentService.class);
				Map<Cell, MutableCell> replacements = new HashMap<>();
				for (Cell newCell : updated.getCells()) {
					Cell oldCell = dummy.getCell(newCell.getId());
					if (oldCell == null) {
						reporter.error("No original cell with ID {0} found", newCell.getId());
					}
					else {
						// TODO detect where there has been no change?

						replacements.put(oldCell, (MutableCell) newCell);
					}
				}
				as.replaceCells(replacements);

				reporter.setSuccess(true);
			} catch (Throwable e) {
				reporter.error("Fatal error when trying to replace entities", e);
				reporter.setSuccess(false);
			} finally {
				HaleUI.getServiceProvider().getService(ReportService.class).addReport(reporter);
			}
		}

		return null;
	}

}
