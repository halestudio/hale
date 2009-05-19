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

package eu.esdihumboldt.goml.align;

import java.net.URI;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class Measure {
	
	/**
	 * a {@link URI} used to uniquely identify a certain Unit of Measure, such 
	 * as an SI unit.
	 */
	private URI datatype;

	// constructors ............................................................
	
	public Measure(URI datatype) {
		super();
		this.datatype = datatype;
	}

	// getters / setters .......................................................
	
	/**
	 * @return the datatype
	 */
	public URI getDatatype() {
		return datatype;
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(URI datatype) {
		this.datatype = datatype;
	}

	
}
