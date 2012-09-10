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
