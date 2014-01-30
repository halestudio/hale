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

package eu.esdihumboldt.hale.ui.lookup;

import java.util.List;

import eu.esdihumboldt.hale.common.lookup.LookupTableImport;
import eu.esdihumboldt.hale.common.lookup.internal.LookupLoadAdvisor;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Loads Lookup Table without saving as ressource (see {@link LookupLoadAdvisor}
 * )
 * 
 * @author Patrick Lieb
 */
@SuppressWarnings("restriction")
public class LookupTableLoadWizard extends LookupTableImportWizard {

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#getConfigurationPages()
	 */
	@Override
	protected List<AbstractConfigurationPage<? extends LookupTableImport, ? extends IOWizard<LookupTableImport>>> getConfigurationPages() {

		List<AbstractConfigurationPage<? extends LookupTableImport, ? extends IOWizard<LookupTableImport>>> configPages = super
				.getConfigurationPages();
		if (configPages == null)
			return null;

		// remove NameAndDescriptionPage from the config pages
		for (int i = 0; i < configPages.size(); i++) {
			if (configPages.get(i) instanceof NameAndDescriptionPage) {
				configPages.remove(i);
				break;
			}
		}
		return configPages;
	}
}
