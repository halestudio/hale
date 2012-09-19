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

package eu.esdihumboldt.hale.ui.util.selection;

import org.eclipse.jface.viewers.ISelection;

/**
 * Selection tracker interface. Implementations track selections of different
 * kinds.
 * 
 * @author Simon Templer
 */
public interface SelectionTracker {

	/**
	 * Get the selection last with the given type
	 * 
	 * @param <T> the selection type
	 * 
	 * @param selectionType the selection class
	 * @return the last matching selection or <code>null</code> if none is
	 *         available
	 */
	public <T extends ISelection> T getSelection(Class<T> selectionType);

}
