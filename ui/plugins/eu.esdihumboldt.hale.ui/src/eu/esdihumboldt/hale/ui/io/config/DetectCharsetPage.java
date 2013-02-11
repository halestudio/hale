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

package eu.esdihumboldt.hale.ui.io.config;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Character set configuration page that allows detecting the encoding from a
 * stream.
 * 
 * @author Simon Templer
 */
public class DetectCharsetPage extends
		CharsetConfigurationPage<ImportProvider, IOWizard<ImportProvider>> {

	/**
	 * Default constructor.
	 */
	public DetectCharsetPage() {
		super(Mode.manualAllowDetect);
	}

}
