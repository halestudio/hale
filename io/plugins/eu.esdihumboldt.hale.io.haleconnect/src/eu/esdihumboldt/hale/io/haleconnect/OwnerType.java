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

/**
 * Owner type
 * 
 * @author Florian Esser
 */
public enum OwnerType {
	/**
	 * A user
	 */
	USER("user"),

	/**
	 * An organisation
	 */
	ORGANISATION("org");

	private final String jsonValue;

	private OwnerType(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	/**
	 * @return Owner type value for the hale connect service APIs
	 */
	public String getJsonValue() {
		return jsonValue;
	}

	/**
	 * Yield the owner type for the respective string value.
	 * 
	 * @param jsonValue the owner string representation
	 * @return the owner type
	 * @throws IllegalArgumentException if there is no owner type for the given
	 *             string representation
	 */
	public static OwnerType fromJsonValue(String jsonValue) {
		switch (jsonValue) {
		case "user":
			return USER;
		case "org":
			return ORGANISATION;
		default:
			throw new IllegalArgumentException(
					MessageFormat.format("Not a valid JSON owner type: {0}", jsonValue));
		}
	}
}
