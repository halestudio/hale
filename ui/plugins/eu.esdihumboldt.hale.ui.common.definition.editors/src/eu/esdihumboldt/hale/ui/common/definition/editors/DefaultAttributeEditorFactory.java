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

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;

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
	public Editor<?> createEditor(Composite parent, PropertyDefinition property) {
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
			EditorChooserEditor<Object> result = new PropertyEditorChooserEditor(parent, property);
			result.selectDefaultEditor();
			return result;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory#createEditor(org.eclipse.swt.widgets.Composite,
	 *      eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter)
	 */
	@Override
	public Editor<?> createEditor(Composite parent, FunctionParameter parameter) {
		// TODO possibility to set input variables for scripts
		// TODO type field for function parameter to see whether a script was
		// used or not
		Class<?> binding = parameter.getBinding();
		if (binding != null) {
			EditorChooserEditor<Object> result = new FunctionParameterEditorChooserEditor(parent,
					binding, parameter.getValidator());
			result.selectDefaultEditor();
			return result;
		}
		else
			return new EnumerationEditor(parent, parameter.getEnumeration());
	}
}
