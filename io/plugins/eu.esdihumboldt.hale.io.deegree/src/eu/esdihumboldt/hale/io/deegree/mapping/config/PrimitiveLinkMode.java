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

package eu.esdihumboldt.hale.io.deegree.mapping.config;

/**
 * Modes for determining which properties don't link to other features.
 * 
 * @author Simon Templer
 */
public enum PrimitiveLinkMode {

	/** Default configuration */
	none("Don't use primitive links"),

	/** Detect based on schema optional information */
	targetElement("All except where targetElement information is found in XML Schema AppInfo"),

	/** like targetElement, but only INSPIRE properties may be primitive */
	inspire("All INSPIRE links except where targetElement information is found in XML Schema AppInfo");

	// TODO codeListAssociation("Use primitive links for properties where code
	// lists are assigned in hale");

	private final String description;

	PrimitiveLinkMode(String description) {
		this.description = description;
	}

	@SuppressWarnings("javadoc")
	public String getDescription() {
		return description;
	}

}
