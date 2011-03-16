/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.views.map.MapView;
import eu.esdihumboldt.hale.rcp.views.mapping.MappingView;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.views.table.ReferenceTableView;
import eu.esdihumboldt.hale.rcp.views.table.TransformedTableView;
import eu.esdihumboldt.hale.rcp.views.tasks.TaskTreeView;


/**
 * The Perspective, i.e. the top layout element for the client. The Perspective 
 * is divided into four folders. Each folder is in one corner of the the 
 * Perspective.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version $Id$
 */
public class DataPerspective implements IPerspectiveFactory {
	
	/**
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	public void createInitialLayout(IPageLayout _layout) {
		String editorArea = _layout.getEditorArea();
		
		// bottom left
		IFolderLayout bottomLeft = _layout.createFolder(
				"bottomLeft", IPageLayout.BOTTOM, 0.5f, editorArea); //$NON-NLS-1$
		bottomLeft.addView(MapView.ID);
		
		// bottom right
		IFolderLayout bottomRight = _layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.5f, "bottomLeft"); //$NON-NLS-1$ //$NON-NLS-2$
		bottomRight.addView(TransformedTableView.ID);
		
		// top left
		IFolderLayout topLeft = _layout.createFolder(
				"topLeft", IPageLayout.TOP, 0.5f, editorArea); //$NON-NLS-1$
		topLeft.addView(MappingView.ID);
		
		// top right
		IFolderLayout topRight = _layout.createFolder("topRight", IPageLayout.RIGHT, 0.5f, "topLeft"); //$NON-NLS-1$ //$NON-NLS-2$
		topRight.addView(ReferenceTableView.ID);
		
		_layout.addShowViewShortcut(ModelNavigationView.ID);
		_layout.addShowViewShortcut(MapView.ID);
		_layout.addShowViewShortcut(MappingView.ID);
		_layout.addShowViewShortcut(ReferenceTableView.ID);
		_layout.addShowViewShortcut(TransformedTableView.ID);
		_layout.addShowViewShortcut(TaskTreeView.ID);
		
		_layout.setEditorAreaVisible(false);
	}
}
