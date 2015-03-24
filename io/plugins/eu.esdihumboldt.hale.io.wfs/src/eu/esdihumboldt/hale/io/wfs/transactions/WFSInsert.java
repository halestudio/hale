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
	 * Constructor.
	 * 
	 * @param wfsVersion the WFS version
	 * @param idgen the ID generation strategy (only WFS 1.1)
	 * @param inputFormat the data input format
	 */
	public WFSInsert(WFSVersion wfsVersion, String idgen, String inputFormat) {
		super(wfsVersion);

		Map<String, String> params = new HashMap<>();

		// ID generation (only WFS 1.1.0)
		if (WFSVersion.V1_1_0.equals(wfsVersion)) {
			// possible values are "UseExisting", "GenerateNew" or
			// "ReplaceDuplicate"
			params.put("idgen", idgen);
		}

		// input format
		if (inputFormat != null) {
			params.put("inputFormat", inputFormat);
		}

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
