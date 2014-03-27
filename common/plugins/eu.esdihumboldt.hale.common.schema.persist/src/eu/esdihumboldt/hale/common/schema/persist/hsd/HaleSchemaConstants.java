/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.persist.hsd;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * HALE Schema Definition constants.
 * 
 * @author Simon Templer
 */
public interface HaleSchemaConstants {

	/**
	 * The HALE Schema Definition XML namespace.
	 */
	public static final String NS = "http://www.esdi-humboldt.eu/hale/schema";

	/**
	 * The default namespace prefixes in a HALE Schema Definition file.
	 */
	public static final Map<String, String> DEF_PREFIXES = ImmutableMap.of( //
			"hsd", "http://www.esdi-humboldt.eu/hale/schema");

}
