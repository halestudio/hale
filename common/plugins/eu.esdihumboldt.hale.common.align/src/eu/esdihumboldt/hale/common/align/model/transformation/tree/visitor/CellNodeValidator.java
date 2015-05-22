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

import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.TransformationMessageImpl;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.util.Pair;

/**
 * Visitor that validates cell nodes. The tree should have already been
 * annotated with the source instance values when applying this visitor.
 * 
 * @author Simon Templer
 */
public class CellNodeValidator extends AbstractTargetToSourceVisitor {

	private static final ALogger log = ALoggerFactory.getLogger(CellNodeValidator.class);
	/**
	 * the transformation reporter
	 */
	protected final TransformationReporter reporter;

	private final ServiceProvider serviceProvider;

	/**
	 * Constructor
	 * 
	 * @param reporter the transformation reporter
	 * @param serviceProvider the service provider
	 */
	public CellNodeValidator(TransformationReporter reporter, ServiceProvider serviceProvider) {
		this.reporter = reporter;
		this.serviceProvider = serviceProvider;
	}

	/**
	 * @see AbstractTargetToSourceVisitor#visit(CellNode)
	 */
	@Override
	public boolean visit(CellNode node) {
		// evaluate if for the cell all needed inputs are set
		Cell cell = node.getCell();

		// collect source and target nodes per entity name

		ListMultimap<String, Pair<SourceNode, Entity>> sources = ArrayListMultimap.create();
		if (cell.getSource() != null) {
			for (Entry<String, ? extends Entity> sourceEntry : cell.getSource().entries()) {
				String name = sourceEntry.getKey();
				Entity entity = sourceEntry.getValue();
				SourceNode sourceNode = findSourceNode(node, entity);
				if (sourceNode != null) {
					if (sourceNode.isDefined()) {
						sources.put(name, new Pair<SourceNode, Entity>(sourceNode, entity));
					}
				}
				else {
					log.error("Source node for entity not found.");
				}
			}
		}

		ListMultimap<String, Pair<TargetNode, Entity>> targets = ArrayListMultimap.create();
		for (Entry<String, ? extends Entity> targetEntry : cell.getTarget().entries()) {
			String name = targetEntry.getKey();
			Entity entity = targetEntry.getValue();
			TargetNode targetNode = findTargetNode(node, entity);
			if (targetNode != null) {
				targets.put(name, new Pair<TargetNode, Entity>(targetNode, entity));
			}
			else {
				log.error("Target node for entity not found.");
			}
		}

		boolean valid = validate(node, sources, targets);

		if (valid) {
			processValid(cell, sources, targets);
		}

		node.setValid(valid);

		// don't visit source nodes
		return false;
	}

	/**
	 * Validate a cell node.
	 * 
	 * @param node the cell node
	 * @param sources the named source entities and nodes
	 * @param targets the named target entities and nodes
	 * @return if the cell node is valid for execution
	 */
	protected boolean validate(CellNode node,
			ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		String functionId = node.getCell().getTransformationIdentifier();
		FunctionDefinition<?> function = serviceProvider.getService(FunctionService.class)
				.getFunction(functionId);
		if (function != null) {
			// check source node occurrence for mandatory source entities
			for (ParameterDefinition sourceParam : function.getSource()) {
				int min = sourceParam.getMinOccurrence();
				if (sources.get(sourceParam.getName()).size() < min) {
					return false;
				}
			}

			// TODO additional validation?

			return true;
		}
		else {
			reporter.error(new TransformationMessageImpl(node.getCell(),
					"Invalid cell - function not found: " + functionId, null));
		}

		return false;
	}

	/**
	 * Process a valid cell node.
	 * 
	 * @param cell the associated cell
	 * @param sources the named source entities and nodes
	 * @param targets the named target entities and nodes
	 */
	protected void processValid(Cell cell, ListMultimap<String, Pair<SourceNode, Entity>> sources,
			ListMultimap<String, Pair<TargetNode, Entity>> targets) {
		// override me
	}

	/**
	 * Find the matching target node for the given entity in a cell node.
	 * 
	 * @param node the cell node the source node must be associated to
	 * @param entity the entity
	 * @return the target node or <code>null</code>
	 */
	private TargetNode findTargetNode(CellNode node, Entity entity) {
		for (TargetNode target : node.getTargets()) {
			if (target.getEntityDefinition().equals(entity.getDefinition())) {
				return target;
			}
		}

		return null;
	}

	/**
	 * Find the matching source node for the given entity in a cell node.
	 * 
	 * @param node the cell node the source node must be associated to
	 * @param entity the entity
	 * @return the source node or <code>null</code>
	 */
	private SourceNode findSourceNode(CellNode node, Entity entity) {
		for (SourceNode source : node.getSources()) {
			if (source.getEntityDefinition().equals(entity.getDefinition())) {
				return source;
			}
		}
		return null;
	}

	/**
	 * @see TransformationNodeVisitor#includeAnnotatedNodes()
	 */
	@Override
	public boolean includeAnnotatedNodes() {
		// include all nodes
		return true;
	}

}
