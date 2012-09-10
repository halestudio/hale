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

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPartSite;

import eu.esdihumboldt.hale.ui.util.ViewContextMenu;

/**
 * A viewer context menu.
 * 
 * @author Simon Templer
 */
public class ViewerMenu extends ViewContextMenu {

	/**
	 * Create a viewer context menu.
	 * 
	 * @param site the (view) site containing the viewer
	 * @param viewer the viewer
	 */
	public ViewerMenu(IWorkbenchPartSite site, Viewer viewer) {
		super(site, viewer, viewer.getControl());
	}

}
