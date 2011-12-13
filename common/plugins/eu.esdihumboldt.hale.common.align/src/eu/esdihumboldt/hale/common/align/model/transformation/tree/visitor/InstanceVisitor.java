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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Visitor that annotates a transformation tree with the values of 
 * properties in a source instance.
 * @author Simon Templer
 */
public class InstanceVisitor extends AbstractSourceToTargetVisitor {
	
	private final Instance instance;
	
	/**
	 * 
	 * @param instance the instance
	 */
	public InstanceVisitor(Instance instance) {
		super();
		this.instance = instance;
		
		//TODO support multiple instances with a instance per type basis or even duplication of type source nodes?
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
		if (source.getParent() == null) {
			// source root
			if (source.getDefinition().equals(instance.getDefinition())) {
				source.setOccurrence(1); //XXX what to do with the occurrence
				source.setValue(instance);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			Object parentValue = source.getParent().getValue();
			
			if (parentValue == null || !(parentValue instanceof Group)) {
				source.setOccurrence(0);
			}
			else {
				Group parentGroup = (Group) parentValue;
				
				Definition<?> currentDef = source.getDefinition();
				Object[] values = parentGroup.getProperty(currentDef.getName());
				if (values == null) {
					source.setOccurrence(0);
				}
				else {
					source.setOccurrence(values.length); //XXX what to do with the occurrence
					
					if (values.length >= 1) {
						// annotate with the first value
						Object value = values[0];
						source.setValue(value);
						//XXX using only this value is similar to the strategy used in HALE 2.1.x
					}
					
					if (values.length > 1) {
						//FIXME what to do with the additional values
						//XXX identify context match (if possible)
						//XXX duplicate subgraph
					}
				}
			}
			
			return true;
		}
	}

}
