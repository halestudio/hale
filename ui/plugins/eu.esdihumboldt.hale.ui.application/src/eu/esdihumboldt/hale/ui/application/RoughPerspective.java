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

import eu.esdihumboldt.hale.ui.views.mapping.AlignmentViewTypeOverview;
import eu.esdihumboldt.hale.ui.views.schemas.SchemasViewTypes;

/**
 * This is the perspective for type only mapping
 * 
 * @author Yasmina Kammeyer
 */
public class RoughPerspective implements IPerspectiveFactory {

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout _layout) {
		// getting EditorArea
		String editorArea = _layout.getEditorArea();

		// Bottom = Properties/Documentation
		IFolderLayout bottom = _layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7f, editorArea); //$NON-NLS-1$
		bottom.addView(IPageLayout.ID_PROP_SHEET);

		// Left = Schema View
		IFolderLayout topLeft = _layout.createFolder("topLeft", IPageLayout.LEFT, 0.6f, editorArea); //$NON-NLS-1$
		topLeft.addView(SchemasViewTypes.ID);

		// Right = Alignment
		IFolderLayout topRight = _layout.createFolder(
				"topRight", IPageLayout.RIGHT, 0.4f, editorArea); //$NON-NLS-1$
		topRight.addView(AlignmentViewTypeOverview.ID);

		_layout.setEditorAreaVisible(false);
	}

}
