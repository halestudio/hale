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

package eu.esdihumboldt.hale.ui.common;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.util.VariableReplacer;

/**
 * Attribute editor interface
 * 
 * @param <T> the attribute value type/binding
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AttributeEditor<T> extends Editor<T> {

	/**
	 * Set the editor value as text
	 * 
	 * @param text the value to set as text
	 */
	public void setAsText(String text);

	/**
	 * Get the editor value as text
	 * 
	 * @return the text representation of the editor value
	 */
	public String getAsText();

	/**
	 * Sets available variables. Editors may ignore this.
	 * 
	 * @param properties the property variables
	 */
	public void setVariables(Collection<PropertyEntityDefinition> properties);

	/**
	 * Provide a replacer for variable references inside a string value. Mainly
	 * intended for replacement before doing a validation.
	 * 
	 * @param variableReplacer the variableReplacer to set
	 */
	public void setVariableReplacer(VariableReplacer variableReplacer);

	/**
	 * Returns the type of the value edited in this editor.
	 * 
	 * @return the type of the value edited in this editor
	 */
	public String getValueType();
}
