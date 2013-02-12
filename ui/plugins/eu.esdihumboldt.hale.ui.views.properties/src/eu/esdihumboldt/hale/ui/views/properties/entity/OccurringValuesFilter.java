/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.views.properties.entity;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesUtil;

/**
 * Filter that only accepts {@link PropertyEntityDefinition}s supported for
 * determining the occurring values.
 * 
 * @author Simon Templer
 */
public class OccurringValuesFilter extends AbstractEntityDefFilter {

	@Override
	public boolean accept(EntityDefinition input) {
		if (input instanceof PropertyEntityDefinition) {
			return OccurringValuesUtil.supportsOccurringValues((PropertyEntityDefinition) input);
		}
		return false;
	}

}
