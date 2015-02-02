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

package eu.esdihumboldt.hale.io.wfs;

/**
 * Enumeration of (supported) WFS versions.
 * 
 * @author Simon Templer
 */
public enum WFSVersion {
	/** WFS 1.1.0 */
	V1_1_0("1.1.0", "http://www.opengis.net/wfs"),
	/** WFS 2.0.0 */
	V2_0_0("2.0.0", "http://www.opengis.net/wfs/2.0");

	/**
	 * The version string.
	 */
	public final String versionString;
	/**
	 * The WFS namespace.
	 */
	public final String wfsNamespace;

	WFSVersion(String versionString, String wfsNamespace) {
		this.versionString = versionString;
		this.wfsNamespace = wfsNamespace;
	}

	@Override
	public String toString() {
		return versionString;
	}

	/**
	 * Derive a WFS version from a version string.
	 * 
	 * @param version the version string
	 * @param defVersion the default version to return if it cannot be
	 *            determined from the given string
	 * @return the detected or default version
	 */
	public static WFSVersion fromString(String version, WFSVersion defVersion) {
		if (version == null) {
			return defVersion;
		}
		switch (version) {
		case "1.1.0":
			return V1_1_0;
		case "2.0.0":
			return V2_0_0;
		default:
			return defVersion;
		}
	}

}
