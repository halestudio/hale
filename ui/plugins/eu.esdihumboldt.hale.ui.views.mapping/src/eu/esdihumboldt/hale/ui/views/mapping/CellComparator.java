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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.Comparator;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.helper.EntityDefinitionComparator;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Comparator for cells.
 */
final class CellComparator implements Comparator<Cell> {

	private final Comparator<EntityDefinition> edComp = new EntityDefinitionComparator();

	@Override
	public int compare(Cell o1, Cell o2) {
		if (emptyOrNull(o1.getSource()) && emptyOrNull(o2.getSource())) {
			// compare first target
			EntityDefinition e1 = o1.getTarget().values().iterator().next().getDefinition();
			EntityDefinition e2 = o2.getTarget().values().iterator().next().getDefinition();
			return edComp.compare(e1, e2);
		}
		else if (emptyOrNull(o1.getSource())) {
			// o1 after o2
			return 1;
		}
		else if (emptyOrNull(o2.getSource())) {
			// o2 after o1
			return -1;
		}

		// compare first entity in source
		// XXX what about multiple sources?
		EntityDefinition e1 = o1.getSource().values().iterator().next().getDefinition();
		EntityDefinition e2 = o2.getSource().values().iterator().next().getDefinition();
		return edComp.compare(e1, e2);
	}

	private boolean emptyOrNull(Multimap<?, ?> source) {
		return source == null || source.isEmpty();
	}
}