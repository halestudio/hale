/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.oml.internal.goml.rdf;

import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IResource;

/**
 * Simple implementation of {@link IResource}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public class Resource implements IResource {

	private String location;

	// constructors ............................................................

	/**
	 * @param location
	 */
	public Resource(String location) {
		super();
		this.location = location;
	}

	// getters / setters .......................................................

	@Override
	public String toString() {
		return this.location.toString();
	}

	/**
	 * @return the location
	 */
	@Override
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

}
