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

package eu.esdihumboldt.hale.io.json.writer;

import eu.esdihumboldt.hale.io.json.JsonInstanceWriter;

/**
 * Writes instances as GeoJSON.
 * 
 * @author Kai Schwierczek
 */
public class GeoJSONInstanceWriter extends JsonInstanceWriter {

	/**
	 * Default constructor.
	 */
	public GeoJSONInstanceWriter() {
		super(true);
	}

	@Override
	protected String getDefaultTypeName() {
		return "GeoJSON";
	}

}
