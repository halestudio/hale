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

import org.apache.commons.lang.StringUtils;

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

	@Override
	public Object idToReference(Object id) {
		if (id == null || id.toString().isEmpty()) {
			throw new IllegalArgumentException("ID must not be null or empty");
		}

		// XXX possible performance impact? this check is done for every
		// reference...
		if (!StringUtils.isNumeric(id.toString().substring(0, 1))
				&& !StringUtils.containsAny(id.toString(), "\"\\ !#$%&'()*+,/:;<=>?@[]^`{|}~")) {
			// if the ID is a valid NCName convert it to a local XPointer
			return "#" + id.toString();
		}
		else {
			return super.idToReference(id);
		}
	}
}
