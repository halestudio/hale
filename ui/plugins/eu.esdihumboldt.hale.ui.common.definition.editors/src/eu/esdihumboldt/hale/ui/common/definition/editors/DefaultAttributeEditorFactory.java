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
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.ui.common.Editor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory;
import eu.esdihumboldt.hale.ui.common.editors.BooleanEditor;

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

		Binding typeBinding = type.getConstraint(Binding.class);
		Class<?> binding = typeBinding.getBinding();

		if (Boolean.class.isAssignableFrom(binding)) {
			// boolean
			return new BooleanEditor(parent);
		}
		// TODO other editors (for date/time for example)

		if (!type.getConstraint(HasValueFlag.class).isEnabled()) {
			return null;
		}
		else {
			// fall back to default editor
			return new DefaultAttributeEditor(parent, property);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditorFactory#createEditor(org.eclipse.swt.widgets.Composite,
	 *      eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter)
	 */
	@Override
	public Editor<?> createEditor(Composite parent, FunctionParameter parameter) {
		// TODO possibility to set input variables for scripts
		// TODO a base editor for cases where there are multiple available
		// editors
		// in which one can select the one to use
		// TODO default editor
		// TODO boolean editor as default possibility of base editor for boolean
		// TODO enumeration editor
		// TODO same for PropertyDefinition above
		// TODO type field for function parameter to see whether a script was
		// used or not
		Class<?> binding = parameter.getBinding();
		if (binding != null) {
			if (Boolean.class.isAssignableFrom(binding))
				return new BooleanEditor(parent);
			else {
				//
			}
		}
		else {
			// enumeration
		}
		return null;
	}
}
