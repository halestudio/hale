/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.server.wms.wizard;

import de.fhg.igd.mapviewer.server.wms.WMSResolutionConfiguration;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.ResolutionConfigurationPage;

/**
 * Extends WMSConfigurationWizard to show additional resolution configuration
 * page
 * 
 * @author Benedikt Hiemenz
 * @param <T> the WMS client configuration type
 */
public class WMSResolutionConfigurationWizard<T extends WMSResolutionConfiguration>
		extends WMSConfigurationWizard<T> {

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS client configuration
	 * @param allowBasicEdit if changing basic settings shall be allowed
	 * @param allowSrsEdit if changing the preferred SRS is allowed
	 */
	public WMSResolutionConfigurationWizard(T configuration, boolean allowBasicEdit,
			boolean allowSrsEdit) {
		super(configuration, allowBasicEdit, allowSrsEdit);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(new ResolutionConfigurationPage(configuration));
	}

}
