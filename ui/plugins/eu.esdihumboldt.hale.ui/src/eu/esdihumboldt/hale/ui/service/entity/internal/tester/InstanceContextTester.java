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

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
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
	 * The property that specifies if adding a new named instance context is
	 * allowed.
	 */
	public static final String PROPERTY_ALLOW_ADD_NAMED = "allow_add_named";
	
	/**
	 * The property that specifies if adding a new instance context index is 
	 * allowed.
	 */
	public static final String PROPERTY_ALLOW_ADD_INDEX = "allow_add_index";
	
	/**
	 * The property that specifies if adding a new condition context index is 
	 * allowed.
	 */
	public static final String PROPERTY_ALLOW_ADD_CONDITION = "allow_add_condition";
	
	/**
	 * The property that specifies if removing the instance context is allowed.
	 */
	public static final String PROPERTY_ALLOW_REMOVE = "allow_remove";
	
	/**
	 * The property that specifies if an entity definition has a named context
	 * other than the default context.
	 */
	public static final String PROPERTY_NAMED_CONTEXT = "has_named_context";
	
	/**
	 * The property that specifies if an entity definition has a context index.
	 */
	public static final String PROPERTY_INDEX_CONTEXT = "has_index_context";
	
	/**
	 * The property that specifies if an entity definition has a condition 
	 * context.
	 */
	public static final String PROPERTY_CONDITION_CONTEXT = "has_condition_context";

	/**
	 * @see IPropertyTester#test(Object, String, Object[], Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver == null) {
			return false;
		}
		
		if (property.equals(PROPERTY_ALLOW_ADD_NAMED) && receiver instanceof EntityDefinition) {
			return testAllowAddNamed((EntityDefinition) receiver);
		}
		
		if (property.equals(PROPERTY_ALLOW_ADD_INDEX) && receiver instanceof EntityDefinition) {
			return testAllowAddIndex((EntityDefinition) receiver);
		}
		
		if (property.equals(PROPERTY_ALLOW_ADD_CONDITION) && receiver instanceof EntityDefinition) {
			return testAllowAddCondition((EntityDefinition) receiver);
		}
		
		if (property.equals(PROPERTY_NAMED_CONTEXT) && receiver instanceof EntityDefinition) {
			return testHasNamedContext((EntityDefinition) receiver);
		}
		
		if (property.equals(PROPERTY_INDEX_CONTEXT) && receiver instanceof EntityDefinition) {
			return testHasIndexContext((EntityDefinition) receiver);
		}
		
		if (property.equals(PROPERTY_CONDITION_CONTEXT) && receiver instanceof EntityDefinition) {
			return testHasConditionContext((EntityDefinition) receiver);
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
		if (!testHasNamedContext(entityDef)
				&& !testHasIndexContext(entityDef)
				&& !testHasConditionContext(entityDef)) {
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
	private boolean testAllowAddNamed(EntityDefinition entityDef) {
		if (entityDef.getSchemaSpace().equals(SchemaSpaceID.SOURCE)) {
			// only allowed on target properties
			return false;
		}
		
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
	 * Tests if for the given entity definition a new index context may be
	 * created.
	 * @param entityDef the entity definition
	 * @return if adding an index context is allowed
	 */
	private boolean testAllowAddIndex(EntityDefinition entityDef) {
		if (entityDef.getSchemaSpace().equals(SchemaSpaceID.TARGET)) {
			// only allowed on source properties
			return false;
		}
		
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
	 * Tests if for the given entity definition a new condition context may be
	 * created.
	 * @param entityDef the entity definition
	 * @return if adding a condition context is allowed
	 */
	private boolean testAllowAddCondition(EntityDefinition entityDef) {
		if (entityDef.getSchemaSpace().equals(SchemaSpaceID.TARGET)
				&& entityDef.getPropertyPath().isEmpty()) {
			// not allowed on target types
			return false;
		}
		
		//FIXME do a test through the entity definition service instead
		
		return true;
	}

	/**
	 * Tests if the given entity definition has a named instance context for
	 * the last path element that is not the default context.
	 * @param entityDef the entity definition
	 * @return if the entity definition has a named instance context for the
	 * last path element
	 */
	private boolean testHasNamedContext(EntityDefinition entityDef) {
		return AlignmentUtil.getContextName(entityDef) != null;
	}
	
	/**
	 * Tests if the given entity definition has an index context for the last
	 * path element that is not the default context.
	 * @param entityDef the entity definition
	 * @return if the entity definition has an index context for the
	 * last path element
	 */
	private boolean testHasIndexContext(EntityDefinition entityDef) {
		return AlignmentUtil.getContextIndex(entityDef) != null;
	}
	
	/**
	 * Tests if the given entity definition has a condition context for the last
	 * path element that is not the default context.
	 * @param entityDef the entity definition
	 * @return if the entity definition has a condition context for the
	 * last path element
	 */
	private boolean testHasConditionContext(EntityDefinition entityDef) {
		return AlignmentUtil.getContextCondition(entityDef) != null;
	}

}
