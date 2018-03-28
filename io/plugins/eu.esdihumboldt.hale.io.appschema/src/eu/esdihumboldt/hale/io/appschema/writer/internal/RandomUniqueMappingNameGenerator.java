/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     GeoSolutions <https://www.geo-solutions.it>
 */

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import java.util.UUID;

import javax.xml.namespace.QName;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import eu.esdihumboldt.hale.io.appschema.writer.UniqueMappingNameGenerator;

/**
 * Unique mapping name generator strategy that generates universally unique
 * names by appending the output of {@link UUID#randomUUID()} to the provided
 * feature type name.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class RandomUniqueMappingNameGenerator implements UniqueMappingNameGenerator {

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.UniqueMappingNameGenerator#generateUniqueMappingName(javax.xml.namespace.QName)
	 */
	@Override
	public String generateUniqueMappingName(QName featureTypeName) {
		String prefix = "";
		String uuid = UUID.randomUUID().toString();

		if (featureTypeName != null) {
			prefix = featureTypeName.getLocalPart();
		}
		if (Strings.isNullOrEmpty(prefix)) {
			prefix = "featureTypeMapping";
		}

		return Joiner.on("-").join(new String[] { prefix, uuid });
	}

}
