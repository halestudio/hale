/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;

/**
 * comparator for helper function
 * 
 * @author Sameer Sheikh
 */
public class HelperFunctionComparator extends ViewerComparator {

	/**
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		HelperFunctionOrCategory hfoc1 = (HelperFunctionOrCategory) e1;
		HelperFunctionOrCategory hfoc2 = (HelperFunctionOrCategory) e2;
		String name1 = hfoc1.getName();
		String name2 = hfoc2.getName();

		return name1.compareTo(name2);
	}

}
