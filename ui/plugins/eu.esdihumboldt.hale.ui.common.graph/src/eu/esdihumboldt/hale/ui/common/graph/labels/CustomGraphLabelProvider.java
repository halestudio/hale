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

package eu.esdihumboldt.hale.ui.common.graph.labels;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.zest.core.viewers.IFigureProvider;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.ui.common.graph.figures.EntityFigureWithData;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.FingerPost;

/**
 * TODO Type description
 * 
 * @author Yasmina Kammeyer
 */
public class CustomGraphLabelProvider extends GraphLabelProvider {

	//

	/**
	 * @param provider
	 */
	public CustomGraphLabelProvider(ServiceProvider provider) {
		super(provider);

	}

	/**
	 * @see IFigureProvider#getFigure(Object)
	 */
	@Override
	public IFigure getFigure(Object element) {

		CustomShapeFigure figure = null;

		// To get one value use this
		// TestValues instanceValue = new InstanceTestValues();

		// Service
		// InstanceService is = (InstanceService)
		// PlatformUI.getWorkbench().getService(InstanceService.class);

		// Copied from GraphLabelProvider
		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}

		if (element instanceof EntityDefinition) {
			String contextText = AlignmentUtil.getContextText((EntityDefinition) element);

			switch (((EntityDefinition) element).getSchemaSpace()) {
			case SOURCE:
				// If it is a property, it can have a value
				if (element instanceof PropertyEntityDefinition) {
					// Get any Instance
					// Instance value = (Instance)
					// instanceValue.get((PropertyEntityDefinition) element);

					figure = new EntityFigureWithData(new FingerPost(10, SWT.RIGHT), contextText,
							"Data:" + "-missing-", getCustomFigureFont());
				}
				else {
					figure = new EntityFigureWithData(new FingerPost(10, SWT.RIGHT), contextText,
							null, getCustomFigureFont());
				}
				break;
			case TARGET:
				// If it is a property, it can have a value
				if (element instanceof PropertyEntityDefinition) {
					// Get any Instance
					// Instance value = (Instance)
					// instanceValue.get((PropertyEntityDefinition) element);

					figure = new EntityFigureWithData(new FingerPost(10, SWT.LEFT), contextText,
							"Result:" + "-missing-", getCustomFigureFont());
				}
				else {
					figure = new EntityFigureWithData(new FingerPost(10, SWT.LEFT), contextText,
							null, getCustomFigureFont());
				}
				break;
			}
		}

		if (figure != null) {
			figure.setMaximumWidth(MAX_FIGURE_WIDTH);
		}

		return figure;
	}

}
