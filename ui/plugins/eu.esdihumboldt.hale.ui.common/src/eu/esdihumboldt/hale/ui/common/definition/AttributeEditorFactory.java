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

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.common.Editor;

/**
 * Factory for editors based on {@link PropertyDefinition}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AttributeEditorFactory {

	/**
	 * Create an attribute editor for the given attribute
	 * 
	 * @param parent the parent composite of the editor control
	 * @param attribute the attribute definition
	 * 
	 * @return the attribute editor or <code>null</code> if no editor could be
	 *         created for the attribute
	 */
	public Editor<?> createEditor(Composite parent, PropertyDefinition attribute);

}
