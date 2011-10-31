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

package eu.esdihumboldt.hale.ui.service.entity.internal.tester;

import java.util.List;

import org.eclipse.core.expressions.IPropertyTester;
import org.eclipse.core.expressions.PropertyTester;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;

/**
 * Tester for properties related to instance contexts 
 * @author Simon Templer
 * @since 2.5
 */
public class InstanceContextTester extends PropertyTester {
	
	/**
	 * The property namespace for this tester.
	 */
	public static final String NAMESPACE = "eu.esdihumboldt.hale.ui.service.entity";
	
	/**
	 * The property that specifies if adding a new instance context is allowed.
	 */
	public static final String PROPERTY_ALLOW_ADD = "allow_add";
	
	/**
	 * The property that specifies if removing the instance context is allowed.
	 */
	public static final String PROPERTY_ALLOW_REMOVE = "allow_remove";
	
	/**
	 * The property that specifies if an entity definition has a context other
	 * than the default context.
	 */
	public static final String PROPERTY_NAMED_CONTEXT = "has_named_context";

	/**
	 * @see IPropertyTester#test(Object, String, Object[], Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver == null) {
			return false;
		}
		
		if (property.equals(PROPERTY_ALLOW_ADD) && receiver instanceof EntityDefinition) {
			return testAllowAdd((EntityDefinition) receiver);
		}
		
		if (property.equals(PROPERTY_NAMED_CONTEXT) && receiver instanceof EntityDefinition) {
			return testHasNamedContext((EntityDefinition) receiver);
		}
		
		if (property.equals(PROPERTY_ALLOW_REMOVE) && receiver instanceof EntityDefinition) {
			return testAllowRemove((EntityDefinition) receiver);
		}
		
		return false;
	}

	/**
	 * Test if for the given entity removing the instance context is allowed.
	 * @param entityDef the entity definition
	 * @return if removing the instance context is allowed
	 */
	private boolean testAllowRemove(EntityDefinition entityDef) {
		if (!testHasNamedContext(entityDef)) {
			return false;
		}
		
		//FIXME it must be checked if there are any alignment cells related to that context
		return true;
	}

	/**
	 * Tests if for the given entity definition a new instance context may be
	 * created.
	 * @param entityDef the entity definition
	 * @return if adding an instance context is allowed
	 */
	private boolean testAllowAdd(EntityDefinition entityDef) {
		//XXX for now only a simple test based on type and cardinality
		List<ChildContext> path = entityDef.getPropertyPath();
		if (path != null && !path.isEmpty()) {
			ChildContext lastContext = path.get(path.size() - 1);
			ChildDefinition<?> lastDef = lastContext.getChild();
			
			// test type & cardinality
			Cardinality cardinality;
			try {
				cardinality = DefinitionUtil.getCardinality(lastDef);
			} catch (Exception e) {
				return false;
			}
			
			if (cardinality.getMaxOccurs() == Cardinality.UNBOUNDED) {
				return true;
			}
			return cardinality.getMaxOccurs() > 1;
		}
		
		//FIXME do a test through the entity definition service instead
		
		return false;
	}

	/**
	 * Tests if the given entity definition has an instance context for the last
	 * path element that is not the default context.
	 * @param entityDef the entity definition
	 * @return if the entity definition has a named instance context for the
	 * last path element
	 */
	private boolean testHasNamedContext(EntityDefinition entityDef) {
		List<ChildContext> path = entityDef.getPropertyPath();
		if (path != null && !path.isEmpty()) {
			return path.get(path.size() - 1).getContextName() != null;
		}
		
		return false;
	}

}
