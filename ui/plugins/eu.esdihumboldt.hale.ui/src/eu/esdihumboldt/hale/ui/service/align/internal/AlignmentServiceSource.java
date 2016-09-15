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
	 * The name of the variable which value is <code>true</code> if there are
	 * cells present in the alignment service
	 */
	public static final String HAS_CELLS = "hale.alignment.has_cells";

	private AlignmentServiceListener alignmentListener;

	/**
	 * Default constructor
	 */
	public AlignmentServiceSource() {
		super();

		final AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				cellsRemoved(null);
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				fireSourceChanged(ISources.WORKBENCH, HAS_CELLS, hasCells(as));
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				// no change to has_cells
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				fireSourceChanged(ISources.WORKBENCH, HAS_CELLS, true);
			}

			@Override
			public void alignmentChanged() {
				fireSourceChanged(ISources.WORKBENCH, HAS_CELLS, hasCells(as));
			}

			@Override
			public void customFunctionsChanged() {
				// custom functions don't affect source currently
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				// no properties that affect source
			}

		});
	}

	/**
	 * @see ISourceProvider#dispose()
	 */
	@Override
	public void dispose() {
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.removeListener(alignmentListener);
	}

	/**
	 * @see ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState() {
		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);

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
		return new String[] { HAS_CELLS };
	}

}
