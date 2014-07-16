/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.application;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import eu.esdihumboldt.hale.ui.views.data.SourceDataView;
import eu.esdihumboldt.hale.ui.views.data.TransformedDataView;
import eu.esdihumboldt.hale.ui.views.mapping.ContextualAlignmentView;
import eu.esdihumboldt.hale.ui.views.schemas.SchemasViewOneTypeFocus;

/**
 * This is the perspective for detailed mapping
 * 
 * @author Yasmina Kammeyer
 */
public class AttributesMappingPerspective implements IPerspectiveFactory {

	/**
	 * The ID of this perspective
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.application.perspective.attributemapping";

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout _layout) {

		// getting EditorArea
		String editorArea = _layout.getEditorArea();

//		// Bottom left = Navigation (types only)
//		IFolderLayout bottom = _layout.createFolder("bottom", IPageLayout.BOTTOM, 0.5f, editorArea);
//		bottom.addView(SourceDataView.ID);
//
//		// Bottom right
//		IFolderLayout bottomRight = _layout.createFolder(
//				"bottomRight", IPageLayout.RIGHT, 0.5f, "bottom"); //$NON-NLS-1$
//		bottomRight.addView(TransformedDataView.ID);
//		bottomRight.addView(IPageLayout.ID_PROP_SHEET);
//
//		// Left = Schema Explorer for one relation
//		IFolderLayout topLeft = _layout.createFolder("topLeft", IPageLayout.LEFT, 0.6f, editorArea); //$NON-NLS-1$
//		topLeft.addView(SchemasViewOneTypeFocus.ID);
//
//		// Right = Alignment View
//		IFolderLayout topRight = _layout.createFolder(
//				"topRight", IPageLayout.RIGHT, 0.4f, editorArea); //$NON-NLS-1$
//		topRight.addView(ContextualAlignmentView.ID);

		// Top Left = Schema Explorer for one relation
		IFolderLayout topLeft = _layout.createFolder("topLeft", IPageLayout.LEFT, 0.6f, editorArea); //$NON-NLS-1$
		topLeft.addView(SchemasViewOneTypeFocus.ID);

		// Bottom Left = Alignment View
		IFolderLayout bottomLeft = _layout.createFolder(
				"bottomLeft", IPageLayout.BOTTOM, 0.4f, "topLeft"); //$NON-NLS-1$
		bottomLeft.addView(ContextualAlignmentView.ID);

		// Top right = Source Data View
		IFolderLayout topRight = _layout.createFolder("topRight", IPageLayout.RIGHT, 0.5f,
				editorArea);
		topRight.addView(SourceDataView.ID);

		// Bottom right = Transformed Data View
		IFolderLayout bottomRight = _layout.createFolder(
				"bottomRight", IPageLayout.BOTTOM, 0.5f, "topRight"); //$NON-NLS-1$
		bottomRight.addView(TransformedDataView.ID);
		bottomRight.addView(IPageLayout.ID_PROP_SHEET);

		_layout.setEditorAreaVisible(false);

	}

}
