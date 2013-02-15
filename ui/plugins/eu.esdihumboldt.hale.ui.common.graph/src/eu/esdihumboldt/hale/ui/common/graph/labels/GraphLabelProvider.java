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

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.figures.CellFigure;
import eu.esdihumboldt.hale.ui.common.graph.figures.EntityFigure;
import eu.esdihumboldt.hale.ui.common.graph.figures.FunctionFigure;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.WrappedText;
import eu.esdihumboldt.hale.ui.util.graph.shapes.FingerPost;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * Label provider for mapping graphs.
 * 
 * @author Simon Templer
 */
public class GraphLabelProvider extends LabelProvider implements IEntityStyleProvider,
		IEntityConnectionStyleProvider, IFigureProvider {

	/**
	 * The maximum figure width
	 */
	public static final int MAX_FIGURE_WIDTH = 150;

	private final int entityBorderWidth = 1;
	private final Color entityBorderColor;
	private final Color entityBorderHighlightColor;
	private final Color typeBackgroundColor;
	private final Color propertyBackgroundColor;
	private final Color entityHighlightColor;
	private final Color entityForegorundColor;

	private final int cellBorderWidth = 1;
	private final Color cellBorderColor;
	private final Color cellBorderHighlightColor;
	private final Color cellBackgroundColor;
	private final Color cellHighlightColor;
	private final Color cellForegroundColor;

	private final LabelProvider definitionLabels;

	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	private final ServiceProvider serviceProvider;

	// TODO set colors for function in graph?

	/**
	 * Default constructor
	 * 
	 * @param provider the service provider that may be needed to obtain cell
	 *            explanations, may be <code>null</code>
	 */
	public GraphLabelProvider(ServiceProvider provider) {
		super();

		serviceProvider = provider;
		definitionLabels = createDefinitionLabels();

		final Display display = Display.getCurrent();

		// XXX keep entity colors in a central place?
		// XXX colors dependent of mapping context?

		// entity colors
		entityBorderColor = null;
		entityForegorundColor = null; // display.getSystemColor(SWT.COLOR_BLACK);
		typeBackgroundColor = new Color(display, 190, 230, 160);
		propertyBackgroundColor = new Color(display, 220, 250, 200);
		entityHighlightColor = new Color(display, 250, 250, 130);
		entityBorderHighlightColor = display.getSystemColor(SWT.COLOR_GRAY);

		// cell colors
		cellBorderColor = null;
		cellBorderHighlightColor = display.getSystemColor(SWT.COLOR_GRAY);
		cellBackgroundColor = null;
		cellForegroundColor = null;
		cellHighlightColor = entityHighlightColor;
	}

	/**
	 * @return the cellHighlightColor
	 */
	protected Color getCellHighlightColor() {
		return cellHighlightColor;
	}

	/**
	 * Create the label provider for {@link Definition}s and
	 * {@link EntityDefinition}s.
	 * 
	 * @return the label provider
	 */
	protected LabelProvider createDefinitionLabels() {
		return new DefinitionLabelProvider(true);
	}

	/**
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}

		if (element instanceof EntityDefinition || element instanceof Definition<?>) {
			// use definition image
			return definitionLabels.getImage(element);
		}

		if (element instanceof Cell) {
			// use function image if possible
			Cell cell = (Cell) element;
			String functionId = cell.getTransformationIdentifier();
			AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
			if (function != null) {
				return functionLabels.getImage(function);
			}
			return null;
		}

		if (element instanceof Function) {
			return functionLabels.getImage(element);
		}

		return super.getImage(element);
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}

		if (element instanceof EntityDefinition) {
			// use definition text
			return definitionLabels.getText(element);
		}

		if (element instanceof Definition<?>) {
			// use definition text
			return definitionLabels.getText(element);
		}

		if (element instanceof Cell) {
			// use function name if possible
			Cell cell = (Cell) element;
			String functionId = cell.getTransformationIdentifier();
			AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
			if (function != null) {
				return functionLabels.getText(function);
			}
			return functionId;
		}

		if (element instanceof Function) {
			return functionLabels.getText(element);
		}

		return super.getText(element);
	}

	/**
	 * @see IDisposable#dispose()
	 */
	@Override
	public void dispose() {
		definitionLabels.dispose();
		functionLabels.dispose();

		// dispose created colors
		typeBackgroundColor.dispose();
		propertyBackgroundColor.dispose();
		entityHighlightColor.dispose();

//		cellBorderHighlightColor.dispose();

		super.dispose();
	}

	/**
	 * @see IEntityStyleProvider#getNodeHighlightColor(Object)
	 */
	@Override
	public Color getNodeHighlightColor(Object entity) {
		if (entity instanceof Entity || entity instanceof EntityDefinition
				|| entity instanceof Definition<?>) {
			return entityHighlightColor;
		}

		if (entity instanceof Cell || entity instanceof Function) {
			return cellHighlightColor;
		}

		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderColor(Object)
	 */
	@Override
	public Color getBorderColor(Object entity) {
		if (entity instanceof Entity || entity instanceof EntityDefinition
				|| entity instanceof Definition<?>) {
			return entityBorderColor;
		}

		if (entity instanceof Cell || entity instanceof Function) {
			return cellBorderColor;
		}

		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderHighlightColor(Object)
	 */
	@Override
	public Color getBorderHighlightColor(Object entity) {
		if (entity instanceof Entity || entity instanceof EntityDefinition
				|| entity instanceof Definition<?>) {
			return entityBorderHighlightColor;
		}

		if (entity instanceof Cell || entity instanceof Function) {
			return cellBorderHighlightColor;
		}

		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderWidth(Object)
	 */
	@Override
	public int getBorderWidth(Object entity) {
		if (entity instanceof Entity || entity instanceof EntityDefinition
				|| entity instanceof Definition<?>) {
			return entityBorderWidth;
		}

		if (entity instanceof Cell || entity instanceof Function) {
			return cellBorderWidth;
		}

		return -1;
	}

	/**
	 * @see IEntityStyleProvider#getBackgroundColour(Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		if (entity instanceof Entity || entity instanceof EntityDefinition
				|| entity instanceof Definition<?>) {
			if (entity instanceof Type || entity instanceof TypeEntityDefinition
					|| entity instanceof TypeDefinition) {
				return typeBackgroundColor;
			}
			return propertyBackgroundColor;
		}

		if (entity instanceof Cell || entity instanceof Function) {
			return cellBackgroundColor;
		}

		return null;
	}

	/**
	 * @see IEntityStyleProvider#getForegroundColour(Object)
	 */
	@Override
	public Color getForegroundColour(Object entity) {
		if (entity instanceof Entity || entity instanceof EntityDefinition
				|| entity instanceof Definition<?>) {
			return entityForegorundColor;
		}

		if (entity instanceof Cell || entity instanceof Function) {
			return cellForegroundColor;
		}

		return null;
	}

	/**
	 * @see IEntityStyleProvider#getTooltip(Object)
	 */
	@Override
	public IFigure getTooltip(Object entity) {
		if (entity instanceof Cell) {
			Cell cell = (Cell) entity;
			AbstractFunction<?> function = FunctionUtil.getFunction(cell
					.getTransformationIdentifier());
			if (function != null) {
				CellExplanation explanation = function.getExplanation();
				if (explanation != null) {
					String text = explanation.getExplanation(cell, serviceProvider);
					if (text != null) {
						return new WrappedText(text, 400);
					}
				}
			}
		}

		// default
		return null;
	}

	/**
	 * @see IEntityStyleProvider#fisheyeNode(Object)
	 */
	@Override
	public boolean fisheyeNode(Object entity) {
		// default
		return false;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getConnectionStyle(Object, Object)
	 */
	@Override
	public int getConnectionStyle(Object src, Object dest) {
		return ZestStyles.CONNECTIONS_SOLID; // |
												// ZestStyles.CONNECTIONS_DIRECTED;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getColor(Object, Object)
	 */
	@Override
	public Color getColor(Object src, Object dest) {
		// default
		return null;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getHighlightColor(Object, Object)
	 */
	@Override
	public Color getHighlightColor(Object src, Object dest) {
		// default
		return null;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getLineWidth(Object, Object)
	 */
	@Override
	public int getLineWidth(Object src, Object dest) {
		// default
		return -1;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getTooltip(Object, Object)
	 */
	@Override
	public IFigure getTooltip(Object src, Object dest) {
		// default
		return null;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getRouter(Object, Object)
	 */
	@Override
	public ConnectionRouter getRouter(Object src, Object dest) {
		// default
		return null;
	}

	/**
	 * @see IFigureProvider#getFigure(Object)
	 */
	@Override
	public IFigure getFigure(Object element) {
		CustomShapeFigure figure = null;
		if (element instanceof Cell) {
			Cell cell = (Cell) element;
			figure = new CellFigure(cell);
		}
		if (element instanceof Function) {
			figure = new FunctionFigure(new StretchedHexagon(10));
		}

		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}

		if (element instanceof EntityDefinition) {
			String contextText = AlignmentUtil.getContextText((EntityDefinition) element);
			switch (((EntityDefinition) element).getSchemaSpace()) {
			case SOURCE:
				figure = new EntityFigure(new FingerPost(10, SWT.RIGHT), contextText, null);
				break;
			case TARGET:
				figure = new EntityFigure(new FingerPost(10, SWT.LEFT), contextText, null);
				break;
			}
		}

		if (figure != null) {
			figure.setMaximumWidth(MAX_FIGURE_WIDTH);
		}

		return figure;
	}

}
