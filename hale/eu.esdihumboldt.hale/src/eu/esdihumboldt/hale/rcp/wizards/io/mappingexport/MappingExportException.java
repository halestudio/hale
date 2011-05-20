/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.io.mappingexport;

import eu.esdihumboldt.commons.goml.align.Alignment;

/**
 * Indicates that exporting an {@link Alignment} didn't work as expected.
 * 
 * @author Thorsten Reitz
 */
public class MappingExportException 
	extends Exception {

	private static final long serialVersionUID = -7483481471451493632L;

	/**
	 * @param message
	 * @param cause
	 */
	public MappingExportException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public MappingExportException(String message) {
		super(message);
	}
	
}
