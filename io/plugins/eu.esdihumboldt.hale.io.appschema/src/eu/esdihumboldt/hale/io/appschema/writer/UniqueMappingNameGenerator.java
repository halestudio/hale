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

package eu.esdihumboldt.hale.io.appschema.writer;

import javax.xml.namespace.QName;

/**
 * Common interface for unique mapping name generation strategies.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public interface UniqueMappingNameGenerator {

	/**
	 * Generates a universally unique mapping name for the provided feature type
	 * name.
	 * 
	 * @param featureTypeName the name of the feature type for which a mapping
	 *            name must be generated
	 * @return a universally unique mapping name
	 */
	public String generateUniqueMappingName(QName featureTypeName);

}
