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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.util;

import eu.esdihumboldt.hale.util.nonosgi.contenttype.describer.XMLRootElementContentDescriber2;

/**
 * GZip content describer with an internal
 * {@link XMLRootElementContentDescriber2}.
 * 
 * @author Simon Templer
 */
public class GZipXMLRootElementContentDescriber extends GZipContentDescriber {

	/**
	 * Default constructor
	 */
	public GZipXMLRootElementContentDescriber() {
		super(new XMLRootElementContentDescriber2());
	}

}
