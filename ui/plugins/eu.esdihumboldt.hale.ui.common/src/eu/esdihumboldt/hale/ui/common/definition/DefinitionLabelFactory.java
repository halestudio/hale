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
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Factory for {@link Definition} labels
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface DefinitionLabelFactory {

	/**
	 * Create a label for the given definition
	 * 
	 * @param parent the parent composite
	 * @param definition the definition
	 * @param verbose show parent type if definition is a
	 *            {@link PropertyDefinition}
	 * 
	 * @return the control presenting the label
	 */
	public Control createLabel(Composite parent, Definition<?> definition, boolean verbose);

}
