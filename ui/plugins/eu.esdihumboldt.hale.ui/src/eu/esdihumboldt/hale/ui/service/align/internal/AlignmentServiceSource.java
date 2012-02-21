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

package eu.esdihumboldt.hale.ui.service.align.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * Provides UI variables related to the {@link AlignmentService}
 *
 * @author Simon Templer
 * @since 2.5
 */
public class AlignmentServiceSource extends AbstractSourceProvider {

	/**
	 * The name of the variable which value is <code>true</code> if there
	 * are cells present in the alignment service
	 */
	public static final String HAS_CELLS = "hale.alignment.has_cells";
	
	private AlignmentServiceListener alignmentListener;
	
	/**
	 * Default constructor
	 */
	public AlignmentServiceSource() {
		super();
		
		final AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				cellRemoved(null);
			}

			@Override
			public void cellRemoved(Cell cell) {
				fireSourceChanged(ISources.WORKBENCH, HAS_CELLS, hasCells(as));
			}

			@Override
			public void cellReplaced(Cell oldCell, Cell newCell) {
				// no change to has_cells
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				fireSourceChanged(ISources.WORKBENCH, HAS_CELLS, true);
			}

		});
	}

	/**
	 * @see ISourceProvider#dispose()
	 */
	@Override
	public void dispose() {
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.removeListener(alignmentListener);
	}

	/**
	 * @see ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(HAS_CELLS, hasCells(as));
		
		return result;
	}

	private static boolean hasCells(AlignmentService as) {
		return !as.getAlignment().getCells().isEmpty();
	}

	/**
	 * @see ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames() {
		return new String[]{
				HAS_CELLS};
	}

}
