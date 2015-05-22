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

package eu.esdihumboldt.hale.ui.service.internal;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.service.impl.AbstractDefaultTransformationFunctionService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Transformation functions service in the UI.
 * 
 * @author Simon Templer
 */
public class HaleTransformationFunctionService extends AbstractDefaultTransformationFunctionService {

	private final AlignmentService alignmentService;

	/**
	 * @param alignmentService the alignment service for retrieving the current
	 *            alignment
	 */
	public HaleTransformationFunctionService(AlignmentService alignmentService) {
		this.alignmentService = alignmentService;
	}

	@Override
	protected Alignment getCurrentAlignment() {
		return alignmentService.getAlignment();
	}

}
