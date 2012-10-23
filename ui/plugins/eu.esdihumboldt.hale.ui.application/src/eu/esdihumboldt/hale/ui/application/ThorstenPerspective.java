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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.application;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import eu.esdihumboldt.hale.ui.views.data.SourceDataView;
import eu.esdihumboldt.hale.ui.views.data.TransformedDataView;
import eu.esdihumboldt.hale.ui.views.mapping.AlignmentView;
import eu.esdihumboldt.hale.ui.views.schemas.SchemasView;

/**
 * Thorsten's perspective
 * 
 * @author Willem Weiss
 */
public class ThorstenPerspective implements IPerspectiveFactory {

	/**
	 * Identifer of the map view (which is not a dependency of this bundle). See
	 * StyledMapView.ID
	 */
	public static final String MAP_ID = "eu.esdihumboldt.hale.ui.views.styledmap";

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout _layout) {
		String editorArea = _layout.getEditorArea();

		// bottom left
		IFolderLayout bottomLeft = _layout.createFolder(
				"bottomLeft", IPageLayout.BOTTOM, 0.5f, editorArea); //$NON-NLS-1$
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
		bottomLeft.addView(AlignmentView.ID);
		bottomLeft.addView(MAP_ID);

		// bottom right
		IFolderLayout bottomRight = _layout.createFolder(
				"bottomRight", IPageLayout.RIGHT, 0.5f, "bottomLeft"); //$NON-NLS-1$ //$NON-NLS-2$
		bottomRight.addView(TransformedDataView.ID);

		// top left
		IFolderLayout topLeft = _layout.createFolder("topLeft", IPageLayout.TOP, 0.5f, editorArea); //$NON-NLS-1$
		topLeft.addView(SchemasView.ID);

		// top right
		IFolderLayout topRight = _layout.createFolder(
				"topRight", IPageLayout.RIGHT, 0.5f, "topLeft"); //$NON-NLS-1$ //$NON-NLS-2$
		topRight.addView(SourceDataView.ID);

		_layout.addShowViewShortcut(SchemasView.ID);
		_layout.addShowViewShortcut(AlignmentView.ID);
		_layout.addShowViewShortcut(SourceDataView.ID);
		_layout.addShowViewShortcut(TransformedDataView.ID);

		_layout.setEditorAreaVisible(false);
	}
}