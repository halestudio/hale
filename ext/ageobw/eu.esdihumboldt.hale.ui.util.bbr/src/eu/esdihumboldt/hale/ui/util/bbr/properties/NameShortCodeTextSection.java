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
 * Properties section with a BBR name and/or short code.
 * 
 * @author Simon Templer
 */
public class NameShortCodeTextSection extends AbstractDocumentationTextSection {

	@Override
	protected String getDocumentationLabel() {
		return "Name [531]:";
	}

	@Override
	protected String getDocumentationText(Documentation doc) {
		StringBuilder str = new StringBuilder();

		if (doc.getName() != null && !doc.getName().isEmpty()) {
			str.append(doc.getName());
		}

		if (doc.getShortCode() != null && !doc.getShortCode().isEmpty()) {
			if (str.length() > 0)
				str.append(' ');
			str.append('[');
			str.append(doc.getShortCode());
			str.append(']');
		}

		return str.toString();
	}

	/**
	 * Do not use a multi-line text field.
	 * 
	 * @see AbstractDocumentationTextSection#useMultilineText()
	 */
	@Override
	protected boolean useMultilineText() {
		return false;
	}

}
