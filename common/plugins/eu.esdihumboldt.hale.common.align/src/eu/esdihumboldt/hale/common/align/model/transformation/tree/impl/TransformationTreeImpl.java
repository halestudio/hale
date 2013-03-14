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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default {@link TransformationTree} implementation
 * 
 * @author Simon Templer
 */
@Immutable
public class TransformationTreeImpl extends AbstractGroupNode implements TransformationTree {

	private static final ALogger log = ALoggerFactory.getLogger(TransformationTreeImpl.class);

	private final Cell typeCell;
	private final TypeDefinition type;
	private final SourceNodeFactory sourceNodes;
	private final List<TargetNode> children;

	/**
	 * Create a transformation tree based on a type cell.
	 * 
	 * @param alignment the alignment holding the cells
	 * @param typeCell the type cell this tree is representing
	 */
	public TransformationTreeImpl(Alignment alignment, Cell typeCell) {
		super(null);
		this.typeCell = typeCell;
		this.type = ((Type) CellUtil.getFirstEntity(typeCell.getTarget())).getDefinition()
				.getDefinition();

		sourceNodes = new SourceNodeFactory();

		Collection<? extends Cell> cells = getRelevantPropertyCells(alignment, typeCell);

		// partition cells by child
		ListMultimap<EntityDefinition, CellNode> childCells = ArrayListMultimap.create();
		for (Cell cell : cells) {
			cell = reparentCell(cell, typeCell);
			CellNode node = new CellNodeImpl(cell, sourceNodes);
			for (Entity target : cell.getTarget().values()) {
				EntityDefinition targetDef = target.getDefinition();
				List<ChildContext> path = targetDef.getPropertyPath();
				if (path != null && !path.isEmpty()) {
					// store cell with child
					childCells.put(AlignmentUtil.deriveEntity(targetDef, 1), node);
				}
			}
		}

		// create child cells
		List<TargetNode> childList = new ArrayList<TargetNode>();
		for (Entry<EntityDefinition, Collection<CellNode>> childEntry : childCells.asMap()
				.entrySet()) {
			TargetNode childNode = new TargetNodeImpl(childEntry.getKey(), childEntry.getValue(),
					type, 1, this);
			childList.add(childNode);
		}

		children = Collections.unmodifiableList(childList);
	}

	/**
	 * Creates a copy of the given property cell with all source and target
	 * types matching those off the given type cell.<br>
	 * If the types already match they are unchanged. If the types are sub types
	 * of the types of the type cell they are changed. Otherwise an
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @param propertyCell the property cell to update
	 * @param typeCell the type cell with the target types
	 * @return the updated cell
	 */
	private Cell reparentCell(Cell propertyCell, Cell typeCell) {
		ListMultimap<String, Entity> sources = propertyCell.getSource() == null ? null
				: ArrayListMultimap.<String, Entity> create();
		ListMultimap<String, Entity> targets = ArrayListMultimap.create();
		boolean updateNecessary = false;

		// XXX are updates to the property path needed?

		TypeDefinition typeCellTargetType = ((Type) CellUtil.getFirstEntity(typeCell.getTarget()))
				.getDefinition().getDefinition();
		for (Entry<String, ? extends Entity> target : propertyCell.getTarget().entries()) {
			TypeDefinition propertyCellTargetType = target.getValue().getDefinition().getType();
			if (propertyCellTargetType.equals(typeCellTargetType))
				targets.put(target.getKey(), target.getValue());
			else if (DefinitionUtil.isSuperType(typeCellTargetType, propertyCellTargetType)) {
				PropertyEntityDefinition oldDef = (PropertyEntityDefinition) target.getValue()
						.getDefinition();
				targets.put(target.getKey(), new DefaultProperty(new PropertyEntityDefinition(
						typeCellTargetType, oldDef.getPropertyPath(), SchemaSpaceID.TARGET, null)));
				updateNecessary = true;
			}
			else {
				// a cell with targets in more than one type
				throw new IllegalStateException();
			}
		}

		if (sources != null) {
			// collect source entity definitions
			Collection<TypeEntityDefinition> typeCellSourceTypes = new ArrayList<TypeEntityDefinition>();
			for (Entity entity : typeCell.getSource().values())
				typeCellSourceTypes.add((TypeEntityDefinition) entity.getDefinition());
			for (Entry<String, ? extends Entity> source : propertyCell.getSource().entries()) {
				TypeEntityDefinition propertyCellSourceType = AlignmentUtil.getTypeEntity(source
						.getValue().getDefinition());
				if (typeCellSourceTypes.contains(propertyCellSourceType))
					sources.put(source.getKey(), source.getValue());
				else {
					boolean matchFound = false;
					// try to find a matching source
					// XXX what if multiple sources match?
					// currently all are added
					// other choices: first match
					// or maybe prioritize matching filter and closest super
					// type
					// maybe the whole cell should be duplicated?
					for (TypeEntityDefinition typeCellSourceType : typeCellSourceTypes) {
						if (DefinitionUtil.isSuperType(typeCellSourceType.getDefinition(),
								propertyCellSourceType.getDefinition())
								&& (propertyCellSourceType.getFilter() == null || propertyCellSourceType
										.getFilter().equals(typeCellSourceType.getFilter()))) {
							if (matchFound)
								log.warn("Inherited property cell source matches multiple sources of type cell.");
							matchFound = true;
							PropertyEntityDefinition oldDef = (PropertyEntityDefinition) source
									.getValue().getDefinition();
							sources.put(
									source.getKey(),
									new DefaultProperty(new PropertyEntityDefinition(
											typeCellSourceType.getDefinition(), oldDef
													.getPropertyPath(), SchemaSpaceID.SOURCE,
											typeCellSourceType.getFilter())));
							updateNecessary = true;
							// XXX break; if only one match should be added
						}
					}
					if (!matchFound) {
						// a cell with a source that does not match the type
						// cell
						throw new IllegalStateException();
					}
				}
			}
		}

		if (updateNecessary) {
			MutableCell copy = new DefaultCell(propertyCell);
			copy.setSource(sources);
			copy.setTarget(targets);
			propertyCell = copy;
		}

		return propertyCell;
	}

