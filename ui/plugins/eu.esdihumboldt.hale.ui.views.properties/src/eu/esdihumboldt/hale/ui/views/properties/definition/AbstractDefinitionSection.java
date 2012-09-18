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

package eu.esdihumboldt.hale.ui.views.properties.definition;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.views.properties.AbstractTextSection;

/**
 * Abstract section for definition properties
 * 
 * @author Patrick Lieb
 * @param <T> the definition type
 */
public abstract class AbstractDefinitionSection<T extends Definition<?>> extends
		AbstractTextSection {

	/**
	 * the general Definition for this package
	 */
	private T definition;

	/**
	 * @param def the Definition
	 */
	protected void setDefinition(T def) {
		definition = def;
	}

	/**
	 * @return the definition
	 */
	public T getDefinition() {
		return definition;
	}

}
