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

package eu.esdihumboldt.hale.ui.common.definition;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Factory for editors based on {@link PropertyDefinition}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AttributeEditorFactory {

	/**
	 * Create an attribute editor for the given property.
	 * 
	 * @param parent the parent composite of the editor control
	 * @param property the property definition
	 * @param entityDef the property entity definition representing the
	 *            property, may be <code>null</code> if unknown or unavailable
	 * @param allowScripts whether the property may be edited with a script
	 *            editor
	 * @return the attribute editor or <code>null</code> if no editor could be
	 *         created for the property
	 */
	public AttributeEditor<?> createEditor(Composite parent, PropertyDefinition property,
			EntityDefinition entityDef, boolean allowScripts);

	/**
	 * Create an attribute editor for the given function parameter.
	 * 
	 * @param parent the parent composite of the editor control
	 * @param parameter the function parameter
	 * @param initialValue initial value, may be <code>null</code>
	 * @return the attribute editor
	 */
	public AttributeEditor<?> createEditor(Composite parent, FunctionParameterDefinition parameter,
			ParameterValue initialValue);
}
