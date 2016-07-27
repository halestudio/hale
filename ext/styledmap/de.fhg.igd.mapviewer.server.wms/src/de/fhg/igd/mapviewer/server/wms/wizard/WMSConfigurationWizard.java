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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.BasicConfigurationPage;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.LayerConfigurationPage;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.SRSConfigurationPage;

/**
 * Wizard for configuring a {@link WMSConfiguration}
 * 
 * @param <T> the WMS client configuration type
 * @author Simon Templer
 */
public class WMSConfigurationWizard<T extends WMSConfiguration> extends Wizard {

	/**
	 * The WMS configuration
	 */
	protected final T configuration;

	/**
	 * Allow user to edit basic configurations
	 */
	protected final boolean allowBasicEdit;

	/**
	 * Allow user to edit the SRS
	 */
	protected final boolean allowSrsEdit;

	/**
	 * Constructor
	 * 
	 * @param configuration the WMS client configuration
	 * @param allowBasicEdit if changing basic settings shall be allowed
	 * @param allowSrsEdit if changing the preferred SRS is allowed
	 */
	public WMSConfigurationWizard(T configuration, boolean allowBasicEdit, boolean allowSrsEdit) {
		this.configuration = configuration;
		this.allowBasicEdit = allowBasicEdit;
		this.allowSrsEdit = allowSrsEdit;
	}

	/**
	 * @see Wizard#addPages()
	 */
	@Override
	public void addPages() {

		BasicConfigurationPage conf = new BasicConfigurationPage(configuration);

		if (allowBasicEdit) {
			addPage(conf);
		}
		if (allowSrsEdit) {
			addPage(new SRSConfigurationPage(conf, configuration));
		}
		addPage(new LayerConfigurationPage(conf, configuration));
	}

	/**
	 * @see Wizard#performFinish()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean performFinish() {
		for (IWizardPage page : getPages()) {
			boolean valid = ((WMSWizardPage) page).updateConfiguration(configuration);
			if (!valid) {
				return false;
			}
		}

		return configuration.validateSettings();
	}

}
