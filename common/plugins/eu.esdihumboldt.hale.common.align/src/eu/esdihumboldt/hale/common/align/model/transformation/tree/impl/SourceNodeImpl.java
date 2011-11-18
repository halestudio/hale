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

}
