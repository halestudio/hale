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

package eu.esdihumboldt.hale.ui.views.mapping.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TypeNode;

/**
 * Label provider for transformation trees
 * @author Simon Templer
 */
public class TransformationTreeLabelProvider extends GraphLabelProvider {

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
			return ((TargetNode) node).getDefinition();
		}
		if (node instanceof CellNode) {
			return ((CellNode) node).getCell();
		}
		if (node instanceof SourceNode) {
			return ((SourceNode) node).getDefinition();
		}
		
		return node;
	}

}
