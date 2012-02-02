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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
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
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class MappingView extends AbstractMappingView {

	private ISelectionListener selectionListener;

	/**
	 * @see AbstractMappingView#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener = new ISelectionListener() {
			
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
	}

	/**
	 * Update the view
	 * @param selection the selection
	 */
	protected void update(SchemaSelection selection) {
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		Alignment alignment = as.getAlignment();
		
		List<Cell> cells = new ArrayList<Cell>(); 
		
		// find cells associated with the selection
		for (Cell cell : alignment.getCells()) {
			if ((cell.getSource() != null && associatedWith(cell.getSource(), selection.getSourceItems()))
					|| associatedWith(cell.getTarget(), selection.getTargetItems())) {
				cells.add(cell);
			}
		}
		
		getViewer().setInput(cells);
	}

	private boolean associatedWith(
			ListMultimap<String, ? extends Entity> entities,
			Set<EntityDefinition> entityDefs) {
		for (Entity entity : entities.values()) {
			if (entityDefs.contains(entity.getDefinition())) {
				return true;
			}
			//XXX also add parent type cells?
		}
		
		return false;
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);
		}
		
		super.dispose();
	}

}
