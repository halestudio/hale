/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.util.groovy.json;

/**
 * Wrapper type to allow a JSON string to be provided to
 * {@link JsonStreamBuilder} that is interpreted as raw JSON.
 * 
 * @author Simon Templer
 */
public class RawJson {

	private final String json;

	/**
	 * Create a new raw JSON wrapper
	 * 
	 * @param json the JSON string
	 */
	public RawJson(String json) {
		super();
		this.json = json;
	}

	/**
	 * @return the raw JSON
	 */
	public String getJson() {
		return json;
	}

}
