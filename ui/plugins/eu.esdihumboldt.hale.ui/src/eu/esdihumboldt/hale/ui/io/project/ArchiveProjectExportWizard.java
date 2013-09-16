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

package eu.esdihumboldt.hale.ui.io.project;

import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectWriter;
import eu.esdihumboldt.hale.ui.io.ExportWizard;

/**
 * Wizard for exporting project archives
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectExportWizard extends ExportWizard<ArchiveProjectWriter> {

	/**
	 * Default constructor
	 */
	public ArchiveProjectExportWizard() {
		super(ArchiveProjectWriter.class);
	}

}
