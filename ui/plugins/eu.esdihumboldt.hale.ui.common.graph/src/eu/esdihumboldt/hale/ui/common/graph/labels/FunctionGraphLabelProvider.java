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

package eu.esdihumboldt.hale.ui.common.graph.labels;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.ui.common.graph.figures.FunctionFigure;
import eu.esdihumboldt.hale.ui.common.graph.figures.ParameterFigure;
import eu.esdihumboldt.hale.ui.util.ResourceManager;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.FingerPost;
import eu.esdihumboldt.util.Pair;

/**
 * Label provider for graphs based on {@link FunctionDefinition}(s).
 * 
 * @author Patrick Lieb
 */
public class FunctionGraphLabelProvider extends GraphLabelProvider {

	private static final int MAX_FIGURE_WIDTH = 150;

	private final Color targetbackgroundcolor;

	private final Color sourcebackgroundcolor;

	private final ResourceManager resourceManager = new ResourceManager();

	private final boolean showAll;

	/**
	 * Default constructor
	 * 
	 * @param provider the service provider that may be needed to obtain cell
	 *            explanations, may be <code>null</code>
	 * @param showAll true if additional information (tooltips, etc.) should be
	 *            shown
	 */
	public FunctionGraphLabelProvider(ServiceProvider provider, boolean showAll) {
		super(null, provider);
		final Display display = Display.getCurrent();

		this.showAll = showAll;

		targetbackgroundcolor = new Color(display, 255, 160, 122);
		sourcebackgroundcolor = new Color(display, 255, 236, 139);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {

		if (element instanceof Pair<?, ?>) {
			return super.getImage(((Pair<?, ?>) element).getFirst());
		}

		return super.getImage(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {

		if (element instanceof Pair<?, ?>) {
			element = ((Pair<?, ?>) element).getFirst();
		}

		if (element instanceof EntityConnectionData) {
			return "";
		}

		if (element instanceof AbstractParameter) {
			String result = ((AbstractParameter) element).getDisplayName();
			if (!result.equals(""))
				return result;
			return "(unnamed)";
		}

		return super.getText(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getFigure(java.lang.Object)
	 */
	@Override
	public IFigure getFigure(Object element) {
		CustomShapeFigure figure = null;

		if (element instanceof AbstractParameter) {
			figure = new ParameterFigure(new FingerPost(10, SWT.LEFT),
					getOccurenceString((ParameterDefinition) element),
					((AbstractParameter) element).getDescription(), showAll);
		}
		else {
			if (element instanceof Pair<?, ?>) {
				element = ((Pair<?, ?>) element).getFirst();
			}

			if (element instanceof PropertyFunctionDefinition) {
				figure = new FunctionFigure(resourceManager,
						((PropertyFunctionDefinition) element).getDefinedParameters(), showAll);
			}
			else if (element instanceof TypeFunctionDefinition) {
				figure = new FunctionFigure(resourceManager,
						((TypeFunctionDefinition) element).getDefinedParameters(), showAll);
			}
			else if (element instanceof ParameterDefinition) {
				figure = new ParameterFigure(new FingerPost(10, SWT.RIGHT),
						getOccurenceString((ParameterDefinition) element),
						((ParameterDefinition) element).getDescription(), showAll);
			}
		}

		if (figure != null) {
			figure.setMaximumWidth(MAX_FIGURE_WIDTH);
			return figure;
		}

		return super.getFigure(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getBackgroundColour(java.lang.Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {

		if (entity instanceof AbstractParameter) {
			return targetbackgroundcolor;
		}

		if (entity instanceof Pair<?, ?>) {
			entity = ((Pair<?, ?>) entity).getFirst();
		}

		if (entity instanceof AbstractParameter) {
			return sourcebackgroundcolor;
		}

		return super.getBackgroundColour(entity);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		resourceManager.dispose();
		targetbackgroundcolor.dispose();
		sourcebackgroundcolor.dispose();
		super.dispose();
	}

	private String getOccurenceString(ParameterDefinition parameter) {

		String max;
		String min;

		if (parameter.getMinOccurrence() == -1) {
			min = "n";
		}
		else {
			min = String.valueOf(parameter.getMinOccurrence());
		}

		if (parameter.getMaxOccurrence() == -1) {
			max = "n";
		}
		else {
			max = String.valueOf(parameter.getMaxOccurrence());
		}

		String text = min + ".." + max;

		return text;
	}

}
