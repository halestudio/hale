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
package eu.esdihumboldt.hale.ui.service.index;

import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * Listener interface for the {@link AlignmentService} for updating index
 * configurations when the alignment changes.
 * 
 * @author Florian Esser
 */
public interface InstanceIndexUpdateService extends AlignmentServiceListener {

	/**
	 * Called when source instances are cleared
	 */
	void instancesCleared();
}
