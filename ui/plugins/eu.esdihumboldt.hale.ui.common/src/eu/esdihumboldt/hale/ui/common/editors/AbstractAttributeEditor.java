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

package eu.esdihumboldt.hale.ui.common.editors;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.util.VariableReplacer;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Abstract base class for editors for events.
 * 
 * @author Kai Schwierczek
 * @param <T> the attribute value type/binding
 */
public abstract class AbstractAttributeEditor<T> extends AbstractEditor<T>
		implements AttributeEditor<T> {

	private VariableReplacer variableReplacer = null;

	/**
	 * @return the variableReplacer
	 */
	public VariableReplacer getVariableReplacer() {
		return variableReplacer;
	}

	@Override
	public void setVariableReplacer(VariableReplacer variableReplacer) {
		this.variableReplacer = variableReplacer;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setVariables(java.util.Collection)
	 */
	@Override
	public void setVariables(Collection<PropertyEntityDefinition> properties) {
		// ignore variables by default
	}
}
