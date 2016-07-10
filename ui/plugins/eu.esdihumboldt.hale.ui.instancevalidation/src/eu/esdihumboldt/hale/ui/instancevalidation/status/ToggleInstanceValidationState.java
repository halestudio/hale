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

package eu.esdihumboldt.hale.ui.instancevalidation.status;

import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReport;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationListener;
import eu.esdihumboldt.hale.ui.service.instance.validation.InstanceValidationService;

/**
 * Toggle state for the instance validation, backed by the
 * {@link InstanceValidationService}.
 * 
 * @author Kai Schwierczek
 */
public class ToggleInstanceValidationState extends State {

	private final InstanceValidationListener listener;

	/**
	 * Constructor.
	 */
	public ToggleInstanceValidationState() {
		final InstanceValidationService ivs = PlatformUI.getWorkbench()
				.getService(InstanceValidationService.class);

		listener = new InstanceValidationListener() {

			@Override
			public void validationEnabledChange() {
				setValue(ivs.isValidationEnabled());
			}

			@Override
			public void instancesValidated(InstanceValidationReport report) {
				// don't care
			}
		};
		ivs.addListener(listener);

		setValue(ivs.isValidationEnabled());
	}

	@Override
	public void dispose() {
		InstanceValidationService ivs = PlatformUI.getWorkbench()
				.getService(InstanceValidationService.class);
		ivs.removeListener(listener);
		super.dispose();
	}
}
