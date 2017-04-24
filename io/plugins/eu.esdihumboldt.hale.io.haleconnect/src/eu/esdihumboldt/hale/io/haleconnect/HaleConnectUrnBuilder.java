/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.io.haleconnect;

import java.net.URI;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

/**
 * TODO Type description
 * 
 * @author Florian Esser
 */
public class HaleConnectUrnBuilder {

	/**
	 * URI schema for hale connect locations
	 */
	public static final String SCHEME_HALECONNECT = "hc";

	public static URI buildProjectUrn(Owner owner, String projectId) {
		return URI.create(MessageFormat.format("{0}:project:{1}:{2}:{3}", SCHEME_HALECONNECT,
				owner.getType().getJsonValue(), owner.getId(), projectId));
	}

	public static Owner extractProjectOwner(URI hcUrn) {
		String[] parts = splitProjectUrn(hcUrn);

		return new Owner(OwnerType.fromJsonValue(parts[1]), parts[2]);
	}

	public static String extractProjectId(URI hcUrn) {
		String[] parts = splitProjectUrn(hcUrn);

		return parts[3];
	}

	/**
	 * @param urn
	 * @return
	 */
	private static String[] splitProjectUrn(URI urn) {
		if (urn == null) {
			throw new NullPointerException("URN must not be null");
		}
		else if (!SCHEME_HALECONNECT.equals(urn.getScheme().toLowerCase())) {
			throw new IllegalArgumentException(
					MessageFormat.format("URN must have scheme \"{0}\"", SCHEME_HALECONNECT));
		}

		if (StringUtils.isEmpty(urn.getSchemeSpecificPart())) {
			throw new IllegalArgumentException(
					MessageFormat.format("Malformed URN: {0}", urn.toString()));
		}

		String[] parts = urn.getSchemeSpecificPart().split(":");
		if (parts.length != 4) {
			throw new IllegalArgumentException(
					MessageFormat.format("Malformed URN: {0}", urn.toString()));
		}
		else if (!"project".equals(parts[0])) {
			throw new IllegalArgumentException(
					MessageFormat.format("No a project URN: {0}", urn.toString()));
		}
		return parts;
	}

	public static boolean isValidProjectUrn(URI urn) {
		try {
			splitProjectUrn(urn);
		} catch (Throwable t) {
			return false;
		}

		return true;
	}

}
