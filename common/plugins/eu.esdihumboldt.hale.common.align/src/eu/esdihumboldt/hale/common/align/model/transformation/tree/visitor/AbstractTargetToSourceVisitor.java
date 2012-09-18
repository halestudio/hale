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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;

/**
 * Transformation tree visitor for target to source traversal.
 * 
 * @author Simon Templer
 */
public abstract class AbstractTargetToSourceVisitor extends AbstractTransformationNodeVisitor {

	/**
	 * @see TransformationNodeVisitor#isFromTargetToSource()
	 */
	@Override
	public final boolean isFromTargetToSource() {
		return true;
	}

}