	/**
	 * Get the property cells relevant for the transformation tree from the
	 * given alignment. The default implementation returns all property cells
	 * related to the type cell.
	 * 
	 * @param alignment the alignment
	 * @param typeCell the type cell type
	 * @return the property cells
	 */
	protected Collection<? extends Cell> getRelevantPropertyCells(Alignment alignment, Cell typeCell) {
		return alignment.getPropertyCells(typeCell);
	}

	/**
	 * @see TransformationNode#accept(TransformationNodeVisitor)
	 */
	@Override
	public void accept(TransformationNodeVisitor visitor) {
		if (visitor.visit(this)) {
			if (visitor.isFromTargetToSource()) {
				// visit children
				for (TargetNode child : getChildren(visitor.includeAnnotatedNodes())) {
					child.accept(visitor);
				}
			}
			else {
				// visit leafs
				for (SourceNode node : sourceNodes.getNodes()) {
					if (node.getParent() == null) {
						node.accept(visitor);
					}
				}
			}
		}
		visitor.leave(this);
	}

	/**
	 * @see TransformationTree#getSourceNode(TypeEntityDefinition)
	 */
	@Override
	public SourceNode getSourceNode(TypeEntityDefinition type) {
		return sourceNodes.getSourceNode(type);
	}

	/**
	 * @see AbstractGroupNode#getFixedChildren()
	 */
	@Override
	public List<TargetNode> getFixedChildren() {
		return children;
	}

	/**
	 * @see TransformationTree#getType()
	 */
	@Override
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * @see TransformationTree#getRootSourceNodes(TypeDefinition)
	 */
	@Override
	public Collection<SourceNode> getRootSourceNodes(TypeDefinition type) {
		List<SourceNode> rootNodes = new ArrayList<SourceNode>();
		for (SourceNode node : sourceNodes.getNodes())
			if (node.getParent() == null && node.getDefinition().equals(type))
				rootNodes.add(node);
		return rootNodes;
	}

	/**
	 * @see TransformationTree#getRootSourceNodes()
	 */
	@Override
	public Collection<SourceNode> getRootSourceNodes() {
		List<SourceNode> rootNodes = new ArrayList<SourceNode>();
		for (SourceNode node : sourceNodes.getNodes())
			if (node.getParent() == null)
				rootNodes.add(node);
		return rootNodes;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree#getTypeCell()
	 */
	@Override
	public Cell getTypeCell() {
		return typeCell;
	}
}
