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

package eu.esdihumboldt.hale.ui.style.io.advisor;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;
import eu.esdihumboldt.hale.ui.style.io.StyleWriter;
import eu.esdihumboldt.hale.ui.style.service.StyleService;

/**
 * Save the styles present in the {@link StyleService}.
 * 
 * @author Simon Templer
 */
public class SaveStyle extends DefaultIOAdvisor<StyleWriter> {

	/**
	 * @see AbstractIOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(StyleWriter provider) {
		super.prepareProvider(provider);

		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);

		provider.setStyle(ss.getStyle());
	}

}
