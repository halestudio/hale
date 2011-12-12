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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Default {@link SourceNode} implementation
 * @author Simon Templer
 */
public class SourceNodeImpl implements SourceNode {

	private final EntityDefinition entityDefinition;
	private final SourceNode parent;
	private final Set<SourceNode> children = new HashSet<SourceNode>();

	/**
	 * Constructor
	 * @param definition the associated entity definition
	 * @param sourceNodeFactory the factory for creating new source nodes
	 */
	public SourceNodeImpl(EntityDefinition definition, 
			SourceNodeFactory sourceNodeFactory) {
		this.entityDefinition = definition;
		
		EntityDefinition parentDef = AlignmentUtil.getParent(definition);
		if (parentDef != null) {
			parent = sourceNodeFactory.getSourceNode(parentDef);
			parent.addChild(this);
		}
		else {
			parent = null;
		}
	}

	/**
	 * @see SourceNode#getDefinition()
	 */
	@Override
	public Definition<?> getDefinition() {
		return entityDefinition.getDefinition();
	}

	/**
	 * @see SourceNode#getParent()
	 */
	@Override
	public SourceNode getParent() {
		return parent;
	}

	/**
	 * @see SourceNode#getEntityDefinition()
	 */
	@Override
	public EntityDefinition getEntityDefinition() {
		return entityDefinition;
	}

	/**
	 * @see SourceNode#addChild(SourceNode)
	 */
	@Override
	public void addChild(SourceNode child) {
		children.add(child);
	}

	/**
	 * @see SourceNode#getChildren()
	 */
	@Override
	public Collection<SourceNode> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((entityDefinition == null) ? 0 : entityDefinition.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceNodeImpl other = (SourceNodeImpl) obj;
		if (entityDefinition == null) {
			if (other.entityDefinition != null)
				return false;
		} else if (!entityDefinition.equals(other.entityDefinition))
			return false;
		return true;
	}

}
