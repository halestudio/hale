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

package eu.esdihumboldt.hale.ui.common.graph.figures;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.ui.util.ResourceManager;
import eu.esdihumboldt.hale.ui.util.ResourceManager.Resource;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * The shape figure for {@link FunctionDefinition}(s)
 * 
 * @author Patrick Lieb
 */
public class FunctionFigure extends CustomShapeFigure {

	/**
	 * Create a simple function figure.
	 * 
	 * @param customFont a custom font, may be <code>null</code>
	 */
	public FunctionFigure(final Font customFont) {
		super(new StretchedHexagon(10), customFont);
	}

	private static final Resource<Font> SMALL_ITALIC_FONT_RESOURCE = new Resource<Font>() {

		@Override
		public Font initializeResource() throws Exception {
			return new Font(Display.getCurrent(), "Arial", 8, SWT.ITALIC);
		}

		@Override
		public void dispose(Font resource) {
			resource.dispose();
		}
	};

	/**
	 * Create a new function figure.
	 * 
	 * @param resourceManager the resource manager
	 * @param parameters the Parameters of the Function
	 * @param showToolTip if the ToolTip should be shown
	 */
	public FunctionFigure(ResourceManager resourceManager,
			Collection<FunctionParameterDefinition> parameters, boolean showToolTip) {
		super(new StretchedHexagon(10));

		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);

		Label label = new Label();
		GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1);
		add(label, gridData);
		setTextLabel(label);
		setIconLabel(label);

		if (!parameters.isEmpty()) {

			Font font;
			try {
				font = resourceManager.getInstance(SMALL_ITALIC_FONT_RESOURCE);
			} catch (Exception e) {
				font = null;
			}

			Label name = new Label();
			GridData nameGrid = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false,
					3, 1);
			name.setText("Defined Parameters");
			if (font != null) {
				name.setFont(font);
			}
			add(name, nameGrid);

			Iterator<FunctionParameterDefinition> iter = parameters.iterator();
			while (iter.hasNext()) {
				FunctionParameterDefinition para = iter.next();

				// tip
				Label descriptionlabel = new Label();

				if (showToolTip && para.getDescription() != null) {
					FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
							.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
					Image image = fieldDecoration.getImage();

					IFigure descriptionfigure = new Label(para.getDescription());
					descriptionlabel.setIcon(image);
					descriptionlabel.setToolTip(descriptionfigure);
				}

				GridData descriptiongrid = new GridData(GridData.CENTER, GridData.CENTER, false,
						false);
				add(descriptionlabel, descriptiongrid);

				// parameter name
				name = new Label();
				nameGrid = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
				name.setText(para.getDisplayName());
				add(name, nameGrid);

				// parameter occurrence
				Label occurence = new Label();
				GridData occurenceGrid = new GridData(GridData.END, GridData.CENTER, false, false);
				occurence.setText(getOccurence(para));
				if (font != null) {
					occurence.setFont(font);
				}
				add(occurence, occurenceGrid);
			}
		}
	}

	private String getOccurence(FunctionParameterDefinition parameter) {
		String result = "";
		if (parameter.getMinOccurrence() == -1) {
			result += "n";
		}
		else {
			result += parameter.getMinOccurrence();
		}
		result += "..";
		if (parameter.getMaxOccurrence() == -1) {
			result += "n";
		}
		else {
			result += parameter.getMaxOccurrence();
		}
		return result;
	}

}
