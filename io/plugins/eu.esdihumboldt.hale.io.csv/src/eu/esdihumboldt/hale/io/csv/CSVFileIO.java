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

package eu.esdihumboldt.hale.io.csv;

import eu.esdihumboldt.hale.common.core.io.ContentType;

/**
 * Utilities and constants for CSVfile I/O
 * 
 * @author Kevin Mais
 */
public abstract class CSVFileIO {

	/**
	 * The default CSV namespace
	 */
	public static final String CSVFILE_NS = "http://www.esdi-humboldt.eu/hale/csv";

	/**
	 * The CSVfile content type identifier
	 */
	public static final String CSVFILE_CT_ID = "CSV";

	/**
	 * The CSVfile content type
	 */
	public static final ContentType CSVFILE_CT = ContentType
			.getContentType(CSVFILE_CT_ID);

}
