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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.context;

import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Duplicates a context sub-tree in a transformation tree.
 * 
 * @author Simon Templer
 */
public interface TransformationContext {

	/**
	 * Duplicate the context sub-tree of the given context source.
	 * 
	 * @param originalSource the original context source
	 * @param duplicate the duplicate source node
	 * @param ignoreCells the cells to be ignored for the duplication
	 * @param log the transformation log
	 */
	public void duplicateContext(SourceNode originalSource, SourceNode duplicate,
			Set<Cell> ignoreCells, TransformationLog log);

}
