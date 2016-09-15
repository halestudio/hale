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

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Chooser editor for property definitions.
 * 
 * @author Kai Schwierczek
 */
public class PropertyEditorChooserEditor extends EditorChooserEditor<Object> {

	private final PropertyDefinition property;
	private final EntityDefinition entityDef;

	/**
	 * Default constructor.
	 * 
	 * @param parent the parent composite
	 * @param property the property definition
	 * @param entityDef the property entity definition representing the
	 *            property, may be <code>null</code> if unknown or unavailable
	 */
	public PropertyEditorChooserEditor(Composite parent, PropertyDefinition property,
			EntityDefinition entityDef) {
		super(parent, property.getPropertyType().getConstraint(Binding.class).getBinding());
		this.property = property;
		this.entityDef = entityDef;
	}

	@Override
	protected AttributeEditor<Object> createDefaultEditor(Composite parent) {
		return new DefaultPropertyEditor(parent, property, entityDef);
	}
}
