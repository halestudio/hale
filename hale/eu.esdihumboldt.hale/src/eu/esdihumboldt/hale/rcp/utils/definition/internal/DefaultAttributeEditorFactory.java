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

package eu.esdihumboldt.hale.rcp.utils.definition.internal;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Composite;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.BooleanAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.DoubleAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.EnumerationAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.FloatAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.IntegerAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.LongAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.StringAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.URIAttributeEditor;
import eu.esdihumboldt.hale.rcp.utils.definition.internal.editors.codelist.CodeListAttributeEditor;
import eu.esdihumboldt.hale.schemaprovider.EnumAttributeType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Default attribute editor factory
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefaultAttributeEditorFactory implements AttributeEditorFactory {

	/**
	 * @see AttributeEditorFactory#createEditor(Composite, AttributeDefinition)
	 */
	@Override
	public AttributeEditor<?> createEditor(Composite parent,
			AttributeDefinition attribute) {
		TypeDefinition attributeType = attribute.getAttributeType();
		
		// Code type
		if (isCodeType(attributeType)) {
			return new CodeListAttributeEditor(parent, attribute);
		}
		
//		if (attributeType.isComplexType()) {
//			// complex type or type that could not be resolved
//			//TODO create composed editor (uses attribute editors and definition labels) XXX check if necessary
//			//XXX check if binding is collection?
//			return null;
//		}
		
		AttributeType type = attributeType.getType(null);
		Class<?> binding = type.getBinding();
		
		if (binding.equals(URI.class)) {
			// URI
			return new URIAttributeEditor(parent);
		}
		else if (type instanceof EnumAttributeType) {
			// enumeration
			EnumAttributeType enumType = (EnumAttributeType) type;
			Set<String> values = enumType.getAllowedValues();
			
			return new EnumerationAttributeEditor(parent, new TreeSet<String>(values), enumType.otherValuesAllowed());
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
		}
		//TODO other editors
		
		if (attributeType.isComplexType()) {
			return null;
		}
		else {
			// fall back to string editor
			return new StringAttributeEditor(parent);
		}
	}

	/**
	 * Determines if the given type definition represents a code type
	 * 
	 * @param type the type definition
	 * 
	 * @return if the type represents a code type
	 */
	public static boolean isCodeType(TypeDefinition type) {
		while (type != null) {
			Name typeName = type.getName();
			//TODO improve check for code type
			if (typeName.getLocalPart().equals("CodeType") && typeName.getNamespaceURI().toLowerCase().contains("gml")) {
				return true;
			}
			
			type = type.getSuperType();
		}
		
		return false;
	}

}
