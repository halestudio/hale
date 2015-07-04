/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.model;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * This enumeration is used to identify the different data sets handled by the
 * application.
 */
public enum DataSet {
	/** The source data set */
	SOURCE,
	/** The transformed data set */
	TRANSFORMED;

	/**
	 * Yield the (default) data set for a schema space.
	 * 
	 * @param ssid the schema space ID
	 * @return the corresponding data set
	 */
	public static DataSet forSchemaSpace(SchemaSpaceID ssid) {
		if (ssid == null) {
			return null;
		}
		switch (ssid) {
		case SOURCE:
			return DataSet.SOURCE;
		case TARGET:
			return DataSet.TRANSFORMED;
		}
		return null;
	}

	/**
	 * @return the ID of the schema space associated with the data set
	 */
	public SchemaSpaceID getSchemaSpace() {
		switch (this) {
		case SOURCE:
			return SchemaSpaceID.SOURCE;
		case TRANSFORMED:
			return SchemaSpaceID.TARGET;
		}
		throw new IllegalStateException("Unknown data set type");
	}
}
