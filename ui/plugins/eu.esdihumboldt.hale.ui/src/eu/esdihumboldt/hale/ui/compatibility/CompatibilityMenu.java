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

package eu.esdihumboldt.hale.ui.compatibility;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.eclipse.ui.util.extension.exclusive.ExclusiveExtensionContribution;
import de.fhg.igd.eclipse.util.extension.exclusive.ExclusiveExtension;
import eu.esdihumboldt.hale.common.align.compatibility.CompatibilityMode;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityModeFactory;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;

/**
 * Contribution Item for displaying a compatibility mode selection menu
 * 
 * @author Sebastian Reinhardt
 */
public class CompatibilityMenu
		extends ExclusiveExtensionContribution<CompatibilityMode, CompatibilityModeFactory> {

	/**
	 * @see de.fhg.igd.eclipse.ui.util.extension.AbstractExtensionContribution#initExtension()
	 */
	@Override
	protected ExclusiveExtension<CompatibilityMode, CompatibilityModeFactory> initExtension() {

		return PlatformUI.getWorkbench().getService(CompatibilityService.class);
	}

}