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

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

import groovy.transform.CompileStatic

/**
 * Helper for building and decomposing hale-connect project URNs.
 * 
 * @author Florian Esser
 */
@CompileStatic
public class HaleConnectUrnBuilder {

	/**
	 * URI schema for hale connect locations
	 */
	public static final String SCHEME_HALECONNECT = "hc";

	/**
	 * Build a project URN from owner and project ID
	 * 
	 * @param owner the project owner
	 * @param projectId the project ID
	 * @return the hale-connect project URN
	 */
	public static URI buildProjectUrn(Owner owner, String projectId) {
		return URI.create(MessageFormat.format("{0}:project:{1}:{2}:{3}", SCHEME_HALECONNECT,
				owner.getType().getJsonValue(), owner.getId(), projectId));
	}

	/**
	 * Extract the owner from a hale-connect project URN.
	 * 
	 * @param hcUrn the project URN to parse
	 * @return the owner information extracted from the URN
	 */
	public static Owner extractProjectOwner(URI hcUrn) {
		String[] parts = splitProjectUrn(hcUrn);

		return new Owner(type: OwnerType.fromJsonValue(parts[1]), id: parts[2]);
	}

	/**
	 * Extract the project ID from a hale-connect project URN.
	 * 
	 * @param hcUrn the project URN to parse
	 * @return the project ID extracted from the URN
	 */
	public static String extractProjectId(URI hcUrn) {
		String[] parts = splitProjectUrn(hcUrn);

		return parts[3];
	}

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

	/**
	 * Tests if a given URI is a valid hale-connect project URN.
	 * 
	 * @param urn the URI to test
	 * @return <code>true</code> if the URI is a valid project URN,
	 *         <code>false</code> otherwise
	 */
	public static boolean isValidProjectUrn(URI urn) {
		try {
			splitProjectUrn(urn);
		} catch (Throwable t) {
			return false;
		}

		return true;
	}

	/**
	 * Build a URL for accessing a hale-connect project in the remote API.
	 * 
	 * @param basePath the base URL of the project service
	 * @param owner the project owner
	 * @param projectId the project ID
	 * @return the access URL
	 */
	public static URI buildClientAccessUrl(String basePath, Owner owner, String projectId) {
		return URI.create(MessageFormat.format("{0}/transformation/{1}/{2}/{3}", basePath,
				owner.getType().getJsonValue(), owner.getId(), projectId));
	}
}
