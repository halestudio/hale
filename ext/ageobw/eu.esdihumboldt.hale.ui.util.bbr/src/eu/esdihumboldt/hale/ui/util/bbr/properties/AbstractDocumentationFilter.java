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

package eu.esdihumboldt.hale.ui.util.bbr.properties;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.util.bbr.Documentation;
import eu.esdihumboldt.hale.ui.util.bbr.DocumentationService;
import eu.esdihumboldt.hale.ui.util.bbr.impl.DocumentationServiceImpl;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionFilter;

/**
 * Filter that lets only {@link Definition}s with an associated BBR
 * documentation pass.
 * 
 * @author Simon Templer
 */
public abstract class AbstractDocumentationFilter extends DefaultDefinitionFilter {

	@Override
	public boolean isFiltered(Definition<?> input) {
		if (!input.getName().getNamespaceURI().equals(DocumentationServiceImpl.NS_URI_AGEOBW)) {
			// namespace does not match
			return true;
		}

		DocumentationService ds = PlatformUI.getWorkbench().getService(DocumentationService.class);
		Documentation doc = ds.getDocumentation(input);
		if (doc == null) {
			return true;
		}
		return !accept(doc);
	}

	/**
	 * Determines if a definition's BBR documentation is accepted.
	 * 
	 * @param doc the documentation
	 * @return if it the definition associated to the documentation should be
	 *         accepted
	 */
	protected abstract boolean accept(Documentation doc);

}
