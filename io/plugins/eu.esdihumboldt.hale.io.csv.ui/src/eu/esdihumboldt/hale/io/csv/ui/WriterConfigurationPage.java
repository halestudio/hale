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

package eu.esdihumboldt.hale.io.csv.ui;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;

/**
 * Configuration page for the csv writer
 * 
 * @author Patrick Lieb
 */
public class WriterConfigurationPage extends AbstractCSVConfigurationPage<ExportProvider> {

	/**
	 * Default Constructor
	 */
	protected WriterConfigurationPage() {
		super("csv_writer.conf");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not required
	}
}
