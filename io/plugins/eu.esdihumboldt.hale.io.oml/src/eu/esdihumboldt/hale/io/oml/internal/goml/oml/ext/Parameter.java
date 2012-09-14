/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : common-library
 * 	 
 * Classname    : eu.esdihumboldt.goml.oml.ext/Parameter.java 
 * 
 * Author       : schneidersb
 * 
 * Created on   : Sep 2, 2009 -- 2:08:36 PM
 *
 */
package eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext;

import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IParameter;

/**
 * @author schneidersb
 * 
 */
@SuppressWarnings("javadoc")
public class Parameter implements IParameter {

	String name;
	String value;

	public Parameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		return value;
	}

}
