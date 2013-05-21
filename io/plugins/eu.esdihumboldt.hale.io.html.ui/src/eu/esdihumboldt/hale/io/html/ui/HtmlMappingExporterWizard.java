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

package eu.esdihumboldt.hale.io.html.ui;

import eu.esdihumboldt.hale.io.html.HtmlMappingExporter;
import eu.esdihumboldt.hale.ui.io.ExportWizard;

/**
 * Wizard for the html mapping exporter
 * 
 * @author Patrick Lieb
 */
public class HtmlMappingExporterWizard extends ExportWizard<HtmlMappingExporter> {

	/**
	 * Create an alignment mapping export wizard
	 */
	public HtmlMappingExporterWizard() {
		super(HtmlMappingExporter.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		// add the html mapping exporter configuration page
		addPage(new HtmlMappingExporterConfigurationPage());
		super.addPages();
	}
}
