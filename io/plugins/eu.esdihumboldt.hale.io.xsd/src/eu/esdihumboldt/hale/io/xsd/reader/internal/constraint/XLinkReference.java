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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;

/**
 * Reference constraint that should be associated with a XLink href attribute.
 * 
 * @author Simon Templer
 */
public class XLinkReference extends Reference {

	/**
	 * Default constructor.
	 */
	public XLinkReference() {
		super(true);
	}

	@Override
	public Object extractId(Object refValue) {
		String ref = refValue.toString().trim();
		if (ref.length() > 1 && ref.charAt(0) == '#' && !ref.contains("(")) {
			// local XPointer referencing a simple ID
			return ref.substring(1);
		}

		return super.extractId(refValue);
	}

}
