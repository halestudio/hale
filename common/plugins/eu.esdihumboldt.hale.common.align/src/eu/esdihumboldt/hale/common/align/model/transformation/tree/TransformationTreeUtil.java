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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.util.IdentityWrapper;

/**
 * Transformation tree utilities.
 * 
 * @author Simon Templer
 */
public abstract class TransformationTreeUtil {

	/**
	 * Extract the definition or cell contained in a transformation node.
	 * 
	 * @param node the node or other kind of object
	 * @return the contained definition, cell or the node/object itself
	 */
	public static Object extractObject(Object node) {
		if (node instanceof IdentityWrapper<?>) {
			node = ((IdentityWrapper<?>) node).getValue();
		}

		if (node instanceof TransformationTree) {
			return ((TransformationTree) node).getType();
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

	/**
	 * Determines if a cell is connected to a source node with eager source
	 * parameters.
	 * 
	 * @param cell the cell
	 * @param source the source node
	 * @param log the transformation log, may be <code>null</code>
	 * @param serviceProvider the service provider
	 * @return if the cell has eager source parameters connected to the source
	 *         node
	 */
	public static boolean isEager(Cell cell, SourceNode source, TransformationLog log,
			ServiceProvider serviceProvider) {
		// find the corresponding cell node
		Collection<CellNode> cells = source.getRelations(true);
		for (CellNode cellNode : cells) {
			if (cell.equals(cellNode.getCell())) {
				return isEager(cellNode, source, log, serviceProvider);
			}
		}

		throw new IllegalStateException(
				"Eager check: Could not find cell node connected to source node.");
	}

	/**
	 * Determines if a cell is connected to a source node with eager source
	 * parameters.
	 * 
	 * @param cell the cell node
	 * @param source the source node
	 * @param log the transformation log, may be <code>null</code>
	 * @param serviceProvider the service provider
	 * @return if the cell contained in the cell node has eager source
	 *         parameters connected to the source node
	 */
	public static boolean isEager(CellNode cell, SourceNode source, TransformationLog log,
			ServiceProvider serviceProvider) {
		// get all entity names the cell is associated to the source node with
		Set<String> names = cell.getSourceNames(source);

		PropertyFunctionDefinition function = FunctionUtil.getPropertyFunction(cell.getCell()
				.getTransformationIdentifier(), serviceProvider);
		if (function != null) {
			Set<? extends PropertyParameterDefinition> defSources = function.getSource();
			Set<String> eager = new HashSet<String>();
			for (PropertyParameterDefinition sourceDef : defSources) {
				String name = sourceDef.getName();
				if (sourceDef.isEager() && names.contains(name)) {
					eager.add(name);
				}
			}

			if (!eager.isEmpty()) {
				// if any connection is eager we cannot duplicate the cell

				if (log != null && eager.size() != names.size()) {
					log.warn(new TransformationMessageImpl(
							cell.getCell(),
							"Source node with a mix of eager and non-eager connections to a cell, treating as eager.",
							null));
				}

				return true;
			}
		}

		return false;
	}

}
