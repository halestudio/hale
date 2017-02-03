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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.figures.EntityFigure;
import eu.esdihumboldt.hale.ui.common.graph.figures.TransformationNodeShape;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure.ShapePainter;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeLabel;
import eu.esdihumboldt.hale.ui.util.graph.shapes.FingerPost;
import eu.esdihumboldt.util.IdentityWrapper;

/**
 * Label provider for transformation trees
 * 
 * @author Simon Templer
 */
public class TransformationTreeLabelProvider extends GraphLabelProvider {

	private static final int MAX_FIGURE_WIDTH = 150;

	private final Color disabledBackgroundColor;
	private final Color valueBackgroundColor;

	/**
	 * Default constructor
	 * 
	 * @param associatedViewer the associated viewer (needed for style legend
	 *            support) or <code>null</code>
	 * @param provider the service provider that may be needed to obtain cell
	 *            explanations, may be <code>null</code>
	 */
	public TransformationTreeLabelProvider(GraphViewer associatedViewer, ServiceProvider provider) {
		super(associatedViewer, provider);

		Display display = PlatformUI.getWorkbench().getDisplay();
		disabledBackgroundColor = new Color(display, 240, 240, 240);
		valueBackgroundColor = new Color(display, 220, 245, 245);
	}

	/**
	 * @see GraphLabelProvider#createDefinitionLabels(GraphViewer)
	 */
	@Override
	protected LabelProvider createDefinitionLabels(GraphViewer viewer) {
		return new DefinitionLabelProvider(viewer, false, true);
	}

	/**
	 * @see GraphLabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		element = TransformationTreeUtil.extractObject(element);

		return super.getImage(element);
	}

	/**
	 * @see GraphLabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IdentityWrapper<?>) {
			element = ((IdentityWrapper<?>) element).getValue();
		}

		if (element instanceof EntityConnectionData) {
			// text for connections
			EntityConnectionData connection = (EntityConnectionData) element;

			Set<String> names = null;

			Object source = connection.source;
			if (source instanceof IdentityWrapper<?>) {
				source = ((IdentityWrapper<?>) source).getValue();
			}

			Object dest = connection.dest;
			if (dest instanceof IdentityWrapper<?>) {
				dest = ((IdentityWrapper<?>) dest).getValue();
			}

			if (source instanceof TargetNode && dest instanceof CellNode) {
				names = ((TargetNode) source).getAssignmentNames((CellNode) dest);
			}
			if (source instanceof CellNode && dest instanceof SourceNode) {
				names = ((CellNode) source).getSourceNames((SourceNode) dest);
			}

			if (names != null && !names.isEmpty()) {
				if (names.contains(null)) {
					names = new HashSet<String>(names);
					names.remove(null);
					if (!names.isEmpty()) {
						names.add("(unnamed)");
					}
				}
				// build name string
				Joiner joiner = Joiner.on(',');
				return joiner.join(names);
			}

			return "";
		}

		if (hasTransformationAnnotations(element)) {
			if (element instanceof SourceNode) {
				SourceNode node = (SourceNode) element;
				if (node.isDefined()) {
					Object value = node.getValue();

					if (value == null) {
						// no value
						return "(not set)";
					}
					else if (value instanceof Instance) {
						// use the instance value if present
						value = ((Instance) value).getValue();
					}

					if (value != null && !(value instanceof Group)) {
						// the value string representation
						return value.toString(); // TODO shorten if needed?
					}
					// otherwise, just display the definition name
				}
			}
		}

		element = TransformationTreeUtil.extractObject(element);

		return super.getText(element);
	}

	/**
	 * @see GraphLabelProvider#getNodeHighlightColor(Object)
	 */
	@Override
	public Color getNodeHighlightColor(Object entity) {
		entity = TransformationTreeUtil.extractObject(entity);

		return super.getNodeHighlightColor(entity);
	}

	/**
	 * @see GraphLabelProvider#getBorderColor(Object)
	 */
	@Override
	public Color getBorderColor(Object entity) {
		entity = TransformationTreeUtil.extractObject(entity);

		return super.getBorderColor(entity);
	}

	/**
	 * @see GraphLabelProvider#getBorderHighlightColor(Object)
	 */
	@Override
	public Color getBorderHighlightColor(Object entity) {
		entity = TransformationTreeUtil.extractObject(entity);

		return super.getBorderHighlightColor(entity);
	}

