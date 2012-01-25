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

package eu.esdihumboldt.hale.ui.common.graph.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutObserver;
import org.eclipse.zest.layouts.algorithms.TreeLayoutObserver.TreeNode;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import eu.esdihumboldt.hale.common.align.extension.function.Function;

/**
 * Tree layout algorithm to show {@link Function}(s) layered in a tree-like layout and
 * placed the root in the middle of the tree
 * The bounds have to be set before the algorithm can properly (otherwise default values are set)
 * 
 * @author Patrick Lieb
 */
public class FunctionTreeLayoutAlgorithm implements LayoutAlgorithm {

	private TreeLayoutObserver treeObserver;
	
	private DisplayIndependentRectangle bounds;
	
	private LayoutContext context;

	/**
	 * @see org.eclipse.zest.layouts.LayoutAlgorithm#setLayoutContext(org.eclipse.zest.layouts.interfaces.LayoutContext)
	 */
	@Override
	public void setLayoutContext(LayoutContext context) {
		
		this.context = context;
		
		if (treeObserver != null) {
			treeObserver.stop();
		}
		if (context != null) {
			treeObserver = new TreeLayoutObserver(context, null);
		}
	}

	/**
	 * @see org.eclipse.zest.layouts.LayoutAlgorithm#applyLayout(boolean)
	 */
	@Override
	public void applyLayout(boolean clean) {
		if (!clean)
			return;

		internalApplyLayout();
	}

	/**
	 * @return the bounds
	 */
	public DisplayIndependentRectangle getBounds() {
		return bounds;
	}

	@SuppressWarnings("unchecked")
	private void internalApplyLayout() {
		TreeNode superRoot = treeObserver.getSuperRoot();
		bounds = context.getBounds();
		int line = 1;
		List<TreeNode> children = new ArrayList<TreeNode>();
		int count = superRoot.getChildren().size();
		for (Iterator<TreeNode> iterator = superRoot.getChildren().iterator(); iterator
				.hasNext();) {

			TreeNode rootInfo = iterator.next();
			computePosition(rootInfo, count, line, 0);
			if (children.isEmpty())
				children = rootInfo.getChildren();

			line++;
		}

		if (!children.isEmpty()) {
			Object child = children.get(0);
			TreeNode newrootInfo = ((TreeNode) child);
			computeMiddlePosition(newrootInfo, 4);
			List<TreeNode> thirdchildren = newrootInfo.getChildren();
			if (!thirdchildren.isEmpty()) {
				Object currentchild = thirdchildren.get(0);
				TreeNode newsuperrootInfo = ((TreeNode) currentchild);
				computeMiddlePosition(newsuperrootInfo, 7);
			}
		}
	}

	private void computePosition(TreeNode entityInfo, double numberOfNodes, double currentNode, double currentColumn) {
		
		double x = ((currentColumn + 1) / 8) * bounds.width;
		
		double y = currentNode/(numberOfNodes + 1) * bounds.height;
		
		entityInfo.getNode().setLocation(x, y);
	}

	private void computeMiddlePosition(TreeNode entityInfo, double currentColumn) {
		
		double x = (currentColumn / 8) * bounds.width;
		double middleY = bounds.height / 2;

		entityInfo.getNode().setLocation(x, middleY);
	}
}
