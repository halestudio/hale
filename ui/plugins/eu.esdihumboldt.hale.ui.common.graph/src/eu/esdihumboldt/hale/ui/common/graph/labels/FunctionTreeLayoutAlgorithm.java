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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutObserver;
import org.eclipse.zest.layouts.algorithms.TreeLayoutObserver.TreeNode;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

/**
 * Tree layout algorithm to show functions layered in a tree-like layout and
 * placed the root in the middle of the tree
 * 
 * @author Patrick Lieb
 */
public class FunctionTreeLayoutAlgorithm implements LayoutAlgorithm {

	private TreeLayoutObserver treeObserver;

	private final double startY = 40;

	private double firstY = 0;

	private double lastY = 0;
	
	private DisplayIndependentRectangle bounds;

	/**
	 * @see org.eclipse.zest.layouts.LayoutAlgorithm#setLayoutContext(org.eclipse.zest.layouts.interfaces.LayoutContext)
	 */
	@Override
	public void setLayoutContext(LayoutContext context) {
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

	/**
	 * @param bounds the bounds to set
	 */
	public void setBounds(DisplayIndependentRectangle bounds) {
		this.bounds = bounds;
	}

	@SuppressWarnings("rawtypes")
	private void internalApplyLayout() {
		TreeNode superRoot = treeObserver.getSuperRoot();
		int line = 0;
		List children = new ArrayList();
		for (Iterator iterator = superRoot.getChildren().iterator(); iterator
				.hasNext();) {

			TreeNode rootInfo = (TreeNode) iterator.next();
			computePosition(rootInfo, line, 0);
			if (children.isEmpty())
				children = rootInfo.getChildren();

			line++;
		}

		if (!children.isEmpty()) {
			Object child = children.get(0);
			TreeNode newrootInfo = ((TreeNode) child);
			computeMiddlePosition(newrootInfo, 4);
			List thirdchildren = newrootInfo.getChildren();
			if (!thirdchildren.isEmpty()) {
				Object currentchild = thirdchildren.get(0);
				TreeNode newsuperrootInfo = ((TreeNode) currentchild);
				computeMiddlePosition(newsuperrootInfo, 7);
			}
		}
	}

	private void computePosition(TreeNode entityInfo, double currentLine, double currentColumn) {
		
		double x = ((currentColumn + 1) / 8) * bounds.width;

		double y = currentLine * 60 + startY;

		if (firstY == 0)
			firstY = y;

		lastY = y;

		entityInfo.getNode().setLocation(x, y);
	}

	private void computeMiddlePosition(TreeNode entityInfo, double currentColumn) {
		
		double x = (currentColumn / 8) * bounds.width;
		double middleY = (firstY + lastY) / 2;

		entityInfo.getNode().setLocation(x, middleY);
	}
}
