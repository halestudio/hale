/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.ui.util.bbr.properties;

import eu.esdihumboldt.hale.ui.util.bbr.Documentation;

/**
 * Properties section with a BBR definition.
 * 
 * @author Simon Templer
 */
public class DefinitionTextSection extends AbstractDocumentationTextSection {

	@Override
	protected String getDocumentationLabel() {
		return "Definition:";
	}

	@Override
	protected String getDocumentationText(Documentation doc) {
		return doc.getDefinition();
	}

}
