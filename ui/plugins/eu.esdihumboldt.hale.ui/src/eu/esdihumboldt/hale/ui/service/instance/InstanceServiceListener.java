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
 * Listener for instance services
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceServiceListener {

	/**
	 * Called when a data set has changed
	 * 
	 * @param type the data set type
	 */
	public void datasetChanged(DataSet type);

	/**
	 * Called when the transformation has been enabled or disabled.
	 * 
	 * @param enabled if the transformation is enabled now
	 */
	public void transformationToggled(boolean enabled);

	/**
	 * Called when a data set is about to change.
	 * 
	 * @param type the data set type
	 */
	public void datasetAboutToChange(DataSet type);

}
