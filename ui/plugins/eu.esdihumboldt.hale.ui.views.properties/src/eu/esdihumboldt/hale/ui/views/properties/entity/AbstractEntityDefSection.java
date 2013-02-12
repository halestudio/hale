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

package eu.esdihumboldt.hale.ui.views.properties.entity;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.ui.views.properties.AbstractSingleObjectSection;

/**
 * Entity definition section base class.
 * 
 * @author Simon Templer
 */
public abstract class AbstractEntityDefSection extends AbstractSingleObjectSection {

	private EntityDefinition entity;

	/**
	 * @see AbstractSingleObjectSection#setInput(Object)
	 */
	@Override
	protected void setInput(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof EntityDefinition) {
			setEntity((EntityDefinition) input);
		}
	}

	/**
	 * Set the input entity definition.
	 * 
	 * @param input the entity definition
	 */
	private void setEntity(EntityDefinition input) {
		this.entity = input;
	}

	/**
	 * Get the current entity definition.
	 * 
	 * @return the entity definition
	 */
	public EntityDefinition getEntity() {
		return entity;
	}

}
