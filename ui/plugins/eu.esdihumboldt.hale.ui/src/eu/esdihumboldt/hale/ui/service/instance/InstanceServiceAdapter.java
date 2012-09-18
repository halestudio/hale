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

package eu.esdihumboldt.hale.ui.service.instance;

import eu.esdihumboldt.hale.common.instance.model.DataSet;

/**
 * Instance service listener adapter
 * 
 * @author Simon Templer
 */
public class InstanceServiceAdapter implements InstanceServiceListener {

	/**
	 * @see InstanceServiceListener#datasetChanged(DataSet)
	 */
	@Override
	public void datasetChanged(DataSet type) {
		// please override me
	}

	/**
	 * @see InstanceServiceListener#transformationToggled(boolean)
	 */
	@Override
	public void transformationToggled(boolean enabled) {
		// please override me
	}

	/**
	 * @see InstanceServiceListener#datasetAboutToChange(DataSet)
	 */
	@Override
	public void datasetAboutToChange(DataSet type) {
		// please override me
	}

}
