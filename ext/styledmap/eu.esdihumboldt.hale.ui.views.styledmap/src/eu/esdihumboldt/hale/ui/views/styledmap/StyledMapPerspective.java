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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.styledmap;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import eu.esdihumboldt.hale.ui.views.data.SourceDataView;
import eu.esdihumboldt.hale.ui.views.data.TransformedDataView;

/**
 * Styled map perspective.
 * 
 * @author Simon Templer
 */
public class StyledMapPerspective implements IPerspectiveFactory {

	/**
	 * The perspective identifier as registered in the application.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.styledmap";

	/**
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		// top
		IFolderLayout top = layout.createFolder("top", IPageLayout.TOP, 0.7f, editorArea);
		top.addView(StyledMapView.ID);

		// bottom left
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.7f,
				editorArea);
		bottomLeft.addView(SourceDataView.ID);

		// bottom right
		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.5f,
				"bottomLeft");
		bottomRight.addView(TransformedDataView.ID);

		layout.setEditorAreaVisible(false);
	}

}
