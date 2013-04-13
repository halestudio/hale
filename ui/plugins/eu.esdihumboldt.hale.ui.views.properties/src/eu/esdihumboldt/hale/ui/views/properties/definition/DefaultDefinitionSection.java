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

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.views.properties.AbstractTextSection;

/**
 * The default definition section
 * 
 * @author Patrick Lieb
 * @param <T> the definition type
 */
public abstract class DefaultDefinitionSection<T extends Definition<?>> extends
		AbstractDefinitionSection<T> {

	/**
	 * @see AbstractTextSection#setInput(Object)
	 */
	@Override
	protected void setInput(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof Entity) {
			input = ((Entity) input).getDefinition();
		}

		if (input instanceof EntityDefinition) {
			input = ((EntityDefinition) input).getDefinition();
		}

		setDefinition(extract(input));
	}

	/**
	 * Extract the definition. The default implementation just does a
	 * corresponding cast.
	 * 
	 * @param input the input object
	 * @return the definition
	 */
	@SuppressWarnings("unchecked")
	protected T extract(Object input) {
		return (T) input;
	}

}
