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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;

/**
 * Default {@link CellNode} implementation
 * @author Simon Templer
 */
@Immutable
public class CellNodeImpl extends AbstractTransformationNode implements CellNode {

	private final Cell cell;
	private final ListMultimap<SourceNode, String> sources;
	private final List<TargetNode> targets = new ArrayList<TargetNode>();

	/**
	 * Constructor
	 * @param cell the cell
	 * @param sourceNodes the factory for creating source nodes
	 */
	public CellNodeImpl(Cell cell, SourceNodeFactory sourceNodes) {
		super();
		this.cell = cell;
		
		ListMultimap<SourceNode, String> sourceList = ArrayListMultimap.create();
		
		if (cell.getSource() != null) {
			for (Entry<String, ? extends Entity> namedEntity : cell.getSource().entries()) {
				SourceNode node = sourceNodes.getSourceNode(
						namedEntity.getValue().getDefinition());
				//XXX what about filter etc.?!
				node.addRelation(this);
				sourceList.put(node, namedEntity.getKey());
			}
		}
		
		sources = Multimaps.unmodifiableListMultimap(sourceList);
	}

	/**
	 * @see TransformationNode#accept(TransformationNodeVisitor)
	 */
	@Override
	public void accept(TransformationNodeVisitor visitor) {
		if (visitor.visit(this)) {
			if (visitor.isFromTargetToSource()) {
				// visit sources
				for (SourceNode source : sources.keySet()) {
					source.accept(visitor);
				}
			}
			else {
				//XXX not supported yet
			}
		}
	}

	/**
	 * @see CellNode#getCell()
	 */
	@Override
	public Cell getCell() {
		return cell;
	}

	/**
	 * @see CellNode#getSources()
	 */
	@Override
	public List<SourceNode> getSources() {
		return new ArrayList<SourceNode>(sources.keySet());
	}

	/**
	 * @see CellNode#getSourceNames(SourceNode)
	 */
	@Override
	public Set<String> getSourceNames(SourceNode source) {
		return new HashSet<String>(sources.get(source));
	}

	/**
	 * @see CellNode#addTarget(TargetNode)
	 */
	@Override
	public void addTarget(TargetNode target) {
		targets.add(target);
	}

	/**
	 * @see CellNode#getTargets()
	 */
	@Override
	public List<TargetNode> getTargets() {
		return Collections.unmodifiableList(targets);
	}

	/**
	 * @see CellNode#setValid(boolean)
	 */
	@Override
	public void setValid(boolean valid) {
		setAnnotation(ANNOTATION_VALID, valid);
	}

	/**
	 * @see CellNode#isValid()
	 */
	@Override
	public boolean isValid() {
		Object value = getAnnotation(ANNOTATION_VALID);
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		return false;
	}

}
