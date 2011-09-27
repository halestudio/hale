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

package eu.esdihumboldt.hale.io.csv.reader.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;

/**
 * Utils for the CSVSchemaReader and CSVInstanceReader
 * 
 * @author Kevin Mais
 */
public class CSVUtil implements CSVConstants{

	/**
	 * Reads only the first line of a given CSV file
	 * 
	 * @param provider provider to get the parameters from
	 * @return a reader containing the first line of the CSV file
	 * @throws IOException if an I/O operation fails
	 */
	public static CSVReader readFirst(ImportProvider provider) throws IOException {
		
		String separator = provider.getParameter(PARAM_SEPARATOR);
		char sep = (separator == null || separator.isEmpty())?(DEFAULT_SEPARATOR):(separator.charAt(0));
		String quote = provider.getParameter(PARAM_QUOTE);
		char qu = (quote == null || quote.isEmpty())?(DEFAULT_QUOTE):(quote.charAt(0));
		String escape = provider.getParameter(PARAM_ESCAPE);
		char esc = (escape == null || escape.isEmpty())?(DEFAULT_ESCAPE):(escape.charAt(0));
		
		Reader streamReader = new BufferedReader(new InputStreamReader(
				provider.getSource().getInput()));
		CSVReader reader = new CSVReader(streamReader, sep, qu, esc);
		
		return reader;
		
	}
}
