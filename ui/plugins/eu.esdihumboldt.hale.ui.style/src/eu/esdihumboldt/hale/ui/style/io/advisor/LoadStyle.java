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

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.style.io.StyleReader;
import eu.esdihumboldt.hale.ui.common.service.style.StyleService;

/**
 * Stores loaded styles in the {@link StyleService}.
 * 
 * @author Simon Templer
 */
public class LoadStyle extends DefaultIOAdvisor<StyleReader> {

	/**
	 * @see AbstractIOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(StyleReader provider) {
		StyleService ss = getService(StyleService.class);

		ss.addStyles(provider.getStyles());

		super.handleResults(provider);
	}

}
