/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.service.impl;

import eu.esdihumboldt.hale.common.align.model.Alignment;

/**
 * Function service based on a specific alignment.
 * 
 * @author Simon Templer
 */
public class AlignmentFunctionService extends AbstractDefaultFunctionService {

	private final Alignment alignment;

	/**
	 * Create a function service based on the given alignment.
	 * 
	 * @param alignment the alignment
	 */
	public AlignmentFunctionService(Alignment alignment) {
		this.alignment = alignment;
	}

	@Override
	protected Alignment getCurrentAlignment() {
		return alignment;
	}

}
