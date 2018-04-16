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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.net.URI;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import eu.esdihumboldt.util.io.IOUtils;

/**
 * Reference updater that updates local references (starting with
 * "<code>#</code>" ) to relative references according to an ID->target map.
 * 
 * @author Florian Esser
 */
public class LocalReferenceUpdater implements ReferenceUpdater {

	private final Map<String, URI> idToTargetMapping;
	private final URI originalTarget;

	/**
	 * Create the reference updater
	 * 
	 * @param idToTargetMapping Mapping of local IDs to the new target URIs
	 * @param originalTarget URI of the original target
	 */
	public LocalReferenceUpdater(Map<String, URI> idToTargetMapping, URI originalTarget) {
		this.idToTargetMapping = idToTargetMapping;
		this.originalTarget = originalTarget;
	}

	@Override
	public String updateReference(String value) {
		if (!value.startsWith("#")) {
			return value;
		}

		String id = value.substring(1);
		if (idToTargetMapping.containsKey(id)) {
			URI idTarget = idToTargetMapping.get(id);
			if (!originalTarget.equals(idTarget)) {
				URI relativeUri = IOUtils.getRelativePath(idTarget, originalTarget);
				URIBuilder builder = new URIBuilder(relativeUri);
				builder.setFragment(id);
				value = builder.toString();
			}
		}

		return value;
	}

}
