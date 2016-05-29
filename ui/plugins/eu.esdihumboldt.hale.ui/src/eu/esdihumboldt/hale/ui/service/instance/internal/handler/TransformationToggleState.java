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

package eu.esdihumboldt.hale.ui.service.instance.internal.handler;

import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceAdapter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceServiceListener;

/**
 * Command state that represents if the {@link InstanceService} transformation
 * is enabled.
 * 
 * @author Simon Templer
 */
public class TransformationToggleState extends State {

	private final InstanceServiceListener instanceListener;

	/**
	 * Default constructor
	 */
	public TransformationToggleState() {
		super();

		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		is.addListener(instanceListener = new InstanceServiceAdapter() {

			@Override
			public void transformationToggled(boolean enabled) {
				// when the transformation is toggled through any means, update
				// the state
				setValue(enabled);
			}

		});
		setValue(is.isTransformationEnabled());
	}

	/**
	 * @see org.eclipse.core.commands.State#dispose()
	 */
	@Override
	public void dispose() {
		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		is.removeListener(instanceListener);
	}

}
