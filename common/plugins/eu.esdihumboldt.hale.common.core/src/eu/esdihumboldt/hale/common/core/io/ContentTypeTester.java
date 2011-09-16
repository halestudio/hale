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

package eu.esdihumboldt.hale.common.core.io;

import java.io.InputStream;

/**
 * Interface for classes that test if an input stream represents the
 * corresponding content type.
 * A tester is associated to a content type by referencing it in the content
 * type definition. 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ContentTypeTester {
	
	/**
	 * Determines if the given input stream matches the content type associated
	 * to the tester
	 * 
	 * @param in the input stream
	 * 
	 * @return if the input matches the content type
	 */
	public boolean matchesContentType(InputStream in);

}
