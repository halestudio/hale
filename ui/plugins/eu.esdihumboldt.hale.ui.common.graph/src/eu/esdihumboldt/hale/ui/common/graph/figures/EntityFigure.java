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

package eu.esdihumboldt.hale.ui.common.graph.figures;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;

import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;

/**
 * Figure for entities displaying an optional context text additional to the
 * entity name and icon.
 * @author Simon Templer
 */
public class EntityFigure extends CustomShapeFigure {

	/**
	 * Create a entity figure.
	 * @param painter the shape
	 * @param contextText the context text, may be <code>null</code>
	 */
	public EntityFigure(ShapePainter painter, final String contextText) {
		super(painter);
		
		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = (contextText == null)?(1):(2);
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);
		
		// the label for the label provider text and image
		Label label = new Label();
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		add(label, gridData);
		
		// the additional label for the context text
		if (contextText != null) {
			Label contextLabel = new Label();
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			//XXX uses the same color as in schema explorer label provider - centralize this?
			contextLabel.setForegroundColor(colorRegistry.get(JFacePreferences.DECORATIONS_COLOR));
			contextLabel.setText(contextText);
			GridData contextGridData = new GridData(GridData.END,
					GridData.CENTER, false, true);
			add(contextLabel, contextGridData);
		}

		setTextLabel(label);
		setIconLabel(label);
	}

}
