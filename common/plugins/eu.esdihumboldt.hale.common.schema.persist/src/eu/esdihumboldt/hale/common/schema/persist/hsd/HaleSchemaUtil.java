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

import java.io.InputStream;

/**
 * HALE Schema Definition related utilities.
 * 
 * @author Simon Templer
 */
public abstract class HaleSchemaUtil {

	/**
	 * Get the input stream to the HALE Schema XML Schema Definition.
	 * 
	 * @return the input stream, the caller has to close it
	 */
	public static InputStream getHaleSchemaXSD() {
		return HaleSchemaUtil.class.getResourceAsStream("hsd.xsd");
	}

}
