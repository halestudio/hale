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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNodeVisitor;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.impl.LeftoversImpl;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Visitor that annotates a transformation tree with the values of 
 * properties in a source instance.
 * @author Simon Templer
 */
public class InstanceVisitor extends AbstractSourceToTargetVisitor {
	
//	private static final ALogger log = ALoggerFactory.getLogger(InstanceVisitor.class);
	
	private final Group instance;
	
	/**
	 * 
	 * @param instance the instance
	 */
	public InstanceVisitor(Group instance) {
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
			if (instance instanceof Instance
					&& source.getDefinition().equals(((Instance) instance).getDefinition())) {
				// check type filter (if any)
				Filter filter = source.getEntityDefinition().getFilter();
				if (filter != null) {
					if (!filter.match((Instance) instance)) {
						// instance does not match filter, don't descend further
						return false;
						/*
						 * XXX What about merged instances? Will this be OK for those?
						 * A type filter should only apply to the original instance if
						 * it is merged - but most filters should evaluate the same
						 */
					}
				}
				
				source.setValue(instance); // also sets the node to defined
				return true;
			}
			else {
				return false;
			}
		}
		else {
			Object parentValue = source.getParent().getValue();
			
			if (parentValue == null || !(parentValue instanceof Group)) {
				source.setDefined(false);
			}
			else {
				Group parentGroup = (Group) parentValue;
				
				Definition<?> currentDef = source.getDefinition();
				Object[] values = parentGroup.getProperty(currentDef.getName());
				if (values == null) {
					source.setDefined(false);
				}
				else {
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
						}
						else {
							source.setDefined(false);
						}
						// no leftovers
						return true;
					}
					
					// condition context
					Condition condition = AlignmentUtil.getContextCondition(entityDef);
					if (condition != null) {
						if (condition.getFilter() == null) {
							// assume exclusion
							source.setDefined(false);
							return false;
						}
						
						// apply condition as filter on values and continue with those values
						Collection<Object> matchedValues = new ArrayList<Object>();
						for (Object value : values) {
							// create dummy instance
							MutableInstance dummy = new DefaultInstance(null, null);
							// add value as property
							dummy.addProperty(new QName("value"), value);
							// add parent value as property
							SourceNode parentNode = source.getParent();
							if (parentNode != null && parentNode.isDefined()) {
								dummy.addProperty(new QName("parent"), parentNode.getValue());
							}
							
							if (condition.getFilter().match(dummy)) {
								matchedValues.add(value);
							}
						}
						
						values = matchedValues.toArray();
					}
					
					// (named contexts not allowed)
					
					// default behavior (default context)
					if (values.length >= 1) {
						// annotate with the first value
						Object value = values[0];
						source.setValue(value);
					}
					else {
						source.setDefined(false);
					}
					
					if (values.length > 1) {
						// handle additional values
						Object[] leftovers = new Object[values.length - 1];
						System.arraycopy(values, 1, leftovers, 0, leftovers.length);
						source.setLeftovers(new LeftoversImpl(leftovers, source));
					}
				}
			}
			
			return true;
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
