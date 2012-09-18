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

package eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.extension;

import org.eclipse.ui.PlatformUI;

import de.cs3d.ui.util.eclipse.extension.AbstractExtensionContribution;
import de.cs3d.ui.util.eclipse.extension.exclusive.ExclusiveExtensionContribution;
import de.cs3d.util.eclipse.extension.exclusive.ExclusiveExtension;
import eu.esdihumboldt.hale.ui.views.styledmap.clip.layout.PainterLayout;

/**
 * {@link PainterLayout} contribution
 * 
 * @author Simon Templer
 */
public class PainterLayoutContribution extends
		ExclusiveExtensionContribution<PainterLayout, PainterLayoutFactory> {

	/**
	 * @see AbstractExtensionContribution#initExtension()
	 */
	@Override
	protected ExclusiveExtension<PainterLayout, PainterLayoutFactory> initExtension() {
		return (PainterLayoutService) PlatformUI.getWorkbench().getService(
				PainterLayoutService.class);
	}

}
