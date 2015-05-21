/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.definition.editors;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.editors.BooleanEditor;

/**
 * Default attribute editor factory
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefaultAttributeEditorFactory implements AttributeEditorFactory {

	@Override
	public AttributeEditor<?> createEditor(Composite parent, PropertyDefinition property,
			EntityDefinition entityDef, boolean allowScripts) {
		TypeDefinition type = property.getPropertyType();

//		if (attributeType.isComplexType()) {
//			// complex type or type that could not be resolved
//			//TODO create composed editor (uses attribute editors and definition labels) XXX check if necessary
//			//XXX check if binding is collection?
//			return null;
//		}

		if (!type.getConstraint(HasValueFlag.class).isEnabled())
			return null;
		else {
			if (allowScripts) {
				EditorChooserEditor<Object> result = new PropertyEditorChooserEditor(parent,
						property, entityDef);
				result.selectDefaultEditor();
				return result;
			}
			else {
				Class<?> binding = type.getConstraint(Binding.class).getBinding();
				if (Boolean.class.equals(binding))
					return new BooleanEditor(parent);
				else
					return new DefaultPropertyEditor(parent, property, entityDef);
			}
		}
	}

	@Override
	public AttributeEditor<?> createEditor(Composite parent, FunctionParameterDefinition parameter,
			ParameterValue initialValue) {
		Class<?> binding = parameter.getBinding();
		// assume String as default binding for parameters
		if (binding == null)
			binding = String.class;
		List<String> enumeration = parameter.getEnumeration();

		if (enumeration != null && !enumeration.isEmpty()) {
			EnumerationEditor editor = new EnumerationEditor(parent, parameter.getEnumeration());
			if (initialValue != null)
				editor.setAsText(initialValue.as(String.class));
			return editor;
		}
		else {
			if (parameter.isScriptable()) {
				EditorChooserEditor<Object> result = new FunctionParameterEditorChooserEditor(
						parent, binding, parameter.getValidator());
				if (initialValue != null) {
					result.selectEditor(initialValue.getType());
					result.setAsText(initialValue.as(String.class));
				}
				else
					result.selectDefaultEditor();
				return result;
			}
			else {
				AttributeEditor<?> resultEditor;
				if (Boolean.class.equals(binding))
					resultEditor = new BooleanEditor(parent);
				else
					resultEditor = new DefaultFunctionParameterEditor(parent, binding,
							parameter.getValidator());
				if (initialValue != null)
					resultEditor.setAsText(initialValue.as(String.class));
				return resultEditor;
			}
		}
	}
}
