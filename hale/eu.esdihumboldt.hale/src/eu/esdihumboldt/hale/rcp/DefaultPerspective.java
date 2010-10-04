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
public class DefaultPerspective implements IPerspectiveFactory {
	
	/**
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	public void createInitialLayout(IPageLayout _layout) {
		String editorArea = _layout.getEditorArea();
		
		// bottom
		IFolderLayout bottom = _layout.createFolder(
				"bottom", IPageLayout.BOTTOM, 0.7f, editorArea);
		bottom.addView(TaskTreeView.ID);
		
		// top left
		IFolderLayout topLeft = _layout.createFolder("topLeft", IPageLayout.LEFT, 0.6f, editorArea);
		topLeft.addView(ModelNavigationView.ID);
		
		// top right
		IFolderLayout topRight = _layout.createFolder("topRight", IPageLayout.RIGHT, 0.4f, editorArea);
		topRight.addView(MapView.ID);
		
		// lesser top right
		IFolderLayout lesserTopRight = _layout.createFolder("lesserTopRight", IPageLayout.BOTTOM, 0.6f, "topRight");
		lesserTopRight.addView(MappingView.ID);
		
		_layout.addShowViewShortcut(ModelNavigationView.ID);
		_layout.addShowViewShortcut(MapView.ID);
		_layout.addShowViewShortcut(MappingView.ID);
		_layout.addShowViewShortcut(ReferenceTableView.ID);
		_layout.addShowViewShortcut(TransformedTableView.ID);
		_layout.addShowViewShortcut(TaskTreeView.ID);
		
		_layout.setEditorAreaVisible(false);
	}
}
