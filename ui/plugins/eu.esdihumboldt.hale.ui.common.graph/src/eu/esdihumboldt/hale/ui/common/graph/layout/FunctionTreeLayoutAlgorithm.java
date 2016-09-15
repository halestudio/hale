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

package eu.esdihumboldt.hale.ui.common.graph.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutObserver;
import org.eclipse.zest.layouts.algorithms.TreeLayoutObserver.TreeNode;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;

/**
 * Tree layout algorithm to show {@link FunctionDefinition}(s) layered in a tree-like
 * layout and placed the root in the middle of the tree
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

	@SuppressWarnings("unchecked")
	private void internalApplyLayout() {
		TreeNode superRoot = treeObserver.getSuperRoot();
		bounds = context.getBounds();
		int line = 1;
		List<TreeNode> children = new ArrayList<TreeNode>();
		int count = superRoot.getChildren().size();
		for (Iterator<TreeNode> iterator = superRoot.getChildren().iterator(); iterator.hasNext();) {

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

	private void computePosition(TreeNode entityInfo, double numberOfNodes, double currentNode,
			double currentColumn) {

		double x = ((currentColumn + 1) / 8) * bounds.width;
		double y = currentNode / (numberOfNodes + 1) * bounds.height;

		entityInfo.getNode().setLocation(x, y);
	}

	private void computeMiddlePosition(TreeNode entityInfo, double currentColumn) {

		double x = (currentColumn / 8) * bounds.width;
		double middleY = bounds.height / 2;

		entityInfo.getNode().setLocation(x, middleY);
	}
}
