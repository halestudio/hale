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

package eu.esdihumboldt.hale.ui.service.instance.sample;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;

/**
 * Service that applies the settings on instance sampling on data to load and
 * thus creates a view on the data.
 * 
 * @author Simon Templer
 */
public interface InstanceViewService {

	/**
	 * Create a view on the given instance collection providing a reduced
	 * sub-set of instances as sample data set.
	 * 
	 * @param instances the instances
	 * @return the instance collection containing only the configured samples,
	 *         or the original instance collection if sampling is disabled
	 */
	public InstanceCollection sample(InstanceCollection instances);

	/**
	 * Determines if sampling of instances is enabled.
	 * 
	 * @return <code>true</code> if sampling is enabled, <code>false</code>
	 *         otherwise
	 */
	public boolean isEnabled();

}
