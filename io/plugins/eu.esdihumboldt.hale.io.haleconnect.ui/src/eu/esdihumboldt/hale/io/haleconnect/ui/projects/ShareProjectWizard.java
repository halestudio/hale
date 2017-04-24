/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.ui.projects;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.project.HaleConnectProjectWriter;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Wizard for sharing projects on hale connect
 * 
 * @author Florian Esser
 */
public class ShareProjectWizard extends ExportWizard<HaleConnectProjectWriter> {

	private static final ALogger log = ALoggerFactory.getLogger(ShareProjectWizard.class);

	/**
	 * Create the wizard
	 */
	public ShareProjectWizard() {
		super(HaleConnectProjectWriter.class);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(new ShareProjectDetailsPage());

		for (AbstractConfigurationPage<? extends HaleConnectProjectWriter, ? extends IOWizard<HaleConnectProjectWriter>> confPage : getConfigurationPages()) {
			confPage.setPageComplete(false);
		}
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();

		if (result) {
			log.userInfo("Project was successfully uploaded to hale connect.");
			// TODO Display URL to project
		}

		return result;
	}

}
