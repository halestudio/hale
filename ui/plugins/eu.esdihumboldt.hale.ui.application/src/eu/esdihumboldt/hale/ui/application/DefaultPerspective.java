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
package eu.esdihumboldt.hale.ui.application;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import eu.esdihumboldt.hale.ui.views.data.SourceDataView;
import eu.esdihumboldt.hale.ui.views.data.TransformedDataView;
import eu.esdihumboldt.hale.ui.views.functions.FunctionsView;
import eu.esdihumboldt.hale.ui.views.mapping.AlignmentView;
import eu.esdihumboldt.hale.ui.views.report.ReportList;
import eu.esdihumboldt.hale.ui.views.schemas.SchemasView;
import eu.esdihumboldt.hale.ui.views.typehierarchy.TypeHierarchyView;

/**
 * The Perspective, i.e. the top layout element for the client. The Perspective
 * is divided into four folders. Each folder is in one corner of the the
 * Perspective.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 */
public class DefaultPerspective implements IPerspectiveFactory {

	/**
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout _layout) {
		String editorArea = _layout.getEditorArea();

		// bottom
		IFolderLayout bottom = _layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7f, editorArea); //$NON-NLS-1$
		bottom.addView("org.eclipse.pde.runtime.LogView");
		bottom.addView(IPageLayout.ID_PROP_SHEET);

		// bottom right
		IFolderLayout bottomRight = _layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.7f,
				"bottom");
		bottomRight.addView(TypeHierarchyView.ID);
		bottomRight.addView(FunctionsView.ID);
		bottomRight.addView(ReportList.ID);

		// top left
		IFolderLayout topLeft = _layout.createFolder("topLeft", IPageLayout.LEFT, 0.6f, editorArea); //$NON-NLS-1$
		topLeft.addView(SchemasView.ID);

		// top right
		IFolderLayout topRight = _layout.createFolder(
				"topRight", IPageLayout.RIGHT, 0.4f, editorArea); //$NON-NLS-1$
		topRight.addView(AlignmentView.ID);
//		topRight.addPlaceholder(MapView.ID);

		// lesser top right
//		IFolderLayout lesserTopRight = _layout.createFolder("lesserTopRight", IPageLayout.BOTTOM, 0.5f, "topRight"); //$NON-NLS-1$ //$NON-NLS-2$
//		lesserTopRight.addView(TransformedDataView.ID);

		_layout.addShowViewShortcut(SchemasView.ID);
//		_layout.addShowViewShortcut(MapView.ID);
		_layout.addShowViewShortcut(AlignmentView.ID);
		_layout.addShowViewShortcut(SourceDataView.ID);
		_layout.addShowViewShortcut(TransformedDataView.ID);
//		_layout.addShowViewShortcut(TaskTreeView.ID);
		_layout.addShowViewShortcut(ReportList.ID);
		_layout.addShowViewShortcut(TypeHierarchyView.ID);
		_layout.addShowViewShortcut(FunctionsView.ID);

		_layout.setEditorAreaVisible(false);
	}
}