	/**
	 * @see GraphLabelProvider#getBackgroundColour(Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		if (hasTransformationAnnotations(entity)) {
			if (isDisabled(entity)) {
				return disabledBackgroundColor;
			}
			if (hasValue(entity)) {
				return valueBackgroundColor;
			}
		}

		entity = TransformationTreeUtil.extractObject(entity);

		return super.getBackgroundColour(entity);
	}

	/**
	 * Determines if a node shows a value.
	 * 
	 * @param entity the node
	 * @return if the node has a value
	 */
	private boolean hasValue(Object entity) {
		if (entity instanceof IdentityWrapper<?>) {
			entity = ((IdentityWrapper<?>) entity).getValue();
		}

		if (entity instanceof SourceNode) {
			return ((SourceNode) entity).isDefined();
		}

		return false;
	}

	/**
	 * Determines if the given node is a transformation node and has
	 * transformation annotations.
	 * 
	 * @param entity the node
	 * @return if there a transformation annotations present
	 */
	private boolean hasTransformationAnnotations(Object entity) {
		if (entity instanceof IdentityWrapper<?>) {
			entity = ((IdentityWrapper<?>) entity).getValue();
		}

		return entity instanceof TransformationNode
				&& ((TransformationNode) entity).hasAnnotations();
	}

	/**
	 * Determines if a node is disabled.
	 * 
	 * @param entity the node
	 * @return if the node is disabled
	 */
	private boolean isDisabled(Object entity) {
		if (entity instanceof IdentityWrapper<?>) {
			entity = ((IdentityWrapper<?>) entity).getValue();
		}

		if (entity instanceof SourceNode) {
			return !((SourceNode) entity).isDefined();
		}
		if (entity instanceof CellNode) {
			return !((CellNode) entity).isValid();
		}

		return false;
	}

	/**
	 * @see GraphLabelProvider#getForegroundColour(Object)
	 */
	@Override
	public Color getForegroundColour(Object entity) {
		entity = TransformationTreeUtil.extractObject(entity);

		return super.getForegroundColour(entity);
	}

	/**
	 * @see GraphLabelProvider#getFigure(Object)
	 */
	@Override
	public IFigure getFigure(Object element) {
		if (element instanceof IdentityWrapper<?>) {
			element = ((IdentityWrapper<?>) element).getValue();
		}

		ShapePainter shape = null;
		String contextText = null;
		String cardText = null;

		if (element instanceof TransformationTree) {
			shape = new TransformationNodeShape(10, SWT.NONE);
		}
		else if (element instanceof TargetNode) {
			TargetNode node = (TargetNode) element;

			contextText = AlignmentUtil.getContextText(node.getEntityDefinition());
			if (!hasTransformationAnnotations(element)) {
				cardText = getCardinalityText(node.getEntityDefinition().getDefinition());
			}

			if (node.getAssignments().isEmpty()) {
				shape = new TransformationNodeShape(10, SWT.NONE);
			}
			else {
//				shape = new TransformationNodeShape(10, SWT.LEFT);
				shape = new FingerPost(10, SWT.LEFT);
			}
		}
		else if (element instanceof SourceNode) {
			SourceNode node = (SourceNode) element;

			contextText = AlignmentUtil.getContextText(node.getEntityDefinition());
			if (!hasTransformationAnnotations(element)) {
				cardText = getCardinalityText(node.getEntityDefinition().getDefinition());
			}

			if (node.getParent() == null) {
				shape = new TransformationNodeShape(10, SWT.NONE);
			}
			else {
				shape = new FingerPost(10, SWT.RIGHT);
			}
		}

		if (shape != null) {
			CustomShapeFigure figure;
			if (contextText != null || cardText != null) {
				figure = new EntityFigure(shape, contextText, cardText, getCustomFigureFont());
			}
			else {
				figure = new CustomShapeLabel(shape, getCustomFigureFont());
			}
			figure.setMaximumWidth(MAX_FIGURE_WIDTH);
			return figure;
		}

		element = TransformationTreeUtil.extractObject(element);

		return super.getFigure(element);
	}

	private String getCardinalityText(Definition<?> definition) {
		if (!(definition instanceof ChildDefinition<?>)) {
			return null;
		}

		Cardinality card = DefinitionUtil.getCardinality((ChildDefinition<?>) definition);
		if (card.getMaxOccurs() == Cardinality.UNBOUNDED) {
			if (card.getMinOccurs() == 0) {
				return "*";
			}
			if (card.getMinOccurs() == 1) {
				return "+";
			}
			return card.getMinOccurs() + "+";
		}
		else if (card.getMaxOccurs() == 1) {
			if (card.getMinOccurs() == 0) {
				return "?";
			}
			if (card.getMinOccurs() == 1) {
				return null;
			}
		}

		return card.getMinOccurs() + ".." + card.getMaxOccurs();
	}

	/**
	 * @see GraphLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		disabledBackgroundColor.dispose();
		valueBackgroundColor.dispose();

		super.dispose();
	}

}
