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
import eu.esdihumboldt.hale.rcp.views.table.ReferenceTableView;
import eu.esdihumboldt.hale.rcp.views.table.TransformedTableView;


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
				"bottomLeft", IPageLayout.BOTTOM, 0.5f, editorArea);
		bottomLeft.addView(MapView.ID);
		
		// bottom right
		IFolderLayout bottomRight = _layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.5f, "bottomLeft");
		bottomRight.addView(TransformedTableView.ID);
		
		// top left
		IFolderLayout topLeft = _layout.createFolder(
				"topLeft", IPageLayout.TOP, 0.5f, editorArea);
		topLeft.addView(MappingView.ID);
		
		// top right
		IFolderLayout topRight = _layout.createFolder("topRight", IPageLayout.RIGHT, 0.5f, "topLeft");
		topRight.addView(ReferenceTableView.ID);
		
		_layout.setEditorAreaVisible(false);
	}
}
