/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
