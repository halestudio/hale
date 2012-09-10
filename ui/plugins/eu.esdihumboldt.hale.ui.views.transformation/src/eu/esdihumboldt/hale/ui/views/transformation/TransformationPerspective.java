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

package eu.esdihumboldt.hale.ui.views.transformation;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import eu.esdihumboldt.hale.ui.views.data.SourceDataView;
import eu.esdihumboldt.hale.ui.views.data.TransformedDataView;

/**
 * Transformation perspective
 * 
 * @author Simon Templer
 */
public class TransformationPerspective implements IPerspectiveFactory {

	/**
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		// top
		IFolderLayout top = layout.createFolder("top", IPageLayout.TOP, 0.7f, editorArea);
		top.addView(TransformationView.ID);

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
