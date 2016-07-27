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

import de.fhg.igd.mapviewer.server.wms.WMSTileConfiguration;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.TileConfigurationPage;

/**
 * Wizard for configuring a {@link WMSTileConfiguration}
 * 
 * @author Simon Templer
 */
public class WMSTileConfigurationWizard extends WMSConfigurationWizard<WMSTileConfiguration> {

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS tile configuration
	 * @param allowBasicEdit if editing of the service URL shall be allowed
	 */
	public WMSTileConfigurationWizard(WMSTileConfiguration configuration, boolean allowBasicEdit) {
		super(configuration, allowBasicEdit, true);
	}

	/**
	 * @see WMSConfigurationWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();

		addPage(new TileConfigurationPage(configuration));
	}

}
