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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.deegree.mapping.model;

import org.deegree.feature.persistence.sql.FeatureTypeMapping;
import org.deegree.feature.persistence.sql.id.FIDMapping;

/**
 * Helper class for using the deegree mapping model.
 * 
 * @author Simon Templer
 */
public class ModelHelper {

	/**
	 * Create a {@link FIDMapping} with a custom prefix.
	 * 
	 * @param prefix the prefix to set
	 * @param mapping the original mapping
	 * @return the adapted mapping
	 */
	public static FIDMapping withPrefix(String prefix, FIDMapping mapping) {
		return new FIDMapping(prefix, mapping.getDelimiter(), mapping.getColumns(),
				mapping.getIdGenerator());
	}

	/**
	 * Create a {@link FeatureTypeMapping} with a custom {@link FIDMapping}.
	 * 
	 * @param idMapping the ID mapping to set
	 * @param ft the original feature type mapping
	 * @return the adapted feature type mapping
	 */
	public static FeatureTypeMapping withFIDMapping(FIDMapping idMapping, FeatureTypeMapping ft) {
		return new FeatureTypeMapping(ft.getFeatureType(), ft.getFtTable(), idMapping,
				ft.getMappings());
	}

}
