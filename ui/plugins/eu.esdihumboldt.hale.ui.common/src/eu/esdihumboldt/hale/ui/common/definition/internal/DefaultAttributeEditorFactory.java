/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.common.definition.internal;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.BooleanAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.DefaultAttributeEditor;

/**
 * Default attribute editor factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefaultAttributeEditorFactory implements AttributeEditorFactory {

	/**
	 * @see AttributeEditorFactory#createEditor(Composite, PropertyDefinition)
	 */
	@Override
	public AttributeEditor<?> createEditor(Composite parent,
			PropertyDefinition property) {
		TypeDefinition type = property.getPropertyType();
		
		// Code type
		//XXX would introduce cycle, should be solved through extension point
//		if (isCodeType(attributeType)) {
//			return new CodeListAttributeEditor(parent, attribute);
//		}
		
//		if (attributeType.isComplexType()) {
//			// complex type or type that could not be resolved
//			//TODO create composed editor (uses attribute editors and definition labels) XXX check if necessary
//			//XXX check if binding is collection?
//			return null;
//		}

		Binding typeBinding = type.getConstraint(Binding.class);
		Class<?> binding = typeBinding.getBinding();
		
		if (Boolean.class.isAssignableFrom(binding)) {
			// boolean
			return new BooleanAttributeEditor(parent);
		}
		// TODO other editors (for date/time for example)
		
		if (!type.getConstraint(HasValueFlag.class).isEnabled()) {
			return null;
		} else {
			// fall back to default editor
			return new DefaultAttributeEditor(parent, type);
		}
	}

//	/**
//	 * Determines if the given type definition represents a code type
//	 * 
//	 * @param type the type definition
//	 * 
//	 * @return if the type represents a code type
//	 */
//	public static boolean isCodeType(TypeDefinition type) {
//		while (type != null) {
//			Name typeName = type.getName();
//			//TODO improve check for code type
//			if (typeName.getLocalPart().equals("CodeType") && typeName.getNamespaceURI().toLowerCase().contains("gml")) { //$NON-NLS-1$ //$NON-NLS-2$
//				return true;
//			}
//			
//			type = type.getSuperType();
//		}
//		
//		return false;
//	}

}
