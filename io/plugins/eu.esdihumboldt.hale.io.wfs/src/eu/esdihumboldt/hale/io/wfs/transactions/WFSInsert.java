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

package eu.esdihumboldt.hale.io.wfs.transactions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.io.wfs.WFSVersion;

/**
 * WFS Insert.
 * 
 * @author Simon Templer
 */
public class WFSInsert extends AbstractWFSTransaction {

	private final Map<String, String> params;

	/**
	 * @param wfsVersion the WFS version
	 */
	public WFSInsert(WFSVersion wfsVersion) {
		super(wfsVersion);

		Map<String, String> params = new HashMap<>();

		// ID generation (only WFS 1.1.0)
		if (WFSVersion.V1_1_0.equals(wfsVersion)) {
			// TODO configurable
			params.put("idgen", "GenerateNew");
		}
		// XXX other values are "UseExisting" or "ReplaceDuplicate"

		// input format
		// FIXME should be configurable
		params.put("inputFormat", "text/xml; subtype=gml/3.2.1");

		// reference system
		// "srsName"

		this.params = Collections.unmodifiableMap(params);
	}

	@Override
	protected String getActionName() {
		return "Insert";
	}

	@Override
	protected Map<String, String> getActionAttributes() {
		return params;
	}

}
