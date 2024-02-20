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
package eu.esdihumboldt.hale.ui.service.index.internal;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.index.InstanceIndexUpdateService;

/**
 * Early startup plugin to add an {@link InstanceIndexUpdateService} listener to
 * the {@link AlignmentService}.
 * 
 * @author Florian Esser
 */
public class IndexStartup implements IStartup {

	@Override
	public void earlyStartup() {
		final InstanceIndexUpdateService indexUpdater = PlatformUI.getWorkbench()
				.getService(InstanceIndexUpdateService.class);
		final AlignmentService alignmentService = PlatformUI.getWorkbench()
				.getService(AlignmentService.class);
		alignmentService.addListener(indexUpdater);
	}

}
