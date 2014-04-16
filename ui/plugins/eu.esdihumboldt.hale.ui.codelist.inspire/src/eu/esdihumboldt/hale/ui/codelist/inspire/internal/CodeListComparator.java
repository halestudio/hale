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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.codelist.inspire.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Comparator for {@link CodeListRef}s.
 * 
 * @author Simon Templer
 */
public class CodeListComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof CodeListRef && e2 instanceof CodeListRef) {
			return compare((CodeListRef) e1, (CodeListRef) e2);
		}

		return super.compare(viewer, e1, e2);
	}

	private int compare(CodeListRef o1, CodeListRef o2) {
		return o1.getName().compareToIgnoreCase(o2.getName());
	}

}
