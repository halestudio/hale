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

import java.util.List;

/**
 * Utility class for signing IDs to instance metadatas
 * 
 * @author Sebastian Reinhardt
 */
public class InstanceMetadata {

	/**
	 * Name for the ID value stored in instance meta data
	 */
	public static final String METADATA_ID = "ID";
	/**
	 * Name for the SourceID value stored in instance meta data
	 */
	public static final String METADATA_SOURCEID = "SourceID";

	/**
	 * sets the ID of an instance in its meta data
	 * 
	 * @param instance the certain instance to change
	 * @param id the id to set
	 */
	public static void setID(MutableInstance instance, Object id) {
		instance.setMetaData(METADATA_ID, id);
	}

	/**
	 * sets the SourceID of an instance in its meta data
	 * 
	 * @param instance the certain instance to change
	 * @param id the id to set, can be multiple IDs
	 */
	public static void setSourceID(MutableInstance instance, Object... id) {
		instance.setMetaData(METADATA_SOURCEID, id);
	}

	/**
	 * gets the SourceID of an instance from its metadata
	 * 
	 * @param instance the instance to get the SourceID from
	 * @return he first SourceID of the instance, my be <code> null <code> 
	 * if the instance doesn't contain an id in its meta data
	 */
	public static String getSourceID(Instance instance) {
		List<Object> data = instance.getMetaData(METADATA_SOURCEID);

		if (data.isEmpty()) {
			return null;
		}

		else {
			return data.get(0).toString();
		}
	}

	/**
	 * gets the ID of an instance from its metadata
	 * 
	 * @param instance the instance with the certain id
	 * @return the first id of the instance, my be <code> null <code> 
	 * if the instance doesn't contain an id in its meta data
	 */
	public static String getID(Instance instance) {
		List<Object> data = instance.getMetaData(METADATA_ID);

		if (data.isEmpty()) {
			return null;
		}

		else {
			return data.get(0).toString();
		}
	}
}
