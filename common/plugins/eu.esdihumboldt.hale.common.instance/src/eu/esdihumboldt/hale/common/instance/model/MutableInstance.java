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

/**
 * A mutable instance that allows adding/changing properties
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface MutableInstance extends Instance, MutableGroup {

	/**
	 * Set the instance value. It may only be a simple value, no collection or
	 * {@link Instance}.
	 * 
	 * @param value the instance value to set
	 */
	public void setValue(Object value);

	/**
	 * Set the data set the instance is associated to.
	 * 
	 * @param dataSet the instance data set
	 */
	public void setDataSet(DataSet dataSet);

	// XXX more manipulation needed? e.g. for transformation?

	/**
	 * add Data to the MetaData, which the Instance can be associated with If
	 * there is no MetaData in the Instance, a new container will be created
	 * 
	 * @param key a key the data to add will be associated with
	 * @param obj the Data to add, may not be {@link Instance} or {@link Group}
	 */
	public void putMetaData(String key, Object obj);

	/**
	 * Sets the metadata of a certain key, may also delet or reset the value of
	 * the key if the values parameter is null or empty
	 * 
	 * @param key the key the data is associated with
	 * @param values the values to set, may not contain {@link Instance} or
	 *            {@link Group}
	 */
	public void setMetaData(String key, Object... values);

}
