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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.context.TransformationContext;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.LeftoversImpl;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.SourceNodeImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Visitor that annotates a transformation tree with the values of properties in
 * a source instance.
 * 
 * @author Simon Templer
 */
public class InstanceVisitor extends AbstractSourceToTargetVisitor {

	private final FamilyInstance instance;
	private final TransformationTree tree;
	private final TransformationLog log;

	/**
	 * Creates an instance visitor.
	 * 
	 * @param instance the instance, may be null
	 * @param tree the transformation tree, may be null if instance is null
	 * @param log the transformation log
	 */
	public InstanceVisitor(FamilyInstance instance, TransformationTree tree,
			TransformationLog log) {
		super();
		this.instance = instance;
		this.tree = tree;
		this.log = log;

		// TODO support multiple instances with a instance per type basis or
		// even duplication of type source nodes?
	}

	/**
	 * @see AbstractSourceToTargetVisitor#visit(CellNode)
	 */
	@Override
	public boolean visit(CellNode cell) {
		return false;
	}

	/**
	 * @see AbstractSourceToTargetVisitor#visit(SourceNode)
	 */
	@Override
	public boolean visit(SourceNode source) {
		if (source.getDefinition() instanceof TypeDefinition) {
			if (instance == null)
				return false;
			// source root
			if (source.getDefinition().equals(instance.getDefinition())) {
				// check type filter (if any)
				Filter filter = source.getEntityDefinition().getFilter();
				if (filter != null && !filter.match(instance)) {
					// instance does not match filter, don't descend further
					return false;
					/*
					 * XXX What about merged instances? Will this be OK for
					 * those? A type filter should only apply to the original
					 * instance if it is merged - but most filters should
					 * evaluate the same
					 */
				}
				else {
					source.setValue(instance); // also sets the node to defined
					for (FamilyInstance child : instance.getChildren()) {
						// Find fitting SourceNodes.
						Collection<SourceNode> candidateNodes = tree
								.getRootSourceNodes(child.getDefinition());

						if (candidateNodes.isEmpty()) {
							/*
							 * No node found - but this may be because no
							 * property of the type is mapped, but there still
							 * might be child instances (in a Join) that have
							 * types with associated relations. To prevent those
							 * being skipped we add an artificial node
							 * representing the instance.
							 */
							candidateNodes = new ArrayList<>();
							EntityDefinition entityDef = new TypeEntityDefinition(
									child.getDefinition(), SchemaSpaceID.SOURCE, null);
							candidateNodes.add(new SourceNodeImpl(entityDef, null, false));
						}

						for (SourceNode candidateNode : candidateNodes) {
							filter = candidateNode.getEntityDefinition().getFilter();
							if (filter == null || filter.match(child)) {
								// XXX add to all candidates!?
								if (candidateNode.getValue() == null) {
									candidateNode.setAnnotatedParent(source);
									source.addAnnotatedChild(candidateNode);
								}
								else {
									// Duplicate here, because there is no
									// guarantee, that the Duplication
									// Visitor will visit candidateNode after
									// this node.
									SourceNodeImpl duplicateNode = new SourceNodeImpl(
											candidateNode.getEntityDefinition(),
											candidateNode.getParent(), false);
									duplicateNode.setAnnotatedParent(source);
									source.addAnnotatedChild(duplicateNode);
									TransformationContext context = candidateNode.getContext();
									duplicateNode.setContext(context);
									if (context != null) {
										context.duplicateContext(candidateNode, duplicateNode,
												Collections.<Cell> emptySet(), log);
									}
									else {
										/*
										 * Not sure what this really means if we
										 * get here.
										 * 
										 * Best guess: Probably that we weren't
										 * able to determine how the duplication
										 * of this source can be propagted to
										 * the target. Thus the duplicated node
										 * will probably not have any
										 * connection.
										 */
										log.warn(log.createMessage(
												"No transformation context for duplicated node of source "
														+ candidateNode.getDefinition()
																.getDisplayName(),
												null));
									}
									candidateNode = duplicateNode;
								}

								// run instance visitor on that annotated child
								InstanceVisitor visitor = new InstanceVisitor(child, tree, log);
								candidateNode.accept(visitor);
							}
						}
					}
					return true;
				}
			}
			else
				return false;
		}
		else {
			Object parentValue = source.getParent().getValue();

			if (parentValue == null || !(parentValue instanceof Group)) {
				source.setDefined(false);
				return false;
			}
			else {
				Group parentGroup = (Group) parentValue;
				Definition<?> currentDef = source.getDefinition();

				Object[] values = parentGroup.getProperty(currentDef.getName());
				if (values == null) {
					source.setDefined(false);
					return false;
				}

				// check for contexts
				EntityDefinition entityDef = source.getEntityDefinition();

				// index context
				Integer index = AlignmentUtil.getContextIndex(entityDef);
				if (index != null) {
					// only use the value at the given index, if present
					if (index < values.length) {
						// annotate with the value at the index
						Object value = values[index];
						source.setValue(value);
						return true;
					}
					else {
						source.setDefined(false);
						return false;
					}
				}

				// condition context
				Condition condition = AlignmentUtil.getContextCondition(entityDef);
				if (condition != null) {
					if (condition.getFilter() == null) {
						// assume exclusion
						source.setDefined(false);
						return false;
					}

					// apply condition as filter on values and continue with
					// those values
					Collection<Object> matchedValues = new ArrayList<Object>();
					for (Object value : values) {
						// determine parent
						Object parent = null;
						SourceNode parentNode = source.getParent();
						if (parentNode != null && parentNode.isDefined()) {
							parent = parentNode.getValue();
						}

						// test the condition
						if (AlignmentUtil.matchCondition(condition, value, parent)) {
							matchedValues.add(value);
						}
					}

					values = matchedValues.toArray();
					System.out.println("matched values" + values);
				}

				// (named contexts not allowed)

				// default behavior (default context)
				if (values.length >= 1) {
					// annotate with the first value
					Object value = values[0];
					source.setValue(value);
					source.setAllValues(values);
				}
				else {
					source.setDefined(false);
					return false;
				}

				if (values.length > 1) {
					// handle additional values
					Object[] leftovers = new Object[values.length - 1];
					System.arraycopy(values, 1, leftovers, 0, leftovers.length);
					source.setLeftovers(new LeftoversImpl(source, leftovers));
				}

				return true;
			}
		}
	}

	/**
	 * @see TransformationNodeVisitor#includeAnnotatedNodes()
	 */
	@Override
	public boolean includeAnnotatedNodes() {
		// annotated nodes are ignored, as these are handled when created
		return false;
	}

}
