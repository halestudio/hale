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

package eu.esdihumboldt.hale.common.core.io.project.model;

import java.util.TreeMap;

/**
 * Maps configuration names to export {@link IOConfiguration}s. Names are case
 * insensitive.
 * 
 * @author Simon Templer
 */
public class ExportConfigurationMap extends TreeMap<String, IOConfiguration> {

	private static final long serialVersionUID = -6351681177576079643L;

	/**
	 * Default constructor.
	 */
	public ExportConfigurationMap() {
		super(String.CASE_INSENSITIVE_ORDER);
	}

}
