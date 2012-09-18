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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.selection.SchemaSelectionHelper;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Mapping view.
 * 
 * @author Simon Templer
 */
public class MappingView extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping";

	private ISelectionListener selectionListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		super.createViewControl(parent);

		getSite().getWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(selectionListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						if (!(selection instanceof SchemaSelection)) {
							// only react on schema selections
							return;
						}

						if (part != MappingView.this) {
							update((SchemaSelection) selection);
						}
					}
				});

		SchemaSelection current = SchemaSelectionHelper.getSchemaSelection();
		if (current != null) {
			update(current);
		}
	}

	/**
	 * Update the view
	 * 
	 * @param selection the selection
	 */
	protected void update(SchemaSelection selection) {
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);
		Alignment alignment = as.getAlignment();

		List<Cell> cells = new ArrayList<Cell>();

		Set<EntityDefinition> sourceItems;
		Set<EntityDefinition> targetItems;

		if (selection instanceof IStructuredSelection) {
			// prefer getting information from the IStructuredSelection, which
			// from
			// the Schema Explorer only contains the recently selected elements
			// on one side
			sourceItems = new HashSet<EntityDefinition>();
			targetItems = new HashSet<EntityDefinition>();

			for (Object object : ((IStructuredSelection) selection).toArray()) {
				if (object instanceof EntityDefinition) {
					EntityDefinition def = (EntityDefinition) object;
					switch (def.getSchemaSpace()) {
					case TARGET:
						targetItems.add(def);
						break;
					case SOURCE:
					default:
						sourceItems.add(def);
					}
				}
			}
		}
		else {
			sourceItems = selection.getSourceItems();
			targetItems = selection.getTargetItems();
		}

		// find cells associated with the selection
		for (Cell cell : alignment.getCells()) {
			if ((cell.getSource() != null && associatedWith(cell.getSource(), sourceItems))
					|| associatedWith(cell.getTarget(), targetItems)) {
				cells.add(cell);
			}
		}

		getViewer().setInput(cells);
	}

	private boolean associatedWith(ListMultimap<String, ? extends Entity> entities,
			Set<EntityDefinition> entityDefs) {
		for (Entity entity : entities.values()) {
			if (entityDefs.contains(entity.getDefinition())) {
				return true;
			}
			// XXX also add parent type cells?
		}

		return false;
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(selectionListener);
		}

		super.dispose();
	}

}
