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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * View displaying the current alignment
 * @author Simon Templer
 */
public class AlignmentView extends AbstractMappingView {

	private AlignmentServiceListener alignmentListener;

	/**
	 * @see AbstractMappingView#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		update();
		
		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				update();
			}

			@Override
			public void cellRemoved(Cell cell) {
				update();
			}

			@Override
			public void cellsUpdated(Iterable<Cell> cells) {
				update();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				update();
			}
		});
	}

	/**
	 * Set the current alignment
	 */
	private void update() {
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		getViewer().setInput(as.getAlignment());
		getViewer().applyLayout();
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (alignmentListener != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.removeListener(alignmentListener);
		}
		
		super.dispose();
	}

}
