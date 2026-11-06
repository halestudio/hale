/*
 * Copyright (c) 2026 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.util.graph;

import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

/**
 * Layout algorithm that places the nodes with their center on the locations
 * computed by another layout algorithm.
 * 
 * The layout algorithms (e.g. TreeLayoutAlgorithm and hale's own) compute the
 * center of each node, as documented by {@link EntityLayout#setLocation}. Zest
 * 2 placed the nodes accordingly, but GEF Classic uses the location as the top
 * left corner of the node since version 3.21. Without this correction the nodes
 * are shifted by half their size: the columns of a mapping are left aligned
 * instead of centered, and the target nodes extend beyond the right edge.
 */
public class CenteredLayoutAlgorithm implements LayoutAlgorithm {

	private final LayoutAlgorithm delegate;

	private LayoutContext context;

	/**
	 * @param delegate the layout algorithm computing the node centers
	 */
	public CenteredLayoutAlgorithm(LayoutAlgorithm delegate) {
		this.delegate = delegate;
	}

	/**
	 * @return the layout algorithm computing the node centers
	 */
	public LayoutAlgorithm getDelegate() {
		return delegate;
	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		this.context = context;
		delegate.setLayoutContext(context);
	}

	@Override
	public void applyLayout(boolean clean) {
		if (context == null) {
			delegate.applyLayout(clean);
			return;
		}

		Map<EntityLayout, DisplayIndependentPoint> before = new IdentityHashMap<>();
		for (EntityLayout entity : context.getEntities()) {
			before.put(entity, entity.getLocation());
		}

		delegate.applyLayout(clean);

		// only move the nodes the delegate placed, the others already have
		// their final location
		for (EntityLayout entity : context.getEntities()) {
			DisplayIndependentPoint location = entity.getLocation();
			DisplayIndependentPoint previous = before.get(entity);
			if (previous != null && previous.x == location.x && previous.y == location.y) {
				continue;
			}
			DisplayIndependentDimension size = entity.getSize();
			entity.setLocation(location.x - size.width / 2, location.y - size.height / 2);
		}
	}

	/**
	 * Get the layout algorithm that computes the node locations.
	 * 
	 * @param algorithm a layout algorithm, may be <code>null</code>
	 * @return the delegate if the algorithm is a {@link CenteredLayoutAlgorithm},
	 *         otherwise the algorithm itself
	 */
	public static LayoutAlgorithm unwrap(LayoutAlgorithm algorithm) {
		if (algorithm instanceof CenteredLayoutAlgorithm) {
			return ((CenteredLayoutAlgorithm) algorithm).getDelegate();
		}
		return algorithm;
	}

}
