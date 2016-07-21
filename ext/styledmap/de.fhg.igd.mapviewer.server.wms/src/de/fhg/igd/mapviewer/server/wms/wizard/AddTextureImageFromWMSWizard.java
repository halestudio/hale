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
import de.fhg.igd.mapviewer.server.wms.wizard.pages.BasicConfigurationWithWMSListPage;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.LayerConfigurationPage;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.ResolutionConfigurationPage;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.SRSConfigurationPage;

/**
 * Derived wizard for the Add Teture Image From WMS feature, described here
 * de.cs3d.ui.views.appearanceAddTextureImageFromWMSAction.run() Extends the
 * basic configuration page by an list showing all stored WMS.
 * 
 * @author Benedikt Hiemenz
 * @param <T> the WMS client configuration type
 */
public class AddTextureImageFromWMSWizard<T extends WMSResolutionConfiguration>
		extends WMSConfigurationWizard<T> {

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS client configuration
	 * @param allowBasicEdit if changing basic settings shall be allowed
	 * @param allowSrsEdit if changing the preferred SRS is allowed
	 */
	public AddTextureImageFromWMSWizard(T configuration, boolean allowBasicEdit,
			boolean allowSrsEdit) {
		super(configuration, allowBasicEdit, allowSrsEdit);
	}

	@Override
	public void addPages() {
		BasicConfigurationWithWMSListPage conf = new BasicConfigurationWithWMSListPage(
				configuration);

		if (allowBasicEdit) {
			addPage(conf);
		}
		if (allowSrsEdit) {
			addPage(new SRSConfigurationPage(conf, configuration));
		}
		addPage(new LayerConfigurationPage(conf, configuration));
		addPage(new ResolutionConfigurationPage(configuration));
	}
}
