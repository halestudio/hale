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

package eu.esdihumboldt.hale.ui.common.graph.labels;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.widgets.custom.CustomShapeFigure.ShapePainter;
import org.eclipse.zest.core.widgets.custom.CustomShapeLabel;
import org.eclipse.zest.core.widgets.custom.shapes.FingerPost;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TypeNode;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.figures.TransformationNodeShape;

/**
 * Label provider for transformation trees
 * @author Simon Templer
 */
public class TransformationTreeLabelProvider extends GraphLabelProvider {

	/**
	 * @see GraphLabelProvider#createDefinitionLabels()
	 */
	@Override
	protected LabelProvider createDefinitionLabels() {
		return new DefinitionLabelProvider(false);
	}

	/**
	 * @see GraphLabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		element = extractObject(element);
		
		return super.getImage(element);
	}

	/**
	 * @see GraphLabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof EntityConnectionData) {
			EntityConnectionData connection = (EntityConnectionData) element;
			
			Set<String> names = null;
			if (connection.source instanceof TargetNode
					&& connection.dest instanceof CellNode) {
				names = ((TargetNode) connection.source)
						.getAssignmentNames((CellNode) connection.dest);
			}
			if (connection.source instanceof CellNode
					&& connection.dest instanceof SourceNode) {
				names = ((CellNode) connection.source)
						.getSourceNames((SourceNode) connection.dest);
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
		
		element = extractObject(element);
		
		return super.getText(element);
	}

	/**
	 * @see GraphLabelProvider#getNodeHighlightColor(Object)
	 */
	@Override
	public Color getNodeHighlightColor(Object entity) {
		entity = extractObject(entity);
		
		return super.getNodeHighlightColor(entity);
	}

	/**
	 * @see GraphLabelProvider#getBorderColor(Object)
	 */
	@Override
	public Color getBorderColor(Object entity) {
		entity = extractObject(entity);
		
		return super.getBorderColor(entity);
	}

	/**
	 * @see GraphLabelProvider#getBorderHighlightColor(Object)
	 */
	@Override
	public Color getBorderHighlightColor(Object entity) {
		entity = extractObject(entity);
		
		return super.getBorderHighlightColor(entity);
	}

	/**
	 * @see GraphLabelProvider#getBackgroundColour(Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		entity = extractObject(entity);
		
		return super.getBackgroundColour(entity);
	}

	/**
	 * @see GraphLabelProvider#getForegroundColour(Object)
	 */
	@Override
	public Color getForegroundColour(Object entity) {
		entity = extractObject(entity);
		
		return super.getForegroundColour(entity);
	}

	/**
	 * @see GraphLabelProvider#getFigure(Object)
	 */
	@Override
	public IFigure getFigure(Object element) {
		ShapePainter shape = null;
		
		if (element instanceof TypeNode) {
			shape = new TransformationNodeShape(10, SWT.NONE);
		}
		else if (element instanceof TargetNode) {
			TargetNode node = (TargetNode) element;
			
			if (node.getAssignments().isEmpty()) {
				shape = new TransformationNodeShape(10, SWT.NONE);
			}
			else {
				shape = new TransformationNodeShape(10, SWT.RIGHT);
			}
		}
		else if (element instanceof SourceNode) {
			SourceNode node = (SourceNode) element;
			
			if (node.getParent() == null) {
				shape = new TransformationNodeShape(10, SWT.NONE);
			}
			else {
				shape = new FingerPost(10, SWT.LEFT);
			}
		}
		
		if (shape != null) {
			return new CustomShapeLabel(shape);
		}
		
		element = extractObject(element);
		
		return super.getFigure(element);
	}

	/**
	 * Extract the definition or cell contained in a node
	 * @param node the node
	 * @return the contained definition, cell or the node itself
	 */
	private Object extractObject(Object node) {
		if (node instanceof TypeNode) {
			return ((TypeNode) node).getType();
		}
		if (node instanceof TargetNode) {
			return ((TargetNode) node).getEntityDefinition();
		}
		if (node instanceof CellNode) {
			return ((CellNode) node).getCell();
		}
		if (node instanceof SourceNode) {
			return ((SourceNode) node).getEntityDefinition();
		}
		
		return node;
	}

}
