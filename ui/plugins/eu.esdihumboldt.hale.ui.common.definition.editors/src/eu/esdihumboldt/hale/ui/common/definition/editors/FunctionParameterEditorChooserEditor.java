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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.definition.editors;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.parameter.Validator;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Chooser editor for function parameters.
 * 
 * @author Kai Schwierczek
 */
public class FunctionParameterEditorChooserEditor extends EditorChooserEditor<Object> {

	private final Class<?> binding;
	private final Validator validator;

	/**
	 * Default constructor.
	 * 
	 * @param parent the parent composite
	 * @param binding the function parameter's binding
	 * @param validator the function parameter's validator
	 */
	public FunctionParameterEditorChooserEditor(Composite parent, Class<?> binding,
			Validator validator) {
		super(parent, binding);

		this.binding = binding;
		this.validator = validator;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.editors.EditorChooserEditor#createDefaultEditor(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected AttributeEditor<Object> createDefaultEditor(Composite parent) {
		return new DefaultFunctionParameterEditor(parent, binding, validator);
	}
}
