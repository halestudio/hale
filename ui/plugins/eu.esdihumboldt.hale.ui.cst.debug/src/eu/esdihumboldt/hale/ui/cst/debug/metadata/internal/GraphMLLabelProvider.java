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

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import eu.esdihumboldt.hale.ui.common.graph.figures.TransformationNodeShape;
import eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure.ShapePainter;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeLabel;
import eu.esdihumboldt.hale.ui.util.graph.shapes.FingerPost;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * Label Provider for zest-graph creation via GraphML
 * 
 * @author Sebastian Reinhardt
 */

public class GraphMLLabelProvider extends LabelProvider implements IFigureProvider,
		IEntityStyleProvider {

	private final int entityBorderWidth = 1;
	private final Color entityBorderColor;
	private final Color entityBorderHighlightColor;
	private final Color typeBackgroundColor;
	private final Color propertyBackgroundColor;
	private final Color entityHighlightColor;
	private final Color entityForegroundColor;

	private final int cellBorderWidth = 1;
	private final Color cellBorderColor;
	private final Color cellBorderHighlightColor;
	private final Color cellBackgroundColor;
	private final Color cellHighlightColor;
	private final Color cellForegroundColor;

	/**
	 * standard constructor
	 */
	/**
	 * 
	 */
	public GraphMLLabelProvider() {
		super();
		final Display display = Display.getCurrent();

		// entity colors
		entityBorderColor = null;
		entityForegroundColor = null; // display.getSystemColor(SWT.COLOR_BLACK);
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
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Vertex) {
			return ((Vertex) element).getProperty("name").toString();
		}
		if (element instanceof Edge) {
			return ((Edge) element).getLabel();

		}

		// TODO FIX.ME
		throw new RuntimeException("Wrong input type in for GraphML Label Provider: "
				+ element.getClass().toString());

	}

	@Override
	public IFigure getFigure(Object element) {
		ShapePainter shape = null;
		// String contextText = null;

		if (element instanceof Vertex) {
			Vertex vex = (Vertex) element;

			if (vex.getProperty("type").equals("root")) {
				shape = new TransformationNodeShape(10, SWT.NONE);
			}
			else if (vex.getProperty("type").equals("target")) {
				boolean assigns = false;
				for (Vertex v : vex.getVertices(Direction.BOTH)) {
					if (v.getProperty("type").equals("cell")) {
						assigns = true;
					}
				}
				if (assigns) {
					shape = new FingerPost(10, SWT.LEFT);
				}
				else
					shape = new TransformationNodeShape(10, SWT.NONE);

			}
			else if (vex.getProperty("type").equals("source")) {

				if (vex.getVertices(Direction.IN).iterator().hasNext()
						&& vex.getVertices(Direction.OUT).iterator().hasNext()) {
					// shape = new SourceNodeRecordShape(10, SWT.RIGHT);
					shape = new TransformationNodeShape(10, SWT.NONE);
				}
				else
					shape = new TransformationNodeShape(10, SWT.NONE);
				// shape = new SourceNodeRecordShape(10, SWT.RIGHT);

			}
			else if (vex.getProperty("type").equals("cell")) {
				shape = new StretchedHexagon(10);
			}

			if (shape != null) {
				CustomShapeFigure figure;

				figure = new CustomShapeLabel(shape);
				// figure.setMaximumWidth(MAX_FIGURE_WIDTH);
				return figure;
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getNodeHighlightColor(java.lang.Object)
	 */
	@Override
	public Color getNodeHighlightColor(Object entity) {
		if (entity instanceof Vertex) {
			Vertex vex = (Vertex) entity;

			if (vex.getProperty("type").equals("root")) {
				return entityHighlightColor;
			}
			else if (vex.getProperty("type").equals("target")
					|| vex.getProperty("type").equals("source")) {
				return entityHighlightColor;
			}
			else if (vex.getProperty("type").equals("cell")) {
				return cellHighlightColor;
			}
			return null;
		}
		return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderColor(java.lang.Object)
	 */
	@Override
	public Color getBorderColor(Object entity) {
		if (entity instanceof Vertex) {
			Vertex vex = (Vertex) entity;

			if (vex.getProperty("type").equals("root")) {
				return entityBorderColor;
			}
			else if (vex.getProperty("type").equals("target")
					|| vex.getProperty("type").equals("source")) {
				return entityBorderColor;
			}
			else if (vex.getProperty("type").equals("cell")) {
				return cellBorderColor;
			}
			return null;
		}
		return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderHighlightColor(java.lang.Object)
	 */
	@Override
	public Color getBorderHighlightColor(Object entity) {
		if (entity instanceof Vertex) {
			Vertex vex = (Vertex) entity;

			if (vex.getProperty("type").equals("root")) {
				return entityBorderHighlightColor;
			}
			else if (vex.getProperty("type").equals("target")
					|| vex.getProperty("type").equals("source")) {
				return entityBorderHighlightColor;
			}
			else if (vex.getProperty("type").equals("cell")) {
				return cellBorderHighlightColor;
			}
			return null;
		}
		return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderWidth(java.lang.Object)
	 */
	@Override
	public int getBorderWidth(Object entity) {
		if (entity instanceof Vertex) {
			Vertex vex = (Vertex) entity;

			if (vex.getProperty("type").equals("root")) {
				return entityBorderWidth;
			}
			else if (vex.getProperty("type").equals("target")
					|| vex.getProperty("type").equals("source")) {
				return entityBorderWidth;
			}
			else if (vex.getProperty("type").equals("cell")) {
				return cellBorderWidth;
			}
			return -1;
		}
		return -1;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBackgroundColour(java.lang.Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		if (entity instanceof Vertex) {
			Vertex vex = (Vertex) entity;
			// TODO disabled and value
			if (vex.getProperty("type").equals("root")) {
				return typeBackgroundColor;
			}
			else if (vex.getProperty("type").equals("target")
					|| vex.getProperty("type").equals("source")) {
				return propertyBackgroundColor;
			}
			else if (vex.getProperty("type").equals("cell")) {
				return cellBackgroundColor;
			}
			return null;
		}
		return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getForegroundColour(java.lang.Object)
	 */
	@Override
	public Color getForegroundColour(Object entity) {
		if (entity instanceof Vertex) {
			Vertex vex = (Vertex) entity;

			if (vex.getProperty("type").equals("root")) {
				return entityForegroundColor;
			}
			else if (vex.getProperty("type").equals("target")
					|| vex.getProperty("type").equals("source")) {
				return entityForegroundColor;
			}
			else if (vex.getProperty("type").equals("cell")) {
				return cellForegroundColor;
			}
			return null;
		}
		return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getTooltip(java.lang.Object)
	 */
	@Override
	public IFigure getTooltip(Object entity) {
		// default
		return null;
	}

	/**
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#fisheyeNode(java.lang.Object)
	 */
	@Override
	public boolean fisheyeNode(Object entity) {
		// default
		return false;
	}

	/**
	 * @see GraphLabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		// default
		return null;
	}
}