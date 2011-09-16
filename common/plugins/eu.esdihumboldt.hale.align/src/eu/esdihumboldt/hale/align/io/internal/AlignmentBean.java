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

package eu.esdihumboldt.hale.align.io.internal;

import eu.esdihumboldt.hale.align.model.MutableAlignment;
import eu.esdihumboldt.hale.core.io.report.IOReporter;

/**
 * Alignment bean serving as model for alignment I/O
 * @author Simon Templer
 */
public class AlignmentBean {

	/**
	 * Default constructor
	 */
	public AlignmentBean() {
		super();
	}
	
	/**
	 * Create a bean for the given alignment
	 * @param alignment the alignment
	 */
	public AlignmentBean(MutableAlignment alignment) {
		super();
		
		//TODO populate bean from alignment
	}
	
	/**
	 * Create an alignment from the information in the bean
	 * @param reporter the I/O reporter to report any errors to, may be <code>null</code>
	 * @return the alignment
	 */
	public MutableAlignment createAlignment(IOReporter reporter) {
		//TODO
		return null;
	}
	
}
