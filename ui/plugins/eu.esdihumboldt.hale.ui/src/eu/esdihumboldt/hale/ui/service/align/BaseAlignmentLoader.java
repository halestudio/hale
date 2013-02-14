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

package eu.esdihumboldt.hale.ui.service.align;

import eu.esdihumboldt.hale.common.align.model.MutableAlignment;

/**
 * Interface for the alignment service to actually load base alignments.
 * 
 * @author Kai Schwierczek
 */
public interface BaseAlignmentLoader {

	/**
	 * Instructs the loader to load its configured base alignment for the given
	 * alignment.
	 * 
	 * @param alignment the alignment to add base alignments to
	 * @return whether the process succesfully added at least one base alignment
	 */
	public boolean load(MutableAlignment alignment);
}
