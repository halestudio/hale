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

import java.net.URI;
import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.BooleanAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.DoubleAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.EnumerationAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.FloatAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.IntegerAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.LongAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.StringAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.internal.editors.URIAttributeEditor;

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
			PropertyDefinition attribute) {
		TypeDefinition attributeType = attribute.getPropertyType();
		
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
		
		//TODO honor collection binding / ElementType
		Binding typeBinding = attributeType.getConstraint(Binding.class);
		Class<?> binding = typeBinding.getBinding();
		
		if (binding.equals(URI.class)) {
			// URI
			return new URIAttributeEditor(parent);
		}
		else if (attributeType.getConstraint(Enumeration.class).getValues() != null) {
			// enumeration
			Collection<?> values = attributeType.getConstraint(Enumeration.class).getValues();
			
			return new EnumerationAttributeEditor(parent, values, attributeType.getConstraint(Enumeration.class).isAllowOthers());
		}
		else if (Boolean.class.isAssignableFrom(binding)) {
			// boolean
			return new BooleanAttributeEditor(parent);
		}
		else if (Double.class.equals(binding) || double.class.equals(binding)) {
			// double
			return new DoubleAttributeEditor(parent);
		}
		else if (Float.class.equals(binding) || float.class.equals(binding)) {
			// float
			return new FloatAttributeEditor(parent);
		}
		else if (Integer.class.equals(binding) || int.class.equals(binding)) {
			// int
			return new IntegerAttributeEditor(parent);
		}
		else if (Long.class.equals(binding) || long.class.equals(binding)) {
			// long
			return new LongAttributeEditor(parent);
		}
		else if (String.class.equals(binding)) {
			// string
			return new StringAttributeEditor(parent);
//			return new CodeListAttributeEditor(parent, attribute);
		}
		//TODO other editors
		
		if (!attributeType.getConstraint(HasValueFlag.class).isEnabled()) {
			return null;
		}
		else {
			// fall back to string editor
			return new StringAttributeEditor(parent);
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
