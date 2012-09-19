/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.align;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.ui.io.ImportWizard;

/**
 * Wizard for importing alignments
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class AlignmentImportWizard extends ImportWizard<AlignmentReader> {

	/**
	 * Create a schema import wizard
	 */
	public AlignmentImportWizard() {
		super(AlignmentReader.class);
	}

}
